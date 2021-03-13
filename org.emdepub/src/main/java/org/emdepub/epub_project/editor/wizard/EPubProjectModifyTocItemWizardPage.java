/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.epub_project.editor.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.emdepub.activator.UI;

/** Generate manifest IDs wizard page */
public class EPubProjectModifyTocItemWizardPage extends WizardPage {

	/* Data */
	private String itemText;
	private String itemSrc;

	
	/* UI */
	private Text itemTextText;
	private Text itemSrcText;
	
	/** Constructor */
	public EPubProjectModifyTocItemWizardPage(String itemText, String itemSrc) {
		super("EPubProjectGenerateIDsWizardPage");

		this.itemText = itemText;
		this.itemSrc = itemSrc;
		
		setTitle("Modify Toc Item");
		setDescription("Modify Toc item to personalize");
	}

	/** Method */
	@Override
	public void createControl(Composite parent) {

		UI ui = new UI(false, Display.getCurrent());


		final int labelWidth = 100;
		
		Label label;
		Composite composite;

		/* Wizard layout */
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(ui.createMarginsVerticalSpacingGridLayout(6, 7));

		/* itemText */
		composite = new Composite(container, SWT.NULL);
		composite.setLayoutData(ui.createFillHorizontalGridData());
		composite.setLayout(ui.createColumnsSpacingGridLayout(3, 7));
		
		label = new Label(composite, SWT.NULL);
		label.setText("Toc entry title");
		label.setLayoutData(ui.createWidthGridData(labelWidth));
		
		itemTextText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		itemTextText.setText(itemText);
		itemTextText.setLayoutData(ui.createFillHorizontalGridData());
		itemTextText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent modifyEvent) {
				itemText = itemTextText.getText().trim().strip();
				dialogChanged();
			}
		});

		/* itemSrc */
		composite = new Composite(container, SWT.NULL);
		composite.setLayoutData(ui.createFillHorizontalGridData());
		composite.setLayout(ui.createColumnsSpacingGridLayout(3, 7));
		
		label = new Label(composite, SWT.NULL);
		label.setText("Toc entry src");
		label.setLayoutData(ui.createWidthGridData(labelWidth));
		
		itemSrcText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		itemSrcText.setText(itemSrc);
		itemSrcText.setLayoutData(ui.createFillHorizontalGridData());
		itemSrcText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent modifyEvent) {
				itemSrc = itemSrcText.getText().trim().strip();
				dialogChanged();
			}
		});

		setControl(container);
	}

	/** Method */
	private void dialogChanged() {
		
		if (getItemText().length() == 0) {
			updateStatus("The Toc entry title must be specified");
			itemTextText.setFocus();
			return;
		}

		if (getItemSrc().length() == 0) {
			updateStatus("The Toc entry src must be specified");
			itemSrcText.setFocus();
			return;
		}

		updateStatus(null);
	}
	
	/** Method */
	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getItemText() {
		return itemText;
	}

	public String getItemSrc() {
		return itemSrc;
	}
}
