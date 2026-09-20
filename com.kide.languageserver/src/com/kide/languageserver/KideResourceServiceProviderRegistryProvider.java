package com.kide.languageserver;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.ISetup;
import org.eclipse.xtext.ide.refactoring.IRenameStrategy2;
import org.eclipse.xtext.parser.IEncodingProvider;
import org.eclipse.xtext.resource.FileExtensionProvider;
import org.eclipse.xtext.resource.IContainer;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.IResourceServiceProvider;
import org.eclipse.xtext.resource.IResourceServiceProviderExtension;
import org.eclipse.xtext.resource.impl.ResourceServiceProviderRegistryImpl;
import org.eclipse.xtext.validation.IResourceValidator;
import org.osgi.framework.Bundle;

import com.google.inject.Injector;
import com.google.inject.Provider;

/**
 * Builds the language-server registry from the exact generated KIDE IdeSetup
 * classes. This avoids relying on Java ServiceLoader visibility across OSGi
 * bundle class loaders while still using the generated Xtext language setup.
 */
public final class KideResourceServiceProviderRegistryProvider
        implements Provider<IResourceServiceProvider.Registry> {

    private static final List<LanguageSetup> LANGUAGES = Arrays.asList(
            new LanguageSetup("com.dml.dsl.ide", "com.dml.dsl.ide.DmlIdeSetup", "dml"),
            new LanguageSetup("com.capability.dsl.ide", "com.capability.ide.CapabilityIdeSetup", "cap"),
            new LanguageSetup("com.mncml.dsl.ide", "com.mncml.dsl.ide.MncIdeSetup", "mncspec"),
            new LanguageSetup("com.operation.dsl.ide", "com.operation.dsl.ide.OperationIdeSetup", "op"),
            new LanguageSetup("com.smr.activity.dsl.ide", "com.smr.activity.dsl.ide.ActivityDiagramIdeSetup", "activity"));

    private volatile IResourceServiceProvider.Registry registry;

    @Override
    public IResourceServiceProvider.Registry get() {
        IResourceServiceProvider.Registry current = registry;
        if (current == null) {
            synchronized (this) {
                current = registry;
                if (current == null) {
                    current = createRegistry();
                    registry = current;
                }
            }
        }
        return current;
    }

    private IResourceServiceProvider.Registry createRegistry() {
        ResourceServiceProviderRegistryImpl result = new ResourceServiceProviderRegistryImpl();
        Map<String, Object> extensions = result.getExtensionToFactoryMap();

        for (LanguageSetup language : LANGUAGES) {
            Injector injector = instantiate(language).createInjectorAndDoEMFRegistration();
            IResourceServiceProvider serviceProvider =
                    renameCompatible(injector, injector.getInstance(IResourceServiceProvider.class));
            FileExtensionProvider extensionProvider = injector.getInstance(FileExtensionProvider.class);

            if (!extensionProvider.getFileExtensions().contains(language.extension)) {
                throw new IllegalStateException("KIDE language " + language.setupClass
                        + " does not advertise expected extension '." + language.extension + "'");
            }
            if (serviceProvider.get(IRenameStrategy2.class) == null) {
                throw new IllegalStateException("KIDE language " + language.setupClass
                        + " has no LSP rename strategy");
            }

            for (String extension : extensionProvider.getFileExtensions()) {
                Object previous = extensions.put(extension, serviceProvider);
                if (previous != null && previous != serviceProvider) {
                    throw new IllegalStateException("Duplicate Xtext language registration for extension '."
                            + extension + "'");
                }
            }
        }

        // Some older generated standalone setups register transitive language
        // dependencies as a side effect. For example Capability setup invokes
        // DML standalone setup, which can overwrite the global DML entry with a
        // runtime-only provider after KIDE already registered the IDE-aware one.
        // Xtext RenameService2 is created from a language injector and therefore
        // resolves its registry through Registry.INSTANCE, not only through this
        // server-local registry. Publish the final IDE-aware providers globally
        // after every generated setup has finished, so transitive registrations
        // cannot downgrade rename/IDE services.
        synchronized (IResourceServiceProvider.Registry.INSTANCE) {
            Map<String, Object> globalExtensions =
                    IResourceServiceProvider.Registry.INSTANCE.getExtensionToFactoryMap();
            for (LanguageSetup language : LANGUAGES) {
                Object provider = extensions.get(language.extension);
                if (!(provider instanceof IResourceServiceProvider)) {
                    throw new IllegalStateException("KIDE language provider missing for '."
                            + language.extension + "'");
                }
                globalExtensions.put(language.extension, provider);
            }
        }

        for (LanguageSetup language : LANGUAGES) {
            URI probe = URI.createURI("memory:/probe." + language.extension);
            IResourceServiceProvider local = result.getResourceServiceProvider(probe);
            IResourceServiceProvider global =
                    IResourceServiceProvider.Registry.INSTANCE.getResourceServiceProvider(probe);
            if (local == null || global == null) {
                throw new IllegalStateException("KIDE language provider missing for '."
                        + language.extension + "'");
            }
            if (local.get(IRenameStrategy2.class) == null
                    || global.get(IRenameStrategy2.class) == null) {
                throw new IllegalStateException("KIDE language rename provider missing for '."
                        + language.extension + "'");
            }
        }
        return result;
    }

    private static IResourceServiceProvider renameCompatible(
            Injector injector,
            IResourceServiceProvider delegate) {
        IRenameStrategy2 renameStrategy = delegate.get(IRenameStrategy2.class);
        if (renameStrategy != null) return delegate;

        IRenameStrategy2.DefaultImpl fallback = new IRenameStrategy2.DefaultImpl();
        injector.injectMembers(fallback);
        return new RenameCompatibleResourceServiceProvider(delegate, fallback);
    }

    private ISetup instantiate(LanguageSetup language) {
        Bundle bundle = Platform.getBundle(language.bundleId);
        if (bundle == null) {
            throw new IllegalStateException("Required KIDE language bundle is not installed: "
                    + language.bundleId);
        }
        String effectiveSetupClass = "dml".equals(language.extension)
                ? "com.dml.dsl.ide.KideDmlIdeSetup"
                : language.setupClass;
        try {
            Class<?> setupType = bundle.loadClass(effectiveSetupClass);
            Object setup = setupType.getDeclaredConstructor().newInstance();
            if (!(setup instanceof ISetup)) {
                throw new IllegalStateException(effectiveSetupClass
                        + " does not implement org.eclipse.xtext.ISetup");
            }
            return (ISetup) setup;
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Cannot instantiate KIDE Xtext setup "
                    + effectiveSetupClass, exception);
        }
    }

    /**
     * Xtext 2.44's rename service resolves IRenameStrategy2 from the language
     * resource service provider, not from the global server injector. KIDE still
     * ships some generated IDE modules from older Xtext generations, so this
     * compatibility wrapper supplies the standard strategy only when the
     * language provider cannot expose its generated binding.
     */
    private static final class RenameCompatibleResourceServiceProvider
            implements IResourceServiceProvider, IResourceServiceProviderExtension {

        private final IResourceServiceProvider delegate;
        private final IRenameStrategy2 renameStrategy;

        private RenameCompatibleResourceServiceProvider(
                IResourceServiceProvider delegate,
                IRenameStrategy2 renameStrategy) {
            this.delegate = delegate;
            this.renameStrategy = renameStrategy;
        }

        @Override
        public IResourceValidator getResourceValidator() {
            return delegate.getResourceValidator();
        }

        @Override
        public IResourceDescription.Manager getResourceDescriptionManager() {
            return delegate.getResourceDescriptionManager();
        }

        @Override
        public IContainer.Manager getContainerManager() {
            return delegate.getContainerManager();
        }

        @Override
        public boolean canHandle(URI uri) {
            return delegate.canHandle(uri);
        }

        @Override
        public IEncodingProvider getEncodingProvider() {
            return delegate.getEncodingProvider();
        }

        @Override
        public <T> T get(Class<T> type) {
            if (IRenameStrategy2.class.equals(type)) {
                return type.cast(renameStrategy);
            }
            return delegate.get(type);
        }

        @Override
        public boolean isSource(URI uri) {
            if (delegate instanceof IResourceServiceProviderExtension extension) {
                return extension.isSource(uri);
            }
            return !uri.isArchive();
        }
    }

    private static final class LanguageSetup {
        private final String bundleId;
        private final String setupClass;
        private final String extension;

        private LanguageSetup(String bundleId, String setupClass, String extension) {
            this.bundleId = bundleId;
            this.setupClass = setupClass;
            this.extension = extension;
        }
    }
}
