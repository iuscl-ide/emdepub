/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.common.resources;

import java.io.InputStream;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.emdepub.common.editor.language.content_assist.CommonCompletionProposal;
import org.emdepub.common.ui.UI;
import org.emdepub.common.utils.CU;
import org.emdepub.markdown.editor.engine.MarkdownCompletionProposal.MarkdownCompletionProposalKey;
import org.emdepub.toml.editor.engine.TomlCompletionProposal;
import org.emdepub.toml.editor.engine.TomlCompletionProposal.TomlCompletionProposalKey;
import org.emdepub.toml.editor.engine.TomlResSupport;

/** Resources */
public class CR {
	
	/** Color names */
//	public enum Colors {
//		DEFAULT_FG_COLOR, HEADING_FG_COLOR,
//		HEADER_COMMENT_FG_COLOR, COMMENT_FG_COLOR,
//		DIVIDER_LINE_FG_COLOR,
//		LINK_FG_COLOR,
//		IMAGE_BG_COLOR,
//		HTML_FG_COLOR,
//		FENCED_CODE_BG_COLOR,
//		INDENTED_CODE_BG_COLOR,
//		INLINE_CODE_BG_COLOR;
//	}
	
	public enum Colors { ControlFace, ControlLight, ControlShadow,
		ListFont, ListFont66, ListFont33, ListFontSelected,
		List, 
		ListSelectedAndFocus, ListSelectedNotFocus,
		ListSelectedSecondaryAndFocus, ListSelectedSecondaryNotFocus,
		ListNotSelectedButFocus };

	/** Fonts */
	public enum Fonts { NORMAL, BOLD, BIGGER, BIGGER_BOLD, RECENTS, SMALLER, MONOSPACED, MONOSPACED_BIGGER }

	/** The shared list of loaded icons */
	private final static HashMap<String, Image> resourceImageRegistry = new HashMap<>();
	private final static HashMap<String, ImageDescriptor> resourceImageDescriptorRegistry = new HashMap<>();

	/** For Eclipse forms */
	private final static FormToolkit formsToolkit = new FormToolkit(Display.getCurrent());
	
	/** Colors */
	private final static HashMap<Colors, Color> colorRegistry = new HashMap<>();
	
	
	/** Fonts */
	private static final EnumMap<Fonts, Font> fontRegistry = new EnumMap<>(Fonts.class);
	
	/** To get from TOML */
	private static MarkdownResSupport markdownProposalsSupport;
	private static TomlResSupport tomlProposalsSupport;
	
	/** Load shared icons, should be called from outside only once */
	public static void load(UI ui, Shell shell) {
		
		Display display = ui.getDisplay();
		
		/* Images, icons */
//		loadGifImageResourceToRegistry("md-file-toolbar-nottext");
		loadGifImageResourceToRegistry("html");
		loadGifImageResourceToRegistry("stylesheet");
		loadGifImageResourceToRegistry("ui_props");
		loadGifImageResourceToRegistry("PD_Toolbar_source");
		
		loadGifImageResourceToRegistry("PD_Toolbar_bold");
		loadGifImageResourceToRegistry("PD_Toolbar_bold_disabled");
		loadGifImageResourceToRegistry("PD_Toolbar_italic");
		loadGifImageResourceToRegistry("PD_Toolbar_italic_disabled");
		
		loadGifImageResourceToRegistry("unknown");
		loadGifImageResourceToRegistry("tag-image");
		loadGifImageResourceToRegistry("show_properties_view");
		loadGifImageResourceToRegistry("run");
		
		loadGifImageResourceToRegistry("XSDIdentityConstraintDefinitionKey");
		
		loadGifImageResourceToRegistry("XML_file");
		
		loadPngImageResourceToRegistry("wordwrap");
		loadPngImageResourceToRegistry("show_whitespace_chars");
		
		loadPngImageResourceToRegistry("markdown-action-format-md");
//		loadPngImageResourceToRegistry("markdown-action-word-wrap");
		loadPngImageResourceToRegistry("markdown-action-create-80");
		loadPngImageResourceToRegistry("markdown-action-repair-paragraph");
		
		loadPngImageResourceToRegistry("markdown-header");
		loadPngImageResourceToRegistry("markdown-content-assist-proposal");
		
		loadPngImageResourceToRegistry("toml-action-verify-file");
		loadPngImageResourceToRegistry("toml-action-comment");
		loadPngImageResourceToRegistry("toml-completion-proposal");
		
		loadPngImageResourceToRegistry("message_warning");
		
		
		loadPngImageResourceToRegistry("classf_generate");
		loadPngImageResourceToRegistry("fileType_filter");
		loadPngImageResourceToRegistry("refresh");
		
		loadPngImageResourceToRegistry("project");
		
		loadPngImageResourceToRegistry("add");
		loadPngImageResourceToRegistry("remove");
		loadPngImageResourceToRegistry("move-up");
		loadPngImageResourceToRegistry("move-down");
		
		loadPngImageResourceToRegistry("asterisk");
		loadPngImageResourceToRegistry("edit_template");
		
//		loadGifImageResourceToRegistry("md-preference-toolbar-notdefault");
//		loadGifImageResourceToRegistry("md-preference-toolbar-apply");
//		loadGifImageResourceToRegistry("md-preference-toolbar-reload");
//		
//		loadPngImageResourceToRegistry("md-heading-1");
//		loadPngImageResourceToRegistry("md-heading-2");
//		loadPngImageResourceToRegistry("md-heading-3");
//		loadPngImageResourceToRegistry("md-heading-4");
//		loadPngImageResourceToRegistry("md-heading-5");
//		loadPngImageResourceToRegistry("md-heading-6");
//		
//		loadPngImageResourceToRegistry("md-action-word-wrap");
//		loadPngImageResourceToRegistry("md-action-format-md");
//		loadPngImageResourceToRegistry("md-action-create-80");
//		loadPngImageResourceToRegistry("md-action-repair-paragraph");
		
//		/* Colors */
//		loadColorToRegistry(Colors.DEFAULT_FG_COLOR.name(), 0, 0, 0); /* Text Black */
//		loadColorToRegistry(Colors.HEADING_FG_COLOR.name(), 187, 0, 0); /* Trac Red */
//		loadColorToRegistry(Colors.HEADER_COMMENT_FG_COLOR.name(), 63, 95, 191); /* JavaDoc Blue */
//		loadColorToRegistry(Colors.COMMENT_FG_COLOR.name(), 63, 127, 95); /* Java Comment Green */
//		loadColorToRegistry(Colors.DIVIDER_LINE_FG_COLOR.name(), 120, 120, 120); /* Line Number Gray */
//		loadColorToRegistry(Colors.LINK_FG_COLOR.name(), 0, 0, 238); /* Standard Web Link Blue */
//		loadColorToRegistry(Colors.IMAGE_BG_COLOR.name(), 255, 243, 224); /* Image Background Orange */
//		loadColorToRegistry(Colors.HTML_FG_COLOR.name(), 127, 0, 127); /* Java Keyword Purple */
//		loadColorToRegistry(Colors.FENCED_CODE_BG_COLOR.name(), 255, 255, 224); /* Code Background Yellow */
//		loadColorToRegistry(Colors.INDENTED_CODE_BG_COLOR.name(), 255, 255, 223); /* Code Background Yellow */
//		loadColorToRegistry(Colors.INLINE_CODE_BG_COLOR.name(), 254, 254, 190); /* Code Background Yellow */
		
		
		colorRegistry.put(Colors.ControlFace, display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		colorRegistry.put(Colors.ControlLight, display.getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
		colorRegistry.put(Colors.ControlShadow, display.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
		
		// SWT.COLOR_LIST_BACKGROUND 25
		colorRegistry.put(Colors.List, display.getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		// SWT.COLOR_LIST_FOREGROUND 24
		colorRegistry.put(Colors.ListFont, display.getSystemColor(SWT.COLOR_LIST_FOREGROUND));
		// SWT.COLOR_LIST_SELECTION_TEXT 27
		colorRegistry.put(Colors.ListFontSelected, display.getSystemColor(SWT.COLOR_LIST_SELECTION_TEXT));

		// SWT.COLOR_LIST_SELECTION 26
		colorRegistry.put(Colors.ListSelectedAndFocus, display.getSystemColor(SWT.COLOR_LIST_SELECTION)); 
		colorRegistry.put(Colors.ListSelectedNotFocus, display.getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW));

		colorRegistry.put(Colors.ListSelectedSecondaryAndFocus, new Color(display,
				CR.blend(colorRegistry.get(Colors.ListSelectedAndFocus).getRGB(), colorRegistry.get(Colors.ListFontSelected).getRGB(), 80)));
		colorRegistry.put(Colors.ListSelectedSecondaryNotFocus, new Color(display,
				CR.blend(colorRegistry.get(Colors.ListSelectedNotFocus).getRGB(), colorRegistry.get(Colors.ListFontSelected).getRGB(), 70)));

		
		/* Tab before anything selected */
		colorRegistry.put(Colors.ListNotSelectedButFocus, colorRegistry.get(Colors.ControlFace));
		
		
		/* Fonts */
		Font defaultFont = shell.getFont();
		int defaultFontHeight = defaultFont.getFontData()[0].getHeight();
		fontRegistry.put(Fonts.NORMAL, defaultFont);
		fontRegistry.put(Fonts.BOLD, ui.newFontAttributes(defaultFont, SWT.BOLD));
		fontRegistry.put(Fonts.BIGGER, ui.newFontSize(defaultFont, defaultFontHeight + 5));
		fontRegistry.put(Fonts.BIGGER_BOLD, ui.newFontSize(fontRegistry.get(Fonts.BOLD), defaultFontHeight + 5));
		fontRegistry.put(Fonts.RECENTS, ui.newFontSize(defaultFont, defaultFontHeight  + 1));
		fontRegistry.put(Fonts.SMALLER, ui.newFontSize(defaultFont, defaultFontHeight - 3));
		Font monospacedFont = getsMonospacedFont(display);
		fontRegistry.put(Fonts.MONOSPACED, monospacedFont);
		fontRegistry.put(Fonts.MONOSPACED_BIGGER, ui.newFontSize(monospacedFont, monospacedFont.getFontData()[0].getHeight() + 5));
		
		
//		colorRegistry.put(Colors.ListFont66, new Color(display, CR.blend(colorRegistry.get(Colors.List).getRGB(), colorRegistry.get(Colors.ListFont).getRGB(), 66)));
//		colorRegistry.put(Colors.ListFont33, new Color(display, CR.blend(colorRegistry.get(Colors.List).getRGB(), colorRegistry.get(Colors.ListFont).getRGB(), 33)));
		
		//colorRegistry.put(Colors.ListSelectedNotFocus, new Color(display, CR.blend(colorRegistry.get(Colors.ListSelectedAndFocus).getRGB(), colorRegistry.get(Colors.ListFontSelected).getRGB(), 85)));
		
		markdownProposalsSupport = CU.tomlDeserialize(getTextResourceAsString("texts/markdown-content-assist-proposals.toml"), MarkdownResSupport.class);
		for (CommonCompletionProposal commonCompletionProposal : markdownProposalsSupport.getProposals().values()) {
			commonCompletionProposal.setImage(getImage("markdown-content-assist-proposal"));
		}

		tomlProposalsSupport = CU.tomlDeserialize(getTextResourceAsString("texts/toml-content-assist-proposals.toml"), TomlResSupport.class);
		for (TomlCompletionProposal tomlCompletionProposal : tomlProposalsSupport.getProposals().values()) {
			tomlCompletionProposal.setImage(getImage("toml-completion-proposal"));
		}
	}

	/** From Grid */
	private static int blend(int v1, int v2, int ratio) {
		
		return (ratio * v1 + (100 - ratio) * v2) / 100;
	}

	/** From Grid */
	public static RGB blend(RGB c1, RGB c2, int ratio) {
		
		int r = blend(c1.red, c2.red, ratio);
		int g = blend(c1.green, c2.green, ratio);
		int b = blend(c1.blue, c2.blue, ratio);
		return new RGB(r, g, b);
	}

	/** Loads from resource */
	private static void loadGifImageResourceToRegistry(String name) {
		
		loadImageResourceToRegistry(name, "gif");
	}

	/** Loads from resource */
	private static void loadPngImageResourceToRegistry(String name) {
		
		loadImageResourceToRegistry(name, "png");
	}

	/** Loads from resource */
	private static void loadImageResourceToRegistry(String name, String ext) {
		
		Image registryImage = getResourceAsImage("icons/" + name + "." + ext);
		resourceImageRegistry.put(name, registryImage);
		resourceImageDescriptorRegistry.put(name, ImageDescriptor.createFromImage(registryImage));
	}

	/** Returns a resource image for the icon file name */
	public static Image getImage(String name) {
		
		return resourceImageRegistry.get(name);
	}

	/** Returns a resource image descriptor for the icon file name */
	public static ImageDescriptor getImageDescriptor(String name) {
		
		return resourceImageDescriptorRegistry.get(name);
	}

	/** Load resource */
	public static InputStream getResourceAsInputStream(String resourceName) {
		
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
	}
	
	/** Load image resource */
	public static Image getResourceAsImage(String imageResourceName) {
		
		InputStream inputStream = getResourceAsInputStream(imageResourceName);
		return new Image(Display.getDefault(), inputStream);
	}

	/** Load text resource */
	public static String getTextResourceAsString(String textResourceName) {
		
		return CU.loadInputStreamInString(getResourceAsInputStream(textResourceName));
	}

	/** For Eclipse forms */
	public static FormToolkit getFormsToolkit() {
		return formsToolkit;
	}

	/** Font by defined name */
	public static Font findFont(Fonts fonts) {
		return fontRegistry.get(fonts);
	}

	/** Per OS */
	private static Font getsMonospacedFont(Display display) {

		String os = System.getProperty("os.name");
		os = CU.deleteWhitespace(os).toLowerCase(Locale.US);

		String fontSerialization = "";
		
		if (os.contains("windows")) {
			fontSerialization = "Consolas,10";
		}
		else {
			fontSerialization = "Liberation Mono,10";
		}

//		switch (os) {
//		case "linux":
//			fontSerialization = "adobe-courier,10";
//			break;
//		case "macosx":
//			fontSerialization = "Monaco,10";
//			break;
//		case "windows":
//			fontSerialization = "Consolas,10";
//			break;
//		default:
//			fontSerialization = "Courier New,10";
//			break;
//		}
		
		return new Font(display, toFontData(fontSerialization));
	}

	public static FontData toFontData(Object fontSerialization) {
		
		return toFontData(fontSerialization.toString());
	}

	public static FontData toFontData(String fontSerialization) {
		
		String[] splitValues = fontSerialization.split(",");
		int index = 0;
		String name = "";
		int height = 0;
		int style = SWT.NORMAL;
		
		for (String splitValue : splitValues) {
			splitValue = splitValue.trim();
			switch (index) {
				case 0: 
					name = splitValue;
					break;
				case 1:
					height = Integer.valueOf(splitValue);
					break;
				case 2, 3:
					style = style | (splitValue.equalsIgnoreCase("bold") ? SWT.BOLD : SWT.ITALIC);
					break;
				default:
					/* ILB */
			}
			index++;
		}

		return new FontData(name, height, style);
	}

//	/** Color from RGB */
//	private static void loadColorToRegistry(String name, int red, int green, int blue) {
//		
//		colorRegistry.put(name, new Color(Display.getDefault(), red, green, blue));
//	}
	
//	/** Color by file name */
//	public static Color getColor(String name) {
//		return colorRegistry.get(name);
//	}

	/** Color by defined name */
	public static Color getColor(Colors colors) {
		return colorRegistry.get(colors);
	}

	/**
	 * @return the preference store
	 */
//	public static IPreferenceStore getPreferenceStore() {
//		return preferenceStore;
//	}
	
	/** Proposals */
	public static LinkedHashMap<MarkdownCompletionProposalKey, CommonCompletionProposal> getMarkdownCompletionProposals() {
		
		return markdownProposalsSupport.getProposals();
	}

	/** Proposals */
	public static LinkedHashMap<TomlCompletionProposalKey, TomlCompletionProposal> getTomlCompletionProposals() {
		
		return tomlProposalsSupport.getProposals();
	}
}
