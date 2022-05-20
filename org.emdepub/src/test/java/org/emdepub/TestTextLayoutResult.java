package org.emdepub;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import swt_text_layout.help.HelpPage1;

public class TestTextLayoutResult {

	public static void main (String [] args) {
		
		Display display = new Display ();
		UI ui = new UI(false, display);
		
		Shell applicationShell = new Shell(display);
		applicationShell.setText("TestTextLayoutResult");
		applicationShell.setMinimumSize(900, 700);
	    applicationShell.setLayout(ui.createGridLayout_ColumnsSpacing(3, UI.sep0));
		
		/* Location */
		applicationShell.setLocation(250, 0);
		applicationShell.setSize(display.getClientArea().width - 600, display.getClientArea().height);
		
		ui.createComposite(applicationShell, ui.createGridData_Width(200), ui.createGridLayout());
		
		Composite textComposite = ui.createComposite(applicationShell, ui.createGridData_FillBoth(), ui.createGridLayout());
		
		textComposite.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		
		ui.createComposite(applicationShell, ui.createGridData_Width(100), ui.createGridLayout());
		
		textComposite.addPaintListener(paramPaintEvent -> {

			HelpPageSub helpPageSub = new HelpPageSub(10, 10, textComposite.getBounds().width - 20, paramPaintEvent.gc);
			helpPageSub.draw();
//			TextLayout textLayout = new TextLayout(paramPaintEvent.gc.getDevice());
//			
//			textLayout.setFont(display.getSystemFont());
//			textLayout.setText("a lot");
//			textLayout.setAlignment(SWT.LEFT);
//			textLayout.setWidth(textComposite.getBounds().width - 20);
//			
//			
//			textLayout.draw(paramPaintEvent.gc, 10, 10);
//			
//			textLayout.draw(paramPaintEvent.gc, 10, 10 + textLayout.getBounds().height);
			
		});
		
		
		applicationShell.open ();
		while (!applicationShell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
	}
}
