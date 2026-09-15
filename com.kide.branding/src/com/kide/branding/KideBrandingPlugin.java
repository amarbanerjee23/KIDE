package com.kide.branding;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * Activator for the KIDE branding bundle. It holds no state of its own; it
 * exists so that the product has an owning bundle for logging and for the
 * images and stylesheets referenced from plugin.xml.
 */
public class KideBrandingPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "com.kide.branding"; //$NON-NLS-1$

	private static KideBrandingPlugin instance;

	public static KideBrandingPlugin getDefault() {
		return instance;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		instance = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		instance = null;
		super.stop(context);
	}

	/**
	 * Records a problem in the workbench log. Used where failing silently would
	 * leave a user staring at a dialog that simply did not appear.
	 */
	public static void logError(String message, Throwable cause) {
		KideBrandingPlugin plugin = getDefault();
		IStatus status = new Status(IStatus.ERROR, PLUGIN_ID, message, cause);
		if (plugin != null) {
			plugin.getLog().log(status);
		} else {
			System.err.println(message);
			if (cause != null) {
				cause.printStackTrace();
			}
		}
	}
}
