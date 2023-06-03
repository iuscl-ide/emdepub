package org.emdepub.activator;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.emdepub.common.resources.CR;
import org.emdepub.common.ui.UI;

import lombok.Getter;

public class App {

	@Getter
	private static UI ui;
	
	public static void initialize() {
		
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
		Shell applicationShell = workbenchWindow.getShell();

		ui = new UI(false, applicationShell.getDisplay());
		ui.computeSizes(applicationShell);
		
		/* Resources */
		CR.load(ui, applicationShell);
	}
	
}
