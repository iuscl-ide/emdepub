package org.emdepub.ai_md.preferences;

import java.util.LinkedHashMap;
import java.util.Map;

import org.emdepub.common.utils.CU;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(Include.NON_NULL)
@FieldNameConstants(asEnum = true, innerTypeName = "AiMdPreferencesHtmlGeneratorField", level = AccessLevel.PUBLIC)
public class AiMdPreferencesHtmlGenerator {

	/* CSS styles */
	public static enum CssStyles { NONE, GIT_HUB, GOOGLE_LIKE, SEMANTIC_UI_LIKE, CUSTOM };

	public static final LinkedHashMap<CssStyles, String> cssStyleNames = new LinkedHashMap<>();
	static {
		cssStyleNames.put(CssStyles.NONE, "None");
		
		cssStyleNames.put(CssStyles.GIT_HUB, "GitHub");
		cssStyleNames.put(CssStyles.GOOGLE_LIKE, "Google Like");
		cssStyleNames.put(CssStyles.SEMANTIC_UI_LIKE, "SemanticUI Like");
		
		cssStyleNames.put(CssStyles.CUSTOM, "Custom");
	}

	public static final Map<CssStyles, String> cssStyleClasses = Map.of(
		CssStyles.NONE, "", CssStyles.GIT_HUB, "github-markdown", CssStyles.GOOGLE_LIKE, "google-like-markdown",
		CssStyles.SEMANTIC_UI_LIKE, "semantic-ui-like-markdown", CssStyles.CUSTOM, "stylesheet");
	
	/* Code styles */
	public static enum CodeStyles {AGATE, ANDROID_STUDIO, ARDUINO_LIGHT, DARK, DEFAULT, FAR, FOUNDATION,
		GITHUB_GIST, GITHUB, GOOGLE_CODE, GRAYSCALE, GRUVBOX_DARK, GRUVBOX_LIGHT, HYBRID, IDEA, IR_BLACK,
		MAGULA, PURE_BASIC, RAILS_CASTS, SUNBURST, VS, VS_2015, VSCODE, XCODE, CUSTOM, NONE };

	public static final LinkedHashMap<CodeStyles, String> codeStyleNames = new LinkedHashMap<>();
	static {
		codeStyleNames.put(CodeStyles.NONE, "None");
		codeStyleNames.put(CodeStyles.DEFAULT, "Default");

		codeStyleNames.put(CodeStyles.AGATE, "Agate");
		codeStyleNames.put(CodeStyles.ANDROID_STUDIO, "Android Studio");
		codeStyleNames.put(CodeStyles.ARDUINO_LIGHT, "Arduino Light");
		codeStyleNames.put(CodeStyles.DARK, "Dark");
		codeStyleNames.put(CodeStyles.FAR, "Far");
		codeStyleNames.put(CodeStyles.FOUNDATION, "Foundation");
		codeStyleNames.put(CodeStyles.GITHUB_GIST, "Github Gist");
		codeStyleNames.put(CodeStyles.GITHUB, "Github");
		codeStyleNames.put(CodeStyles.GOOGLE_CODE, "Googlecode");
		codeStyleNames.put(CodeStyles.GRAYSCALE, "Grayscale");
		codeStyleNames.put(CodeStyles.GRUVBOX_DARK, "Gruvbox Dark");
		codeStyleNames.put(CodeStyles.GRUVBOX_LIGHT, "Gruvbox Light");
		codeStyleNames.put(CodeStyles.HYBRID, "Hybrid");
		codeStyleNames.put(CodeStyles.IDEA, "Idea");
		codeStyleNames.put(CodeStyles.IR_BLACK, "Ir Black");
		codeStyleNames.put(CodeStyles.MAGULA, "Magula");
		codeStyleNames.put(CodeStyles.PURE_BASIC, "Purebasic");
		codeStyleNames.put(CodeStyles.RAILS_CASTS, "Railscasts");
		codeStyleNames.put(CodeStyles.SUNBURST, "Sunburst");
		codeStyleNames.put(CodeStyles.VS, "Vs");
		codeStyleNames.put(CodeStyles.VS_2015, "Vs2015");
		codeStyleNames.put(CodeStyles.VSCODE, "VSCode");
		codeStyleNames.put(CodeStyles.XCODE, "Xcode");

		codeStyleNames.put(CodeStyles.CUSTOM, "Custom");
	}

	public static final LinkedHashMap<CodeStyles, String> codeStyleClasses = new LinkedHashMap<>();
	static {
		codeStyleClasses.put(CodeStyles.NONE, null);
		codeStyleClasses.put(CodeStyles.DEFAULT, "default");
		
		codeStyleClasses.put(CodeStyles.AGATE, "agate");
		codeStyleClasses.put(CodeStyles.ANDROID_STUDIO, "androidstudio");
		codeStyleClasses.put(CodeStyles.ARDUINO_LIGHT, "arduino-light");
		codeStyleClasses.put(CodeStyles.DARK, "dark");
		codeStyleClasses.put(CodeStyles.FAR, "far");
		codeStyleClasses.put(CodeStyles.FOUNDATION, "foundation");
		codeStyleClasses.put(CodeStyles.GITHUB_GIST, "github-gist");
		codeStyleClasses.put(CodeStyles.GITHUB, "github");
		codeStyleClasses.put(CodeStyles.GOOGLE_CODE, "googlecode");
		codeStyleClasses.put(CodeStyles.GRAYSCALE, "grayscale");
		codeStyleClasses.put(CodeStyles.GRUVBOX_DARK, "gruvbox-dark");
		codeStyleClasses.put(CodeStyles.GRUVBOX_LIGHT, "gruvbox-light");
		codeStyleClasses.put(CodeStyles.HYBRID, "hybrid");
		codeStyleClasses.put(CodeStyles.IDEA, "idea");
		codeStyleClasses.put(CodeStyles.IR_BLACK, "ir-black");
		codeStyleClasses.put(CodeStyles.MAGULA, "magula");
		codeStyleClasses.put(CodeStyles.PURE_BASIC, "purebasic");
		codeStyleClasses.put(CodeStyles.RAILS_CASTS, "railscasts");
		codeStyleClasses.put(CodeStyles.SUNBURST, "sunburst");
		codeStyleClasses.put(CodeStyles.VS, "vs");
		codeStyleClasses.put(CodeStyles.VS_2015, "vs2015");
		codeStyleClasses.put(CodeStyles.VSCODE, "vscode");
		codeStyleClasses.put(CodeStyles.XCODE, "xcode");

		codeStyleClasses.put(CodeStyles.CUSTOM, "custom");
	}
	
	public static final LinkedHashMap<CodeStyles, String> codeStylePreValues = new LinkedHashMap<>();
	static {
		codeStylePreValues.put(CodeStyles.NONE, CU.ES);
		codeStylePreValues.put(CodeStyles.DEFAULT, "background: rgb(240, 240, 240);color: rgb(68, 68, 68);");
		
		codeStylePreValues.put(CodeStyles.AGATE, "background: rgb(51, 51, 51); color: white;");
		codeStylePreValues.put(CodeStyles.ANDROID_STUDIO, "background: rgb(40, 43, 46); color: rgb(169, 183, 198);");
		codeStylePreValues.put(CodeStyles.ARDUINO_LIGHT, "background: rgb(255, 255, 255); color: rgb(67, 79, 84);");
		codeStylePreValues.put(CodeStyles.DARK, "background: rgb(68, 68, 68); color: rgb(221, 221, 221);");
		codeStylePreValues.put(CodeStyles.FAR, "background: rgb(0, 0, 128); color: rgb(0, 255, 255);");
		codeStylePreValues.put(CodeStyles.FOUNDATION, "background: rgb(238, 238, 238); color: black;");
		codeStylePreValues.put(CodeStyles.GITHUB_GIST, "background: white; color: rgb(51, 51, 51);");
		codeStylePreValues.put(CodeStyles.GITHUB, "background: rgb(248, 248, 248); color: rgb(51, 51, 51);");
		codeStylePreValues.put(CodeStyles.GOOGLE_CODE, "background: white; color: black;");
		codeStylePreValues.put(CodeStyles.GRAYSCALE, "background: rgb(255, 255, 255); color: rgb(51, 51, 51);");
		codeStylePreValues.put(CodeStyles.GRUVBOX_DARK, "background: rgb(40, 40, 40); color: rgb(235, 219, 178);");
		codeStylePreValues.put(CodeStyles.GRUVBOX_LIGHT, "background: rgb(251, 241, 199); color: rgb(60, 56, 54);");
		codeStylePreValues.put(CodeStyles.HYBRID, "background: rgb(29, 31, 33); color: rgb(197, 200, 198);");
		codeStylePreValues.put(CodeStyles.IDEA, "background: rgb(255, 255, 255); color: rgb(0, 0, 0);");
		codeStylePreValues.put(CodeStyles.IR_BLACK, "background: rgb(0, 0, 0); color: rgb(248, 248, 248);");
		codeStylePreValues.put(CodeStyles.MAGULA, "color: black;");
		codeStylePreValues.put(CodeStyles.PURE_BASIC, "background: rgb(255, 255, 223); color: rgb(0, 0, 0);");
		codeStylePreValues.put(CodeStyles.RAILS_CASTS, "background: rgb(35, 35, 35); color: rgb(230, 225, 220);");
		codeStylePreValues.put(CodeStyles.SUNBURST, "background: rgb(0, 0, 0); color: rgb(248, 248, 248);");
		codeStylePreValues.put(CodeStyles.VS, "background: white; color: black;");
		codeStylePreValues.put(CodeStyles.VS_2015, "background: rgb(30, 30, 30); color: rgb(220, 220, 220);");
		codeStylePreValues.put(CodeStyles.VSCODE, "background: black; color: lemonchiffon;");
		codeStylePreValues.put(CodeStyles.XCODE, "background: rgb(255, 255, 255); color: black;");

		codeStylePreValues.put(CodeStyles.CUSTOM, "background: white; color: black;");
	}
	
	@JsonProperty("CSS Style")
	String cssStyle;

	@JsonProperty("Code Style")
	String codeStyle;
	
	@JsonProperty("Fixed Content Width")
	Boolean fixedContentWidth;
	
	@JsonProperty("Justified Paragraphs")
	Boolean justifiedParagraphs;
	
	@JsonProperty("Center Headers")
	Boolean centerHeaders;

	@JsonProperty("Center Tables")
	Boolean centerTables;
	
	@JsonProperty("Percent Width Tables")
	Integer percentWidthTables;
	
	@JsonProperty("Not Striped Tables")
	Boolean noStripedTables;

	private AiMdPreferencesHtmlGenerator() {
		super();
	}

	public static AiMdPreferencesHtmlGenerator create() {
		
		AiMdPreferencesHtmlGenerator aiMdPreferencesHtmlGenerator = new AiMdPreferencesHtmlGenerator();
		aiMdPreferencesHtmlGenerator.reset();
		
		return aiMdPreferencesHtmlGenerator;
	}

	public String findDefaultCssStyle() {
		return "GIT_HUB";
	}

	public String findDefaultCodeStyle() {
		return "GITHUB";
	}

	public Boolean findDefaultFixedContentWidth() {
		return true;
	}

	public Boolean findDefaultJustifiedParagraphs() {
		return true;
	}

	public Boolean findDefaultCenterHeaders() {
		return false;
	}

	public Boolean findDefaultCenterTables() {
		return true;
	}

	public Integer findDefaultPercentWidthTables() {
		return 75;
	}

	public Boolean findDefaultNoStripedTables() {
		return false;
	}

	public void reset() {

		AiMdPreferences.reset(this, AiMdPreferencesHtmlGeneratorField.values());
	}

	public void load(String preferencesFileName) {
		
		if (!CU.findIfFileExists(preferencesFileName)) {
			reset();
			
			return;
		}

		AiMdPreferencesHtmlGenerator deserialized = CU.yamlDeserializeFromFile(preferencesFileName, AiMdPreferencesHtmlGenerator.class);

		AiMdPreferences.load(this, AiMdPreferencesHtmlGeneratorField.values(), deserialized);
	}
	
	public void save(String preferencesFileName) {
		
		AiMdPreferencesHtmlGenerator serialized = new AiMdPreferencesHtmlGenerator();
		
		AiMdPreferences.save(this, AiMdPreferencesHtmlGeneratorField.values(), serialized, preferencesFileName);
	}
}
