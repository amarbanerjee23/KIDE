package com.kide.krl.dsl.ui.internal;

import java.util.Collections;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.ui.shared.SharedStateModule;
import org.eclipse.xtext.util.Modules2;
import org.osgi.framework.BundleContext;

import com.google.common.collect.Maps;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.kide.krl.dsl.KrlRuntimeModule;
import com.kide.krl.dsl.ui.KrlUiModule;

public final class DslActivator extends AbstractUIPlugin {
    public static final String PLUGIN_ID = "com.kide.krl.dsl.ui";
    public static final String COM_KIDE_KRL_DSL_KRL = "com.kide.krl.dsl.Krl";

    private static DslActivator instance;

    private final Map<String, Injector> injectors =
            Collections.synchronizedMap(
                    Maps.<String, Injector>newHashMapWithExpectedSize(1));

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        instance = this;
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        injectors.clear();
        instance = null;
        super.stop(context);
    }

    public static DslActivator getInstance() {
        return instance;
    }

    public Injector getInjector(String language) {
        synchronized (injectors) {
            Injector injector = injectors.get(language);
            if (injector == null) {
                injector = createInjector(language);
                injectors.put(language, injector);
            }
            return injector;
        }
    }

    protected Injector createInjector(String language) {
        try {
            return Guice.createInjector(Modules2.mixin(
                    getRuntimeModule(language),
                    new SharedStateModule(),
                    getUiModule(language)));
        } catch (RuntimeException exception) {
            getLog().log(new Status(
                    IStatus.ERROR,
                    PLUGIN_ID,
                    "Failed to create injector for " + language,
                    exception));
            throw exception;
        }
    }

    protected com.google.inject.Module getRuntimeModule(String language) {
        if (COM_KIDE_KRL_DSL_KRL.equals(language)) {
            return new KrlRuntimeModule();
        }
        throw new IllegalArgumentException(language);
    }

    protected com.google.inject.Module getUiModule(String language) {
        if (COM_KIDE_KRL_DSL_KRL.equals(language)) {
            return new KrlUiModule(this);
        }
        throw new IllegalArgumentException(language);
    }
}
