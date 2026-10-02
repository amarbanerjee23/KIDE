package com.mncml.dsl.ide.quickfix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionKind;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.WorkspaceEdit;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.xtext.ide.server.codeActions.ICodeActionService2;

import com.mncml.dsl.validation.MncValidator;

/** LSP adapter for the active MNC Eclipse quick fixes. */
public final class MncCodeActionService implements ICodeActionService2 {

    @Override
    public List<Either<org.eclipse.lsp4j.Command, CodeAction>> getCodeActions(
            Options options) {
        if (options == null || options.getCodeActionParams() == null
                || options.getCodeActionParams().getContext() == null) {
            return Collections.emptyList();
        }

        List<Either<org.eclipse.lsp4j.Command, CodeAction>> result =
                new ArrayList<>();
        for (Diagnostic diagnostic
                : options.getCodeActionParams().getContext().getDiagnostics()) {
            if (!MncValidator.INVALID_NAME.equals(code(diagnostic))) {
                continue;
            }
            Range range = diagnostic.getRange();
            if (range == null || options.getLanguageServerAccess() == null
                    || options.getURI() == null) {
                continue;
            }
            String issueText = options.getLanguageServerAccess().doSyncRead(
                    options.getURI(),
                    context -> context.getDocument().getSubstring(range));
            if (issueText.isEmpty()) {
                continue;
            }
            String replacement = Character.toUpperCase(issueText.charAt(0))
                    + issueText.substring(1);
            result.add(Either.forRight(action(
                    "Capitalize name",
                    "Capitalize the name.",
                    options.getURI(),
                    diagnostic,
                    new TextEdit(range, replacement))));
        }
        return result;
    }

    private static CodeAction action(
            String title,
            String detail,
            String uri,
            Diagnostic diagnostic,
            TextEdit edit) {
        CodeAction action = new CodeAction(title);
        action.setKind(CodeActionKind.QuickFix);
        action.setDiagnostics(List.of(diagnostic));
        WorkspaceEdit workspace = new WorkspaceEdit();
        workspace.setChanges(Map.of(uri, List.of(edit)));
        action.setEdit(workspace);
        action.setData(detail);
        return action;
    }

    private static String code(Diagnostic diagnostic) {
        Either<String, Integer> code = diagnostic.getCode();
        if (code == null) {
            return null;
        }
        return code.isLeft() ? code.getLeft() : String.valueOf(code.getRight());
    }
}
