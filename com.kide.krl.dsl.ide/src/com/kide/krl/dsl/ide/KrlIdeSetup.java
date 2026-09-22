package com.kide.krl.dsl.ide;

import org.eclipse.xtext.util.Modules2;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.kide.krl.dsl.KrlRuntimeModule;
import com.kide.krl.dsl.KrlStandaloneSetup;

public class KrlIdeSetup extends KrlStandaloneSetup {
    @Override
    public Injector createInjector() {
        return Guice.createInjector(
                Modules2.mixin(new KrlRuntimeModule(), new KrlIdeModule()));
    }
}
