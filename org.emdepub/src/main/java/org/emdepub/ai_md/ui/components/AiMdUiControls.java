package org.emdepub.ai_md.ui.components;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.emdepub.common.resources.CR;
import org.emdepub.common.ui.UI;
import org.emdepub.common.utils.CU;

public class AiMdUiControls {

	private static final int indentWidth = UI.sep16;
	
	public static void loadControls(List<AiMdUiControl> controls) {
		
		for (AiMdUiControl aiMdUiControl : controls) {
			aiMdUiControl.load();
		}
	}

	/** Create read only text control */
	public static AiMdUiStringControl addReadOnlyTextControl(UI ui, Composite parentComposite, int indent, String labelText, Integer labelWidth,
			Object fieldSourceObject, String fieldName, int lines) {
		
		String capFieldName = CU.capitalize(fieldName);
		final String getter = "get" + capFieldName;
		
		int noColumns = 2;
		if (indent > 0) {
			noColumns++;
		}
		
		final Composite readOnlyTextComposite = ui.createComposite_ColumnsSpacing_FillHorizontal_Height(parentComposite, noColumns, UI.sep8, ui.getLineHeight() * lines);

		if (indent > 0) {
			final Composite indentComposite = ui.createComposite(readOnlyTextComposite);
			indentComposite.setLayoutData(ui.createGridData_FillVerticalMiddle_Width(indent * indentWidth));
		}

		final Label readOnlyTextLabel = new Label(readOnlyTextComposite, SWT.NONE);
		readOnlyTextLabel.setLayoutData(labelWidth == null ?
			ui.createGridData_FillHorizontal() : ui.createGridData_Width(labelWidth));
		readOnlyTextLabel.setText(labelText);

		final Text readOnlyText = new Text(readOnlyTextComposite, SWT.READ_ONLY | SWT.MULTI | SWT.WRAP | SWT.BORDER);
		readOnlyText.setLayoutData(ui.createGridData_FillBoth());
		
		return new AiMdUiStringControl() {
			@Override
			public void addEvent(String eventName, Runnable runnable) {}
			@Override
			public void load() {
				String stringValue = (String) CU.val(fieldSourceObject, getter);
				readOnlyText.setText(stringValue);
			}
			@Override
			public String getString() {
				return readOnlyText.getText();
			}
			@Override
			public void enable(boolean enabled) {
				readOnlyTextLabel.setEnabled(enabled);
				readOnlyText.setEnabled(enabled);
			}
		};
	}
	
	/** Create file/folder control */
	public static AiMdUiStringControl addFileControl(UI ui, Composite parentComposite, int indent, String labelText, Integer labelWidth,
			Object fieldSourceObject, String fieldName, boolean isFolder) {
		
		String capFieldName = CU.capitalize(fieldName);
		final String getter = "get" + capFieldName;
		final String setter = "set" + capFieldName;
		final String defaultGetter = "findDefault" + capFieldName;
		
		int noColumns = 4;
		if (indent > 0) {
			noColumns++;
		}
		
		final Composite fileComposite = ui.createComposite_ColumnsSpacing_FillHorizontal_HeightLine(parentComposite, noColumns, UI.sep8);

		if (indent > 0) {
			final Composite indentComposite = ui.createComposite(fileComposite);
			indentComposite.setLayoutData(ui.createGridData_FillVerticalMiddle_Width(indent * indentWidth));
		}

		final Label fileLabel = new Label(fileComposite, SWT.NONE);
		fileLabel.setLayoutData(labelWidth == null ?
			ui.createGridData_FillVerticalMiddle_FillHorizontal() : ui.createGridData_FillVerticalMiddle_Width(labelWidth));
		fileLabel.setText(labelText);

		final Text fileText = new Text(fileComposite, SWT.BORDER);
		fileText.setLayoutData(ui.createGridData_FillVerticalMiddle_FillHorizontal());
		
		fileText.addVerifyListener(verifyEvent -> {

			final String oldText = fileText.getText().trim();
		    final String newText = oldText.substring(0, verifyEvent.start) + verifyEvent.text + oldText.substring(verifyEvent.end);
			CU.exec(fieldSourceObject, setter, newText);
		});

		final Button browseButton = new Button(fileComposite, SWT.PUSH | SWT.FLAT);
		browseButton.setText("Browse");
		browseButton.setLayoutData(ui.createGridData_Width(2 * UI.smaller32));
		browseButton.addSelectionListener(SelectionListener.widgetSelectedAdapter(selectionEvent -> {
			
		}));
		
		final Button resetButton = new Button(fileComposite, SWT.PUSH | SWT.FLAT);
		resetButton.setImage(CR.getImage("initializ_parameter"));
		resetButton.setToolTipText("Reset to the Default Integer Value");
		resetButton.setLayoutData(ui.createGridData_Width(ui.getLineHeight()));
		resetButton.addSelectionListener(SelectionListener.widgetSelectedAdapter(selectionEvent -> {

			String stringValue = (String) CU.val(fieldSourceObject, defaultGetter);
			fileText.setText(stringValue);
		}));
		
		return new AiMdUiStringControl() {
			@Override
			public void addEvent(String eventName, Runnable runnable) {}
			@Override
			public void load() {
				String stringValue = (String) CU.val(fieldSourceObject, getter);
				fileText.setText(stringValue);
			}
			@Override
			public String getString() {
				return fileText.getText();
			}
			@Override
			public void enable(boolean enabled) {
				fileLabel.setEnabled(enabled);
				browseButton.setEnabled(enabled);
				resetButton.setEnabled(enabled);
				resetButton.setEnabled(enabled);
			}
		};
	}

	/** Create combo control */
	public static AiMdUiStringControl addComboControl(UI ui, Composite parentComposite, int indent, String labelText, Integer labelWidth, Integer comboWidth,
			Object fieldSourceObject, String fieldName, LinkedHashMap<?, String> names) {
		
		String nameValues[] = CU.toArray(names.values().stream());
		List<String> nameKeys = names.keySet().stream().map(obj -> ((Enum<?>) obj).name()).collect(Collectors.toList());
 		
		String capFieldName = CU.capitalize(fieldName);
		final String getter = "get" + capFieldName;
		final String setter = "set" + capFieldName;
		final String defaultGetter = "findDefault" + capFieldName;
		
		int noColumns = 3;
		if (indent > 0) {
			noColumns++;
		}
		if (comboWidth != null) {
			noColumns++;
		}
		
		final Composite comboComposite = ui.createComposite_ColumnsSpacing_FillHorizontal_HeightLine(parentComposite, noColumns, UI.sep8);

		if (indent > 0) {
			final Composite indentComposite = ui.createComposite(comboComposite);
			indentComposite.setLayoutData(ui.createGridData_FillVerticalMiddle_Width(indent * indentWidth));
		}

		final Label comboLabel = new Label(comboComposite, SWT.NONE);
		comboLabel.setLayoutData(labelWidth == null ?
			ui.createGridData_FillVerticalMiddle_FillHorizontal() : ui.createGridData_FillVerticalMiddle_Width(labelWidth));
		comboLabel.setText(labelText);

		final Combo combo = new Combo(comboComposite, SWT.READ_ONLY);
		combo.setItems(nameValues);

		if (comboWidth == null) {
			combo.setLayoutData(ui.createGridData_FillVerticalMiddle_FillHorizontal());
		}
		else {
			combo.setLayoutData(ui.createGridData_FillVerticalMiddle_Width(comboWidth));
			ui.createComposite_FillHorizontal_Empty(comboComposite);
		}
		
		combo.addModifyListener(modifyEvent -> {
			System.out.println("qddq" + combo.getSelectionIndex());
			CU.exec(fieldSourceObject, setter, nameKeys.get(combo.getSelectionIndex()));
		});
		
		final Button resetButton = new Button(comboComposite, SWT.PUSH | SWT.FLAT);
		resetButton.setImage(CR.getImage("initializ_parameter"));
		resetButton.setToolTipText("Reset to the Default Integer Value");
		resetButton.setLayoutData(ui.createGridData_Width(ui.getLineHeight()));
		resetButton.addSelectionListener(SelectionListener.widgetSelectedAdapter(selectionEvent -> {

			String stringValue = (String) CU.val(fieldSourceObject, defaultGetter);
			combo.select(nameKeys.indexOf(stringValue));
		}));
		
		return new AiMdUiStringControl() {
			@Override
			public void addEvent(String eventName, Runnable runnable) {}
			@Override
			public void load() {
				String stringValue = (String) CU.val(fieldSourceObject, getter);
				combo.select(nameKeys.indexOf(stringValue));
			}
			@Override
			public String getString() {
				return nameKeys.get(combo.getSelectionIndex());
			}
			@Override
			public void enable(boolean enabled) {
				comboLabel.setEnabled(enabled);
				combo.setEnabled(enabled);
				resetButton.setEnabled(enabled);
			}
		};
	}
	
	/** Create text control */
	public static AiMdUiStringControl addTextControl(UI ui, Composite parentComposite, String labelText, Integer labelWidth, int lines,
			Object fieldSourceObject, String fieldName) {
		
		String cap = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		final String getter = "get" + cap;
		final String setter = "set" + cap;
		
		final Composite textComposite = new Composite(parentComposite, SWT.NONE);
	    textComposite.setLayoutData(ui.createGridData_FillHorizontal());
		textComposite.setLayout(ui.createGridLayout_ColumnsSpacing(2, UI.sep8));
		
		final Label textLabel = new Label(textComposite, SWT.NONE);
		GridData textLabelGridData = labelWidth == null ? ui.createGridData() : ui.createGridData_Width(labelWidth);
		textLabel.setLayoutData(textLabelGridData);
		textLabel.setText(labelText);

		final Text text = new Text(textComposite, (lines > 1 ? SWT.MULTI | SWT.V_SCROLL | SWT.WRAP : SWT.SINGLE) | SWT.BORDER );
		String textValue = (String) CU.val(fieldSourceObject, getter);
		text.setText(CU.isEmpty(textValue) ? "" : textValue);
		GridData textGridData = ui.createGridData_FillHorizontal();
		if (lines > 1) {
			textGridData.minimumHeight = lines * text.getLineHeight();
			textGridData.heightHint = lines * text.getLineHeight();
		}
		text.setLayoutData(textGridData);
		
		/* Text */
		text.addFocusListener(FocusListener.focusLostAdapter(focusLostEvent -> {
			
			String focusLostText = text.getText().trim();
			if (!focusLostText.equalsIgnoreCase((String) CU.val(fieldSourceObject, getter))) {
				CU.exec(fieldSourceObject, setter, focusLostText);
//					serialize();
			}
		}));
		
		return new AiMdUiStringControl() {
			@Override
			public void addEvent(String eventName, Runnable runnable) {}
			@Override
			public void load() {
				String textValue = (String) CU.val(fieldSourceObject, getter);
				text.setText(CU.isEmpty(textValue) ? "" : textValue);
			}
			@Override
			public String getString() {
				return text.getText();
			}
			@Override
			public void enable(boolean enabled) {
				//integerLabel.setEnabled(enabled);
				text.setEnabled(enabled);
				//resetButton.setEnabled(enabled);
			}
		};
	}

	/** Create integer control */
	public static AiMdUiIntegerControl addIntegerControl(UI ui, Composite parentComposite, int indent, String labelText, Integer labelWidth, Integer textWidth,
			Object fieldSourceObject, String fieldName, Runnable onChange, int minValue, int maxValue) {
		
		String capFieldName = CU.capitalize(fieldName);
		final String getter = "get" + capFieldName;
		final String setter = "set" + capFieldName;
		final String defaultGetter = "findDefault" + capFieldName;
		
		int noColumns = 3;
		if (indent > 0) {
			noColumns++;
		}
		if (textWidth != null) {
			noColumns++;
		}
		
		final Composite integerComposite = ui.createComposite_ColumnsSpacing_FillHorizontal_HeightLine(parentComposite, noColumns, UI.sep8); 

		if (indent > 0) {
			final Composite indentComposite = ui.createComposite(integerComposite);
			indentComposite.setLayoutData(ui.createGridData_FillVerticalMiddle_Width(indent * indentWidth));
		}

		final Label integerLabel = new Label(integerComposite, SWT.NONE);
		integerLabel.setLayoutData(labelWidth == null ?
			ui.createGridData_FillVerticalMiddle_FillHorizontal() : ui.createGridData_FillVerticalMiddle_Width(labelWidth));
		integerLabel.setText(labelText);

		final Text integerText = new Text(integerComposite, SWT.SINGLE | SWT.RIGHT |SWT.BORDER );
		Integer integerValue = (Integer) CU.val(fieldSourceObject, getter);
		integerText.setText(integerValue + "");
		if (textWidth == null) {
			integerText.setLayoutData(ui.createGridData_FillVerticalMiddle_FillHorizontal());
		}
		else {
			integerText.setLayoutData(ui.createGridData_FillVerticalMiddle_Width(textWidth));
			ui.createComposite_FillHorizontal_Empty(integerComposite);
		}
		
		integerText.addVerifyListener(verifyEvent -> {
			try {
				final String oldText = integerText.getText().trim();
			    final String newText = oldText.substring(0, verifyEvent.start) + verifyEvent.text + oldText.substring(verifyEvent.end);
				Integer value = Integer.valueOf(newText);
				if ((value < minValue) || (value > maxValue)) {
					verifyEvent.doit = false;
				}
				else {
					verifyEvent.doit = true;
					CU.exec(fieldSourceObject, setter, value);
					ui.syncNotNull(onChange);
				}
			} catch (NumberFormatException numberFormatException) {
				verifyEvent.doit = false;
			}
		});
		
		final Button resetButton = new Button(integerComposite, SWT.PUSH | SWT.FLAT);
		resetButton.setImage(CR.getImage("initializ_parameter"));
		resetButton.setToolTipText("Reset to the Default Integer Value");
		resetButton.setLayoutData(ui.createGridData_Width(ui.getLineHeight()));
		resetButton.addSelectionListener(SelectionListener.widgetSelectedAdapter(selectionEvent -> {
			
			Integer resetValue = (Integer) CU.val(fieldSourceObject, defaultGetter);
			integerText.setText(resetValue + "");
		}));
		
		return new AiMdUiIntegerControl() {
			@Override
			public void addEvent(String eventName, Runnable runnable) {}
			@Override
			public void load() {
				Integer integerValue = (Integer) CU.val(fieldSourceObject, getter);
				integerText.setText(integerValue + "");
			}
			@Override
			public Integer getInteger() {
				return Integer.valueOf(integerText.getText());	
			}
			@Override
			public void enable(boolean enabled) {
				integerLabel.setEnabled(enabled);
				integerText.setEnabled(enabled);
				resetButton.setEnabled(enabled);
			}
		};
	}

	/** Create text control */
	public static AiMdUiBooleanControl addBooleanControl(UI ui, Composite parentComposite, int indent, String labelText,
			Object fieldSourceObject, String fieldName) {
		
		final LinkedHashMap<String, Runnable> events = new LinkedHashMap<>(); 
		
		String capFieldName = CU.capitalize(fieldName);
		final String getter = "get" + capFieldName;
		final String setter = "set" + capFieldName;
		final String defaultGetter = "findDefault" + capFieldName;

		int noColumns = 2;
		if (indent > 0) {
			noColumns++;
		}
		
		final Composite booleanComposite = ui.createComposite_ColumnsSpacing_FillHorizontal_HeightLine(parentComposite, noColumns, UI.sep8); 
		
		if (indent > 0) {
			final Composite indentComposite = ui.createComposite(booleanComposite);
			indentComposite.setLayoutData(ui.createGridData_FillVerticalMiddle_Width(indent * indentWidth));
		}

		final Button check = new Button(booleanComposite, SWT.CHECK);
		check.setText(labelText);
		check.setLayoutData(ui.createGridData_FillVerticalMiddle_FillHorizontal());
		Boolean booleanValue = (Boolean) CU.val(fieldSourceObject, getter);
		check.setSelection(booleanValue);
		
		check.addSelectionListener(SelectionListener.widgetSelectedAdapter(selectionEvent -> {
			
			Boolean value = check.getSelection();
			CU.exec(fieldSourceObject, setter, value);
			ui.syncNotNull(events.get("onChange"));
		}));
		
		final Button resetButton = new Button(booleanComposite, SWT.PUSH | SWT.FLAT);
		resetButton.setImage(CR.getImage("initializ_parameter"));
		resetButton.setToolTipText("Reset to the Default True or False Value");
		resetButton.setLayoutData(ui.createGridData_Width(ui.getLineHeight()));
		resetButton.addSelectionListener(SelectionListener.widgetSelectedAdapter(selectionEvent -> {
			
			Boolean resetValue = (Boolean) CU.val(fieldSourceObject, defaultGetter);
			check.setSelection(resetValue);
		}));
		
		return new AiMdUiBooleanControl() {
			@Override
			public void addEvent(String eventName, Runnable runnable) {
				events.put(eventName, runnable);
			}
			@Override
			public void load() {
				check.setSelection((Boolean) CU.val(fieldSourceObject, getter));
				ui.syncNotNull(events.get("onChange"));
			}
			@Override
			public Boolean getBoolean() {
				return check.getSelection();
			}
			@Override
			public void enable(boolean enabled) {
				check.setEnabled(enabled);
				resetButton.setEnabled(enabled);
			}
		};
	}
}
