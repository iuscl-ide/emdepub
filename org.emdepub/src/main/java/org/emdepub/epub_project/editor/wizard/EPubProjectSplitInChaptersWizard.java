/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.epub_project.editor.wizard;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.emdepub.epub_project.editor.EPubProjectEditor;
import org.emdepub.epub_project.model.EPUB_project;

/** Markdown export as HTML wizard */
public class EPubProjectSplitInChaptersWizard extends Wizard implements IExportWizard {

	private EPubProjectSplitInChaptersWizardPage ePubProjectSplitInChaptersWizardPage;
	private EPubProjectEditor ePubProjectEditor;
	private EPUB_project project_ePub;
	
	public String splitStartExpression;
	public String splitSourceFileNameWithFullPath;
	public String splitTargetFileNamesWithFullPath;
	
	/** Method */
	public EPubProjectSplitInChaptersWizard(EPubProjectEditor ePubProjectEditor) {
		super();

		this.ePubProjectEditor = ePubProjectEditor;
		project_ePub = ePubProjectEditor.getEPubProject();

		splitStartExpression = project_ePub.splitStartExpression;
		splitSourceFileNameWithFullPath = project_ePub.splitSourceFileNameWithFullPath;
		splitTargetFileNamesWithFullPath = project_ePub.splitTargetFileNamesWithFullPath;
		
		this.setWindowTitle("Split a File in Separate Files Wizard");
		this.setDefaultPageImageDescriptor(ImageDescriptor.createFromURL(this.getClass().getResource("/images/md-wizard-banner-exportashtml.png")));
	}

	/** Method */
	public void addPages() {

		ePubProjectSplitInChaptersWizardPage = new EPubProjectSplitInChaptersWizardPage(splitStartExpression,
			splitSourceFileNameWithFullPath, splitTargetFileNamesWithFullPath);
		addPage(ePubProjectSplitInChaptersWizardPage);
	}

	/** Method */
	@Override
	public boolean performFinish() {

		project_ePub.splitStartExpression = ePubProjectSplitInChaptersWizardPage.getSplitStartExpression();
		project_ePub.splitSourceFileNameWithFullPath = ePubProjectSplitInChaptersWizardPage.getSplitSourceFileNameWithFullPath();
		project_ePub.splitTargetFileNamesWithFullPath = ePubProjectSplitInChaptersWizardPage.getSplitTargetFileNamesWithFullPath();
		
		ePubProjectEditor.splitFileInFiles();
		ePubProjectEditor.serialize();
		
		return true; /* To close the wizard */
	}

	/** ? */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection structuredSelection) { /* ? */ }
}
