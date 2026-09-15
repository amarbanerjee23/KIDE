package com.kide.branding.intro;

import java.util.Properties;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.intro.IIntroPart;
import org.eclipse.ui.intro.IIntroSite;
import org.eclipse.ui.intro.config.IIntroAction;

import com.kide.branding.KideBrandingPlugin;
import com.kide.branding.wizards.NewKideProjectWizard;

/**
 * Opens the project wizard from the Welcome page, and closes Welcome once a
 * project exists so the user lands in the editor rather than back on an
 * introduction they have finished with.
 */
public class NewProjectIntroAction implements IIntroAction {

	@Override
	public void run(IIntroSite site, Properties params) {
		NewKideProjectWizard wizard = new NewKideProjectWizard();
		wizard.init(PlatformUI.getWorkbench(), StructuredSelection.EMPTY);

		WizardDialog dialog = new WizardDialog(site.getShell(), wizard);
		dialog.create();

		if (dialog.open() == Window.OK && wizard.getCreatedProject() != null) {
			closeIntro();
		}
	}

	private void closeIntro() {
		try {
			IIntroPart intro = PlatformUI.getWorkbench().getIntroManager().getIntro();
			if (intro != null) {
				PlatformUI.getWorkbench().getIntroManager().closeIntro(intro);
			}
		} catch (RuntimeException e) {
			KideBrandingPlugin.logError("Could not close the Welcome page", e); //$NON-NLS-1$
		}
	}
}
