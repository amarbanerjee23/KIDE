package com.kide.enterprise.context.ui;

import java.nio.file.Path;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.kide.enterprise.context.ContextDiagnostic;
import com.kide.enterprise.context.ContextStatus;
import com.kide.enterprise.context.EnterpriseContext;
import com.kide.enterprise.context.EnterpriseContextResult;
import com.kide.enterprise.context.EnterpriseContextStore;

/** Preference page for safe provisioning and inspection of E04 stable enterprise identities. */
public final class EnterpriseContextPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
    private final EnterpriseContextStore store = new EnterpriseContextStore();

    private Combo projectCombo;
    private Text organizationName;
    private Text portfolioName;
    private Text projectName;
    private Text workspaceName;
    private Label organizationId;
    private Label portfolioId;
    private Label projectId;
    private Label workspaceId;
    private Label status;
    private IProject[] projects = new IProject[0];

    public EnterpriseContextPreferencePage() {
        setDescription("Manage stable organization, portfolio, project and workspace identities. "
                + "Security and audit features use these IDs, never display names or filesystem paths.");
        noDefaultAndApplyButton();
    }

    @Override
    public void init(IWorkbench workbench) {
        // No eager workbench state is retained. All Eclipse resources are queried lazily on page creation.
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(3, false));
        container.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        new Label(container, SWT.NONE).setText("Eclipse project:");
        projectCombo = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
        GridData comboData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        comboData.horizontalSpan = 2;
        projectCombo.setLayoutData(comboData);
        projectCombo.addListener(SWT.Selection, event -> guardedRefresh());

        organizationName = createNameRow(container, "Organization:");
        organizationId = createIdRow(container, "Organization ID:");
        portfolioName = createNameRow(container, "Portfolio:");
        portfolioId = createIdRow(container, "Portfolio ID:");
        projectName = createNameRow(container, "Project:");
        projectId = createIdRow(container, "Project ID:");
        workspaceName = createNameRow(container, "Workspace:");
        workspaceId = createIdRow(container, "Workspace ID:");

        Composite actions = new Composite(container, SWT.NONE);
        actions.setLayout(new GridLayout(2, false));
        GridData actionsData = new GridData(SWT.RIGHT, SWT.CENTER, true, false);
        actionsData.horizontalSpan = 3;
        actions.setLayoutData(actionsData);

        Button reload = new Button(actions, SWT.PUSH);
        reload.setText("Reload");
        reload.addListener(SWT.Selection, event -> guardedRefresh());

        Button save = new Button(actions, SWT.PUSH);
        save.setText("Initialize / update context");
        save.addListener(SWT.Selection, event -> guardedSave());

        status = new Label(container, SWT.WRAP);
        GridData statusData = new GridData(SWT.FILL, SWT.TOP, true, false);
        statusData.horizontalSpan = 3;
        statusData.widthHint = 620;
        status.setLayoutData(statusData);

        Label guidance = new Label(container, SWT.WRAP);
        guidance.setText("IDs are generated once and preserved when names or paths change. "
                + "If an identity descriptor is malformed, KIDE refuses silent regeneration and reports a diagnostic instead. "
                + "This prevents accidental identity drift in future RBAC, audit and policy controls.");
        GridData guidanceData = new GridData(SWT.FILL, SWT.TOP, true, false);
        guidanceData.horizontalSpan = 3;
        guidanceData.widthHint = 620;
        guidance.setLayoutData(guidanceData);

        loadProjectsSafely();
        return container;
    }

    private Text createNameRow(Composite parent, String label) {
        new Label(parent, SWT.NONE).setText(label);
        Text text = new Text(parent, SWT.BORDER);
        GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
        data.horizontalSpan = 2;
        text.setLayoutData(data);
        return text;
    }

    private Label createIdRow(Composite parent, String label) {
        new Label(parent, SWT.NONE).setText(label);
        Label value = new Label(parent, SWT.NONE);
        GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
        data.horizontalSpan = 2;
        value.setLayoutData(data);
        value.setText("—");
        return value;
    }

    private void loadProjectsSafely() {
        setErrorMessage(null);
        try {
            projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
            projectCombo.removeAll();
            for (IProject project : projects) {
                projectCombo.add(project.getName());
            }
            if (projects.length == 0) {
                clearFields();
                status.setText("No Eclipse projects are available. Create or import a project before provisioning enterprise context.");
                return;
            }
            projectCombo.select(0);
            refreshSelectedProject();
        } catch (RuntimeException e) {
            projects = new IProject[0];
            clearFields();
            setErrorMessage("KIDE could not enumerate Eclipse projects safely. The workbench remains usable.");
            status.setText("Enterprise context is unavailable until the Eclipse workspace resource state can be read.");
        }
    }

    private void guardedRefresh() {
        try {
            setErrorMessage(null);
            refreshSelectedProject();
        } catch (RuntimeException e) {
            setErrorMessage("KIDE could not refresh enterprise context safely. No identity data was changed.");
        }
    }

    private void guardedSave() {
        try {
            setErrorMessage(null);
            saveSelectedProject();
        } catch (RuntimeException e) {
            setErrorMessage("KIDE could not save enterprise context safely. Existing identity files were not intentionally replaced.");
        }
    }

    private void refreshSelectedProject() {
        IProject selected = selectedProject();
        Path workspace = workspacePath();
        Path project = projectPath(selected);
        if (selected == null || workspace == null || project == null) {
            clearFields();
            setErrorMessage("The selected project or workspace does not have a local filesystem location.");
            status.setText("Enterprise identity provisioning requires local Eclipse project and workspace locations.");
            return;
        }

        EnterpriseContextResult result = store.load(workspace, project);
        if (result.isReady()) {
            showContext(result.context().get());
            status.setText("Enterprise context is ready. Stable IDs will be retained when display names are updated.");
            setMessage(null);
            return;
        }

        clearIds();
        if (result.status() == ContextStatus.UNINITIALIZED) {
            if (projectName.getText().trim().isEmpty()) {
                projectName.setText(selected.getName());
            }
            if (workspaceName.getText().trim().isEmpty()) {
                workspaceName.setText(defaultWorkspaceName(workspace));
            }
            status.setText(result.summary());
            setMessage("Enterprise context is not initialized; no runtime error has occurred.");
        } else {
            status.setText(result.summary());
            showDiagnostic(result);
        }
    }

    private void saveSelectedProject() {
        IProject selected = selectedProject();
        Path workspace = workspacePath();
        Path project = projectPath(selected);
        if (selected == null || workspace == null || project == null) {
            setErrorMessage("A local Eclipse project and workspace are required.");
            return;
        }
        EnterpriseContextResult result = store.provision(workspace, project,
                organizationName.getText(), portfolioName.getText(), projectName.getText(), workspaceName.getText());
        if (result.isReady()) {
            showContext(result.context().get());
            status.setText("Enterprise context saved successfully with stable IDs.");
            setMessage("Enterprise context saved.");
        } else {
            status.setText(result.summary());
            showDiagnostic(result);
        }
    }

    private void showContext(EnterpriseContext context) {
        organizationName.setText(context.organization().displayName());
        portfolioName.setText(context.portfolio().displayName());
        projectName.setText(context.project().displayName());
        workspaceName.setText(context.workspace().displayName());
        organizationId.setText(context.organization().id().value());
        portfolioId.setText(context.portfolio().id().value());
        projectId.setText(context.project().id().value());
        workspaceId.setText(context.workspace().id().value());
    }

    private void showDiagnostic(EnterpriseContextResult result) {
        String message = result.summary();
        if (!result.diagnostics().isEmpty()) {
            ContextDiagnostic diagnostic = result.diagnostics().get(0);
            message = diagnostic.code() + ": " + diagnostic.message();
        }
        setErrorMessage(message);
    }

    private IProject selectedProject() {
        int index = projectCombo == null ? -1 : projectCombo.getSelectionIndex();
        return index >= 0 && index < projects.length ? projects[index] : null;
    }

    private static Path projectPath(IProject project) {
        try {
            if (project == null) {
                return null;
            }
            IPath location = project.getLocation();
            return location == null ? null : location.toFile().toPath().toAbsolutePath().normalize();
        } catch (RuntimeException e) {
            return null;
        }
    }

    private static Path workspacePath() {
        try {
            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
            IPath location = root.getLocation();
            return location == null ? null : location.toFile().toPath().toAbsolutePath().normalize();
        } catch (RuntimeException e) {
            return null;
        }
    }

    private static String defaultWorkspaceName(Path workspace) {
        try {
            Path fileName = workspace.getFileName();
            return fileName == null ? "KIDE Workspace" : fileName.toString();
        } catch (RuntimeException e) {
            return "KIDE Workspace";
        }
    }

    private void clearFields() {
        if (organizationName != null) {
            organizationName.setText("");
            portfolioName.setText("");
            projectName.setText("");
            workspaceName.setText("");
        }
        clearIds();
    }

    private void clearIds() {
        if (organizationId != null) {
            organizationId.setText("—");
            portfolioId.setText("—");
            projectId.setText("—");
            workspaceId.setText("—");
        }
    }
}
