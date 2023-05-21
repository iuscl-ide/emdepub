/* Emdepub Eclipse Plugin - emdepub.org */ 
package org.emdepub.common.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/** UI over SWT */
public class UI {

	/** Separator, margin, padding */
	public final static int sep = 8;
	
	/** debug */
	private boolean isDebug = false;

	/** Main display */
	private Display display;

	/** Both needed */
	public UI(boolean isDebug, Display display) {
		super();
		this.isDebug = isDebug;
		this.display = display;
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
		verticalAlignment	2	
		verticalIndent	0	
		verticalSpan	1	
		widthHint	-1	
		*/

		GridData gridData = new GridData();
		/* TODO ? */
		return gridData;
	}

	/** GridData */
	public GridData createTopAlignedGridData() {
		
		GridData gridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		/* TODO ? */
		return gridData;
	}

	/** GridData */
	public GridData createWidthTopAlignedGridData(int width) {
		
		GridData gridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.widthHint = width;
		
		return gridData;
	}

	/** GridData fill horizontal */
	public GridData createFillHorizontalGridData() {
		
		GridData gridData = createGridData();
	    gridData.horizontalAlignment = SWT.FILL;
	    gridData.grabExcessHorizontalSpace = true;
	    
	    return gridData;
	}

	/** GridData fill horizontal */
	public GridData createTopAlignedFillHorizontalGridData() {
		
		GridData gridData = createTopAlignedGridData();
	    gridData.horizontalAlignment = SWT.FILL;
	    gridData.grabExcessHorizontalSpace = true;
	    
	    return gridData;
	}

	/** GridData fill horizontal */
	public GridData createFillBothGridData() {
		
		GridData gridData = createGridData();
	    gridData.horizontalAlignment = SWT.FILL;
	    gridData.grabExcessHorizontalSpace = true;
	    gridData.verticalAlignment = SWT.FILL;
	    gridData.grabExcessVerticalSpace = true;
	    
	    return gridData;
	}

	/** GridData width 120 */
	public GridData createWidthButtonGridData() {
		
	    return createWidthGridData(150);
	}

	/** GridData width */
	public GridData createWidthGridData(int width) {
		
		GridData gridData = createGridData();
	    gridData.widthHint = width;
	    
	    return gridData;
	}

	/** GridData width */
	public GridData createWidthFillVerticalGridData(int width) {
		
		GridData gridData = createGridData();
	    gridData.widthHint = width;
	    gridData.verticalAlignment = SWT.FILL;
	    gridData.grabExcessVerticalSpace = true;
	    
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
	public GridLayout createColumnsGridLayout(int numColumns) {
		
		GridLayout gridLayout = createGridLayout();
		gridLayout.numColumns = numColumns;
		
		return gridLayout;
	}

	/** GridLayout */
	public GridLayout createColumnsSpacingGridLayout(int numColumns, int horizontalSpacing) {
		
		GridLayout gridLayout = createGridLayout();
		gridLayout.numColumns = numColumns;
		gridLayout.horizontalSpacing = horizontalSpacing;
		
		return gridLayout;
	}

	/** GridLayout */
	public GridLayout createMarginsColumnsSpacingGridLayout(int margin, int numColumns, int horizontalSpacing) {
		
		GridLayout gridLayout = createMarginsGridLayout(margin);
		gridLayout.numColumns = numColumns;
		gridLayout.horizontalSpacing = horizontalSpacing;
		
		return gridLayout;
	}

	/** GridLayout */
	public GridLayout createVerticalSpacingGridLayout(int verticalSpacing) {
		
		GridLayout gridLayout = createGridLayout();
		gridLayout.verticalSpacing = verticalSpacing;
		
		return gridLayout;
	}

	/** GridLayout */
	public GridLayout createMarginTopVerticalSpacingGridLayout(int marginTop, int verticalSpacing) {
		
		GridLayout gridLayout = createGridLayout();
		gridLayout.marginTop = marginTop;
		gridLayout.verticalSpacing = verticalSpacing;
		
		return gridLayout;
	}
	
	/** GridLayout */
	public GridLayout createMarginsVerticalSpacingGridLayout(int margin, int verticalSpacing) {
		
		GridLayout gridLayout = createMarginsGridLayout(margin);
		gridLayout.verticalSpacing = verticalSpacing;
		
		return gridLayout;
	}

	/** GridLayout */
	public GridLayout createMarginsGridLayout(int margin) {
		
		GridLayout gridLayout = createGridLayout();
	    gridLayout.marginTop = margin;
	    gridLayout.marginBottom = margin;
	    gridLayout.marginLeft = margin;
	    gridLayout.marginRight = margin;
		
		return gridLayout;
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
	public void addDebug(Composite composite) {
		
		if (isDebug) {
			composite.setBackground(randomColor());
		}
	}

	/** Display */
	public Display getDisplay() {
		return display;
	}
	
	
	/** Helper */
	public interface OnMouseDown extends MouseListener {
		@Override
		default
		void mouseDoubleClick(MouseEvent mouseEvent) { };
		@Override
		default
		void mouseUp(MouseEvent mouseEvent) { };
	}

	/** Helper */
	public interface OnMouseDoubleClick extends MouseListener {
		@Override
		default
		void mouseDown(MouseEvent mouseEvent) { };
		@Override
		default
		void mouseUp(MouseEvent mouseEvent) { };
	}

	/** Helper */
	public interface OnResize extends ControlListener {
		@Override
		default
		void controlMoved(ControlEvent controlEvent) { };
	}
	
	/** Helper */
	public interface OnSelection extends SelectionListener {
		@Override
		default
		void widgetDefaultSelected(SelectionEvent selectionEvent) { }
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
