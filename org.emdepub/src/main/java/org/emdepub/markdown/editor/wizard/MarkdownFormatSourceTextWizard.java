/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.markdown.editor.wizard;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.emdepub.markdown.editor.MarkdownEditor;
import org.emdepub.markdown.editor.preferences.MarkdownPreferences;

/** Markdown format source text wizard */
public class MarkdownFormatSourceTextWizard extends Wizard implements IExportWizard {

	private MarkdownEditor markdownMultiPageEditor;
	private MarkdownPreferences markdownPreferences;

	/** Call */
	public MarkdownFormatSourceTextWizard(MarkdownEditor markdownEditor) {
		super();
		
		this.markdownMultiPageEditor = markdownEditor;
		this.markdownPreferences = markdownEditor.getPreferences();
		
		this.setWindowTitle("Markdown Format Options");
		this.setDefaultPageImageDescriptor(ImageDescriptor.createFromURL(this.getClass().getResource("/images/md-wizard-banner-exportashtml.png")));
	}

	/** The wizard page */
	public void addPages() {
		
		MarkdownFormatSourceTextWizardPage markdownFormatSourceTextWizardPage = new MarkdownFormatSourceTextWizardPage(markdownPreferences);
		addPage(markdownFormatSourceTextWizardPage);
	}

	/** Perform finish */
	@Override
	public boolean performFinish() {
		
		markdownMultiPageEditor.saveMarkdownPreferences();
		markdownMultiPageEditor.doSpecialFormatting(markdownPreferences);
		
		return true; /* To close the wizard */
	}

	/** ? */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection structuredSelection) { /* ? */ }
}
