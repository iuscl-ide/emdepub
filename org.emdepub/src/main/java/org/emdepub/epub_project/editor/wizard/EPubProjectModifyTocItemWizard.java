/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.epub_project.editor.wizard;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.emdepub.epub_project.editor.EPubProjectEditor;
import org.emdepub.epub_project.model.EPUB_project_toc_item;

/** Generate manifest IDs wizard */
public class EPubProjectModifyTocItemWizard extends Wizard implements IExportWizard {

	private EPubProjectModifyTocItemWizardPage ePubProjectModifyTocItemWizardPage;

	private EPubProjectEditor ePubProjectEditor;
	private GridItem gridItem;
	
	private String itemText;
	private String itemSrc;

	
	/** Method */
	public EPubProjectModifyTocItemWizard(EPubProjectEditor ePubProjectEditor, GridItem gridItem) {
		super();

		this.ePubProjectEditor = ePubProjectEditor;
		this.gridItem = gridItem;
		
		EPUB_project_toc_item epub_project_toc_item = (EPUB_project_toc_item) gridItem.getData("toc_item");
		
		itemText = epub_project_toc_item.itemText;
		itemSrc = epub_project_toc_item.itemSrc;

		this.setWindowTitle("Modify Toc Item Wizard");
		this.setDefaultPageImageDescriptor(ImageDescriptor.createFromURL(this.getClass().getResource("/images/epub-project-banner.png")));
	}

	/** Method */
	public void addPages() {
		
		ePubProjectModifyTocItemWizardPage = new EPubProjectModifyTocItemWizardPage(itemText, itemSrc);
		addPage(ePubProjectModifyTocItemWizardPage);
	}

	/** Method */
	@Override
	public boolean performFinish() {

		EPUB_project_toc_item epub_project_toc_item = (EPUB_project_toc_item) gridItem.getData("toc_item");

		itemText = ePubProjectModifyTocItemWizardPage.getItemText();
		itemSrc = ePubProjectModifyTocItemWizardPage.getItemSrc();

		if (epub_project_toc_item.itemText.equals(itemText) &&
				epub_project_toc_item.itemSrc.equals(itemSrc)) {
			
			return true;
		}
		
		epub_project_toc_item.itemText = itemText;
		epub_project_toc_item.itemSrc = itemSrc;


		gridItem.setText(EPubProjectEditor.itemTocTextIndex, itemText);
		gridItem.setText(EPubProjectEditor.itemTocSrcIndex, itemSrc);
		
		ePubProjectEditor.serialize();
		
		return true; /* To close the wizard */
	}

	/** ? */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection structuredSelection) { }
}
