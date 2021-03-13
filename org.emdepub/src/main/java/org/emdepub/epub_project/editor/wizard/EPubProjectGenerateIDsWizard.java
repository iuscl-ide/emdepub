/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.epub_project.editor.wizard;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.emdepub.epub_project.editor.EPubProjectEditor;
import org.emdepub.epub_project.model.EPUB_project;

/** Generate manifest IDs wizard */
public class EPubProjectGenerateIDsWizard extends Wizard implements IExportWizard {

	private EPubProjectGenerateIDsWizardPage ePubProjectGenerateIDsWizardPage;
	private EPubProjectEditor ePubProjectEditor;
	private EPUB_project project_ePub;
	
	private Boolean manifestIDGuid;
	private Boolean manifestIDCounter;
	private String manifestIDPrefix;

	/** Method */
	public EPubProjectGenerateIDsWizard(EPubProjectEditor ePubProjectEditor) {
		super();

		this.ePubProjectEditor = ePubProjectEditor;
		project_ePub = ePubProjectEditor.getEPubProject();

		manifestIDGuid = project_ePub.manifestIDGuid;
		manifestIDCounter = project_ePub.manifestIDCounter;
		manifestIDPrefix = project_ePub.manifestIDPrefix;
		
		this.setWindowTitle("Generate Manifest IDs Wizard");
		this.setDefaultPageImageDescriptor(ImageDescriptor.createFromURL(this.getClass().getResource("/images/md-wizard-banner-exportashtml.png")));
	}

	/** Method */
	public void addPages() {
		
		ePubProjectGenerateIDsWizardPage = new EPubProjectGenerateIDsWizardPage(manifestIDGuid, manifestIDCounter, manifestIDPrefix);
		addPage(ePubProjectGenerateIDsWizardPage);
	}

	/** Method */
	@Override
	public boolean performFinish() {
		
		project_ePub.manifestIDGuid = ePubProjectGenerateIDsWizardPage.getManifestIDGuid();
		project_ePub.manifestIDCounter = ePubProjectGenerateIDsWizardPage.getManifestIDCounter();
		project_ePub.manifestIDPrefix = ePubProjectGenerateIDsWizardPage.getManifestIDPrefix();
		
		ePubProjectEditor.generateManifestIDs();
		ePubProjectEditor.serialize();
		
		return true; /* To close the wizard */
	}

	/** ? */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection structuredSelection) { }
}
