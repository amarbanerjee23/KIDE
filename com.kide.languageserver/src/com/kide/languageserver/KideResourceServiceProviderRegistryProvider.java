package com.kide.languageserver;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.ISetup;
import org.eclipse.xtext.resource.FileExtensionProvider;
import org.eclipse.xtext.resource.IResourceServiceProvider;
import org.eclipse.xtext.resource.impl.ResourceServiceProviderRegistryImpl;
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
            IResourceServiceProvider serviceProvider = injector.getInstance(IResourceServiceProvider.class);
            FileExtensionProvider extensionProvider = injector.getInstance(FileExtensionProvider.class);

            if (!extensionProvider.getFileExtensions().contains(language.extension)) {
                throw new IllegalStateException("KIDE language " + language.setupClass
                        + " does not advertise expected extension '." + language.extension + "'");
            }

            for (String extension : extensionProvider.getFileExtensions()) {
                Object previous = extensions.put(extension, serviceProvider);
                if (previous != null && previous != serviceProvider) {
                    throw new IllegalStateException("Duplicate Xtext language registration for extension '."
                            + extension + "'");
                }
            }
        }

        for (LanguageSetup language : LANGUAGES) {
            URI probe = URI.createURI("memory:/probe." + language.extension);
            if (result.getResourceServiceProvider(probe) == null) {
                throw new IllegalStateException("KIDE language provider missing for '."
                        + language.extension + "'");
            }
        }
        return result;
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
