package org.emdepub.ui.editor.md.prefs;

import java.util.LinkedHashMap;

public class MarkdownHtmlGeneratorPrefs {

	public static enum FormatStyle { None, GitHub, Google, BitBucket };
	public static enum FormatOption { FixedContentWidth, JustifiedParagraphs, CenterHeaders };
	public static enum FormatCodeStyle { None,
		Agate,
		Androidstudio,
		ArduinoLight,
		Arta,
		Ascetic,
		AtelierCaveDark,
		AtelierCaveLight,
		AtelierDuneDark,
		AtelierDuneLight,
		AtelierEstuaryDark,
		AtelierEstuaryLight,
		AtelierForestDark,
		AtelierForestLight,
		AtelierHeathDark,
		AtelierHeathLight,
		AtelierLakesideDark,
		AtelierLakesideLight,
		AtelierPlateauDark,
		AtelierPlateauLight,
		AtelierSavannaDark,
		AtelierSavannaLight,
		AtelierSeasideDark,
		AtelierSeasideLight,
		AtelierSulphurpoolDark,
		AtelierSulphurpoolLight,
		AtomOneDark,
		AtomOneLight,
		BrownPaper,
		CodepenEmbed,
		ColorBrewer,
		Darcula,
		Dark,
		Darkula,
		Default,
		Docco,
		Dracula,
		Far,
		Foundation,
		GithubGist,
		Github,
		Googlecode,
		Grayscale,
		GruvboxDark,
		GruvboxLight,
		Hopscotch,
		Hybrid,
		Idea,
		IrBlack,
		KimbieDark,
		KimbieLight,
		Magula,
		MonoBlue,
		MonokaiSublime,
		Monokai,
		Obsidian,
		Ocean,
		ParaisoDark,
		ParaisoLight,
		Pojoaque,
		Purebasic,
		Qtcreator_dark,
		Qtcreator_light,
		Railscasts,
		Rainbow,
		Routeros,
		SchoolBook,
		SolarizedDark,
		SolarizedLight,
		Sunburst,
		TomorrowNightBlue,
		TomorrowNightBright,
		TomorrowNightEighties,
		TomorrowNight,
		Tomorrow,
		Vs,
		Vs2015,
		Xcode,
		Xt256,
		Zenburn
	};
	
	
	
	
//	private static final FormatStyle initialFormatStyle = FormatStyle.GitHub;
//	private static final FormatCodeStyle initialFormatCodeStyle = FormatCodeStyle.GitHub;
	
	
	
	private FormatStyle formatStyle = FormatStyle.GitHub;
	
	private final LinkedHashMap<FormatOption, Boolean> formatOptions = new LinkedHashMap<>();
	
	
	private FormatCodeStyle formatCodeStyle = FormatCodeStyle.Github;
	
	
//	public LinkedHashMap<String , Object> getSerialization() {
//		
//		LinkedHashMap<String, Object> ser = new LinkedHashMap<>();
//		
//		if (formatStyle != initialFormatStyle) {
//			ser.put("formatStyle", formatStyle);
//		}
//		
//		return ser;
//	}
//
//	public void setSerialization(LinkedHashMap<String, Object> ser) {
//		
//		for (Entry<String, Object> entry : ser.entrySet()) {
//			
//			if (entry.getKey().equals("formatStyle")) {
//				this.formatStyle = (FormatStyle) entry.getValue();
//			}
//		}
//	}

	
	public MarkdownHtmlGeneratorPrefs() {
		super();
		
		formatOptions.put(FormatOption.FixedContentWidth, true);
		formatOptions.put(FormatOption.JustifiedParagraphs, true);
		formatOptions.put(FormatOption.CenterHeaders, false);
	}

	
	public FormatStyle getFormatStyle() {
		return formatStyle;
	}

	public void setFormatStyle(FormatStyle formatStyle) {
		this.formatStyle = formatStyle;
	}
	
	public LinkedHashMap<FormatOption, Boolean> getFormatOptions() {
		return formatOptions;
	}

	public FormatCodeStyle getFormatCodeStyle() {
		return formatCodeStyle;
	}

	public void setFormatCodeStyle(FormatCodeStyle formatCodeStyle) {
		this.formatCodeStyle = formatCodeStyle;
	}
	
	
}
