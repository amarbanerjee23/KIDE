package com.kide.branding.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Root of the KIDE preference tree. It carries no settings itself: it tells the
 * reader which sub-page holds what, so that nobody has to guess whether a
 * setting lives under KIDE, under Xtext or under Sirius.
 */
public class KidePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	@Override
	public void init(IWorkbench workbench) {
		noDefaultAndApplyButton();
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite area = new Composite(parent, SWT.NONE);
		area.setLayout(new GridLayout(1, false));
		area.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		addParagraph(area, "KIDE settings are grouped by what you are working on.");
		addEntry(area, "Capability language",
				"Editor behaviour, validation and templates for .cap files. See Capability under this page.");
		addEntry(area, "Activity language",
				"Editor behaviour, validation and templates for .activity files. See Activity Diagram under this page.");
		addEntry(area, "Sirius diagrams",
				"Diagram refresh, printing and appearance live under the Sirius page.");
		addEntry(area, "Text editors",
				"Line numbers, tabs and the print margin apply to every KIDE editor and live under General > Editors > Text Editors.");

		return area;
	}

	private void addParagraph(Composite parent, String text) {
		Label label = new Label(parent, SWT.WRAP);
		label.setText(text);
		GridData data = new GridData(SWT.FILL, SWT.TOP, true, false);
		data.widthHint = 420;
		label.setLayoutData(data);
	}

	private void addEntry(Composite parent, String title, String description) {
		Label heading = new Label(parent, SWT.NONE);
		heading.setText("\n" + title); //$NON-NLS-1$
		addParagraph(parent, description);
	}
}
