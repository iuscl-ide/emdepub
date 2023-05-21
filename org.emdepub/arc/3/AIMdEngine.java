package org.emdepub.ai_md.engine;

import java.util.UUID;

import org.eclipse.swt.internal.ole.win32.GUID;
import org.emdepub.activator.L;
import org.emdepub.ai_md.model.AIMdReferenceText;
import org.emdepub.ai_md.model.AIMdReferences;

public class AIMdEngine {

	/** Generate ePub */
//	@SneakyThrows(IOException.class)
	public static void generate(String aiMarkdownText) {
	
		L.i(aiMarkdownText);
	}
	
	public static AIMdReferences generateReferences(String aiMarkdownText) {
		
		L.i("generateReferences");
		
		AIMdReferences references = new AIMdReferences();

		final int referenceLine[] = { 0 };
		final AIMdReferenceText aiMdReferenceText[] = { null };
		// TODO problems
		aiMarkdownText.lines().forEachOrdered(line -> {
			
			referenceLine[0]++;
			String sline = line.trim().strip();
			if (sline.startsWith("● ai")) {
				aiMdReferenceText[0] = new AIMdReferenceText();
				aiMdReferenceText[0].setId(UUID.randomUUID().toString().substring(0, 8));
				aiMdReferenceText[0].setReferenceLine(referenceLine[0]);
			}
			if (sline.endsWith("▶")) {
				aiMdReferenceText[0].setTextStartLine(referenceLine[0]);
			}
			if (sline.equals("◀")) {
				aiMdReferenceText[0].setTextEndLine(referenceLine[0]);
				references.getReferences().add(aiMdReferenceText[0]);
			}
		});
		
		L.i("generateReferences " + references);
		
		return references;
	}

}
