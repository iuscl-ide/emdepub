/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.markdown.editor.engine;

import java.util.Arrays;

import org.emdepub.common.utils.CU;
import org.emdepub.markdown.editor.language.MarkdownOutlineNode;
import org.emdepub.markdown.editor.preferences.MarkdownPreferences;
import org.emdepub.markdown.editor.preferences.MarkdownPreferences.PreferenceNames;

import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.formatter.Formatter;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.util.sequence.LineAppendable;

/** Flexmark stuff Markdown editor engine */
public class MarkdownFormatterEngine {
	
	/** Parser options */
	private static MutableDataSet parserOptions = new MutableDataSet();
	static {
		parserOptions.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(), TaskListExtension.create()));	
	}
	
	/** Markdown parser */
	private final static Parser parser = Parser.builder(parserOptions).build();
	
	/** Formatter options */
	private static MutableDataSet formatterOptions = new MutableDataSet();
	static {
		formatterOptions.setFrom(parserOptions);
		formatterOptions.set(Formatter.FENCED_CODE_SPACE_BEFORE_INFO, Boolean.valueOf(true));
	}
	private static int formatFlagsOptions = 0;
	static {
		formatFlagsOptions = formatFlagsOptions | LineAppendable.F_TRIM_TRAILING_WHITESPACE;	
	}

	
	/** Format */
	public static String formatMarkdown(String markdownString, MarkdownPreferences markdownPreferences) {

		MutableDataSet formatterCustomOptions = MutableDataSet.merge(formatterOptions);

		int formatFlags = formatFlagsOptions;

		if (markdownPreferences != null) {
			if (markdownPreferences.<Boolean>get(PreferenceNames.SourceFormatCollapseWhitespace)) {
				formatFlags = formatFlags | LineAppendable.F_COLLAPSE_WHITESPACE;
			}

			if (markdownPreferences.<Boolean>get(PreferenceNames.SourceFormatRightMarginWrap)) {
				formatterCustomOptions.set(Formatter.RIGHT_MARGIN, markdownPreferences.<Integer>get(PreferenceNames.SourceFormatRightMarginColumns));
			}
		}
		if (formatFlags != 0) {
			formatterCustomOptions.set(Formatter.FORMAT_FLAGS, formatFlags);	
		}

		Formatter formatter = Formatter.builder(formatterCustomOptions).build();
		
		return formatter.render(parser.parse(markdownString));
	}

	/** Format */
	public static String repairBrokenText(String text) {

		String enter = "\r\n";
		if (!text.contains(enter)) {
			enter = "\r";
			if (!text.contains(enter)) {
				enter = "\n";
				if (!text.contains(enter)) {
					enter = CU.E;				
				}
			}
		}
		
		String one = Character.valueOf((char) 1) + "";
		
		/* Double enter */
		text = text.replace(enter + enter, one + one);
		text = text.replace(one + enter, one + one);
		
		/* Sign and enter */
		text = text.replace("\"" + enter, "\"" + one);
		text = text.replace("'" + enter, "'" + one);
		
		text = text.replace("-" + enter, "-" + one);
		text = text.replace("_" + enter, "_" + one);
		
		text = text.replace("." + enter, "." + one);
		text = text.replace("?" + enter, "?" + one);
		text = text.replace("!" + enter, "!" + one);
		
		/* Enter and sign */
		text = text.replace(enter + "1", one + "1");
		text = text.replace(enter + "2", one + "2");
		text = text.replace(enter + "3", one + "3");
		text = text.replace(enter + "4", one + "4");
		text = text.replace(enter + "5", one + "5");
		text = text.replace(enter + "6", one + "6");
		text = text.replace(enter + "7", one + "7");
		text = text.replace(enter + "8", one + "8");
		text = text.replace(enter + "9", one + "9");
		text = text.replace(enter + "0", one + "0");
		
		text = text.replace(enter + "\"", one + "\"");
		text = text.replace(enter + "'", one + "'");
		text = text.replace(enter + "-", one + "-");
		text = text.replace(enter + "_", one + "_");

		if (enter.endsWith("\n")) {
			text = text.replaceAll("\n[\\s]*", "\n");	
		}
		else {
			text = text.replaceAll("\r[\\s]*", "\r");
		}
		
		/* Main replace */
		text = text.replace(enter, ' ' + "");
		text = text.replace(one, enter);

		return text;
	}

	/** Actual outline reconcile */
	public static MarkdownOutlineNode updateDocumentOutline(String markdownString) {

		MarkdownOutlineEngine markdownVisitorsEngine = new MarkdownOutlineEngine();
		
		return markdownVisitorsEngine.runOutline(markdownString);
	}

	public static Parser getParser() {
		return parser;
	}
	
	
}
