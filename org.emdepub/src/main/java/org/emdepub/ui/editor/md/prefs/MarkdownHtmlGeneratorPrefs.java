package org.emdepub.ui.editor.md.prefs;



public class MarkdownHtmlGeneratorPrefs {

	public static enum FormatStyle { None, GitHub, Google, BitBucket };
	public static enum FormatCodeStyle { None, GitHub };
	
	private FormatStyle formatStyle = FormatStyle.GitHub;
	private FormatCodeStyle formatCodeStyle = FormatCodeStyle.GitHub;

	
	
	
	public FormatStyle getFormatStyle() {
		return formatStyle;
	}

	public void setFormatStyle(FormatStyle formatStyle) {
		this.formatStyle = formatStyle;
	}

	public FormatCodeStyle getFormatCodeStyle() {
		return formatCodeStyle;
	}

	public void setFormatCodeStyle(FormatCodeStyle formatCodeStyle) {
		this.formatCodeStyle = formatCodeStyle;
	}
	
	
}
