/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.activator;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.emdepub"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	/** Plug-in root folder */
	private static String pluginFolderPathName;
	
	/**
	 * The constructor
	 */
	public Activator() {
		/* Resources */
		R.loadResourceRegistry();
		
		/* Plug-in root folder */
		URL pluginRootURL = FileLocator.find(Platform.getBundle(PLUGIN_ID), new Path("/"), null);
		try {
			pluginFolderPathName = (new File(FileLocator.resolve(pluginRootURL).toURI())).getCanonicalFile().getCanonicalPath();
		}
		catch (IOException ioException) {
			L.e("IOException in Activator", ioException);
		}
		catch (URISyntaxException uriSyntaxException) {
			L.e("URISyntaxException in Activator", uriSyntaxException);
		}

		/* Log */
		File logFile = new File(pluginFolderPathName, "log/emdepub.log");
		L.initLog(logFile);
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
	
	/** Returns an image descriptor for the image file at the given plug-in relative path */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/** Returns plug-in root folder */
	public static String getPluginFolder() {
		return pluginFolderPathName;
	}
}
