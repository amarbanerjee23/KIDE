package com.kide.krl.dsl;

import org.eclipse.xtext.formatting2.IFormatter2;
import com.kide.krl.dsl.formatting2.KrlFormatter;

public class KrlRuntimeModule extends AbstractKrlRuntimeModule {
    public Class<? extends IFormatter2> bindIFormatter2() {
        return KrlFormatter.class;
    }
}
