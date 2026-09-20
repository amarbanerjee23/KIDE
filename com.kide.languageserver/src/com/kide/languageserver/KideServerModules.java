package com.kide.languageserver;

import org.eclipse.xtext.ide.server.ServerModule;
import org.eclipse.xtext.resource.IResourceServiceProvider;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.util.Modules;

/** Guice composition for the Xtext server with KIDE's deterministic language registry. */
public final class KideServerModules {

    /*
     * One process-wide provider prevents each embedded WebSocket session from
     * replaying generated standalone setup side effects against Xtext's global
     * registry. The provider already performs thread-safe lazy initialization.
     */
    private static final KideResourceServiceProviderRegistryProvider REGISTRY_PROVIDER =
            new KideResourceServiceProviderRegistryProvider();

    private KideServerModules() {
    }

    public static Module create() {
        return Modules.override(new ServerModule()).with(new AbstractModule() {
            @Override
            protected void configure() {
                bind(IResourceServiceProvider.Registry.class).toProvider(REGISTRY_PROVIDER);
            }
        });
    }
}
