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
//		Arta,
//		Ascetic,
//		AtelierCaveDark,
//		AtelierCaveLight,
//		AtelierDuneDark,
//		AtelierDuneLight,
//		AtelierEstuaryDark,
//		AtelierEstuaryLight,
//		AtelierForestDark,
//		AtelierForestLight,
//		AtelierHeathDark,
//		AtelierHeathLight,
//		AtelierLakesideDark,
//		AtelierLakesideLight,
//		AtelierPlateauDark,
//		AtelierPlateauLight,
//		AtelierSavannaDark,
//		AtelierSavannaLight,
//		AtelierSeasideDark,
//		AtelierSeasideLight,
//		AtelierSulphurpoolDark,
//		AtelierSulphurpoolLight,
//		AtomOneDark,
//		AtomOneLight,
//		BrownPaper,
//		CodepenEmbed,
//		ColorBrewer,
//		Darcula,
		Dark,
//		Darkula,
		Default,
//		Docco,
//		Dracula,
		Far,
		Foundation,
		GithubGist,
		Github,
		Googlecode,
		Grayscale,
		GruvboxDark,
		GruvboxLight,
//		Hopscotch,
		Hybrid,
		Idea,
		IrBlack,
//		KimbieDark,
//		KimbieLight,
		Magula,
//		MonoBlue,
//		MonokaiSublime,
//		Monokai,
//		Obsidian,
//		Ocean,
//		ParaisoDark,
//		ParaisoLight,
//		Pojoaque,
		Purebasic,
//		Qtcreator_dark,
//		Qtcreator_light,
		Railscasts,
//		Rainbow,
//		Routeros,
//		SchoolBook,
//		SolarizedDark,
//		SolarizedLight,
		Sunburst,
//		TomorrowNightBlue,
//		TomorrowNightBright,
//		TomorrowNightEighties,
//		TomorrowNight,
//		Tomorrow,
		Vs,
		Vs2015,
		Xcode,
//		Xt256,
//		Zenburn
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

	/** Serialization */
	public LinkedHashMap<Pref, Object> getSerialization() {
		
		LinkedHashMap<Pref, Object> serialization = new LinkedHashMap<>();
		
		for (Entry<Pref, Object> preference : preferences.entrySet()) {
			Pref preferenceKey = preference.getKey();
			Object preferenceValue = preference.getValue();
			if (!preferenceValue.equals(initialPreferences.get(preferenceKey))) {
				serialization.put(preferenceKey, preferenceValue);
			}
		}
		
		return serialization;
	}

	/** Serialization */
	public void setSerialization(LinkedHashMap<Pref, Object> serialization) {
		
		for (Entry<Pref, Object> entry : serialization.entrySet()) {
			preferences.replace(entry.getKey(), entry.getValue());	
		}
	}

	/** Save */
	public void saveProperties(String fileNameWithPath) {
		
		LinkedHashMap<Pref, Object> modifieds = getSerialization();
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
	
	
//	public LinkedHashMap<FormatOption, Object> getPreferences() {
//		return preferences;
//	}

	@SuppressWarnings("unchecked")
	public <T> T get(Pref preferenceKey) {
		
		return (T) preferences.get(preferenceKey);
	}

	public void set(Pref preferenceKey, Object preferenceValue) {
		
		preferences.replace(preferenceKey, preferenceValue);
	}
}
