/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.md.ui.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
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
import org.eclipse.swt.widgets.Text;
import org.emdepub.activator.UI;
import org.emdepub.ui.editor.md.prefs.MarkdownPreferences;
import org.emdepub.ui.editor.md.prefs.MarkdownPreferences.PreferenceNames;

/** Markdown format source text wizard page */
public class MarkdownFormatSourceTextWizardPage extends WizardPage {

	/** Tuple */
	private class CheckBoxAndLabel {
		
		private Button checkBox;
		private Text text;
		private Label label;

		private PreferenceNames preferenceName;
		private String descriptionChecked;
		private String descriptionUnchecked;

		/** Constructor, 5 columns */
		public CheckBoxAndLabel(PreferenceNames preferenceName,
				String captionName, String descriptionChecked, String descriptionUnchecked,
				boolean indented, boolean indented2, int textWidth, String textValue) {

			this.preferenceName = preferenceName;
			this.descriptionChecked = descriptionChecked;
			this.descriptionUnchecked = descriptionUnchecked;
			
			Composite composite = new Composite(container, SWT.NULL);
			composite.setLayoutData(ui.createFillHorizontalGridData());
			
			int textColumn = 0;
			if (textWidth > 0) {
				textColumn = 1;
			}
			if (indented) {
				composite.setLayout(ui.createColumnsSpacingGridLayout(3 + textColumn, 7));
				Label leadLabel = new Label(composite, SWT.LEAD);
				leadLabel.setLayoutData(ui.createWidthGridData(32));
			}
			else if (indented2) {
				composite.setLayout(ui.createColumnsSpacingGridLayout(4 + textColumn, 7));
				Label leadLabel = new Label(composite, SWT.LEAD);
				leadLabel.setLayoutData(ui.createWidthGridData(32));
				leadLabel = new Label(composite, SWT.LEAD);
				leadLabel.setLayoutData(ui.createWidthGridData(32));
			}
			else {
				composite.setLayout(ui.createColumnsSpacingGridLayout(2 + textColumn, 7));
			}
			Boolean value = markdownPreferences.get(preferenceName);
			checkBox = new Button(composite, SWT.CHECK);
			checkBox.setText(captionName);
			//checkBox.setLayoutData(ui.createFillHorizontalGridData());
			checkBox.setLayoutData(ui.createGridData());
			checkBox.setSelection(value);
			if (indented) {
				composite.setLayout(ui.createColumnsSpacingGridLayout(3, 7));
				Label leadLabel = new Label(composite, SWT.LEAD);
				leadLabel.setLayoutData(ui.createWidthGridData(32));
			}
			checkBox.pack();
			if (textWidth > 0) {
				text = new Text(composite, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
				text.setText(textValue);
				text.setLayoutData(ui.createWidthGridData(textWidth));
				text.setEnabled(value);
			}
			label = new Label(composite, SWT.TRAIL);
			label.setText(value ? descriptionChecked : descriptionUnchecked);
			//label.setLayoutData(ui.createWidthGridData(320));
			label.setLayoutData(ui.createFillHorizontalGridData());
			label.setFont(fontItalic);
			checkBox.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent selectionEvent) {
					setSelection(checkBox.getSelection());
					dialogChanged();
				}
				@Override
				public void widgetDefaultSelected(SelectionEvent selectionEvent) { }
			});
		}

//		public void setEnabled(boolean enabled) {
//			checkBox.setEnabled(enabled);
//			label.setEnabled(enabled);
//			text.setEnabled(enabled);
//		}

		public void setSelection(boolean isSelected) {
			label.setText(isSelected ? descriptionChecked : descriptionUnchecked);
			markdownPreferences.set(preferenceName, isSelected);
		}
		
		public Button getCheckBox() {
			return checkBox;
		}

		public Text getText() {
			return text;
		}
	};
	
	/* Data */
	private MarkdownPreferences markdownPreferences;
	
	/* UI */
	private UI ui;
	private Font fontItalic;
	private Composite container;
	private CheckBoxAndLabel sourceFormatWrap;
	
	/** Constructor */
	public MarkdownFormatSourceTextWizardPage(MarkdownPreferences markdownPreferences) {
		super("MarkdownExportAsHtmlWizardPage");
		
		this.markdownPreferences = markdownPreferences;
		
		setTitle("Markdown Source Format Options");
		setDescription("Format the Markdown source text with more options.");
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
	
	/** Create the options */
	@Override
	public void createControl(Composite parent) {

		ui = new UI(false, Display.getCurrent());

		/* Italic */
		fontItalic = ui.newFontAttributes(parent.getFont(), SWT.ITALIC);

		/* Wizard layout */
		container = new Composite(parent, SWT.NULL);
		container.setLayout(ui.createMarginsVerticalSpacingGridLayout(6, 7));

		new CheckBoxAndLabel(PreferenceNames.SourceFormatCollapseWhitespace,
				"Remove spoiler spaces", "Reduce consecutive spaces to one (collapse whitespace)", "",
				false, false, 0, null);

		sourceFormatWrap = new CheckBoxAndLabel(PreferenceNames.SourceFormatRightMarginWrap,
				"Wrap text for max columns", "Re-flow the text to fit in columns (right margin)", "",
				false, false, 24, "80");
		
		sourceFormatWrap.getCheckBox().addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				boolean sourceFormatWrapSelected = sourceFormatWrap.getCheckBox().getSelection();
				sourceFormatWrap.getText().setEnabled(sourceFormatWrapSelected);
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent selectionEvent) { }
		});
		Text sourceFormatColumnsText = sourceFormatWrap.getText();
		sourceFormatColumnsText.setText(markdownPreferences.<Integer>get((PreferenceNames.SourceFormatRightMarginColumns)).toString());
		sourceFormatColumnsText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent focusEvent) {
				dialogChanged();
				super.focusLost(focusEvent);
			}
		});
		
		dialogChanged();
		setControl(container);
	}

	/** Method to remove */
	private void dialogChanged() {
			
		if (sourceFormatWrap.getCheckBox().getSelection()) {
			Text maxColumnsText = sourceFormatWrap.getText();
			
			try {
				int value = Integer.parseInt(maxColumnsText.getText());
				if ((value < 0) || (value > 1000)) {
					updateStatus("The number of columns for wrap must be between 0 and 1000");
					maxColumnsText.setFocus();
					return;
				}
				markdownPreferences.set(PreferenceNames.SourceFormatRightMarginColumns, value);
			} catch (NumberFormatException numberFormatException) {
				updateStatus("The number of columns for wrap must be an integer value between 0 and 1000");
				maxColumnsText.setFocus();
				return;
			}
		}
		updateStatus(null);
	}

	/** Method to remove */
	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}
}
