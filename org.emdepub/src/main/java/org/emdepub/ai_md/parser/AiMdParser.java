/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.ai_md.parser;

import java.util.Arrays;

import org.emdepub.ai_md.parser.ext.references.AiMdReferencesExtension;

import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

/** Flexmark stuff Markdown editor engine */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AiMdParser {
	
	/** Parser options */
	@Getter
	static MutableDataSet parserOptions = new MutableDataSet();
	static {
		parserOptions.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(), TaskListExtension.create(),
				AiMdReferencesExtension.create()));	
	}
	
	/** Markdown parser */
	@Getter
	static Parser parser = Parser.builder(parserOptions).build();
}
