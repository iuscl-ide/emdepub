/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.ui.editor.md.engine;

import java.util.Arrays;

import org.emdepub.ui.editor.md.language.MarkdownOutlineNode;
import org.emdepub.ui.editor.md.prefs.MarkdownPreferences;
import org.emdepub.ui.editor.md.prefs.MarkdownPreferences.PreferenceNames;

import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.formatter.Formatter;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.util.sequence.LineAppendable;

/** Flexmark stuff Markdown editor engine */
public class MarkdownEditorEngine {
	
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
	
	/** Actual outline reconcile */
	public static MarkdownOutlineNode updateDocumentOutline(String markdownString) {

		MarkdownOutlineEngine markdownVisitorsEngine = new MarkdownOutlineEngine();
		
		return markdownVisitorsEngine.runOutline(markdownString);
	}

	public static Parser getParser() {
		return parser;
	}
	
	
}
