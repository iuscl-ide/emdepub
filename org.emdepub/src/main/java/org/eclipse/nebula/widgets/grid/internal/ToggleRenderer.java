/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    chris.gross@us.ibm.com - initial API and implementation
 *******************************************************************************/ 
package org.eclipse.nebula.widgets.grid.internal;

import org.eclipse.nebula.widgets.grid.AbstractRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/**
 * The renderer for tree item plus/minus expand/collapse toggle.
 *
 * @author chris.gross@us.ibm.com
 * @since 2.0.0
 */
public class ToggleRenderer extends AbstractRenderer
{

    /**
     * Default constructor.
     */
    public ToggleRenderer()
    {
        this.setSize(16, 16);
    }

    /** 
     * {@inheritDoc}
     */
    public void paint(GC gc, Object value)
    {
        
//    	if (isSelected()) {
//    		gc.setBackground(CR.getColor(CR.Colors.SelectBlue));
//    	}
//    	else {
    		//gc.setBackground(CR.getColor(CR.Colors.List));	
//    	}
        //gc.fillRectangle(getBounds());


//        gc.drawRectangle(getBounds().x, getBounds().y, getBounds().width - 1,
//                         getBounds().height - 1);
//
//        gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));
//
//        gc.drawLine(getBounds().x + 2, getBounds().y + 4, getBounds().x + 6, getBounds().y + 4);
//
//        if (!isExpanded())
//        {
//            gc.drawLine(getBounds().x + 4, getBounds().y + 2, getBounds().x + 4, getBounds().y + 6);
//        }

        //gc.setBackground(CR.getColor(CR.Colors.List));
        gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));

        //gc.fillRectangle(getBounds().x + 2, getBounds().y + 2, getBounds().width - 4, getBounds().height - 4);
        gc.drawRectangle(getBounds().x + 2, getBounds().y + 2, getBounds().width - 4, getBounds().height - 4);

		gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));
		
		gc.drawLine(getBounds().x + 5, getBounds().y + 8, getBounds().x + 11, getBounds().y + 8);
//		gc.drawLine(getBounds().x + 5, getBounds().y + 9, getBounds().x + 12, getBounds().y + 9);
		
		if (!isExpanded())
		{
		   gc.drawLine(getBounds().x + 8, getBounds().y + 5, getBounds().x + 8, getBounds().y + 11);
//		   gc.drawLine(getBounds().x + 9, getBounds().y + 5, getBounds().x + 9, getBounds().y + 12);
		}

    }

    /** 
     * {@inheritDoc}
     */
    public Point computeSize(GC gc, int wHint, int hHint, Object value)
    {
        return new Point(16, 16);
    }
}
