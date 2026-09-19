package com.dml.dsl.ide;

import org.eclipse.xtext.ide.editor.contentassist.IdeContentProposalProvider;
import org.eclipse.xtext.ide.server.contentassist.ContentAssistService;
import org.eclipse.xtext.ide.server.symbol.DocumentSymbolMapper;
import org.eclipse.xtext.ide.server.symbol.DocumentSymbolService;
import org.eclipse.xtext.util.Modules2;

import com.dml.dsl.DmlRuntimeModule;
import com.dml.dsl.DmlStandaloneSetup;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;

/**
 * KIDE language-server setup for DML.
 *
 * <p>The generated DML setup remains frozen by PR15. KIDE layers the current
 * headless completion compatibility binding outside generator-owned bytes.</p>
 */
public final class KideDmlIdeSetup extends DmlStandaloneSetup {
    @Override
    public Injector createInjector() {
        Module generated = Modules2.mixin(new DmlRuntimeModule(), new DmlIdeModule());
        Module kideCompatibility = new AbstractModule() {
            @Override
            protected void configure() {
                bind(IdeContentProposalProvider.class)
                        .to(KideDmlIdeContentProposalProvider.class);
                bind(ContentAssistService.class)
                        .to(KideDmlContentAssistService.class);
                bind(DocumentSymbolMapper.DocumentSymbolNameProvider.class)
                        .to(KideDmlDocumentSymbolNameProvider.class);
                bind(DocumentSymbolService.class)
                        .to(KideDmlDocumentSymbolService.class);
            }
        };
        return Guice.createInjector(Modules.override(generated).with(kideCompatibility));
    }
}
