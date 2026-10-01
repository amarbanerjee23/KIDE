package com.smr.activity.dsl.ide;

import org.eclipse.xtext.ide.editor.syntaxcoloring.ISemanticHighlightingCalculator;
import org.eclipse.xtext.util.Modules2;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import com.smr.activity.dsl.ActivityDiagramRuntimeModule;
import com.smr.activity.dsl.ActivityDiagramStandaloneSetup;
import com.smr.activity.dsl.ide.highlighting.KideActivityIdeSemanticHighlightingCalculator;

/** KIDE-owned headless editor-service bindings for Activity. */
public final class KideActivityDiagramIdeSetup extends ActivityDiagramStandaloneSetup {
    @Override
    public Injector createInjector() {
        Module generated =
                Modules2.mixin(new ActivityDiagramRuntimeModule(), new ActivityDiagramIdeModule());
        Module parity = new AbstractModule() {
            @Override
            protected void configure() {
                bind(ISemanticHighlightingCalculator.class)
                        .to(KideActivityIdeSemanticHighlightingCalculator.class);
            }
        };
        return Guice.createInjector(Modules.override(generated).with(parity));
    }
}
