package com.dml.dsl.ide;

import org.eclipse.xtext.ide.server.symbol.DocumentSymbolMapper;
import org.eclipse.xtext.naming.QualifiedName;

/** Restores the legacy simple DML names exposed to document-symbol clients. */
public final class KideDmlDocumentSymbolNameProvider
        extends DocumentSymbolMapper.DocumentSymbolNameProvider {
    @Override
    protected String getName(QualifiedName qualifiedName) {
        return qualifiedName == null ? null : qualifiedName.getLastSegment();
    }
}
