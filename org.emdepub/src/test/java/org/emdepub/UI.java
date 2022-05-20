/* Log Values; log-values.com 2022 */ 
/* Log Values; log-values.com 2022 */
package org.emdepub;

import java.util.List;
import java.util.function.Consumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
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

/** UI over SWT */
public class UI {

	/** Separator, margin, padding */
	
	
	public final static int sep0 = 0;
	public final static int sep1 = 1;
	public final static int sep2 = 2;
	public final static int sep4 = 4;
	public final static int sep8 = 8;
	public final static int sep12 = 12;
	public final static int sep16 = 16;
	
	public final static int smallest24 = 24;
	public final static int smaller32 = 32;
	public final static int small48 = 48;
	public final static int little80 = 80;
	public final static int medium120 = 120;
	public final static int mediumlarge140 = 140;
	public final static int large160 = 160;

	/** debug */
	private boolean isDebug = false;

	/** Main display */
	private Display display;

	/** Max line height */
	private int lineHeight;

	/** Max tools height */
	private int toolsHeight;

	/** Max label height */
	private int labelHeight;

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

//	/** GridData */
//	public GridData createGridData_AlignmentTop() {
//		
//		GridData gridData = createGridData();
//		gridData.verticalAlignment = SWT.TOP;
//		
//		return gridData;
//	}
//
//	/** GridData */
//	public GridData createGridData_AlignmentTop_Width(int width) {
//		
//		GridData gridData = createGridData_AlignmentTop();
//		gridData.widthHint = width;
//		
//		return gridData;
//	}

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

//	/** GridData fill horizontal */
//	public GridData createGridData_AlignedTop_FillHorizontal() {
//		
//		GridData gridData = createGridData_AlignmentTop();
//		fillHorizontal(gridData);
//	    
//	    return gridData;
//	}

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
	public Button createButton_Large(Composite composite, String text, Consumer<SelectionEvent> consumerSelectionEvent) {
		
		return createButton(composite, text, false, UI.large160, consumerSelectionEvent);
	}

	/** Button */
	public Button createButton_Little(Composite composite, String text, Consumer<SelectionEvent> consumerSelectionEvent) {
		
		return createButton(composite, text, false, UI.little80, consumerSelectionEvent);
	}

	/** Button */
	public Button createButton_Medium(Composite composite, String text, Consumer<SelectionEvent> consumerSelectionEvent) {
		
		return createButton(composite, text, false, UI.medium120, consumerSelectionEvent);
	}

	/** Button */
	public Button createButton_MediumLarge(Composite composite, String text, Consumer<SelectionEvent> consumerSelectionEvent) {
		
		return createButton(composite, text, false, UI.mediumlarge140, consumerSelectionEvent);
	}

	/** Button */
	public Button createButton_Bold_Medium(Composite composite, String text, Consumer<SelectionEvent> consumerSelectionEvent) {
		
		return createButton(composite, text, true, UI.medium120, consumerSelectionEvent);
	}

	/** Button */
	private Button createButton(Composite composite, String text, boolean isBoldFont, int width, Consumer<SelectionEvent> consumerSelectionEvent) {
		
		Button button = new Button(composite, SWT.PUSH);
		if (isBoldFont) {
			//button.setFont(CR.getFont(CR.Fonts.Bold));
		}
		button.setText(text);
		button.setLayoutData(this.createGridData_Width(width));
		if (consumerSelectionEvent != null) {
			button.addSelectionListener(SelectionListener.widgetSelectedAdapter(consumerSelectionEvent));	
		}
		
		return button;
	}

	/** Button */
	public Button createButton_Color(Composite composite) {
	
		Button colorButton = new Button(composite, SWT.FLAT);
		colorButton.setLayoutData(this.createGridData_Width(lineHeight));
		
		return colorButton;
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

//	/** Label */
//	public void createLabel(Composite composite, String text) {
//
//		createLabel(composite, text, createGridData());
//	}
//
//	/** Label */
//	public void createLabel(Composite composite, String text, GridData gridData) {
//
//		createLabel(composite, text, null, gridData);
//	}
//
//	/** Label */
//	public void createLabel_Bold(Composite composite, String text) {
//
//		createLabel(composite, text, CR.Fonts.Bold, createGridData());
//	}
//
//	/** Label */
//	public void createLabel_MiddleVertical(Composite composite, String text) {
//
//		createLabel(composite, text, null, createGridData_FillVerticalMiddle());
//	}
//
//	/** Label */
//	public void createLabel_Bold_MiddleVertical(Composite composite, String text) {
//
//		createLabel(composite, text, CR.Fonts.Bold, createGridData_FillVerticalMiddle());
//	}
//
//	/** Label */
//	public void createLabel(Composite composite, String text, Fonts fonts, GridData gridData) {
//
//		final Label label = new Label(composite, SWT.NONE);
//		label.setText(text);
//		if (fonts != null) {
//			label.setFont(CR.getFont(fonts));
//		}
//		label.setLayoutData(gridData);
//	}
//
//	/** Label */
//	public void createLabel_Bold_MiddleVertical_Width(Composite composite, String text, int width) {
//
//		createLabel_MiddleVertical_Width(composite, text, CR.Fonts.Bold, width);
//	}
//
//	/** Label */
//	public void createLabel_MiddleVertical_Width(Composite composite, String text, int width) {
//
//		createLabel_MiddleVertical_Width(composite, text, null, width);
//	}
//
//	/** Label */
//	private void createLabel_MiddleVertical_Width(Composite composite, String text, Fonts fonts, int width) {
//
//		final Label label = new Label(composite, SWT.NONE);
//		label.setText(text);
//		if (fonts != null) {
//			label.setFont(CR.getFont(fonts));
//		}
//		label.setLayoutData(createGridData_FillVerticalMiddle_Width(width));
//	}
//
//	/** Modifiable label */
//	public Label createLabel_Modifiable(Composite composite, String text, String toolTipText, Fonts fonts, GridData gridData) {
//
//		final Label label = new Label(composite, SWT.WRAP);
//		label.setLayoutData(gridData);
//		label.setText(text);
//		if (fonts != null) {
//			label.setFont(CR.getFont(fonts));
//		}
//		if (!CU.isEmpty(toolTipText)) {
//			label.setToolTipText(toolTipText);	
//		}
//		
//		return label;
//	}

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
		//fontData.data.lfUnderline = 1;
		
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
			composite.addMouseListener(MouseListener.mouseDownAdapter(mouseDownEvent -> {
				//CL.i(composite.getBounds().toString());
			}));
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
	
	/** Display */
	public Display getDisplay() {
		return display;
	}
	
	/** Visible, exclude for Grid Layout Data */
	public static void visible(Control control, boolean toVisible) {
		
		control.setVisible(toVisible);
		((GridData) control.getLayoutData()).exclude = !toVisible;
	}
	
	public int getLineHeight() {
		return lineHeight;
	}

	public void setLineHeight(int lineHeight) {
		this.lineHeight = lineHeight;
	}

	public int getToolsHeight() {
		return toolsHeight;
	}

	public void setToolsHeight(int toolsHeight) {
		this.toolsHeight = toolsHeight;
	}
	
	public int getLabelHeight() {
		return labelHeight;
	}

	public void setLabelHeight(int labelHeight) {
		this.labelHeight = labelHeight;
	}

	/** Helper */
	public interface IRunFunction {
		public void run();
	}

	/** Sync */
	public void sync(Runnable runnable) {
		
		display.syncExec(runnable);
	}
}
