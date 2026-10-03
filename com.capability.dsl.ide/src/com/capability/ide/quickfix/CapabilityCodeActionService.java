package com.capability.ide.quickfix;

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

import com.capability.validation.CapabilityValidator;

/** LSP adapter for the active Capability Eclipse quick fixes. */
public final class CapabilityCodeActionService implements ICodeActionService2 {

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
        if (CapabilityValidator.INVALID_CONTROL_CAPABILITIES_COMAND.equals(code)) {
            return new QuickFix(
                    "Remove this command",
                    "The component interface does not offer this command, so remove it from the capability.");
        }
        if (CapabilityValidator.INVALID_CONTROL_CAPABILITIES_EVENT.equals(code)) {
            return new QuickFix(
                    "Remove this event",
                    "The component interface does not publish this event, so remove it from the capability.");
        }
        if (CapabilityValidator.INVALID_CONTROL_CAPABILITIES_ALARM.equals(code)) {
            return new QuickFix(
                    "Remove this alarm",
                    "The component interface does not raise this alarm, so remove it from the capability.");
        }
        if (CapabilityValidator.INVALID_CONTROL_CAPABILITIES_DATAPOINT.equals(code)) {
            return new QuickFix(
                    "Remove this data point",
                    "The component interface does not expose this data point, so remove it from the capability.");
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
