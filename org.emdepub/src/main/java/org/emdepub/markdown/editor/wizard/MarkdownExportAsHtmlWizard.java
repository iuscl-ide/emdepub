/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.markdown.editor.wizard;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.emdepub.common.utils.CU;
import org.emdepub.markdown.editor.MarkdownEditor;
import org.emdepub.markdown.editor.preferences.MarkdownPreferences;
import org.emdepub.markdown.editor.preferences.MarkdownPreferences.PreferenceNames;

/** Markdown export as HTML wizard */
public class MarkdownExportAsHtmlWizard extends Wizard implements IExportWizard {

	public static enum MarkdownExportType { ExportAssetsFolder, ExportFileOnlyWithCssReference, ExportFileOnly };
	
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

		MarkdownPreferences preferences = markdownEditor.getPreferences();

		String exportName = preferences.get(PreferenceNames.ExportName);
		if (CU.isEmpty(exportName)) {
			exportName = CU.findFileNameWithoutExtension(markdownEditor.getSourceMarkdownFilePathAndName());
		}

		String exportHtmlTitle = preferences.get(PreferenceNames.ExportHtmlTitle);
		if (CU.isEmpty(exportHtmlTitle)) {
			if (exportName.length() > 1) {
				exportHtmlTitle = exportName.substring(0, 1).toUpperCase() + exportName.substring(1);				
			}
			else {
				exportHtmlTitle = exportName.toUpperCase();				
			}
		}

		markdownExportAsHtmlWizardPage = new MarkdownExportAsHtmlWizardPage(preferences.get(PreferenceNames.ExportType),
			preferences.get(PreferenceNames.ExportCssReference), exportHtmlTitle, exportName, preferences.get(PreferenceNames.ExportLocation));
		addPage(markdownExportAsHtmlWizardPage);
	}

	/** Method */
	@Override
	public boolean performFinish() {

		MarkdownExportType markdownExportType = markdownExportAsHtmlWizardPage.getMarkdownExportType();
		String exportCssReference = markdownExportAsHtmlWizardPage.getExportCssReference();
		String exportHtmlTitle = markdownExportAsHtmlWizardPage.getExportHtmlTitle();
		String exportName = markdownExportAsHtmlWizardPage.getExportFileOrFolderName();
		String exportLocation = markdownExportAsHtmlWizardPage.getExportLocation();

		MarkdownPreferences preferences = markdownEditor.getPreferences();
		preferences.set(PreferenceNames.ExportType, markdownExportType);
		preferences.set(PreferenceNames.ExportCssReference, exportCssReference);
		preferences.set(PreferenceNames.ExportHtmlTitle, exportHtmlTitle);		
		preferences.set(PreferenceNames.ExportName, exportName);
		preferences.set(PreferenceNames.ExportLocation, exportLocation);
		markdownEditor.saveMarkdownPreferences();
		
		markdownEditor.exportAsHtml(markdownExportType, exportCssReference, exportHtmlTitle, exportName, exportLocation);
		
		return true; /* To close the wizard */
	}

	/** ? */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection structuredSelection) { /* ? */ }
}
