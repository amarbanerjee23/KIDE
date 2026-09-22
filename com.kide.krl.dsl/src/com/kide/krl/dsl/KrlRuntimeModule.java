package com.kide.krl.dsl;

import org.eclipse.xtext.formatting2.IFormatter2;
import org.eclipse.xtext.validation.AbstractDeclarativeValidator;

import com.kide.krl.dsl.formatting2.KrlFormatter;
import com.kide.krl.dsl.validation.KrlValidator;

public class KrlRuntimeModule extends AbstractKrlRuntimeModule {
    public Class<? extends IFormatter2> bindIFormatter2() {
        return KrlFormatter.class;
    }

    public Class<? extends AbstractDeclarativeValidator> bindAbstractDeclarativeValidator() {
        return KrlValidator.class;
    }
}
