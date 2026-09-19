/*
 * KIDE-owned Eclipse UI activator.
 *
 * Kept in src/ deliberately: Xtext-generated language infrastructure remains in
 * src-gen/, while lifecycle/error handling is hand-written and uses Eclipse
 * logging rather than the legacy Log4j 1 API.
 */
package com.smr.activity.dsl.ui.internal;

import com.smr.activity.dsl.ActivityDiagramRuntimeModule;
import com.smr.activity.dsl.ui.ActivityDiagramUiModule;
import com.google.common.collect.Maps;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.util.Collections;
import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.ui.shared.SharedStateModule;
import org.eclipse.xtext.util.Modules2;
import org.osgi.framework.BundleContext;

public class DslActivator extends AbstractUIPlugin {

    public static final String PLUGIN_ID = "com.smr.activity.dsl.ui";
    public static final String COM_SMR_ACTIVITY_DSL_ACTIVITYDIAGRAM = "com.smr.activity.dsl.ActivityDiagram";

    private static DslActivator INSTANCE;

    private final Map<String, Injector> injectors =
            Collections.synchronizedMap(Maps.<String, Injector>newHashMapWithExpectedSize(1));

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        INSTANCE = this;
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        injectors.clear();
        INSTANCE = null;
        super.stop(context);
    }

    public static DslActivator getInstance() {
        return INSTANCE;
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
            com.google.inject.Module runtimeModule = getRuntimeModule(language);
            com.google.inject.Module sharedStateModule = getSharedStateModule();
            com.google.inject.Module uiModule = getUiModule(language);
            com.google.inject.Module mergedModule =
                    Modules2.mixin(runtimeModule, sharedStateModule, uiModule);
            return Guice.createInjector(mergedModule);
        } catch (Exception e) {
            getLog().log(new Status(
                    IStatus.ERROR,
                    PLUGIN_ID,
                    "Failed to create injector for " + language,
                    e));
            throw new RuntimeException("Failed to create injector for " + language, e);
        }
    }

    protected com.google.inject.Module getRuntimeModule(String grammar) {
        if (COM_SMR_ACTIVITY_DSL_ACTIVITYDIAGRAM.equals(grammar)) {
            return new ActivityDiagramRuntimeModule();
        }
        throw new IllegalArgumentException(grammar);
    }

    protected com.google.inject.Module getUiModule(String grammar) {
        if (COM_SMR_ACTIVITY_DSL_ACTIVITYDIAGRAM.equals(grammar)) {
            return new ActivityDiagramUiModule(this);
        }
        throw new IllegalArgumentException(grammar);
    }

    protected com.google.inject.Module getSharedStateModule() {
        return new SharedStateModule();
    }
}
