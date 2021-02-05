/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.md.ui.wizards;

import java.util.LinkedHashMap;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.emdepub.activator.UI;
import org.emdepub.ui.editor.md.engine.MarkdownEditorEngine.SpecialFormattingOptions;

public class MarkdownFormatSourceTextWizardPage extends WizardPage {

	/** Tuple */
	private class CheckBoxAndLabel {
		
		private Button checkBox;
		private Label label;

		private SpecialFormattingOptions wizardOption;
		private String descriptionChecked;
		private String descriptionUnchecked;

		public CheckBoxAndLabel(SpecialFormattingOptions wizardOption, String optionName,
				String descriptionChecked, String descriptionUnchecked) {
		
			this(wizardOption, optionName, descriptionChecked, descriptionUnchecked, false, false);
		}

		public CheckBoxAndLabel(SpecialFormattingOptions wizardOption, String optionName,
				String descriptionChecked, String descriptionUnchecked, boolean indented) {
		
			this(wizardOption, optionName, descriptionChecked, descriptionUnchecked, indented, false);
		}

		public CheckBoxAndLabel(SpecialFormattingOptions wizardOption, String optionName,
				String descriptionChecked, String descriptionUnchecked, boolean indented, boolean indented2) {

			this.wizardOption = wizardOption;
			this.descriptionChecked = descriptionChecked;
			this.descriptionUnchecked = descriptionUnchecked;
			
			Composite composite = new Composite(container, SWT.NULL);
			composite.setLayoutData(ui.createFillHorizontalGridData());
			if (indented) {
				composite.setLayout(ui.createColumnsSpacingGridLayout(3, 7));
				Label leadLabel = new Label(composite, SWT.LEAD);
				leadLabel.setLayoutData(ui.createWidthGridData(32));
			}
			else if (indented2) {
				composite.setLayout(ui.createColumnsSpacingGridLayout(4, 7));
				Label leadLabel = new Label(composite, SWT.LEAD);
				leadLabel.setLayoutData(ui.createWidthGridData(32));
				leadLabel = new Label(composite, SWT.LEAD);
				leadLabel.setLayoutData(ui.createWidthGridData(32));
			}
			else {
				composite.setLayout(ui.createColumnsSpacingGridLayout(2, 7));
			}
			checkBox = new Button(composite, SWT.CHECK);
			checkBox.setText(optionName);
			checkBox.setLayoutData(ui.createFillHorizontalGridData());
			checkBox.setSelection(wizardOptions.get(wizardOption));
			label = new Label(composite, SWT.TRAIL);
			label.setText(wizardOptions.get(wizardOption) ? descriptionChecked : descriptionUnchecked);
			label.setLayoutData(ui.createWidthGridData(320));
			label.setFont(fontItalic);
			checkBox.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent selectionEvent) {
					setSelection(checkBox.getSelection());
				}
				@Override
				public void widgetDefaultSelected(SelectionEvent selectionEvent) { }
			});
		}

		public void setEnabled(boolean enabled) {
			checkBox.setEnabled(enabled);
			label.setEnabled(enabled);
		}

		public void setSelection(boolean isSelected) {
			label.setText(isSelected ? descriptionChecked : descriptionUnchecked);
			wizardOptions.replace(wizardOption, isSelected);
		}
		
		public Button getCheckBox() {
			return checkBox;
		}
	};
	
	/* Data */
	private LinkedHashMap<SpecialFormattingOptions, Boolean> wizardOptions;
	
	/* UI */
	private UI ui;
	private Font fontItalic;
	private Composite container;
	
//	private Button formatMarkdownCheckBox;
//	private Button applyToSelectionCheckBox;
//	private Button repairParagraphsCheckBox;
//	private Button repairParagraphsSmartCheckBox;
//	private Button putOneEmptyLineBetweenParagraphsCheckBox;
//	private Button putTwoSpacesBeforeSentenceCheckBox;
//	private Button create80ColumnsCheckBox;
//	private Button create60ColumnsCheckBox;
//	private Button removeSpoilerLinesCheckBox;
//	private Button removeEmptyLinesCheckBox;
//	private Button removeSpoilerSpacesCheckBox;
	
	/** Constructor */
	public MarkdownFormatSourceTextWizardPage(LinkedHashMap<SpecialFormattingOptions, Boolean> wizardOptions) {
		super("MarkdownExportAsHtmlWizardPage");
		
		this.wizardOptions = wizardOptions;
		
//		this.formatMarkdown = true;
//		this.applyToSelection = true;
//		this.repairParagraphs = false;
//		this.repairParagraphsSmart = false;
//		this.putOneEmptyLineBetweenParagraphs = false;
//		this.putTwoSpacesBeforeSentence = false;
//		this.create80Columns = false;
//		this.create60Columns = false;
//		this.removeSpoilerLines = false;
//		this.removeEmptyLines = false;
//		this.removeSpoilerSpaces = false;
		
		setTitle("Format Markdown Source");
		setDescription("Format the Markdown source text.");
	}

	/** Wizard size */
	@Override
	public void setVisible(final boolean visible) {
		super.setVisible(visible);

		if (visible) {
			final Shell shell = getShell();

			final Point newSize = new Point(640, 480);
			shell.setSize(newSize);
			
			Monitor primary = Display.getCurrent().getPrimaryMonitor();
		    Rectangle bounds = primary.getBounds();
		    
		    int x = bounds.x + (bounds.width - newSize.x) / 2;
		    int y = bounds.y + (bounds.height - newSize.y) / 2;
		    
		    shell.setLocation(x, y);
		}
	}
	
	/** Method */
	@Override
	public void createControl(Composite parent) {

		ui = new UI(false, Display.getCurrent());

		/* Italic */
		fontItalic = ui.newFontAttributes(parent.getFont(), SWT.ITALIC);

		final int indentWidth = 8;
		final int indentWidth2 = 32;
		final int descriptionWidth = 250;
		final int labelWidth = 90;
		final int buttonWidth = 92;

		Label label;
		Composite composite;

		/* Wizard layout */
		container = new Composite(parent, SWT.NULL);
		container.setLayout(ui.createMarginsVerticalSpacingGridLayout(6, 7));
		
//		formatMarkdownCheckBox = new Button(container, SWT.CHECK);
//		formatMarkdownCheckBox.setText("formatMarkdown");
//		formatMarkdownCheckBox.setLayoutData(ui.createFillHorizontalGridData());
//		formatMarkdownCheckBox.setSelection(formatMarkdown);
//
//		composite = new Composite(container, SWT.NULL);
//		composite.setLayoutData(ui.createFillHorizontalGridData());
//		composite.setLayout(ui.createColumnsSpacingGridLayout(2, 7));
//		label = new Label(composite, SWT.LEAD);
//		label.setLayoutData(ui.createWidthGridData(indentWidth));
//		label = new Label(composite, SWT.LEAD);
//		label.setText("formatMarkdown");
//		label.setLayoutData(ui.createFillHorizontalGridData());
		
//		applyToSelectionCheckBox = new Button(container, SWT.CHECK);
//		applyToSelectionCheckBox.setText("Apply to selected text");
//		applyToSelectionCheckBox.setLayoutData(ui.createFillHorizontalGridData());
//		applyToSelectionCheckBox.setSelection(applyToSelection);

//		composite = new Composite(container, SWT.NULL);
//		composite.setLayoutData(ui.createFillHorizontalGridData());
//		composite.setLayout(ui.createColumnsSpacingGridLayout(2, 7));
//		applyToSelectionCheckBox = new Button(composite, SWT.CHECK);
//		applyToSelectionCheckBox.setText("Apply to selection");
//		applyToSelectionCheckBox.setLayoutData(ui.createFillHorizontalGridData());
//		applyToSelectionCheckBox.setSelection(applyToSelection);
//		Label applyToSelectionLabel = new Label(composite, SWT.TRAIL);
//		applyToSelectionLabel.setText("Apply formatting only to the selected text");
//		applyToSelectionLabel.setLayoutData(ui.createWidthGridData(descriptionWidth));
//		applyToSelectionLabel.setFont(fontItalic);
//		applyToSelectionCheckBox.addSelectionListener(new SelectionListener() {
//			@Override
//			public void widgetSelected(SelectionEvent selectionEvent) {
//				applyToSelection = applyToSelectionCheckBox.getSelection();
//				if (applyToSelection) {
//					applyToSelectionLabel.setText("Apply formatting only to the selected text");
//				}
//				else {
//					applyToSelectionLabel.setText("Apply formatting to the whole document!");
//				}
//			}
//			@Override
//			public void widgetDefaultSelected(SelectionEvent selectionEvent) {
//				/* ILB */
//			}
//		});
//		
//		composite = new Composite(container, SWT.NULL);
//		composite.setLayoutData(ui.createFillHorizontalGridData());
//		composite.setLayout(ui.createColumnsSpacingGridLayout(2, 7));
//		repairParagraphsCheckBox = new Button(composite, SWT.CHECK);
//		repairParagraphsCheckBox.setText("repairParagraphs");
//		repairParagraphsCheckBox.setLayoutData(ui.createFillHorizontalGridData());
//		repairParagraphsCheckBox.setSelection(repairParagraphs);
//		Label repairParagraphsLabel = new Label(composite, SWT.TRAIL);
//		repairParagraphsLabel.setText("repairParagraphs");
//		repairParagraphsLabel.setLayoutData(ui.createWidthGridData(descriptionWidth));
//		repairParagraphsLabel.setFont(fontItalic);
//		repairParagraphsCheckBox.addSelectionListener(new SelectionListener() {
//			@Override
//			public void widgetSelected(SelectionEvent selectionEvent) {
//				applyToSelection = applyToSelectionCheckBox.getSelection();
//				if (applyToSelection) {
//					applyToSelectionLabel.setText("Apply formatting only to the selected text");
//				}
//				else {
//					applyToSelectionLabel.setText("Apply formatting to the whole document!");
//				}
//			}
//			@Override
//			public void widgetDefaultSelected(SelectionEvent selectionEvent) {
//				/* ILB */
//			}
//		});
//		
//		composite = new Composite(container, SWT.NULL);
//		composite.setLayoutData(ui.createFillHorizontalGridData());
//		composite.setLayout(ui.createColumnsSpacingGridLayout(3, 7));
//		label = new Label(composite, SWT.LEAD);
//		label.setLayoutData(ui.createWidthGridData(indentWidth2));
//		repairParagraphsSmartCheckBox = new Button(composite, SWT.CHECK);
//		repairParagraphsSmartCheckBox.setText("repairParagraphsSmart");
//		repairParagraphsSmartCheckBox.setLayoutData(ui.createFillHorizontalGridData());
//		repairParagraphsSmartCheckBox.setSelection(repairParagraphsSmart);
//		Label repairParagraphsSmartLabel = new Label(composite, SWT.TRAIL);
//		repairParagraphsSmartLabel.setText("repairParagraphsSmart");
//		repairParagraphsSmartLabel.setLayoutData(ui.createWidthGridData(descriptionWidth));
//		repairParagraphsSmartLabel.setFont(fontItalic);
//		repairParagraphsSmartCheckBox.addSelectionListener(new SelectionListener() {
//			@Override
//			public void widgetSelected(SelectionEvent selectionEvent) {
//				applyToSelection = applyToSelectionCheckBox.getSelection();
//				if (applyToSelection) {
//					applyToSelectionLabel.setText("Apply formatting only to the selected text");
//				}
//				else {
//					applyToSelectionLabel.setText("Apply formatting to the whole document!");
//				}
//			}
//			@Override
//			public void widgetDefaultSelected(SelectionEvent selectionEvent) {
//				/* ILB */
//			}
//		});
//		
//		composite = new Composite(container, SWT.NULL);
//		composite.setLayoutData(ui.createFillHorizontalGridData());
//		composite.setLayout(ui.createColumnsSpacingGridLayout(3, 7));
//		label = new Label(composite, SWT.LEAD);
//		label.setLayoutData(ui.createWidthGridData(indentWidth2));
//		putOneEmptyLineBetweenParagraphsCheckBox = new Button(composite, SWT.CHECK);
//		putOneEmptyLineBetweenParagraphsCheckBox.setText("putOneEmptyLineBetweenParagraphs");
//		putOneEmptyLineBetweenParagraphsCheckBox.setLayoutData(ui.createFillHorizontalGridData());
//		putOneEmptyLineBetweenParagraphsCheckBox.setSelection(putOneEmptyLineBetweenParagraphs);
//		Label putOneEmptyLineBetweenParagraphsLabel = new Label(composite, SWT.TRAIL);
//		putOneEmptyLineBetweenParagraphsLabel.setText("putOneEmptyLineBetweenParagraphs");
//		putOneEmptyLineBetweenParagraphsLabel.setLayoutData(ui.createWidthGridData(descriptionWidth));
//		putOneEmptyLineBetweenParagraphsLabel.setFont(fontItalic);
//		putOneEmptyLineBetweenParagraphsCheckBox.addSelectionListener(new SelectionListener() {
//			@Override
//			public void widgetSelected(SelectionEvent selectionEvent) {
//				applyToSelection = applyToSelectionCheckBox.getSelection();
//				if (applyToSelection) {
//					applyToSelectionLabel.setText("Apply formatting only to the selected text");
//				}
//				else {
//					applyToSelectionLabel.setText("Apply formatting to the whole document!");
//				}
//			}
//			@Override
//			public void widgetDefaultSelected(SelectionEvent selectionEvent) {
//				/* ILB */
//			}
//		});
//
//		composite = new Composite(container, SWT.NULL);
//		composite.setLayoutData(ui.createFillHorizontalGridData());
//		composite.setLayout(ui.createColumnsSpacingGridLayout(3, 7));
//		label = new Label(composite, SWT.LEAD);
//		label.setLayoutData(ui.createWidthGridData(indentWidth2));
//		putTwoSpacesBeforeSentenceCheckBox = new Button(composite, SWT.CHECK);
//		putTwoSpacesBeforeSentenceCheckBox.setText("putTwoSpacesBeforeSentence");
//		putTwoSpacesBeforeSentenceCheckBox.setLayoutData(ui.createFillHorizontalGridData());
//		putTwoSpacesBeforeSentenceCheckBox.setSelection(putTwoSpacesBeforeSentence);
//		Label putTwoSpacesBeforeSentenceLabel = new Label(composite, SWT.TRAIL);
//		putTwoSpacesBeforeSentenceLabel.setText("putTwoSpacesBeforeSentence");
//		putTwoSpacesBeforeSentenceLabel.setLayoutData(ui.createWidthGridData(descriptionWidth));
//		putTwoSpacesBeforeSentenceLabel.setFont(fontItalic);
//		putTwoSpacesBeforeSentenceCheckBox.addSelectionListener(new SelectionListener() {
//			@Override
//			public void widgetSelected(SelectionEvent selectionEvent) {
//				applyToSelection = applyToSelectionCheckBox.getSelection();
//				if (applyToSelection) {
//					applyToSelectionLabel.setText("Apply formatting only to the selected text");
//				}
//				else {
//					applyToSelectionLabel.setText("Apply formatting to the whole document!");
//				}
//			}
//			@Override
//			public void widgetDefaultSelected(SelectionEvent selectionEvent) {
//				/* ILB */
//			}
//		});
//
//		composite = new Composite(container, SWT.NULL);
//		composite.setLayoutData(ui.createFillHorizontalGridData());
//		composite.setLayout(ui.createColumnsSpacingGridLayout(2, 7));
//		create80ColumnsCheckBox = new Button(composite, SWT.CHECK);
//		create80ColumnsCheckBox.setText("create80Columns");
//		create80ColumnsCheckBox.setLayoutData(ui.createFillHorizontalGridData());
//		create80ColumnsCheckBox.setSelection(create80Columns);
//		Label create80ColumnsLabel = new Label(composite, SWT.TRAIL);
//		create80ColumnsLabel.setText("create80Columns");
//		create80ColumnsLabel.setLayoutData(ui.createWidthGridData(descriptionWidth));
//		create80ColumnsLabel.setFont(fontItalic);
//		create80ColumnsCheckBox.addSelectionListener(new SelectionListener() {
//			@Override
//			public void widgetSelected(SelectionEvent selectionEvent) {
//				applyToSelection = applyToSelectionCheckBox.getSelection();
//				if (applyToSelection) {
//					applyToSelectionLabel.setText("Apply formatting only to the selected text");
//				}
//				else {
//					applyToSelectionLabel.setText("Apply formatting to the whole document!");
//				}
//			}
//			@Override
//			public void widgetDefaultSelected(SelectionEvent selectionEvent) {
//				/* ILB */
//			}
//		});
//
//		composite = new Composite(container, SWT.NULL);
//		composite.setLayoutData(ui.createFillHorizontalGridData());
//		composite.setLayout(ui.createColumnsSpacingGridLayout(2, 7));
//		create60ColumnsCheckBox = new Button(composite, SWT.CHECK);
//		create60ColumnsCheckBox.setText("create60Columns");
//		create60ColumnsCheckBox.setLayoutData(ui.createFillHorizontalGridData());
//		create60ColumnsCheckBox.setSelection(create60Columns);
//		Label create60ColumnsLabel = new Label(composite, SWT.TRAIL);
//		create60ColumnsLabel.setText("create60Columns");
//		create60ColumnsLabel.setLayoutData(ui.createWidthGridData(descriptionWidth));
//		create60ColumnsLabel.setFont(fontItalic);
//		create60ColumnsCheckBox.addSelectionListener(new SelectionListener() {
//			@Override
//			public void widgetSelected(SelectionEvent selectionEvent) {
//				applyToSelection = applyToSelectionCheckBox.getSelection();
//				if (applyToSelection) {
//					applyToSelectionLabel.setText("Apply formatting only to the selected text");
//				}
//				else {
//					applyToSelectionLabel.setText("Apply formatting to the whole document!");
//				}
//			}
//			@Override
//			public void widgetDefaultSelected(SelectionEvent selectionEvent) {
//				/* ILB */
//			}
//		});
//
//		composite = new Composite(container, SWT.NULL);
//		composite.setLayoutData(ui.createFillHorizontalGridData());
//		composite.setLayout(ui.createColumnsSpacingGridLayout(2, 7));
//		removeSpoilerLinesCheckBox = new Button(composite, SWT.CHECK);
//		removeSpoilerLinesCheckBox.setText("removeSpoilerLines");
//		removeSpoilerLinesCheckBox.setLayoutData(ui.createFillHorizontalGridData());
//		removeSpoilerLinesCheckBox.setSelection(removeSpoilerLines);
//		Label removeSpoilerLinesLabel = new Label(composite, SWT.TRAIL);
//		removeSpoilerLinesLabel.setText("removeSpoilerLines");
//		removeSpoilerLinesLabel.setLayoutData(ui.createWidthGridData(descriptionWidth));
//		removeSpoilerLinesLabel.setFont(fontItalic);
//		removeSpoilerLinesCheckBox.addSelectionListener(new SelectionListener() {
//			@Override
//			public void widgetSelected(SelectionEvent selectionEvent) {
//				applyToSelection = applyToSelectionCheckBox.getSelection();
//				if (applyToSelection) {
//					applyToSelectionLabel.setText("Apply formatting only to the selected text");
//				}
//				else {
//					applyToSelectionLabel.setText("Apply formatting to the whole document!");
//				}
//			}
//			@Override
//			public void widgetDefaultSelected(SelectionEvent selectionEvent) {
//				/* ILB */
//			}
//		});
//
//		composite = new Composite(container, SWT.NULL);
//		composite.setLayoutData(ui.createFillHorizontalGridData());
//		composite.setLayout(ui.createColumnsSpacingGridLayout(2, 7));
//		removeEmptyLinesCheckBox = new Button(composite, SWT.CHECK);
//		removeEmptyLinesCheckBox.setText("removeEmptyLines");
//		removeEmptyLinesCheckBox.setLayoutData(ui.createFillHorizontalGridData());
//		removeEmptyLinesCheckBox.setSelection(removeEmptyLines);
//		Label removeEmptyLinesLabel = new Label(composite, SWT.TRAIL);
//		removeEmptyLinesLabel.setText("removeEmptyLines");
//		removeEmptyLinesLabel.setLayoutData(ui.createWidthGridData(descriptionWidth));
//		removeEmptyLinesLabel.setFont(fontItalic);
//		removeEmptyLinesCheckBox.addSelectionListener(new SelectionListener() {
//			@Override
//			public void widgetSelected(SelectionEvent selectionEvent) {
//				applyToSelection = applyToSelectionCheckBox.getSelection();
//				if (applyToSelection) {
//					applyToSelectionLabel.setText("Apply formatting only to the selected text");
//				}
//				else {
//					applyToSelectionLabel.setText("Apply formatting to the whole document!");
//				}
//			}
//			@Override
//			public void widgetDefaultSelected(SelectionEvent selectionEvent) {
//				/* ILB */
//			}
//		});
//
//		composite = new Composite(container, SWT.NULL);
//		composite.setLayoutData(ui.createFillHorizontalGridData());
//		composite.setLayout(ui.createColumnsSpacingGridLayout(2, 7));
//		removeSpoilerSpacesCheckBox = new Button(composite, SWT.CHECK);
//		removeSpoilerSpacesCheckBox.setText("removeSpoilerSpaces");
//		removeSpoilerSpacesCheckBox.setLayoutData(ui.createFillHorizontalGridData());
//		removeSpoilerSpacesCheckBox.setSelection(removeSpoilerSpaces);
//		Label removeSpoilerSpacesLabel = new Label(composite, SWT.TRAIL);
//		removeSpoilerSpacesLabel.setText("removeSpoilerSpaces");
//		removeSpoilerSpacesLabel.setLayoutData(ui.createWidthGridData(descriptionWidth));
//		removeSpoilerSpacesLabel.setFont(fontItalic);
//		removeSpoilerSpacesCheckBox.addSelectionListener(new SelectionListener() {
//			@Override
//			public void widgetSelected(SelectionEvent selectionEvent) {
//				applyToSelection = applyToSelectionCheckBox.getSelection();
//				if (applyToSelection) {
//					applyToSelectionLabel.setText("Apply formatting only to the selected text");
//				}
//				else {
//					applyToSelectionLabel.setText("Apply formatting to the whole document!");
//				}
//			}
//			@Override
//			public void widgetDefaultSelected(SelectionEvent selectionEvent) {
//				/* ILB */
//			}
//		});

//		/* Export type */
//		label = new Label(container, SWT.LEAD);
//		label.setText("Export type:");
//		label.setLayoutData(ui.createFillHorizontalGridData());
//		
//		Button exportAllRadio = new Button(container, SWT.RADIO);
//		exportAllRadio.setText("Export the HTML and its assets in a folder");
//		exportAllRadio.setLayoutData(ui.createFillHorizontalGridData());
//		exportAllRadio.setSelection(markdownExportType == MarkdownExportType.ExportAssetsFolder);
//		exportAllRadio.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent sSelectionEvent) {
//				markdownExportType = MarkdownExportType.ExportAssetsFolder;
//				dialogChanged();
//			}
//		});
//
//		composite = new Composite(container, SWT.NULL);
//		composite.setLayoutData(ui.createFillHorizontalGridData());
//		composite.setLayout(ui.createColumnsSpacingGridLayout(2, 7));
//
//		label = new Label(composite, SWT.LEAD);
//		label.setLayoutData(ui.createWidthGridData(indentWidth));
//		
//		label = new Label(composite, SWT.LEAD);
//		label.setText("If the folder already exists it xill be deleted!");
//		label.setLayoutData(ui.createFillHorizontalGridData());
//		label.setFont(fontItalic);
//
//		Button exportFileOnlyRadio = new Button(container, SWT.RADIO);
//		exportFileOnlyRadio.setText("Export the HTML file only");
//		exportFileOnlyRadio.setLayoutData(ui.createFillHorizontalGridData());
//		exportFileOnlyRadio.setSelection(markdownExportType == MarkdownExportType.ExportFileOnly);
//		exportFileOnlyRadio.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent sSelectionEvent) {
//				markdownExportType = MarkdownExportType.ExportFileOnly;
//				dialogChanged();
//			}
//		});
//		
//		/* Horizontal separator */
//		label = new Label(container, SWT.LEAD);
//		label.setLayoutData(ui.createFillHorizontalGridData());
//
//		/* Export name */
//		composite = new Composite(container, SWT.NULL);
//		composite.setLayoutData(ui.createFillHorizontalGridData());
//		composite.setLayout(ui.createColumnsSpacingGridLayout(3, 7));
//		
//		label = new Label(composite, SWT.NULL);
//		label.setText("Export name");
//		label.setLayoutData(ui.createWidthGridData(labelWidth));
//		
//		exportNameText = new Text(composite, SWT.BORDER | SWT.SINGLE);
//		exportNameText.setText(exportName);
//		exportNameText.setLayoutData(ui.createFillHorizontalGridData());
//		exportNameText.addModifyListener(new ModifyListener() {
//			public void modifyText(ModifyEvent e) {
//				exportName = exportNameText.getText();
//				dialogChanged();
//			}
//		});
//
//		label = new Label(composite, SWT.LEAD);
//		label.setLayoutData(ui.createWidthGridData(buttonWidth));
//
//		/* Export location */
//		composite = new Composite(container, SWT.NULL);
//		composite.setLayoutData(ui.createFillHorizontalGridData());
//		composite.setLayout(ui.createColumnsSpacingGridLayout(3, 7));
//
//		label = new Label(composite, SWT.NULL);
//		label.setText("Export location");
//		label.setLayoutData(ui.createWidthGridData(labelWidth));
//
//		exportLocationText = new Text(composite, SWT.BORDER | SWT.SINGLE);
//		exportLocationText.setText(exportLocation);
//		exportLocationText.setLayoutData(ui.createFillHorizontalGridData());
//		exportLocationText.addModifyListener(new ModifyListener() {
//			public void modifyText(ModifyEvent modifyEvent) {
//				exportLocation = exportLocationText.getText();
//				dialogChanged();
//			}
//		});
//
//		Button button = new Button(composite, SWT.PUSH);
//		button.setText("Change...");
//		button.setLayoutData(ui.createWidthGridData(buttonWidth));
//		button.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
//				handleBrowse();
//			}
//		});
//
//		
//		/* Horizontal separator */
//		label = new Label(container, SWT.LEAD);
//		label.setLayoutData(ui.createFillHorizontalGridData());
//
//		label = new Label(container, SWT.NULL);
//		label.setLayoutData(ui.createFillHorizontalGridData());
//		label.setText("The export will be created in:");
//		
//		exportPathLabel = new Label(container, SWT.NULL);
//		exportPathLabel.setLayoutData(ui.createFillHorizontalGridData());
//		exportPathLabel.setFont(fontItalic);

//		/* Horizontal separator */
//		label = new Label(container, SWT.LEAD);
//		label.setLayoutData(ui.createFillHorizontalGridData());

		new CheckBoxAndLabel(SpecialFormattingOptions.ApplyToSelection,
			"Apply to selection",
			"Apply formatting only to the selected text", "Apply formatting to the whole document!");

		final CheckBoxAndLabel repairParagraphsCheckBoxAndLabel = new CheckBoxAndLabel(SpecialFormattingOptions.RepairParagraphs,
			"Repair broken paragraphs",
			"Replace single enter with space", "");
		
		final CheckBoxAndLabel repairParagraphsSmartCheckBoxAndLabel = new CheckBoxAndLabel(SpecialFormattingOptions.RepairParagraphsSmart,
			"Smart repair paragraphs",
			"Try to re-create the paragraphs original structure", "", true);

		final CheckBoxAndLabel putOneEmptyLineBetweenParagraphsCheckBoxAndLabel = new CheckBoxAndLabel(SpecialFormattingOptions.PutOneEmptyLineBetweenParagraphs,
			"Empty line between paragraphs",
			"Put one empty line between paragraphs", "No line between paragraphs", false, true);

		boolean isSelected = repairParagraphsCheckBoxAndLabel.getCheckBox().getSelection();
		repairParagraphsSmartCheckBoxAndLabel.setEnabled(isSelected);
		putOneEmptyLineBetweenParagraphsCheckBoxAndLabel.setEnabled(isSelected);
		repairParagraphsCheckBoxAndLabel.getCheckBox().addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				boolean isSelected = repairParagraphsCheckBoxAndLabel.getCheckBox().getSelection();
				repairParagraphsSmartCheckBoxAndLabel.setEnabled(isSelected);
				putOneEmptyLineBetweenParagraphsCheckBoxAndLabel.setEnabled(isSelected);
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent selectionEvent) { }
		});

//		createCheckBoxAndDescription(MarkdownFormatSourceTextWizardOptions.PutTwoSpacesBeforeSentence,
//			"Two spaces before sentence",
//			"Put two spaces between a period and the start of a sentence", "Do not change space between a period and the start of a sentence");

		final CheckBoxAndLabel create80ColumnsCheckBoxAndLabel = new CheckBoxAndLabel(SpecialFormattingOptions.Create80Columns,
			"80 columns",
			"Re-flow the text to fit in 80 columns", "");

		final CheckBoxAndLabel create60ColumnsCheckBoxAndLabel = new CheckBoxAndLabel(SpecialFormattingOptions.Create60Columns,
			"60 columns",
			"Re-flow the text to fit in 60 columns", "");

		create80ColumnsCheckBoxAndLabel.getCheckBox().addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				boolean isSelected = create80ColumnsCheckBoxAndLabel.getCheckBox().getSelection();
				if (isSelected) {
					create60ColumnsCheckBoxAndLabel.getCheckBox().setSelection(false);
					create60ColumnsCheckBoxAndLabel.setSelection(false);
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent selectionEvent) { }
		});
		create60ColumnsCheckBoxAndLabel.getCheckBox().addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				boolean isSelected = create60ColumnsCheckBoxAndLabel.getCheckBox().getSelection();
				if (isSelected) {
					create80ColumnsCheckBoxAndLabel.getCheckBox().setSelection(false);
					create80ColumnsCheckBoxAndLabel.setSelection(false);
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent selectionEvent) { }
		});

		
		new CheckBoxAndLabel(SpecialFormattingOptions.RemoveSpoilerLines,
			"Remove spoiler lines",
			"Reduce multiple consecutive empty lines to one empty line", "");
		
		new CheckBoxAndLabel(SpecialFormattingOptions.RemoveEmptyLines,
			"Remove empty lines",
			"Remove all empty lines", "");

		new CheckBoxAndLabel(SpecialFormattingOptions.RemoveSpoilerSpaces,
			"Remove spoiler spaces",
			"Reduce multiple consecutive spaces to one space", "");
		
		dialogChanged();
		setControl(container);
	}

//	/** Method */
//	private CheckBoxAndLabel createCheckBoxAndDescription(MarkdownFormatSourceTextWizardOptions wizardOption, String optionName,
//			String descriptionChecked, String descriptionUnchecked) {
//		
//		return createCheckBoxAndDescription(wizardOption, optionName, descriptionChecked, descriptionUnchecked, false);
//	}
//	
//	/** Method */
//	private CheckBoxAndLabel createCheckBoxAndDescription(MarkdownFormatSourceTextWizardOptions wizardOption, String optionName,
//			String descriptionChecked, String descriptionUnchecked, boolean indented) {
//
//		Composite composite = new Composite(container, SWT.NULL);
//		composite.setLayoutData(ui.createFillHorizontalGridData());
//		if (indented) {
//			composite.setLayout(ui.createColumnsSpacingGridLayout(3, 7));
//			Label label = new Label(composite, SWT.LEAD);
//			label.setLayoutData(ui.createWidthGridData(32));
//		}
//		else {
//			composite.setLayout(ui.createColumnsSpacingGridLayout(2, 7));
//		}
//		Button optionCheckBox = new Button(composite, SWT.CHECK);
//		optionCheckBox.setText(optionName);
//		optionCheckBox.setLayoutData(ui.createFillHorizontalGridData());
//		optionCheckBox.setSelection(wizardOptions.get(wizardOption));
//		Label descriptionLabel = new Label(composite, SWT.TRAIL);
//		descriptionLabel.setText(wizardOptions.get(wizardOption) ? descriptionChecked : descriptionUnchecked);
//		descriptionLabel.setLayoutData(ui.createWidthGridData(350));
//		descriptionLabel.setFont(fontItalic);
//		optionCheckBox.addSelectionListener(new SelectionListener() {
//			@Override
//			public void widgetSelected(SelectionEvent selectionEvent) {
//				descriptionLabel.setText(optionCheckBox.getSelection() ? descriptionChecked : descriptionUnchecked);
//				wizardOptions.replace(wizardOption, optionCheckBox.getSelection());
//			}
//			@Override
//			public void widgetDefaultSelected(SelectionEvent selectionEvent) {
//				/* ILB */
//			}
//		});
//		
//		return new CheckBoxAndLabel(optionCheckBox, descriptionLabel);
//	}

	/** Method */
	private void dialogChanged() {
		
//		if (getExportName().length() == 0) {
//			updateStatus("Export name must be specified");
//			exportNameText.setFocus();
//			return;
//		}
//
//		if (getExportLocation().length() == 0) {
//			updateStatus("Export location must be specified");
//			exportLocationText.setFocus();
//			return;
//		}
////		if (getExportLocation().replace('\\', '/').indexOf('/', 1) > 0) {
////			updateStatus("Export location must be valid");
////			return;
////		}
//
//		String sep = F.sep();
//		switch (markdownExportType) {
//		case ExportAssetsFolder:
//			exportPathLabel.setText(exportLocation + sep + exportName + sep);
//			break;
//		case ExportFileOnly:
//			exportPathLabel.setText(exportLocation + sep + exportName + ".html");
//			break;
//		}
			
		updateStatus(null);
	}

	/** Method */
	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public LinkedHashMap<SpecialFormattingOptions, Boolean> getWizardOptions() {
		return wizardOptions;
	}
}
