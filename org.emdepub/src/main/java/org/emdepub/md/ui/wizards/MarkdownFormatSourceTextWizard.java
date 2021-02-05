/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.md.ui.wizards;

import java.util.LinkedHashMap;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.emdepub.ui.editor.md.MarkdownEditor;
import org.emdepub.ui.editor.md.MarkdownTextEditor;
import org.emdepub.ui.editor.md.engine.MarkdownEditorEngine;
import org.emdepub.ui.editor.md.engine.MarkdownEditorEngine.SpecialFormattingOptions;

public class MarkdownFormatSourceTextWizard extends Wizard implements IExportWizard {

	private final LinkedHashMap<SpecialFormattingOptions, Boolean> formattingOptions = MarkdownEditorEngine.createSpecialFormattingOptions();
	
	private MarkdownEditor markdownEditor;

	/** Call */
	public MarkdownFormatSourceTextWizard(MarkdownEditor markdownEditor) {
		super();
		
		this.markdownEditor = markdownEditor;
		
		this.setWindowTitle("Special formatting options");
		this.setDefaultPageImageDescriptor(ImageDescriptor.createFromURL(this.getClass().getResource("/images/md-wizard-banner-exportashtml.png")));
	}

	/** The wizard page */
	public void addPages() {
		
		MarkdownFormatSourceTextWizardPage markdownFormatSourceTextWizardPage = new MarkdownFormatSourceTextWizardPage(formattingOptions);
		addPage(markdownFormatSourceTextWizardPage);
	}

	/** Perform finish */
	@Override
	public boolean performFinish() {
		
		markdownEditor.doSpecialFormatting(formattingOptions);
		
		return true; /* To close the wizard */
	}

	/** ? */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection structuredSelection) { /* ? */ }
}
