/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.epub_project.editor.wizard;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.emdepub.common.utils.CU;
import org.emdepub.epub_project.editor.EPubProjectEditor;
import org.emdepub.epub_project.model.EPUB_project_manifest_item;

/** Generate manifest IDs wizard */
public class EPubProjectModifyManifestItemWizard extends Wizard implements IExportWizard {

	private EPubProjectModifyManifestItemWizardPage ePubProjectModifyManifestItemWizardPage;

	private EPubProjectEditor ePubProjectEditor;
	private GridItem gridItem;
	
	private String itemFileName;
	private String itemFileRelativePath;

	private String itemFileId;
	private String itemFileMediaType;
	private String itemFileProperties;

	
	/** Method */
	public EPubProjectModifyManifestItemWizard(EPubProjectEditor ePubProjectEditor, GridItem gridItem) {
		super();

		this.ePubProjectEditor = ePubProjectEditor;
		this.gridItem = gridItem;
		
		EPUB_project_manifest_item project_ePub_manifest_item = (EPUB_project_manifest_item) gridItem.getData("manifest_item");
		
		itemFileName = project_ePub_manifest_item.itemFileName;
		itemFileRelativePath = project_ePub_manifest_item.itemFileRelativePath;

		itemFileId = project_ePub_manifest_item.itemFileId;
		itemFileMediaType = project_ePub_manifest_item.itemFileMediaType;
		itemFileProperties = project_ePub_manifest_item.itemFileProperties;
		
		this.setWindowTitle("Modify Manifest Item Wizard");
		this.setDefaultPageImageDescriptor(ImageDescriptor.createFromURL(this.getClass().getResource("/images/epub-project-banner.png")));
	}

	/** Method */
	public void addPages() {
		
		ePubProjectModifyManifestItemWizardPage = new EPubProjectModifyManifestItemWizardPage(itemFileName, itemFileRelativePath, itemFileId, itemFileMediaType, itemFileProperties);
		addPage(ePubProjectModifyManifestItemWizardPage);
	}

	/** Method */
	@Override
	public boolean performFinish() {

		EPUB_project_manifest_item project_ePub_manifest_item = (EPUB_project_manifest_item) gridItem.getData("manifest_item");

		itemFileId = ePubProjectModifyManifestItemWizardPage.getItemFileId();
		itemFileMediaType = ePubProjectModifyManifestItemWizardPage.getItemFileMediaType();
		itemFileProperties = ePubProjectModifyManifestItemWizardPage.getItemFileProperties();

		if (project_ePub_manifest_item.itemFileId.equals(itemFileId) &&
			project_ePub_manifest_item.itemFileMediaType.equals(itemFileMediaType) &&
			project_ePub_manifest_item.itemFileProperties.equals(itemFileProperties)) {
			
			return true;
		}
		
		if (!project_ePub_manifest_item.itemFileId.equals(itemFileId)) {
			project_ePub_manifest_item.itemFileManualId = itemFileId;
		}
		project_ePub_manifest_item.itemFileId = itemFileId;
		project_ePub_manifest_item.itemFileMediaType = itemFileMediaType;
		project_ePub_manifest_item.itemFileProperties = itemFileProperties;

		gridItem.setFont(EPubProjectEditor.itemFileIdIndex, CU.isEmpty(project_ePub_manifest_item.itemFileManualId) ?
				ePubProjectEditor.getGridFont() : ePubProjectEditor.getGridFontItalic());
		gridItem.setText(EPubProjectEditor.itemFileIdIndex, itemFileId);
		gridItem.setText(EPubProjectEditor.itemFileMediaTypeIndex, itemFileMediaType);
		gridItem.setText(EPubProjectEditor.itemFilePropertiesIndex, itemFileProperties);
		
		ePubProjectEditor.serialize();
		
		return true; /* To close the wizard */
	}

	/** ? */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection structuredSelection) { }
}
