package com.capability.ide;

import org.eclipse.xtext.ide.editor.syntaxcoloring.ISemanticHighlightingCalculator;
import org.eclipse.xtext.util.Modules2;

import com.capability.CapabilityRuntimeModule;
import com.capability.CapabilityStandaloneSetup;
import com.capability.ide.highlighting.CapabilityLspSemanticHighlightingCalculator;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;

/** KIDE-owned headless editor-service bindings for Capability. */
public final class KideCapabilityIdeSetup extends CapabilityStandaloneSetup {
    @Override
    public Injector createInjector() {
        Module generated =
                Modules2.mixin(new CapabilityRuntimeModule(), new CapabilityIdeModule());
        Module parity = new AbstractModule() {
            @Override
            protected void configure() {
                bind(ISemanticHighlightingCalculator.class)
                        .to(CapabilityLspSemanticHighlightingCalculator.class);
            }
        };
        return Guice.createInjector(Modules.override(generated).with(parity));
    }
}
