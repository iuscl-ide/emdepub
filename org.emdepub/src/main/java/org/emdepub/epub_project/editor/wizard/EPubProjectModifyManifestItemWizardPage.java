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
import org.emdepub.activator.UI;

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
		container.setLayout(ui.createMarginsVerticalSpacingGridLayout(6, 7));

		/* itemFileName */
		composite = new Composite(container, SWT.NULL);
		composite.setLayoutData(ui.createFillHorizontalGridData());
		composite.setLayout(ui.createColumnsSpacingGridLayout(3, 7));
		
		label = new Label(composite, SWT.NULL);
		label.setText("File name");
		label.setLayoutData(ui.createWidthGridData(labelWidth));

		label = new Label(composite, SWT.NULL);
		label.setText(itemFileName);
		label.setFont(fontItalic);
		label.setLayoutData(ui.createFillHorizontalGridData());

		/* itemFileRelativePath */
		composite = new Composite(container, SWT.NULL);
		composite.setLayoutData(ui.createFillHorizontalGridData());
		composite.setLayout(ui.createColumnsSpacingGridLayout(3, 7));
		
		label = new Label(composite, SWT.NULL);
		label.setText("File relative path");
		label.setLayoutData(ui.createWidthGridData(labelWidth));

		label = new Label(composite, SWT.NULL);
		label.setText(itemFileRelativePath);
		label.setFont(fontItalic);
		label.setLayoutData(ui.createFillHorizontalGridData());
		
		/* Horizontal separator */
		label = new Label(container, SWT.LEAD);
		label.setLayoutData(ui.createFillHorizontalGridData());

		/* itemFileId */
		composite = new Composite(container, SWT.NULL);
		composite.setLayoutData(ui.createFillHorizontalGridData());
		composite.setLayout(ui.createColumnsSpacingGridLayout(3, 7));
		
		label = new Label(composite, SWT.NULL);
		label.setText("File ID");
		label.setLayoutData(ui.createWidthGridData(labelWidth));
		
		itemFileIdText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		itemFileIdText.setText(itemFileId);
		itemFileIdText.setLayoutData(ui.createFillHorizontalGridData());
		itemFileIdText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				itemFileId = itemFileIdText.getText().trim().strip();
				dialogChanged();
			}
		});

		/* itemFileMediaType */
		composite = new Composite(container, SWT.NULL);
		composite.setLayoutData(ui.createFillHorizontalGridData());
		composite.setLayout(ui.createColumnsSpacingGridLayout(3, 7));
		
		label = new Label(composite, SWT.NULL);
		label.setText("File media type");
		label.setLayoutData(ui.createWidthGridData(labelWidth));
		
		itemFileMediaTypeText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		itemFileMediaTypeText.setText(itemFileMediaType);
		itemFileMediaTypeText.setLayoutData(ui.createFillHorizontalGridData());
		itemFileMediaTypeText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				itemFileMediaType = itemFileMediaTypeText.getText().trim().strip();
				dialogChanged();
			}
		});

		/* itemFileProperties */
		composite = new Composite(container, SWT.NULL);
		composite.setLayoutData(ui.createFillHorizontalGridData());
		composite.setLayout(ui.createColumnsSpacingGridLayout(3, 7));
		
		label = new Label(composite, SWT.NULL);
		label.setText("Item properties");
		label.setLayoutData(ui.createWidthGridData(labelWidth));
		
		itemFilePropertiesText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		itemFilePropertiesText.setText(itemFileProperties);
		itemFilePropertiesText.setLayoutData(ui.createFillHorizontalGridData());
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
