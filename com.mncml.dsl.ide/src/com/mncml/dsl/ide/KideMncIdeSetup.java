package com.mncml.dsl.ide;

import org.eclipse.xtext.ide.editor.syntaxcoloring.ISemanticHighlightingCalculator;
import org.eclipse.xtext.util.Modules2;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import com.mncml.dsl.MncRuntimeModule;
import com.mncml.dsl.MncStandaloneSetup;
import com.mncml.dsl.ide.highlighting.KideMncIdeSemanticHighlightingCalculator;

/** KIDE-owned headless editor-service bindings for MNC. */
public final class KideMncIdeSetup extends MncStandaloneSetup {
    @Override
    public Injector createInjector() {
        Module generated = Modules2.mixin(new MncRuntimeModule(), new MncIdeModule());
        Module parity = new AbstractModule() {
            @Override
            protected void configure() {
                bind(ISemanticHighlightingCalculator.class)
                        .to(KideMncIdeSemanticHighlightingCalculator.class);
            }
        };
        return Guice.createInjector(Modules.override(generated).with(parity));
    }
}
