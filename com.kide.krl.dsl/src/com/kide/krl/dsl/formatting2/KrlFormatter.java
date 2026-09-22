package com.kide.krl.dsl.formatting2;

import org.eclipse.xtext.formatting2.AbstractFormatter2;
import org.eclipse.xtext.formatting2.IFormattableDocument;

public final class KrlFormatter extends AbstractFormatter2 {
    @Override
    protected void format(Object object, IFormattableDocument document) {
        // KRL generation semantics never depend on whitespace or formatter side effects.
    }
}
