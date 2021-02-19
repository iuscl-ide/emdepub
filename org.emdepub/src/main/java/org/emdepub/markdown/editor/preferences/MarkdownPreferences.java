/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.markdown.editor.preferences;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.emdepub.activator.F;
import org.emdepub.activator.P;

/** Markdown preferences */
public class MarkdownPreferences {

	public static enum PreferenceNames {
		DisplayFormatStyle,
		DisplayFixedContentWidth,
		DisplayJustifiedParagraphs,
		DisplayCenterHeaders,
		DisplayFormatCodeStyle,
		SourceFormatRightMarginWrap,
		SourceFormatRightMarginColumns,
		SourceFormatCollapseWhitespace
	};
	
	public static enum DisplayFormatStyles { None, GitHub, GoogleLike, SemanticUILike, Custom };
	public static enum DisplayFormatCodeStyles { None,
		Agate,
		Androidstudio,
		ArduinoLight,
		Dark,
		Default,
		Far,
		Foundation,
		GithubGist,
		Github,
		Googlecode,
		Grayscale,
		GruvboxDark,
		GruvboxLight,
		Hybrid,
		Idea,
		IrBlack,
		Magula,
		Purebasic,
		Railscasts,
		Sunburst,
		Vs,
		Vs2015,
		VSCode,
		Xcode,
		Custom
	};

	public static final Set<PreferenceNames> DisplayFormatOptions = Set.of( PreferenceNames.DisplayFixedContentWidth, PreferenceNames.DisplayJustifiedParagraphs, PreferenceNames.DisplayCenterHeaders );

	private final LinkedHashMap<PreferenceNames, Object> initialPreferences = new LinkedHashMap<>();
	{
		initialPreferences.put(PreferenceNames.DisplayFormatStyle, DisplayFormatStyles.GitHub);
		
		initialPreferences.put(PreferenceNames.DisplayFixedContentWidth, Boolean.valueOf(true));
		initialPreferences.put(PreferenceNames.DisplayJustifiedParagraphs, Boolean.valueOf(true));
		initialPreferences.put(PreferenceNames.DisplayCenterHeaders, Boolean.valueOf(false));
		
		initialPreferences.put(PreferenceNames.DisplayFormatCodeStyle, DisplayFormatCodeStyles.Github);

		initialPreferences.put(PreferenceNames.SourceFormatRightMarginWrap, Boolean.valueOf(false));
		initialPreferences.put(PreferenceNames.SourceFormatRightMarginColumns, Integer.valueOf(80));
		initialPreferences.put(PreferenceNames.SourceFormatCollapseWhitespace, Boolean.valueOf(false));
	}
	
	private final LinkedHashMap<PreferenceNames, Object> preferences = new LinkedHashMap<>(initialPreferences);

	/** Save */
	public void saveProperties(String fileNameWithPath) {

		LinkedHashMap<PreferenceNames, Object> modifieds = new LinkedHashMap<>();
		
		for (Entry<PreferenceNames, Object> preference : preferences.entrySet()) {
			PreferenceNames preferenceKey = preference.getKey();
			Object preferenceValue = preference.getValue();
			if (!preferenceValue.equals(initialPreferences.get(preferenceKey))) {
				modifieds.put(preferenceKey, preferenceValue);
			}
		}
		if (modifieds.size() == 0) {
			F.deleteFile(fileNameWithPath);
			return;
		}
		
		Properties properties = new Properties();
		for (Entry<PreferenceNames, Object> entry : modifieds.entrySet()) {
			properties.put(entry.getKey().name(), entry.getValue().toString());
		}
		P.savePropertiesInFile(properties, "Preferences for displaying Markdown rendering; Emdepub Eclipse Plugin - emdepub.org", fileNameWithPath);
	}
	
	/** Load */
	public void loadProperties(String fileNameWithPath) {
		
		if (!F.fileExists(fileNameWithPath)) {
			return;
		}

		LinkedHashMap<PreferenceNames, Object> modifieds = new LinkedHashMap<>();
		Properties properties = P.loadPropertiesFromFile(fileNameWithPath);

		for (Entry<Object, Object> entry : properties.entrySet()) {
			String propertyKey = (String) entry.getKey();
			String propertyValue = (String) entry.getValue();
			
			PreferenceNames pref = PreferenceNames.valueOf(propertyKey);
			switch (pref) {
			case DisplayFormatStyle:
				modifieds.put(pref, DisplayFormatStyles.valueOf(propertyValue));
				break;
			case DisplayCenterHeaders:
			case DisplayFixedContentWidth:
			case DisplayJustifiedParagraphs:
				modifieds.put(pref, Boolean.parseBoolean(propertyValue));
				break;
			case DisplayFormatCodeStyle:
				modifieds.put(pref, DisplayFormatCodeStyles.valueOf(propertyValue));
				break;

			case SourceFormatRightMarginWrap:
			case SourceFormatCollapseWhitespace:
				modifieds.put(pref, Boolean.parseBoolean(propertyValue));
				break;
			case SourceFormatRightMarginColumns:
				modifieds.put(pref, Integer.valueOf(propertyValue));
				break;
			}
		}

		for (Entry<PreferenceNames, Object> entry : modifieds.entrySet()) {
			preferences.replace(entry.getKey(), entry.getValue());	
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T get(PreferenceNames preferenceKey) {
		
		return (T) preferences.get(preferenceKey);
	}

	public void set(PreferenceNames preferenceKey, Object preferenceValue) {
		
		preferences.replace(preferenceKey, preferenceValue);
	}
}
