package swt_text_layout.help;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.graphics.TextStyle;

public class HelpBand {

	private final int margin;

	private final int width;

	private final GC gc;
	
	private final HelpPage helpPage;
	
	private final TextLayout textLayout;

	public HelpBand(HelpPage helpPage) {
		super();
		
		this.helpPage = helpPage;
		this.gc = helpPage.getGc();
		this.margin = helpPage.getMarginLeft();
		this.width = helpPage.getWidth();

		this.textLayout = new TextLayout(gc.getDevice());
		this.textLayout.setWidth(width);
		
		this.textLayout.setJustify(true);
		
		//this.textLayout.setStyle(normalTextStyle, 0);

	}

	public TextLayout getTextLayout() {
		return textLayout;
	}

	public int draw(int verticalPosition) {
		
		textLayout.draw(gc, margin, verticalPosition);
		
		return textLayout.getBounds().height + verticalPosition;
	};

}
