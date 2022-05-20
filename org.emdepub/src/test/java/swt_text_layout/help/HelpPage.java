package swt_text_layout.help;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.TextStyle;

public abstract class HelpPage {

	public TextStyle normalTextStyle = new TextStyle();

	private final int marginLeft;

	private final int marginTop;

	private final int width;

	private final GC gc;
	
	private final List<HelpBand> bands = new ArrayList<>();

	public List<HelpBand> getBands() {
		return bands;
	}

	public HelpPage(int marginLeft, int marginTop, int width, GC gc) {
		super();
		this.marginLeft = marginLeft;
		this.marginTop = marginTop;
		this.width = width;
		this.gc = gc;
		
		//FontData fontData = new FontData("Consolas");
		normalTextStyle.font = new Font(gc.getDevice(), new FontData("Consolas", 10, SWT.NONE));

		createBands();
	}

	public GC getGc() {
		return gc;
	}
		
	public int getMarginLeft() {
		return marginLeft;
	}

	public int getWidth() {
		return width;
	}

	public void draw() {
		
		int verticalPosition = marginTop;
		
		for (HelpBand helpBand : bands) {
			verticalPosition = helpBand.draw(verticalPosition);
		}
	}
	
	protected abstract void createBands();
}
