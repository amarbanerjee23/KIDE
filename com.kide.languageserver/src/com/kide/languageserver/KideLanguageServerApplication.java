package com.kide.languageserver;

import java.util.Map;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.xtext.ide.server.ServerLauncher;

/** Headless Equinox entry point for the KIDE multi-language LSP server. */
public final class KideLanguageServerApplication implements IApplication {

    @Override
    public Object start(IApplicationContext context) {
        String[] args = applicationArguments(context);
        ServerLauncher.launch("kide-language-server", args, KideServerModules.create());
        return IApplication.EXIT_OK;
    }

    @Override
    public void stop() {
        // The LSP shutdown/exit lifecycle is owned by Xtext's LanguageServerImpl.
    }

    private String[] applicationArguments(IApplicationContext context) {
        Map<?, ?> arguments = context.getArguments();
        Object value = arguments.get(IApplicationContext.APPLICATION_ARGS);
        return value instanceof String[] ? (String[]) value : new String[0];
    }
}
