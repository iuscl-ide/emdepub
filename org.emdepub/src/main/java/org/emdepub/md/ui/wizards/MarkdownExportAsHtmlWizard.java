/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.md.ui.wizards;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.emdepub.activator.F;
import org.emdepub.ui.editor.md.MarkdownEditor;

/** Markdown export as HTML wizard */
public class MarkdownExportAsHtmlWizard extends Wizard implements IExportWizard {

	public static enum MarkdownExportType { ExportAssetsFolder, ExportFileOnly };
	
	private MarkdownExportAsHtmlWizardPage markdownExportAsHtmlWizardPage;
	private MarkdownEditor markdownEditor;

	/** Method */
	public MarkdownExportAsHtmlWizard(MarkdownEditor markdownEditor) {
		super();

		this.markdownEditor = markdownEditor;
		
		this.setWindowTitle("Export Markdown as HTML Wizard");
		this.setDefaultPageImageDescriptor(ImageDescriptor.createFromURL(this.getClass().getResource("/images/md-wizard-banner-exportashtml.png")));
	}

	/** Method */
	public void addPages() {
		
		String exportName = markdownEditor.getSourceMarkdownFilePathAndName();
		exportName = exportName.substring(F.getFileFolderName(exportName).length() + 1);
		exportName = exportName.substring(0, exportName.length() - (F.getExtension(exportName).length() + 1));
		
		markdownExportAsHtmlWizardPage = new MarkdownExportAsHtmlWizardPage(MarkdownExportType.ExportAssetsFolder,
				exportName, "C:\\Iustin\\Programming\\_emdepub\\tools\\exports");
		addPage(markdownExportAsHtmlWizardPage);
	}

	/** Method */
	@Override
	public boolean performFinish() {
		
		markdownEditor.exportAsHtml(markdownExportAsHtmlWizardPage.getMarkdownExportType(),
				markdownExportAsHtmlWizardPage.getExportName(), markdownExportAsHtmlWizardPage.getExportLocation());
		
		return true; /* To close the wizard */
	}

	/** ? */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection structuredSelection) { /* ? */ }
}
