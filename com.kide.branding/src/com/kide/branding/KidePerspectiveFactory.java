package com.kide.branding;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * The KIDE Modelling perspective: models on the left, editors in the middle,
 * outline and guided tours on the right, problems and properties below.
 *
 * Sirius and console views are added as placeholders rather than hard view
 * references, so the perspective still opens cleanly in an Eclipse where those
 * components are not installed.
 */
public class KidePerspectiveFactory implements IPerspectiveFactory {

	public static final String ID = "com.kide.branding.perspective"; //$NON-NLS-1$

	private static final String NEW_PROJECT_WIZARD = "com.kide.branding.wizards.newProject"; //$NON-NLS-1$
	private static final String PROJECT_EXPLORER = "org.eclipse.ui.navigator.ProjectExplorer"; //$NON-NLS-1$
	private static final String SIRIUS_MODEL_EXPLORER = "org.eclipse.sirius.ui.tools.views.model.explorer"; //$NON-NLS-1$
	private static final String SIRIUS_VALIDATION = "org.eclipse.sirius.ui.tools.views.validation"; //$NON-NLS-1$
	private static final String CHEAT_SHEET_VIEW = "org.eclipse.ui.cheatsheets.views.CheatSheetView"; //$NON-NLS-1$
	private static final String ERROR_LOG = "org.eclipse.pde.runtime.LogView"; //$NON-NLS-1$
	private static final String CONSOLE = "org.eclipse.ui.console.ConsoleView"; //$NON-NLS-1$

	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);

		IFolderLayout left = layout.createFolder("kide.left", IPageLayout.LEFT, 0.22f, editorArea); //$NON-NLS-1$
		left.addView(PROJECT_EXPLORER);
		left.addPlaceholder(SIRIUS_MODEL_EXPLORER);

		IFolderLayout right = layout.createFolder("kide.right", IPageLayout.RIGHT, 0.76f, editorArea); //$NON-NLS-1$
		right.addView(IPageLayout.ID_OUTLINE);
		right.addPlaceholder(CHEAT_SHEET_VIEW);

		IFolderLayout bottom = layout.createFolder("kide.bottom", IPageLayout.BOTTOM, 0.70f, editorArea); //$NON-NLS-1$
		bottom.addView(IPageLayout.ID_PROBLEM_VIEW);
		bottom.addView(IPageLayout.ID_PROP_SHEET);
		bottom.addPlaceholder(SIRIUS_VALIDATION);
		bottom.addPlaceholder(CONSOLE);
		bottom.addPlaceholder(ERROR_LOG);
		bottom.addPlaceholder(IPageLayout.ID_TASK_LIST);

		layout.addNewWizardShortcut(NEW_PROJECT_WIZARD);
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder"); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.file"); //$NON-NLS-1$

		layout.addShowViewShortcut(PROJECT_EXPLORER);
		layout.addShowViewShortcut(SIRIUS_MODEL_EXPLORER);
		layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
		layout.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
		layout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);
		layout.addShowViewShortcut(CHEAT_SHEET_VIEW);
		layout.addShowViewShortcut(CONSOLE);
		layout.addShowViewShortcut(ERROR_LOG);

		layout.addPerspectiveShortcut("org.eclipse.jdt.ui.JavaPerspective"); //$NON-NLS-1$
		layout.addPerspectiveShortcut("org.eclipse.ui.resourcePerspective"); //$NON-NLS-1$
	}
}
