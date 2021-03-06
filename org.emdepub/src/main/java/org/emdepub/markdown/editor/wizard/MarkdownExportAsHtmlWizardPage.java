/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.markdown.editor.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.emdepub.activator.F;
import org.emdepub.activator.UI;
import org.emdepub.markdown.editor.wizard.MarkdownExportAsHtmlWizard.MarkdownExportType;

/** Markdown export as HTML wizard page */
public class MarkdownExportAsHtmlWizardPage extends WizardPage {

	/** Convenient */
	private static final String s = F.s;
	
	/* Data */
	private MarkdownExportType markdownExportType;
	private String exportCssReference;
	private String exportName;
	private String exportLocation;

	/* UI */
	private Text exportCssReferenceText;
	private Text exportNameText;
	private Text exportLocationText;	
	private Label exportPathLabel;
	
	/** Constructor */
	public MarkdownExportAsHtmlWizardPage(MarkdownExportType markdownExportType, String exportCssReference,
			String exportName, String exportLocation) {
		super("MarkdownExportAsHtmlWizardPage");
		
		this.markdownExportType = markdownExportType;
		this.exportCssReference = exportCssReference;
		this.exportName = exportName;
		this.exportLocation = exportLocation;
		
		setTitle("Export Markdown as HTML");
		setDescription("Export the Markdown generated HTML.");
	}

	/** Method */
	@Override
	public void createControl(Composite parent) {

		UI ui = new UI(false, Display.getCurrent());

		/* Italic */
		Font fontItalic = ui.newFontAttributes(parent.getFont(), SWT.ITALIC);

		final int indentWidth = 24;
		final int labelWidth = 90;
		final int buttonWidth = 92;

		Label label;
		Composite composite;

		/* Wizard layout */
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(ui.createMarginsVerticalSpacingGridLayout(6, 7));
		
		/* Export type */
		label = new Label(container, SWT.LEAD);
		label.setText("Export type:");
		label.setLayoutData(ui.createFillHorizontalGridData());

		
		Button exportAllRadio = new Button(container, SWT.RADIO);
		exportAllRadio.setText("Export the HTML and its assets in a folder");
		exportAllRadio.setLayoutData(ui.createFillHorizontalGridData());
		exportAllRadio.setSelection(markdownExportType == MarkdownExportType.ExportAssetsFolder);
		exportAllRadio.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				markdownExportType = MarkdownExportType.ExportAssetsFolder;
				dialogChanged();
			}
		});

		composite = new Composite(container, SWT.NULL);
		composite.setLayoutData(ui.createFillHorizontalGridData());
		composite.setLayout(ui.createColumnsSpacingGridLayout(2, 7));

		label = new Label(composite, SWT.LEAD);
		label.setLayoutData(ui.createWidthGridData(indentWidth));
		
		label = new Label(composite, SWT.LEAD);
		label.setText("If the folder already exists it xill be deleted!");
		label.setLayoutData(ui.createFillHorizontalGridData());
		label.setFont(fontItalic);

		
		Button exportEPubRadio = new Button(container, SWT.RADIO);
		exportEPubRadio.setText("Export the HTML as file with a custom style file reference (for ePub)");
		exportEPubRadio.setLayoutData(ui.createFillHorizontalGridData());
		exportEPubRadio.setSelection(markdownExportType == MarkdownExportType.ExportFileOnlyWithCssReference);
		exportEPubRadio.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				markdownExportType = MarkdownExportType.ExportFileOnlyWithCssReference;
				dialogChanged();
			}
		});

		composite = new Composite(container, SWT.NULL);
		composite.setLayoutData(ui.createFillHorizontalGridData());
		composite.setLayout(ui.createColumnsSpacingGridLayout(3, 7));

		label = new Label(composite, SWT.LEAD);
		label.setLayoutData(ui.createWidthGridData(indentWidth));

		label = new Label(composite, SWT.NULL);
		label.setText("Style (.css) file name with relative path");
		//label.setLayoutData(ui.createWidthGridData((int) (labelWidth * 2.5)));
		label.setLayoutData(ui.createGridData());
		label.pack();
		
		exportCssReferenceText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		exportCssReferenceText.setText(exportCssReference);
		exportCssReferenceText.setLayoutData(ui.createFillHorizontalGridData());
		exportCssReferenceText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				exportCssReference = exportCssReferenceText.getText();
				dialogChanged();
			}
		});

//		label = new Label(composite, SWT.LEAD);
//		label.setLayoutData(ui.createWidthGridData(buttonWidth));

		
				
		Button exportFileOnlyRadio = new Button(container, SWT.RADIO);
		exportFileOnlyRadio.setText("Export the HTML file only");
		exportFileOnlyRadio.setLayoutData(ui.createFillHorizontalGridData());
		exportFileOnlyRadio.setSelection(markdownExportType == MarkdownExportType.ExportFileOnly);
		exportFileOnlyRadio.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				markdownExportType = MarkdownExportType.ExportFileOnly;
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
		label.setText("Export name");
		label.setLayoutData(ui.createWidthGridData(labelWidth));
		
		exportNameText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		exportNameText.setText(exportName);
		exportNameText.setLayoutData(ui.createFillHorizontalGridData());
		exportNameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				exportName = exportNameText.getText();
				dialogChanged();
			}
		});

		label = new Label(composite, SWT.LEAD);
		label.setLayoutData(ui.createWidthGridData(buttonWidth));

		/* Export location */
		composite = new Composite(container, SWT.NULL);
		composite.setLayoutData(ui.createFillHorizontalGridData());
		composite.setLayout(ui.createColumnsSpacingGridLayout(3, 7));

		label = new Label(composite, SWT.NULL);
		label.setText("Export location");
		label.setLayoutData(ui.createWidthGridData(labelWidth));

		exportLocationText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		exportLocationText.setText(exportLocation);
		exportLocationText.setLayoutData(ui.createFillHorizontalGridData());
		exportLocationText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent modifyEvent) {
				exportLocation = exportLocationText.getText();
				dialogChanged();
			}
		});

		Button button = new Button(composite, SWT.PUSH);
		button.setText("Change...");
		button.setLayoutData(ui.createWidthGridData(buttonWidth));
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowse();
			}
		});

		
		/* Horizontal separator */
		label = new Label(container, SWT.LEAD);
		label.setLayoutData(ui.createFillHorizontalGridData());

		label = new Label(container, SWT.NULL);
		label.setLayoutData(ui.createFillHorizontalGridData());
		label.setText("The export will be created in:");
		
		exportPathLabel = new Label(container, SWT.WRAP);
		GridData exportPathLabelGridData =  ui.createFillHorizontalGridData();
		exportPathLabelGridData.minimumHeight = 3 * exportLocationText.getLineHeight();
		exportPathLabelGridData.heightHint = 3 * exportLocationText.getLineHeight();
		exportPathLabel.setLayoutData(exportPathLabelGridData);
		exportPathLabel.setFont(fontItalic);

		/* Horizontal separator */
		label = new Label(container, SWT.LEAD);
		label.setLayoutData(ui.createFillHorizontalGridData());
		
		dialogChanged();
		setControl(container);
	}

	/** Method */
	private void handleBrowse() {
		
		DirectoryDialog directoryDialog = new DirectoryDialog(getShell());
		directoryDialog.setFilterPath(getExportLocation());
		directoryDialog.setText("Export location");
		directoryDialog.setMessage("Select export location");
		String newPath = directoryDialog.open();
		if (newPath != null) {
			exportLocation = newPath;
			exportLocationText.setText(exportLocation);
		}
	}

	/** Method */
	private void dialogChanged() {
		
		if (getExportName().length() == 0) {
			updateStatus("Export name must be specified");
			exportNameText.setFocus();
			return;
		}

		if (getExportLocation().length() == 0) {
			updateStatus("Export location must be specified");
			exportLocationText.setFocus();
			return;
		}
//		if (getExportLocation().replace('\\', '/').indexOf('/', 1) > 0) {
//			updateStatus("Export location must be valid");
//			return;
//		}

		switch (markdownExportType) {
		case ExportAssetsFolder:
			exportCssReferenceText.setEnabled(false);
			exportPathLabel.setText(exportLocation + s + exportName + s);
			break;
		case ExportFileOnlyWithCssReference:
			exportCssReferenceText.setEnabled(true);
			exportPathLabel.setText(exportLocation + s + exportName + ".html");
			
			if (getExportCssReference().length() == 0) {
				updateStatus("Export CSS reference must be specified");
				exportCssReferenceText.setFocus();
				return;
			}
			break;
		case ExportFileOnly:
			exportCssReferenceText.setEnabled(false);
			exportPathLabel.setText(exportLocation + s + exportName + ".html");
			break;
		default:
			break;
		}
			
		updateStatus(null);
	}

	/** Method */
	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public MarkdownExportType getMarkdownExportType() {
		return markdownExportType;
	}

	public String getExportName() {
		return exportName;
	}

	public String getExportLocation() {
		return exportLocation;
	}

	public String getExportCssReference() {
		return exportCssReference;
	}
}
