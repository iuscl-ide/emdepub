package swt_text_layout.help;

import org.eclipse.swt.graphics.GC;

public class HelpPage1 extends HelpPage {

	public HelpPage1(int marginLeft, int marginTop, int width, GC gc) {
		super(marginLeft, marginTop, width, gc);
	}

	@Override
	protected void createBands() {
		
		HelpBand helpBand1  = new HelpBand(this);
		helpBand1.getTextLayout().setText("%s");
		
	}
}
