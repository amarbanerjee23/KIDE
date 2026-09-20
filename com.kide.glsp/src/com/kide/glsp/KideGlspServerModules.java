package com.kide.glsp;

import org.eclipse.glsp.server.di.ServerModule;

public final class KideGlspServerModules {
    private KideGlspServerModules() { }

    public static ServerModule create() {
        return new ServerModule()
                .configureDiagramModule(new ActivityDiagramModule())
                .configureDiagramModule(new MncDiagramModule());
    }
}
