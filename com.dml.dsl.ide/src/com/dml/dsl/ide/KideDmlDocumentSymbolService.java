package com.dml.dsl.ide;

import org.eclipse.xtext.ide.server.symbol.DocumentSymbolService;
import org.eclipse.xtext.naming.QualifiedName;

/** Restores simple DML names for workspace symbols without changing model scoping. */
public final class KideDmlDocumentSymbolService extends DocumentSymbolService {
    @Override
    protected String getSymbolName(QualifiedName qualifiedName) {
        return qualifiedName == null ? null : qualifiedName.getLastSegment();
    }
}
