/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.ui.editor.md.engine;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import com.vladsch.flexmark.Extension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.formatter.internal.Formatter;
import com.vladsch.flexmark.parser.Parser;

public class MarkdownEditorEngine {

	private static final List<Extension> extensions = Arrays.asList(
//			AbbreviationExtension.create(),
//			AsideExtension.create(),
//			FootnoteExtension.create(),
//			AutolinkExtension.create(),
//			DefinitionExtension.create(),
//			StrikethroughSubscriptExtension.create(),
//			InsExtension.create(),
//			JekyllFrontMatterExtension.create(),
//			SuperscriptExtension.create(),
			TablesExtension.create()
			//,
//			TypographicExtension.create(),
//			TocExtension.create(),
//			EmojiExtension.create(),
			);
//			MarkdownSemanticEPRenderExtension.create());
	
	/** Markdown parser */
	private final static Parser parser = Parser.builder().extensions(extensions).build();
	//private final static Parser parser = Parser.builder().build();
	
	/** Markdown formatter */
	private final static Formatter formatter = Formatter.builder().extensions(extensions).build();
	//private final static Formatter formatter = Formatter.builder().build();

	/** Format */
	public static String formatMarkdown(String markdownString) {
		
		return formatter.render(parser.parse(markdownString));
	}
	
	public static enum SpecialFormattingOptions {
		ApplyToSelection,
		RepairParagraphs,
		RepairParagraphsSmart,
		PutOneEmptyLineBetweenParagraphs,
		PutTwoSpacesBeforeSentence,
		Create80Columns,
		Create60Columns,
		RemoveSpoilerLines,
		RemoveEmptyLines,
		RemoveSpoilerSpaces
	};

	/** Constructor */
	public static LinkedHashMap<SpecialFormattingOptions, Boolean> createSpecialFormattingOptions() {

		LinkedHashMap<SpecialFormattingOptions, Boolean> formattingOptions = new LinkedHashMap<>();

		formattingOptions.put(SpecialFormattingOptions.ApplyToSelection, true);
		formattingOptions.put(SpecialFormattingOptions.RepairParagraphs, false);
		formattingOptions.put(SpecialFormattingOptions.RepairParagraphsSmart, false);
		formattingOptions.put(SpecialFormattingOptions.PutOneEmptyLineBetweenParagraphs, false);
		formattingOptions.put(SpecialFormattingOptions.PutTwoSpacesBeforeSentence, false);
		formattingOptions.put(SpecialFormattingOptions.Create80Columns, false);
		formattingOptions.put(SpecialFormattingOptions.Create60Columns, false);
		formattingOptions.put(SpecialFormattingOptions.RemoveSpoilerLines, false);
		formattingOptions.put(SpecialFormattingOptions.RemoveEmptyLines, false);
		formattingOptions.put(SpecialFormattingOptions.RemoveSpoilerSpaces, false);

		return formattingOptions;
	}

	/** Special formatting */
	public static String doSpecialFormatting(String selection, LinkedHashMap<SpecialFormattingOptions, Boolean> formattingOptions, String enter) {
		
		String formattedSelection = selection;

		if (formattingOptions.get(SpecialFormattingOptions.RepairParagraphs)) {
			formattedSelection = repairBrokenParagraph(formattedSelection, enter,
				formattingOptions.get(SpecialFormattingOptions.RepairParagraphsSmart),
				formattingOptions.get(SpecialFormattingOptions.PutOneEmptyLineBetweenParagraphs));
		}
		
		if (formattingOptions.get(SpecialFormattingOptions.Create80Columns)) {
			formattedSelection = formatColumns(formattedSelection, enter, 80);
		}
		if (formattingOptions.get(SpecialFormattingOptions.Create60Columns)) {
			formattedSelection = formatColumns(formattedSelection, enter, 60);
		}

		if (formattingOptions.get(SpecialFormattingOptions.RemoveSpoilerLines)) {
			formattedSelection = deleteSpoilerLines(formattedSelection, enter);
		}
		if (formattingOptions.get(SpecialFormattingOptions.RemoveEmptyLines)) {
			formattedSelection = deleteEmptyLines(formattedSelection, enter);
		}
		if (formattingOptions.get(SpecialFormattingOptions.RemoveSpoilerSpaces)) {
			formattedSelection = deleteSpoilerSpaces(formattedSelection, enter);
		}
		
		return formattedSelection;
	}
	
	/** Repair broken paragraph */
	private static String repairBrokenParagraph(String text, String enter, boolean smart, boolean doubleEnter) {
		
		String one = Character.valueOf((char) 1) + "";
		
		/* Double enter */
		text = text.replace(enter + enter, one + one);
		text = text.replace(one + enter, one + one);
		
		if (smart) {
			String smartOne = one;
			if (doubleEnter) {
				smartOne = one + one;
			}
			/* Sign and enter */
			text = text.replace("\"" + enter, "\"" + smartOne);
			text = text.replace("'" + enter, "'" + smartOne);
			
			text = text.replace("-" + enter, "-" + smartOne);
			text = text.replace("_" + enter, "_" + smartOne);
			
			text = text.replace("." + enter, "." + smartOne);
			text = text.replace("?" + enter, "?" + smartOne);
			text = text.replace("!" + enter, "!" + smartOne);
			
			/* Enter and sign */
			text = text.replace(enter + "1", smartOne + "1");
			text = text.replace(enter + "2", smartOne + "2");
			text = text.replace(enter + "3", smartOne + "3");
			text = text.replace(enter + "4", smartOne + "4");
			text = text.replace(enter + "5", smartOne + "5");
			text = text.replace(enter + "6", smartOne + "6");
			text = text.replace(enter + "7", smartOne + "7");
			text = text.replace(enter + "8", smartOne + "8");
			text = text.replace(enter + "9", smartOne + "9");
			text = text.replace(enter + "0", smartOne + "0");
			
			text = text.replace(enter + "\"", smartOne + "\"");
			text = text.replace(enter + "'", smartOne + "'");
			text = text.replace(enter + "-", smartOne + "-");
			text = text.replace(enter + "_", smartOne + "_");
		}

		text = deleteSpacesAfterEnter(text, enter);

		/* Main replace */
		text = text.replace(enter, " ");
		text = text.replace(one, enter);

		return text;
	}

	/** Remove spoiler lines */
	private static String doubleEnter(String text, String enter) {
		
		text = deleteSpacesAfterEnter(text, enter);
		
		String reference;
		do {
			reference = text;
			text = text.replace(enter + enter + enter, enter + enter);
		}
		while (!reference.equals(text));
		
		return text;
	}

	
	/** Format columns number */
	private static String formatColumns(String text, String enter, int numberOfColumns) {
		
		Character one = Character.valueOf((char) 1);
		text = text.replace(enter, one + "");
		StringBuffer stringBuffer = new StringBuffer();
		
		char[] chars = text.toCharArray();
		boolean endOfString = false;
		int start = 0;
		int end = start;
		int charsWidth = numberOfColumns;
				
		while (start < chars.length - 1) {
		    int charCount = 0;
		    int lastSpace = 0;
		    while (charCount < charsWidth) {
		    	if (chars[charCount + start] == one) {
		        	lastSpace = charCount;
		            break;
		        }
		    	if (chars[charCount + start] == ' ') {
		            lastSpace = charCount;
		        }
		        charCount++;
		        if (charCount + start == text.length()) {
		            endOfString = true;
		            break;
		        }
		    }
		    if (endOfString) {
		    	end = text.length();
		    }
		    else {
		    	if (lastSpace > 0) {
		    		end = lastSpace + start;
		    	}
		    	else {
		    		end = charCount + start;
		    	}
		    }
		    stringBuffer.append(text.substring(start, end) + enter);
		    start = end;
		    if (end < chars.length) {
		    	if ((chars[end] == ' ') || (chars[end] == one)) {
		    		start = end + 1;
		    	}
		    }
		}

		return stringBuffer.toString();
	}

	/** Remove spaces after enter */
	private static String deleteSpacesAfterEnter(String text, String enter) {
		
		String reference;
		do {
			reference = text;
			text = text.replace(enter + " ", enter);
		}
		while (!reference.equals(text));
		/* At the start */
		do {
			reference = text;
			text = text.replace(" " + enter, enter);
		}
		while (!reference.equals(text));
		
		return text;
	}
	
	/** Remove spoiler lines */
	private static String deleteSpoilerLines(String text, String enter) {
		
		text = deleteSpacesAfterEnter(text, enter);
		
		String reference;
		do {
			reference = text;
			text = text.replace(enter + enter + enter, enter + enter);
		}
		while (!reference.equals(text));
		
		return text;
	}

	/** Remove empty lines */
	private static String deleteEmptyLines(String text, String enter) {
		
		text = deleteSpacesAfterEnter(text, enter);
		
		String reference;
		do {
			reference = text;
			text = text.replace(enter + enter, enter);
		}
		while (!reference.equals(text));
		
		return text;
	}

	/** Remove spoiler spaces */
	private static String deleteSpoilerSpaces(String text, String enter) {

		text = deleteSpacesAfterEnter(text, enter);

		String reference;
		do {
			reference = text;
			text = text.replace("  ", " ");
		}
		while (!reference.equals(text));
		
		return text;
	}
}
