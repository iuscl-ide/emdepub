/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.ui.editor.md.prefs;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.emdepub.activator.F;
import org.emdepub.activator.P;

public class MarkdownHtmlGeneratorPrefs {

	public static enum Pref { FormatStyle, FixedContentWidth, JustifiedParagraphs, CenterHeaders, FormatCodeStyle };
	
	public static enum FormatStyle { None, GitHub, GoogleLike, SemanticUILike, Custom };
	public static enum FormatCodeStyle { None,
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
		Xcode,
		Custom
	};

	public static final Set<Pref> FormatOption = Set.of( Pref.FixedContentWidth, Pref.JustifiedParagraphs, Pref.CenterHeaders );

	private final LinkedHashMap<Pref, Object> initialPreferences = new LinkedHashMap<>();
	{
		initialPreferences.put(Pref.FormatStyle, FormatStyle.GitHub);
		
		initialPreferences.put(Pref.FixedContentWidth, true);
		initialPreferences.put(Pref.JustifiedParagraphs, true);
		initialPreferences.put(Pref.CenterHeaders, false);
		
		initialPreferences.put(Pref.FormatCodeStyle, FormatCodeStyle.Github);
	}
	
	private final LinkedHashMap<Pref, Object> preferences = new LinkedHashMap<>(initialPreferences);

//	/** Serialization */
//	private LinkedHashMap<Pref, Object> getSerialization() {
//		
//		LinkedHashMap<Pref, Object> serialization = new LinkedHashMap<>();
//		
//		for (Entry<Pref, Object> preference : preferences.entrySet()) {
//			Pref preferenceKey = preference.getKey();
//			Object preferenceValue = preference.getValue();
//			if (!preferenceValue.equals(initialPreferences.get(preferenceKey))) {
//				serialization.put(preferenceKey, preferenceValue);
//			}
//		}
//		
//		return serialization;
//	}

	/** Save */
	public void saveProperties(String fileNameWithPath) {

		LinkedHashMap<Pref, Object> modifieds = new LinkedHashMap<>();
		
		for (Entry<Pref, Object> preference : preferences.entrySet()) {
			Pref preferenceKey = preference.getKey();
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
		for (Entry<Pref, Object> entry : modifieds.entrySet()) {
			properties.put(entry.getKey().name(), entry.getValue().toString());
		}
		P.savePropertiesInFile(properties, "Preferences for displaying Markdown rendering; Emdepub Eclipse Plugin - emdepub.org", fileNameWithPath);
	}
	
//	/** Serialization */
//	private void setSerialization(LinkedHashMap<Pref, Object> serialization) {
//		
//		for (Entry<Pref, Object> entry : serialization.entrySet()) {
//			preferences.replace(entry.getKey(), entry.getValue());	
//		}
//	}

	/** Load */
	public void loadProperties(String fileNameWithPath) {
		
		if (!F.fileExists(fileNameWithPath)) {
			return;
		}

		LinkedHashMap<Pref, Object> modifieds = new LinkedHashMap<>();
		Properties properties = P.loadPropertiesFromFile(fileNameWithPath);

		for (Entry<Object, Object> entry : properties.entrySet()) {
			String propertyKey = (String) entry.getKey();
			String propertyValue = (String) entry.getValue();
			
			Pref pref = Pref.valueOf(propertyKey);
			switch (pref) {
			case FormatStyle:
				modifieds.put(pref, FormatStyle.valueOf(propertyValue));
				break;
			case CenterHeaders:
			case FixedContentWidth:
			case JustifiedParagraphs:
				modifieds.put(pref, Boolean.parseBoolean(propertyValue));
				break;
			case FormatCodeStyle:
				modifieds.put(pref, FormatCodeStyle.valueOf(propertyValue));
				break;
			}
		}

		for (Entry<Pref, Object> entry : modifieds.entrySet()) {
			preferences.replace(entry.getKey(), entry.getValue());	
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T get(Pref preferenceKey) {
		
		return (T) preferences.get(preferenceKey);
	}

	public void set(Pref preferenceKey, Object preferenceValue) {
		
		preferences.replace(preferenceKey, preferenceValue);
	}
}
