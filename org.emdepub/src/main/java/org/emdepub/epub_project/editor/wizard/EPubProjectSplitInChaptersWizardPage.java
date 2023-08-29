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
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.emdepub.activator.UI;

/** Markdown export as HTML wizard page */
public class EPubProjectSplitInChaptersWizardPage extends WizardPage {

	/* Data */
	public String splitStartExpression;
	public String splitSourceFileNameWithFullPath;
	public String splitTargetFileNamesWithFullPath;

	/* UI */
	private Text splitStartExpressionText;
	private Text splitSourceFileNameWithFullPathText;
	private Text splitTargetFileNamesWithFullPathText;	
	
	/** Constructor */
	public EPubProjectSplitInChaptersWizardPage(String splitStartExpression,
		String splitSourceFileNameWithFullPath, String splitTargetFileNamesWithFullPath) {
		super("EPubProjectSplitInChaptersWizardPage");
		
		this.splitStartExpression = splitStartExpression == null ? "" : splitStartExpression;
		this.splitSourceFileNameWithFullPath = splitSourceFileNameWithFullPath == null ? "" : splitSourceFileNameWithFullPath;
		this.splitTargetFileNamesWithFullPath = splitTargetFileNamesWithFullPath == null ? "" : splitTargetFileNamesWithFullPath;
		
		setTitle("Split a File in Separate Files");
		setDescription("Split a file in separate files (for chapters).");
	}

	/** Method */
	@Override
	public void createControl(Composite parent) {

		UI ui = new UI(false, Display.getCurrent());

		final int labelWidth = 180;
		final int buttonWidth = 92;

		Label label;
		Composite composite;

		/* Wizard layout */
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(ui.createMarginsVerticalSpacingGridLayout(6, 7));

		/* splitStartExpression */
		composite = new Composite(container, SWT.NULL);
		composite.setLayoutData(ui.createFillHorizontalGridData());
		composite.setLayout(ui.createColumnsSpacingGridLayout(3, 7));
		
		label = new Label(composite, SWT.NULL);
		label.setText("Split start expression");
		label.setLayoutData(ui.createWidthGridData(labelWidth));
		
		splitStartExpressionText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		splitStartExpressionText.setText(splitStartExpression);
		splitStartExpressionText.setLayoutData(ui.createFillHorizontalGridData());
		splitStartExpressionText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent modifyEvent) {
				splitStartExpression = splitStartExpressionText.getText();
				dialogChanged();
			}
		});

		label = new Label(composite, SWT.LEAD);
		label.setLayoutData(ui.createWidthGridData(buttonWidth));

		/* splitSourceFileNameWithFullPath */
		composite = new Composite(container, SWT.NULL);
		composite.setLayoutData(ui.createFillHorizontalGridData());
		composite.setLayout(ui.createColumnsSpacingGridLayout(3, 7));

		label = new Label(composite, SWT.NULL);
		label.setText("Split source file");
		label.setLayoutData(ui.createWidthGridData(labelWidth));

		splitSourceFileNameWithFullPathText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		splitSourceFileNameWithFullPathText.setText(splitSourceFileNameWithFullPath);
		splitSourceFileNameWithFullPathText.setLayoutData(ui.createFillHorizontalGridData());
		splitSourceFileNameWithFullPathText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent modifyEvent) {
				splitSourceFileNameWithFullPath = splitSourceFileNameWithFullPathText.getText();
				dialogChanged();
			}
		});

		Button button = new Button(composite, SWT.PUSH);
		button.setText("Change...");
		button.setLayoutData(ui.createWidthGridData(buttonWidth));
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent selectionEvent) {
				
				DirectoryDialog directoryDialog = new DirectoryDialog(getShell());
				directoryDialog.setFilterPath(getSplitSourceFileNameWithFullPath());
				directoryDialog.setText("Split source file");
				directoryDialog.setMessage("Split source file location");
				String newPath = directoryDialog.open();
				if (newPath != null) {
					splitSourceFileNameWithFullPath = newPath;
					splitSourceFileNameWithFullPathText.setText(splitSourceFileNameWithFullPath);
				}
			}
		});

		/* splitTargetFileNamesWithFullPath */
		composite = new Composite(container, SWT.NULL);
		composite.setLayoutData(ui.createFillHorizontalGridData());
		composite.setLayout(ui.createColumnsSpacingGridLayout(3, 7));

		label = new Label(composite, SWT.NULL);
		label.setText("Split target files (\"*\" for counter)");
		label.setLayoutData(ui.createWidthGridData(labelWidth));

		splitTargetFileNamesWithFullPathText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		splitTargetFileNamesWithFullPathText.setText(splitTargetFileNamesWithFullPath);
		splitTargetFileNamesWithFullPathText.setLayoutData(ui.createFillHorizontalGridData());
		splitTargetFileNamesWithFullPathText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent modifyEvent) {
				splitTargetFileNamesWithFullPath = splitTargetFileNamesWithFullPathText.getText();
				dialogChanged();
			}
		});

		button = new Button(composite, SWT.PUSH);
		button.setText("Change...");
		button.setLayoutData(ui.createWidthGridData(buttonWidth));
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent selectionEvent) {
				
				DirectoryDialog directoryDialog = new DirectoryDialog(getShell());
				directoryDialog.setFilterPath(getSplitTargetFileNamesWithFullPath());
				directoryDialog.setText("Split target files");
				directoryDialog.setMessage("Split target files location");
				String newPath = directoryDialog.open();
				if (newPath != null) {
					splitTargetFileNamesWithFullPath = newPath;
					splitTargetFileNamesWithFullPathText.setText(splitTargetFileNamesWithFullPath);
				}
			}
		});
		
		dialogChanged();
		setControl(container);
	}

	/** Method */
	private void dialogChanged() {
		
		if (getSplitStartExpression().length() == 0) {
			updateStatus("Split start expression must be specified");
			splitStartExpressionText.setFocus();
			return;
		}

		if (getSplitSourceFileNameWithFullPath().length() == 0) {
			updateStatus("Split source file must be specified");
			splitSourceFileNameWithFullPathText.setFocus();
			return;
		}

		if (getSplitTargetFileNamesWithFullPath().length() == 0) {
			updateStatus("Split target files must be specified");
			splitTargetFileNamesWithFullPathText.setFocus();
			return;
		}
			
		updateStatus(null);
	}

	/** Method */
	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getSplitStartExpression() {
		return splitStartExpression;
	}

	public String getSplitSourceFileNameWithFullPath() {
		return splitSourceFileNameWithFullPath;
	}

	public String getSplitTargetFileNamesWithFullPath() {
		return splitTargetFileNamesWithFullPath;
	}
}
