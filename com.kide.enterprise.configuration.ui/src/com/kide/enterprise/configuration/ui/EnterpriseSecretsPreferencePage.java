package com.kide.enterprise.configuration.ui;

import java.util.Arrays;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.kide.enterprise.configuration.ConfigurationException;
import com.kide.enterprise.configuration.SecureStorageSecretResolver;

/** Preference page for provisioning encrypted aliases used by secret://secure references. */
public final class EnterpriseSecretsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
    private Text aliasText;
    private Text secretText;

    public EnterpriseSecretsPreferencePage() {
        setDescription("Provision encrypted secret aliases for KIDE enterprise configuration. "
                + "Project and workspace files should contain only secret://secure/<alias> references.");
        noDefaultAndApplyButton();
    }

    @Override
    public void init(IWorkbench workbench) {
        // No workbench state is required. Equinox secure storage is user-scoped.
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(2, false));
        container.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        Label aliasLabel = new Label(container, SWT.NONE);
        aliasLabel.setText("Alias:");
        aliasText = new Text(container, SWT.BORDER);
        aliasText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        aliasText.setMessage("e.g. primary-provider");

        Label secretLabel = new Label(container, SWT.NONE);
        secretLabel.setText("Secret:");
        secretText = new Text(container, SWT.BORDER | SWT.PASSWORD);
        secretText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Composite actions = new Composite(container, SWT.NONE);
        actions.setLayout(new GridLayout(2, false));
        GridData actionsData = new GridData(SWT.RIGHT, SWT.CENTER, true, false);
        actionsData.horizontalSpan = 2;
        actions.setLayoutData(actionsData);

        Button save = new Button(actions, SWT.PUSH);
        save.setText("Save encrypted secret");
        save.addListener(SWT.Selection, event -> saveSecret());

        Button remove = new Button(actions, SWT.PUSH);
        remove.setText("Remove alias");
        remove.addListener(SWT.Selection, event -> removeSecret());

        Label guidance = new Label(container, SWT.WRAP);
        guidance.setText("Reference the alias as secret://secure/<alias>. "
                + "For centrally injected runtime credentials use secret://env/<VARIABLE>. "
                + "KIDE never writes the secret value to project or workspace configuration.");
        GridData guidanceData = new GridData(SWT.FILL, SWT.TOP, true, false);
        guidanceData.horizontalSpan = 2;
        guidanceData.widthHint = 520;
        guidance.setLayoutData(guidanceData);

        return container;
    }

    private void saveSecret() {
        setErrorMessage(null);
        String alias = aliasText.getText().trim();
        char[] secret = secretText.getText().toCharArray();
        try {
            new SecureStorageSecretResolver().put(alias, secret);
            secretText.setText("");
            setMessage("Encrypted secret alias saved: " + alias);
        } catch (ConfigurationException e) {
            setErrorMessage(e.getMessage());
        } finally {
            Arrays.fill(secret, '\0');
        }
    }

    private void removeSecret() {
        setErrorMessage(null);
        String alias = aliasText.getText().trim();
        try {
            new SecureStorageSecretResolver().remove(alias);
            secretText.setText("");
            setMessage("Secret alias removed: " + alias);
        } catch (ConfigurationException e) {
            setErrorMessage(e.getMessage());
        }
    }
}
