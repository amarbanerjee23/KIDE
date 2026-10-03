package com.smr.activity.dsl.ide.quickfix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionKind;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.WorkspaceEdit;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.xtext.ide.server.codeActions.ICodeActionService2;

import com.smr.activity.dsl.validation.ActivityDiagramValidator;

/** LSP adapter for the active Activity Eclipse quick fixes. */
public final class ActivityDiagramCodeActionService implements ICodeActionService2 {

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
            QuickFix fix = fix(code(diagnostic));
            if (fix == null || diagnostic.getRange() == null) {
                continue;
            }
            result.add(Either.forRight(removeAction(
                    fix.title,
                    fix.detail,
                    options.getURI(),
                    diagnostic)));
        }
        return result;
    }

    private static QuickFix fix(String code) {
        if (ActivityDiagramValidator.INVALID_OUTCOME.equals(code)) {
            return new QuickFix(
                    "Remove this outcome",
                    "The required capability never produces this outcome, so this branch can never be taken.");
        }
        if (ActivityDiagramValidator.INVALID_RESULT.equals(code)) {
            return new QuickFix(
                    "Remove this result",
                    "The activity diagram does not declare this result, so nothing downstream can use it.");
        }
        if (ActivityDiagramValidator.INVALID_CONTROL_CAPABILITY.equals(code)) {
            return new QuickFix(
                    "Remove this control item",
                    "The required capability does not offer this command, event, alarm or data point.");
        }
        if (ActivityDiagramValidator.INPUT_PARAMETER_NOT_FOUND.equals(code)) {
            return new QuickFix(
                    "Remove this input parameter",
                    "Nothing upstream supplies this input, so the activity cannot read it.");
        }
        return null;
    }

    private static CodeAction removeAction(
            String title,
            String detail,
            String uri,
            Diagnostic diagnostic) {
        CodeAction action = new CodeAction(title);
        action.setKind(CodeActionKind.QuickFix);
        action.setDiagnostics(List.of(diagnostic));
        WorkspaceEdit workspace = new WorkspaceEdit();
        workspace.setChanges(Map.of(
                uri,
                List.of(new TextEdit(diagnostic.getRange(), ""))));
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

    private record QuickFix(String title, String detail) {
    }
}
