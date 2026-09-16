package com.kide.branding.wizards;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.ide.IDE;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.kide.branding.KideBrandingPlugin;

/**
 * Creates a KIDE modelling project that is useful the moment it appears: a data
 * model, a component interface, a capability and an activity flow that already
 * validate, so the first thing a new user sees is a working model rather than
 * an empty folder.
 */
public class NewKideProjectWizard extends Wizard implements INewWizard {

	private static final String XTEXT_NATURE = "org.eclipse.xtext.ui.shared.xtextNature"; //$NON-NLS-1$
	private static final String MODELS_FOLDER = "models"; //$NON-NLS-1$
	private static final String DIAGRAMS_FOLDER = "diagrams"; //$NON-NLS-1$
	private static final String FIRST_FILE = MODELS_FOLDER + "/Loading.cap"; //$NON-NLS-1$

	/** Template in the bundle -> destination inside the new project. */
	private static final Map<String, String> TEMPLATES = new LinkedHashMap<>();
	static {
		TEMPLATES.put("templates/Ecre.dml", MODELS_FOLDER + "/Ecre.dml"); //$NON-NLS-1$ //$NON-NLS-2$
		TEMPLATES.put("templates/Ecre.mncspec", MODELS_FOLDER + "/Ecre.mncspec"); //$NON-NLS-1$ //$NON-NLS-2$
		TEMPLATES.put("templates/Loading.cap", MODELS_FOLDER + "/Loading.cap"); //$NON-NLS-1$ //$NON-NLS-2$
		TEMPLATES.put("templates/MissionPlanning.activity", MODELS_FOLDER + "/MissionPlanning.activity"); //$NON-NLS-1$ //$NON-NLS-2$
		TEMPLATES.put("templates/README.md", "README.md"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private WizardNewProjectCreationPage mainPage;
	private IProject createdProject;

	public NewKideProjectWizard() {
		setWindowTitle("New KIDE Modelling Project"); //$NON-NLS-1$
		setNeedsProgressMonitor(true);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// Nothing to take from the selection: the wizard always starts fresh.
	}

	@Override
	public void addPages() {
		mainPage = new WizardNewProjectCreationPage("kideProjectPage"); //$NON-NLS-1$
		mainPage.setTitle("KIDE Modelling Project"); //$NON-NLS-1$
		mainPage.setDescription(
				"The project starts with a data model, a component interface, a capability and an activity flow you can edit straight away."); //$NON-NLS-1$
		addPage(mainPage);
	}

	public IProject getCreatedProject() {
		return createdProject;
	}

	@Override
	public boolean performFinish() {
		final IProject project = mainPage.getProjectHandle();
		final java.net.URI location = mainPage.useDefaults() ? null : mainPage.getLocationURI();

		try {
			getContainer().run(true, true, new IRunnableWithProgress() {
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException {
					try {
						createProject(project, location, monitor);
					} catch (CoreException | IOException e) {
						throw new InvocationTargetException(e);
					}
				}
			});
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause() == null ? e : e.getCause();
			KideBrandingPlugin.logError("Could not create the KIDE project", cause); //$NON-NLS-1$
			MessageDialog.openError(getShell(), "KIDE", //$NON-NLS-1$
					"The project could not be created: " + cause.getLocalizedMessage()); //$NON-NLS-1$
			return false;
		}

		createdProject = project;
		openFirstModel(project);
		return true;
	}

	private void createProject(IProject project, java.net.URI location, IProgressMonitor monitor)
			throws CoreException, IOException {
		SubMonitor progress = SubMonitor.convert(monitor, "Creating the KIDE project", 10); //$NON-NLS-1$

		IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(project.getName());
		if (location != null) {
			description.setLocationURI(location);
		}
		description.setNatureIds(new String[] { XTEXT_NATURE });

		project.create(description, progress.split(2));
		project.open(progress.split(2));

		createFolder(project.getFolder(MODELS_FOLDER), progress.split(1));
		createFolder(project.getFolder(DIAGRAMS_FOLDER), progress.split(1));

		SubMonitor copying = progress.split(4).setWorkRemaining(TEMPLATES.size());
		for (Map.Entry<String, String> template : TEMPLATES.entrySet()) {
			copyTemplate(project, template.getKey(), template.getValue(), copying.split(1));
		}
	}

	private void createFolder(IFolder folder, IProgressMonitor monitor) throws CoreException {
		if (!folder.exists()) {
			folder.create(true, true, monitor);
		}
	}

	private void copyTemplate(IProject project, String bundlePath, String target, IProgressMonitor monitor)
			throws CoreException, IOException {
		Bundle bundle = FrameworkUtil.getBundle(NewKideProjectWizard.class);
		URL entry = bundle == null ? null : FileLocator.find(bundle, new Path(bundlePath), null);
		if (entry == null) {
			KideBrandingPlugin.logError("Missing project template: " + bundlePath, null); //$NON-NLS-1$
			return;
		}

		IFile file = project.getFile(new Path(target));
		if (file.exists()) {
			return;
		}
		try (InputStream in = entry.openStream()) {
			file.create(in, true, monitor);
		}
	}

	private void openFirstModel(IProject project) {
		IFile file = project.getFile(new Path(FIRST_FILE));
		if (!file.exists()) {
			return;
		}
		try {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			if (page != null) {
				IDE.openEditor(page, file, true);
			}
		} catch (CoreException | RuntimeException e) {
			KideBrandingPlugin.logError("Could not open " + FIRST_FILE, e); //$NON-NLS-1$
		}
	}
}
