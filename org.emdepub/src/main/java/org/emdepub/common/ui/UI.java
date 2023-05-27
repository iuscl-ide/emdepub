/* Log Values; log-values.com 2023 */ 
/* Log Values; log-values.com 2022 */
package org.emdepub.common.ui;

import java.util.List;
import java.util.function.Consumer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.emdepub.activator.L;
import org.emdepub.common.resources.CR;
import org.emdepub.common.resources.CR.Fonts;
import org.emdepub.common.utils.CU;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

/** UI over SWT */
//@SuppressWarnings({ "java:S3776", "java:S1319", "java:S1066", "java:S5663", "java:S1612", "java:S135", "java:S125" })
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UI {

	/** Separator, margin, padding */
	
	public static final int sep0 = 0;
	public static final int sep1 = 1;
	public static final int sep2 = 2;
	public static final int sep4 = 4;
	public static final int sep8 = 8;
	public static final int sep12 = 12;
	public static final int sep16 = 16;
	
	public static final int smallest24 = 24;
	public static final int smaller32 = 32;
	public static final int small48 = 48;
	public static final int little80 = 80;
	public static final int medium120 = 120;
	public static final int mediumlarge140 = 140;
	public static final int large160 = 160;
	public static final int large180 = 180;

	public static int monospacedTextWidth;

	/** debug */
	boolean isDebug = false;

	/** Main display */
	@Getter Display display;

	/** Max line height */
	@Getter int lineHeight;

	/** Max tools height */
	@Getter int toolsHeight;

	/** Max label height */
	@Getter	int labelHeight;

	/** Both needed */
	public UI(boolean isDebug, Display display) {
		super();
		this.isDebug = isDebug;
		this.display = display;
	}
	
	/**  */
	public Menu createMenu(Shell parentShell, Menu parentMenuBar, String menuBarItemText) {

		MenuItem headerMenuItem = new MenuItem(parentMenuBar, SWT.CASCADE);
		headerMenuItem.setText(menuBarItemText);
	    Menu menu = new Menu(parentShell, SWT.DROP_DOWN);
	    headerMenuItem.setMenu(menu);
	    
	    return menu;
	}

	/**  */
	public void createMenuItem(Menu parentMenu, String menuItemText, Consumer<SelectionEvent> onSelection) {

	    MenuItem menuItem = new MenuItem(parentMenu, SWT.NONE);
	    menuItem.setText(menuItemText);
	    menuItem.addSelectionListener(SelectionListener.widgetSelectedAdapter(onSelection));
	}

	/**  */
	public MenuItem createPopupMenuItem(Menu parentMenu, String menuItemText, Consumer<SelectionEvent> onSelection) {

	    MenuItem menuItem = new MenuItem(parentMenu, SWT.NONE);
	    menuItem.setText(menuItemText);
	    menuItem.addSelectionListener(SelectionListener.widgetSelectedAdapter(onSelection));
	    
	    return menuItem;
	}

	/**  */
	public void createMenuItem_Separator(Menu parentMenu) {
		
		new MenuItem(parentMenu, SWT.SEPARATOR);
	}
	
	/** GridData */
	public Composite createComposite(Composite parentComposite) {
	
		Composite composite = new Composite(parentComposite, SWT.NONE);
		this.addDebug(composite);
		
		return composite;
	}

	/** GridData */
	public Composite createComposite_Border(Composite parentComposite) {
	
		Composite composite = new Composite(parentComposite, SWT.BORDER);
		this.addDebug(composite);
		
		return composite;
	}

	/** GridData */
	public Composite createComposite_VerticalSpacing(Composite parentComposite, int verticalSpacing) {
	
		return createComposite(parentComposite, createGridData(), createGridLayout_VerticalSpacing(verticalSpacing));
	}

	/** GridData */
	public Composite createComposite_FillHorizontal_VerticalSpacing(Composite parentComposite, int verticalSpacing) {
	
		return createComposite(parentComposite, createGridData_FillHorizontal(), createGridLayout_VerticalSpacing(verticalSpacing));
	}

	/** GridData */
	public Composite createComposite(Composite parentComposite, GridData gridData, GridLayout gridLayout) {
	
		Composite composite = createComposite(parentComposite);
		composite.setLayoutData(gridData);
		composite.setLayout(gridLayout);
		
		return composite;
	}

	/** GridData fill horizontal */
	public void fillHorizontal(GridData gridData) {
		
	    gridData.horizontalAlignment = SWT.FILL;
	    gridData.grabExcessHorizontalSpace = true;
	}

	/** GridData fill vertical */
	public void fillVertical(GridData gridData) {
		
	    gridData.verticalAlignment = SWT.FILL;
	    gridData.grabExcessVerticalSpace = true;
	}

	/** GridData fill vertical */
	public void fillVerticalMiddle(GridData gridData) {
		
	    gridData.verticalAlignment = SWT.CENTER;
	    gridData.grabExcessVerticalSpace = true;
	}

	/** GridData */
	public GridData createGridData() {
		
		/*
		exclude	false	
		grabExcessHorizontalSpace	false	
		grabExcessVerticalSpace	false	
		heightHint	-1	
		horizontalAlignment	1	
		horizontalIndent	0	
		horizontalSpan	1	
		minimumHeight	0	
		minimumWidth	0	
		verticalAlignment	2	CENTER MIDDLE
		verticalIndent	0	
		verticalSpan	1	
		widthHint	-1	
		*/

		GridData gridData = new GridData();
		gridData.verticalAlignment = SWT.TOP;

		return gridData;
	}

	/** GridData fill horizontal */
	public GridData createGridData_FillHorizontal() {
		
		GridData gridData = createGridData();
		fillHorizontal(gridData);
	    
	    return gridData;
	}

	/** GridData fill horizontal */
	public GridData createGridData_FillHorizontal_HeightLine() {
		
		GridData gridData = createGridData_FillHorizontal();
		gridData.heightHint = lineHeight;
	    
	    return gridData;
	}

	/** GridData fill horizontal */
	public GridData createGridData_FillHorizontal_HeightTools() {
		
		GridData gridData = createGridData_FillHorizontal();
		gridData.heightHint = toolsHeight;
	    
	    return gridData;
	}

	/** GridData fill horizontal */
	public GridData createGridData_FillHorizontal_Height(int height) {
		
		GridData gridData = createGridData_FillHorizontal();
		gridData.heightHint = height;
	    
	    return gridData;
	}

	/** GridData fill horizontal */
	public GridData createGridData_Height(int height) {
		
		GridData gridData = createGridData();
		gridData.heightHint = height;
	    
	    return gridData;
	}

	/** GridData width */
	public GridData createGridData_HeightLine() {
		
		GridData gridData = createGridData();
		gridData.heightHint = lineHeight;

	    return gridData;
	}

	/** GridData width */
	public GridData createGridData_HeightTools() {
		
		GridData gridData = createGridData();
		gridData.heightHint = toolsHeight;

	    return gridData;
	}

	/** GridData width */
	public GridData createGridData_Width(int width) {
		
		GridData gridData = createGridData();
	    gridData.widthHint = width;

	    return gridData;
	}

	/** GridData width */
	public GridData createGridData_HeightLine_Width(int width) {
		
		GridData gridData = createGridData_Width(width);
		gridData.heightHint = lineHeight;

	    return gridData;
	}

	/** GridData fill horizontal */
	public GridData createGridData_FillBoth() {
		
		GridData gridData = createGridData();
		fillHorizontal(gridData);
		fillVertical(gridData);
	    
	    return gridData;
	}

	/** GridData width */
	public GridData createGridData_FillVerticalMiddle() {
		
		GridData gridData = createGridData();
		fillVerticalMiddle(gridData);
	    
	    return gridData;
	}

	/** GridData width */
	public GridData createGridData_FillVerticalMiddle_Width(int width) {
		
		GridData gridData = createGridData_Width(width);
		fillVerticalMiddle(gridData);
		
	    return gridData;
	}

	/** GridData width */
	public GridData createGridData_FillVerticalMiddle_FillHorizontal() {
		
		GridData gridData = createGridData();
		fillVerticalMiddle(gridData);
	    fillHorizontal(gridData);
	    
	    return gridData;
	}

	
	/** GridLayout */
	public GridLayout createGridLayout() {
		
		/*
		horizontalSpacing	5	
		makeColumnsEqualWidth	false	
		marginBottom	0	
		marginHeight	5	
		marginLeft	0	
		marginRight	0	
		marginTop	0	
		marginWidth	5	
		numColumns	1	
		verticalSpacing	5	
		*/

		GridLayout gridLayout = new GridLayout();
		
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		
		return gridLayout;
	}

	/** GridLayout */
	public GridLayout createGridLayout_ColumnsSpacing(int numColumns, int horizontalSpacing) {
		
		GridLayout gridLayout = createGridLayout();
		gridLayout.numColumns = numColumns;
		gridLayout.horizontalSpacing = horizontalSpacing;
		
		return gridLayout;
	}

	/** GridLayout */
	public GridLayout createGridLayout_VerticalSpacing(int verticalSpacing) {
		
		GridLayout gridLayout = createGridLayout();
		gridLayout.verticalSpacing = verticalSpacing;
		
		return gridLayout;
	}

	/** GridLayout */
	public GridLayout createGridLayout_Margins(int margin) {
		
		GridLayout gridLayout = createGridLayout();
	    gridLayout.marginTop = margin;
	    gridLayout.marginBottom = margin;
	    gridLayout.marginLeft = margin;
	    gridLayout.marginRight = margin;
		
		return gridLayout;
	}

	/** GridLayout */
	public GridLayout createGridLayout_Margins_VerticalSpacing(int margin, int verticalSpacing) {
		
		GridLayout gridLayout = createGridLayout_Margins(margin);
		gridLayout.verticalSpacing = verticalSpacing;
		
		return gridLayout;
	}

	/** GridLayout */
	public GridLayout createGridLayout_Margins_ColumnsSpacing(int margin, int numColumns, int horizontalSpacing) {
		
		GridLayout gridLayout = createGridLayout_Margins(margin);
		gridLayout.numColumns = numColumns;
		gridLayout.horizontalSpacing = horizontalSpacing;
		
		return gridLayout;
	}
	
	/** GridLayout */
	public GridLayout createGridLayout_MarginTop_VerticalSpacing(int marginTop, int verticalSpacing) {
		
		GridLayout gridLayout = createGridLayout();
		gridLayout.marginTop = marginTop;
		gridLayout.verticalSpacing = verticalSpacing;
		
		return gridLayout;
	}

	/** Button */
	public Button createButton_Symbol(Composite composite, String text, String toolTipText, Consumer<SelectionEvent> consumerSelectionEvent) {
		
		Button button = createButton(composite, text, toolTipText, false, UI.smallest24, consumerSelectionEvent);

		button.setFont(CR.findFont(CR.Fonts.NORMAL));
		// button.setSize(24, 16);		
		return button;
	}
	
	/** Button */
	public Button createButton_Large(Composite composite, String text, Consumer<SelectionEvent> consumerSelectionEvent) {
		
		return createButton(composite, text, null, false, UI.large160, consumerSelectionEvent);
	}

	/** Button */
	public Button createButton_Bold_Large(Composite composite, String text, Consumer<SelectionEvent> consumerSelectionEvent) {
		
		return createButton(composite, text, null, true, UI.large160, consumerSelectionEvent);
	}

	/** Button */
	public Button createButton_Little(Composite composite, String text, Consumer<SelectionEvent> consumerSelectionEvent) {
		
		return createButton(composite, text, null, false, UI.little80, consumerSelectionEvent);
	}

	/** Button */
	public Button createButton_Medium(Composite composite, String text, Consumer<SelectionEvent> consumerSelectionEvent) {
		
		return createButton(composite, text, null, false, UI.medium120, consumerSelectionEvent);
	}

	/** Button */
	public Button createButton_MediumLarge(Composite composite, String text, Consumer<SelectionEvent> consumerSelectionEvent) {
		
		return createButton(composite, text, null, false, UI.mediumlarge140, consumerSelectionEvent);
	}

	/** Button */
	public Button createButton_Bold_Medium(Composite composite, String text, Consumer<SelectionEvent> consumerSelectionEvent) {
		
		return createButton(composite, text, null, true, UI.medium120, consumerSelectionEvent);
	}

	/** Button */
	private Button createButton(Composite composite, String text, String toolTipText, boolean isBoldFont, int width,
		Consumer<SelectionEvent> consumerSelectionEvent) {
		
		Button button = new Button(composite, SWT.PUSH);
		if (isBoldFont) {
			button.setFont(CR.findFont(CR.Fonts.BOLD));
		}
		button.setText(text);
		if (toolTipText != null) {
			button.setToolTipText(toolTipText);	
		}
		button.setLayoutData(this.createGridData_Width(width));
		if (consumerSelectionEvent != null) {
			button.addSelectionListener(SelectionListener.widgetSelectedAdapter(consumerSelectionEvent));	
		}
		
		return button;
	}

	/** Button */
	public Label createLabel_Color(Composite composite) {
	
		Label colorLabel = new Label(composite, SWT.NONE);
		colorLabel.addPaintListener(paintEvent -> {
			colorLabel.setForeground(display.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
			paintEvent.gc.drawRectangle(paintEvent.x, paintEvent.y, paintEvent.width - 1, paintEvent.height - 1);
		});
		colorLabel.setLayoutData(this.createGridData_HeightLine_Width(lineHeight));
		
		return colorLabel;
	}

	/** Separator line */
	public void createSeparatorLine(Composite composite) {

		(new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR)).setLayoutData(createGridData_FillHorizontal());
	}

	/** Separator line */
	public void createSeparatorLine_Vertical_HeightTools(Composite composite) {

		(new Label(composite, SWT.SEPARATOR)).setLayoutData(createGridData_HeightTools());
	}

	/** Separator line */
	public void createSeparatorLine_Vertical_HeightLine(Composite composite) {

		(new Label(composite, SWT.SEPARATOR)).setLayoutData(createGridData_HeightLine());
	}

	/** Separator composite */
	public void createComposite_FillHorizontal_Separator(Composite composite, int verticalSize) {

		createComposite(composite, createGridData_FillHorizontal_Height(verticalSize), null);
	}

	/** Horizontal filler composite */
	public void createComposite_FillHorizontal_Empty(Composite composite) {

		createComposite(composite, createGridData_FillHorizontal(), createGridLayout());
	}

	/** Label */
	public void createLabel(Composite composite, String text) {

		createLabel(composite, text, createGridData());
	}

	/** Label */
	public void createLabel(Composite composite, String text, GridData gridData) {

		createLabel(composite, text, null, gridData);
	}

	/** Label */
	public void createLabel_Bold(Composite composite, String text) {

		createLabel(composite, text, CR.Fonts.BOLD, createGridData());
	}

	/** Label */
	public void createLabel_MiddleVertical(Composite composite, String text) {

		createLabel(composite, text, null, createGridData_FillVerticalMiddle());
	}

	/** Label */
	public void createLabel_Bold_MiddleVertical(Composite composite, String text) {

		createLabel(composite, text, CR.Fonts.BOLD, createGridData_FillVerticalMiddle());
	}

	/** Label */
	public void createLabel(Composite composite, String text, Fonts fonts, GridData gridData) {

		final Label label = new Label(composite, SWT.NONE);
		label.setText(text);
		if (fonts != null) {
			label.setFont(CR.findFont(fonts));
		}
		label.setLayoutData(gridData);
	}

	/** Label */
	public void createLabel_Bold_MiddleVertical_Width(Composite composite, String text, int width) {

		createLabel_MiddleVertical_Width(composite, text, CR.Fonts.BOLD, width);
	}

	/** Label */
	public void createLabel_MiddleVertical_Width(Composite composite, String text, int width) {

		createLabel_MiddleVertical_Width(composite, text, null, width);
	}

	/** Label */
	private void createLabel_MiddleVertical_Width(Composite composite, String text, Fonts fonts, int width) {

		final Label label = new Label(composite, SWT.NONE);
		label.setText(text);
		if (fonts != null) {
			label.setFont(CR.findFont(fonts));
		}
		label.setLayoutData(createGridData_FillVerticalMiddle_Width(width));
	}

	/** Modifiable label */
	public Label createLabel_Modifiable(Composite composite, String text, String toolTipText, Fonts fonts, GridData gridData) {

		final Label label = new Label(composite, SWT.WRAP);
		label.setLayoutData(gridData);
		label.setText(text);
		if (fonts != null) {
			label.setFont(CR.findFont(fonts));
		}
		if (!CU.isEmpty(toolTipText)) {
			label.setToolTipText(toolTipText);	
		}
		
		return label;
	}

	/** Line height composite */
	public Composite createComposite_ColumnsSpacing_HeightLine(Composite composite, int numColumns, int horizontalSpacing) {
		
		return createComposite(composite, createGridData_HeightLine(), createGridLayout_ColumnsSpacing(numColumns, horizontalSpacing));
	}

	/** Line height composite */
	public Composite createComposite_FillHorizontal_HeightLine(Composite composite) {
		
		GridData gridData = createGridData_HeightLine();
		fillHorizontal(gridData);
		return createComposite(composite, gridData, createGridLayout());
	}

	/** Line height composite */
	public Composite createComposite_ColumnsSpacing_FillHorizontal_HeightLine(Composite composite, int numColumns, int horizontalSpacing) {
		
		GridData gridData = createGridData_HeightLine();
		fillHorizontal(gridData);
		return createComposite(composite, gridData, createGridLayout_ColumnsSpacing(numColumns, horizontalSpacing));
	}

	/** Tools height composite */
	public Composite createComposite_ColumnsSpacing_FillHorizontal_HeightTools(Composite composite, int numColumns, int horizontalSpacing) {
		
		GridData gridData = createGridData_HeightTools();
		fillHorizontal(gridData);
		return createComposite(composite, gridData, createGridLayout_ColumnsSpacing(numColumns, horizontalSpacing));
	}

	/** Line height composite */
	public Composite createComposite_ColumnsSpacing(Composite composite, int numColumns, int horizontalSpacing) {
		
		return createComposite(composite, createGridData(), createGridLayout_ColumnsSpacing(numColumns, horizontalSpacing));
	}

	public Button createCheckBox(Composite parentComposite, String text) {

		final Composite checkBoxComposite = createComposite_ColumnsSpacing_HeightLine(parentComposite, 2, UI.sep4);
		final Button checkBoxButton = new Button(checkBoxComposite, SWT.CHECK);
		checkBoxButton.setLayoutData(createGridData_FillVerticalMiddle());
		final Label checkBoxLabel = new Label(checkBoxComposite, SWT.NONE);
		checkBoxLabel.setText(text);
		checkBoxLabel.setLayoutData(createGridData_FillVerticalMiddle());
		checkBoxLabel.addMouseListener(MouseListener.mouseDownAdapter(mouseDownEvent -> {
			checkBoxButton.setSelection(!checkBoxButton.getSelection());
			checkBoxButton.notifyListeners(SWT.Selection, new Event());
		}));
		
		return checkBoxButton;
	}

	public Button createCheckBox(Composite parentComposite, String text, String toolTipText) {

		final Composite checkBoxComposite = createComposite_ColumnsSpacing_HeightLine(parentComposite, 2, UI.sep4);
		final Button checkBoxButton = new Button(checkBoxComposite, SWT.CHECK);
		checkBoxButton.setLayoutData(createGridData_FillVerticalMiddle());
		final Label checkBoxLabel = new Label(checkBoxComposite, SWT.NONE);
		checkBoxLabel.setText(text);
		checkBoxLabel.setToolTipText(toolTipText);
		checkBoxLabel.setLayoutData(createGridData_FillVerticalMiddle());
		checkBoxLabel.addMouseListener(MouseListener.mouseDownAdapter(mouseDownEvent -> {
			checkBoxButton.setSelection(!checkBoxButton.getSelection());
			checkBoxButton.notifyListeners(SWT.Selection, new Event());
		}));
		
		return checkBoxButton;
	}

	public Button createRightCheckBox(Composite parentComposite, String text, int labelWidth) {

		final Composite checkBoxComposite = createComposite_ColumnsSpacing_HeightLine(parentComposite, 2, UI.sep4);
		final Label checkBoxLabel = new Label(checkBoxComposite, SWT.NONE);
		checkBoxLabel.setText(text);
		checkBoxLabel.setLayoutData(createGridData_FillVerticalMiddle_Width(labelWidth));
		final Button checkBoxButton = new Button(checkBoxComposite, SWT.CHECK);
		checkBoxButton.setLayoutData(createGridData_FillVerticalMiddle());
		checkBoxLabel.addMouseListener(MouseListener.mouseDownAdapter(mouseDownEvent -> {
			checkBoxButton.setSelection(!checkBoxButton.getSelection());
			checkBoxButton.notifyListeners(SWT.Selection, new Event());
		}));
		
		return checkBoxButton;
	}

	/** Random color component */
	public int random255() {
		
		double d = Math.random() * 255d;
		return (int) d;
	}

	/** Random color */
	public Color randomColor() {
		
		return new Color(Display.getDefault(), random255(), random255(), random255());
	}

	/** New font attributes */
	public Font newFontAttributes(Font font, int attr) {
		
		FontData fontData = font.getFontData()[0];
		fontData = new FontData(fontData.getName(), fontData.getHeight(), attr);
		// fontData.data.lfUnderline = 1;
		
		return new Font(display, fontData);
	}

	/** New font size */
	public Font newFontSize(Font font, int height) {
		
		FontData fontData = font.getFontData()[0];
		fontData = new FontData(fontData.getName(), height, fontData.getStyle());
		
		return new Font(display, fontData);
	}

	/** Debug background */
	private void addDebug(Composite composite) {
		
		if (isDebug) {
			composite.setBackground(randomColor());
			composite.addMouseListener(MouseListener.mouseDownAdapter(mouseDownEvent -> L.i(composite.getBounds().toString()) ));
		}
	}

	/** Largest text */
	public int findMaxWidth(List<String> texts) {
		
		int maxWidth = 0;
		GC gc = new GC(display);
				
		for (String text : texts) {
			int width = gc.textExtent(text).x;
			if (maxWidth < width) {
				maxWidth = width;
			}
		}
		gc.dispose();		
		
		return maxWidth; 
	}
	
	/** Dependent values */
	public void computeSizes(Shell applicationShell) {
		
		/* Measure line (button) height */
		Button measurementButton = new Button(applicationShell, SWT.NONE);
		lineHeight = measurementButton.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
		measurementButton.dispose();

		toolsHeight = 2 * lineHeight + UI.sep4;

		/* Measure font (label) height */
		Label measurementLabel = new Label(applicationShell, SWT.NONE);
		measurementLabel.setFont(CR.findFont(Fonts.BOLD));
		measurementLabel.setText("_");
		labelHeight = measurementLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
		measurementLabel.dispose();

		/* Measure monospaced font (label) width */
		Label monospacedMeasurementLabel = new Label(applicationShell, SWT.NONE);
		monospacedMeasurementLabel.setFont(CR.findFont(Fonts.MONOSPACED));
		monospacedMeasurementLabel.setText("_");
		monospacedTextWidth = monospacedMeasurementLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
		monospacedMeasurementLabel.dispose();
	}
	
	/** Helper */
	public Image loadImage(String imageFileNameWithFullPath) {
		
		return new Image(display, imageFileNameWithFullPath);
	}
	
	/** Visible, exclude for Grid Layout Data */
	public static void visible(Control control, boolean toVisible) {
		
		control.setVisible(toVisible);
		((GridData) control.getLayoutData()).exclude = !toVisible;
	}

	/** Sync */
	public void sync(Runnable runnable) {
		
		display.syncExec(runnable);
	}
	
	/** Helper */
	public interface IRunFunction {
		public void run();
	}
	
	/** Helper */
	public static class ActionFactory {
		
		public static Action create(String id, String text, String toolTipText, ImageDescriptor imageDescriptor, IRunFunction runFunction) {
		
			Action action = new Action() {
				public void run() {
					runFunction.run();
				}
			};
			action.setId(id);
			action.setText(text);
			action.setToolTipText(toolTipText);
			action.setImageDescriptor(imageDescriptor);
			
			return action;
		}
	}	
}
