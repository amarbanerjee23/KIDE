package com.kide.krl.dsl;

import org.eclipse.xtext.formatting2.AbstractFormatter2;
import org.eclipse.xtext.formatting2.IFormattableDocument;

public final class KrlFormatter extends AbstractFormatter2 {
    @Override
    protected void format(Object object, IFormattableDocument document) {
        // KRL generation is deterministic and does not rely on formatter side effects.
        // Xtext's default whitespace serializer remains available through LSP formatting.
    }
}
