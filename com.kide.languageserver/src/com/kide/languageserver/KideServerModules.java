package com.kide.languageserver;

import org.eclipse.xtext.ide.refactoring.IRenameStrategy2;
import org.eclipse.xtext.ide.server.ServerModule;
import org.eclipse.xtext.resource.IResourceServiceProvider;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.util.Modules;

/** Guice composition for the Xtext server with KIDE's deterministic language registry. */
public final class KideServerModules {

    private KideServerModules() {
    }

    public static Module create() {
        KideResourceServiceProviderRegistryProvider registryProvider =
                new KideResourceServiceProviderRegistryProvider();
        return Modules.override(new ServerModule()).with(new AbstractModule() {
            @Override
            protected void configure() {
                bind(IResourceServiceProvider.Registry.class).toProvider(registryProvider);
                bind(IRenameStrategy2.class).to(IRenameStrategy2.DefaultImpl.class);
            }
        });
    }
}
