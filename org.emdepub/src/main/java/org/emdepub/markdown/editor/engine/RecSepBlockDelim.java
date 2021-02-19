/* Record Separator Block Delimiter */
package org.emdepub.markdown.editor.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Record Separator Block Delimiter format engine */
public abstract class RecSepBlockDelim {

	/** To find the separator and the delimiter */
	private static Pattern enterPattern = Pattern.compile("(?m)^.*$");

	/** Must implement specific loadFields */
	public RecSepBlockDelim(String text) {
		super();
		
		loadFromString(text);
	}

	/** Engine */
	private void loadFromString(String rsmdlText) {
		
		Matcher recordsSeparatorMatcher = enterPattern.matcher(rsmdlText);
		recordsSeparatorMatcher.find();
		String recordsSeparator = recordsSeparatorMatcher.group();
		recordsSeparatorMatcher.find();
		String blocksDelimiter = recordsSeparatorMatcher.group();
		
		String[] records = rsmdlText.split(recordsSeparator + "\\R");
		
		for (int index = 2; index < records.length; index++) {
			
			ArrayList<String> fields = new ArrayList<>();
			
			String[] blocks = records[index].split(blocksDelimiter + "\\R");
			boolean multiline = false;
			for (int jndex = 0; jndex < blocks.length; jndex++) {
				String block = blocks[jndex];
				if (multiline) {
					fields.add(block);
				}
				else {
					if (block.trim().length() > 0) {
						Collections.addAll(fields, block.split("\\R"));	
					}
				}
				multiline = !multiline;
			}

			loadFields(fields);
		}
	}
	
	/** To implement */
	public abstract void loadFields(ArrayList<String> fields);
}
