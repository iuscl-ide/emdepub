package org.emdepub.site_project.wizard;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.emdepub.activator.Activator;
import org.emdepub.common.utils.CU;

import lombok.SneakyThrows;

public class SiteNewWizard extends BasicNewProjectResourceWizard {

	@Override
	@SneakyThrows(CoreException.class)
	public boolean performFinish() {

		boolean result = super.performFinish();

		IProject project = getNewProject();
		
		String projectFolder = project.getLocation().toOSString();
		
		String pluginFolder = Activator.getPluginFolder() + CU.S + "site-project-samples" + CU.S + "basic";
		
		
		String _project = CU.loadFileInString(pluginFolder + CU.S + ".project");
		_project.replace("{{projectName}}", project.getName());
		CU.saveStringToFile(_project, projectFolder + CU.S + ".project");
		
		selectAndReveal(project);
		project.refreshLocal(IResource.DEPTH_INFINITE, null);
		
		return result;
	}
	
	
}