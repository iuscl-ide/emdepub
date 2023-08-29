/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.epub_project.editor.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.emdepub.common.ui.UI;

/** Generate manifest IDs wizard page */
public class EPubProjectGenerateIDsWizardPage extends WizardPage {

	/* Data */
	private Boolean manifestIDGuid;
	private Boolean manifestIDCounter;
	private String manifestIDPrefix;
	
	/* UI */
	private Text prefixText;
	
	/** Constructor */
	public EPubProjectGenerateIDsWizardPage(Boolean manifestIDGuid, Boolean manifestIDCounter, String manifestIDPrefix) {
		super("EPubProjectGenerateIDsWizardPage");
		
		this.manifestIDGuid = manifestIDGuid;
		this.manifestIDCounter = manifestIDCounter;
		this.manifestIDPrefix = manifestIDPrefix;
		
		setTitle("Generate Manifest IDs");
		setDescription("Generate Manifest IDs as GUIDs or increments.");
	}

	/** Method */
	@Override
	public void createControl(Composite parent) {

		UI ui = new UI(false, Display.getCurrent());

		Label label;
		Composite composite;

		/* Wizard layout */
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(ui.createMarginsVerticalSpacingGridLayout(6, 7));
		
		/* Export type */
		label = new Label(container, SWT.LEAD);
		label.setText("Generate IDs type:");
		label.setLayoutData(ui.createFillHorizontalGridData());
		
		Button guidsRadio = new Button(container, SWT.RADIO);
		guidsRadio.setText("Generate IDs as unique GUIDs");
		guidsRadio.setLayoutData(ui.createFillHorizontalGridData());
		guidsRadio.setSelection(manifestIDGuid);
		guidsRadio.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				manifestIDGuid = true;
				manifestIDCounter = false;
				dialogChanged();
			}
		});

		Button incrementsRadio = new Button(container, SWT.RADIO);
		incrementsRadio.setText("Generate IDs as counter increments");
		incrementsRadio.setLayoutData(ui.createFillHorizontalGridData());
		incrementsRadio.setSelection(manifestIDCounter);
		incrementsRadio.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent sSelectionEvent) {
				manifestIDGuid = false;
				manifestIDCounter = true;
				dialogChanged();
			}
		});
		
		/* Horizontal separator */
		label = new Label(container, SWT.LEAD);
		label.setLayoutData(ui.createFillHorizontalGridData());

		/* Export name */
		composite = new Composite(container, SWT.NULL);
		composite.setLayoutData(ui.createFillHorizontalGridData());
		composite.setLayout(ui.createColumnsSpacingGridLayout(3, 7));
		
		label = new Label(composite, SWT.NULL);
		label.setText("ID prefix");
		label.setLayoutData(ui.createWidthGridData(90));
		
		prefixText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		prefixText.setText(manifestIDPrefix);
		prefixText.setLayoutData(ui.createFillHorizontalGridData());
		prefixText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				manifestIDPrefix = prefixText.getText().trim().strip();
				dialogChanged();
			}
		});

		setControl(container);
	}

	/** Method */
	private void dialogChanged() {
		
		if (getManifestIDPrefix().length() == 0) {
			updateStatus("The prefix must be specified");
			prefixText.setFocus();
			return;
		}
			
		updateStatus(null);
	}
	
	/** Method */
	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public Boolean getManifestIDGuid() {
		return manifestIDGuid;
	}

	public Boolean getManifestIDCounter() {
		return manifestIDCounter;
	}

	public String getManifestIDPrefix() {
		return manifestIDPrefix;
	}
}
