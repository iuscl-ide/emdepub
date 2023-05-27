/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.epub_project.editor.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.emdepub.common.ui.UI;

/** Generate manifest IDs wizard page */
public class EPubProjectModifyManifestItemWizardPage extends WizardPage {

	/* Data */
	private String itemFileName;
	private String itemFileRelativePath;

	private String itemFileId;
	private String itemFileMediaType;
	private String itemFileProperties;
	
	/* UI */
	private Text itemFileIdText;
	private Text itemFileMediaTypeText;
	private Text itemFilePropertiesText;
	
	/** Constructor */
	public EPubProjectModifyManifestItemWizardPage(String itemFileName, String itemFileRelativePath, String itemFileId, String itemFileMediaType, String itemFileProperties) {
		super("EPubProjectGenerateIDsWizardPage");

		this.itemFileName = itemFileName;
		this.itemFileRelativePath = itemFileRelativePath;
		
		this.itemFileId = itemFileId;
		this.itemFileMediaType = itemFileMediaType;
		this.itemFileProperties = itemFileProperties;
		
		setTitle("Modify Manifest Item");
		setDescription("Modify manifest item to personalize");
	}

	/** Method */
	@Override
	public void createControl(Composite parent) {

		UI ui = new UI(false, Display.getCurrent());

		/* Italic */
		Font fontItalic = ui.newFontAttributes(parent.getFont(), SWT.ITALIC);

		final int labelWidth = 100;
		
		Label label;
		Composite composite;

		/* Wizard layout */
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(ui.createGridLayout_Margins_VerticalSpacing(6, 7));

		/* itemFileName */
		composite = new Composite(container, SWT.NULL);
		composite.setLayoutData(ui.createGridData_FillHorizontal());
		composite.setLayout(ui.createGridLayout_ColumnsSpacing(3, 7));
		
		label = new Label(composite, SWT.NULL);
		label.setText("File name");
		label.setLayoutData(ui.createGridData_Width(labelWidth));

		label = new Label(composite, SWT.NULL);
		label.setText(itemFileName);
		label.setFont(fontItalic);
		label.setLayoutData(ui.createGridData_FillHorizontal());

		/* itemFileRelativePath */
		composite = new Composite(container, SWT.NULL);
		composite.setLayoutData(ui.createGridData_FillHorizontal());
		composite.setLayout(ui.createGridLayout_ColumnsSpacing(3, 7));
		
		label = new Label(composite, SWT.NULL);
		label.setText("File relative path");
		label.setLayoutData(ui.createGridData_Width(labelWidth));

		label = new Label(composite, SWT.NULL);
		label.setText(itemFileRelativePath);
		label.setFont(fontItalic);
		label.setLayoutData(ui.createGridData_FillHorizontal());
		
		/* Horizontal separator */
		label = new Label(container, SWT.LEAD);
		label.setLayoutData(ui.createGridData_FillHorizontal());

		/* itemFileId */
		composite = new Composite(container, SWT.NULL);
		composite.setLayoutData(ui.createGridData_FillHorizontal());
		composite.setLayout(ui.createGridLayout_ColumnsSpacing(3, 7));
		
		label = new Label(composite, SWT.NULL);
		label.setText("File ID");
		label.setLayoutData(ui.createGridData_Width(labelWidth));
		
		itemFileIdText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		itemFileIdText.setText(itemFileId);
		itemFileIdText.setLayoutData(ui.createGridData_FillHorizontal());
		itemFileIdText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				itemFileId = itemFileIdText.getText().trim().strip();
				dialogChanged();
			}
		});

		/* itemFileMediaType */
		composite = new Composite(container, SWT.NULL);
		composite.setLayoutData(ui.createGridData_FillHorizontal());
		composite.setLayout(ui.createGridLayout_ColumnsSpacing(3, 7));
		
		label = new Label(composite, SWT.NULL);
		label.setText("File media type");
		label.setLayoutData(ui.createGridData_Width(labelWidth));
		
		itemFileMediaTypeText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		itemFileMediaTypeText.setText(itemFileMediaType);
		itemFileMediaTypeText.setLayoutData(ui.createGridData_FillHorizontal());
		itemFileMediaTypeText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				itemFileMediaType = itemFileMediaTypeText.getText().trim().strip();
				dialogChanged();
			}
		});

		/* itemFileProperties */
		composite = new Composite(container, SWT.NULL);
		composite.setLayoutData(ui.createGridData_FillHorizontal());
		composite.setLayout(ui.createGridLayout_ColumnsSpacing(3, 7));
		
		label = new Label(composite, SWT.NULL);
		label.setText("Item properties");
		label.setLayoutData(ui.createGridData_Width(labelWidth));
		
		itemFilePropertiesText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		itemFilePropertiesText.setText(itemFileProperties);
		itemFilePropertiesText.setLayoutData(ui.createGridData_FillHorizontal());
		itemFilePropertiesText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				itemFileProperties = itemFilePropertiesText.getText().trim().strip();
				dialogChanged();
			}
		});

		setControl(container);
	}

	/** Method */
	private void dialogChanged() {
		
//		if (getItemFileId().length() == 0) {
//			updateStatus("The file ID must be specified");
//			itemFileIdText.setFocus();
//			return;
//		}

		if (getItemFileMediaType().length() == 0) {
			updateStatus("The file media type must be specified");
			itemFileMediaTypeText.setFocus();
			return;
		}

		updateStatus(null);
	}
	
	/** Method */
	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getItemFileId() {
		return itemFileId;
	}

	public String getItemFileMediaType() {
		return itemFileMediaType;
	}

	public String getItemFileProperties() {
		return itemFileProperties;
	}
}
