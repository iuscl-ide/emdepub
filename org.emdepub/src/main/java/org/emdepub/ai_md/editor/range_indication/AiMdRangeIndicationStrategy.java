package org.emdepub.ai_md.editor.range_indication;

import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.widgets.Display;
import org.emdepub.ai_md.parser.AiMdParser;
import org.emdepub.ai_md.parser.ext.references.AiMdReferenceBlock;

import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import com.vladsch.flexmark.util.ast.VisitHandler;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AiMdRangeIndicationStrategy {

	NavigableMap<Integer, Integer> rangesMap = new TreeMap<>();

	/** Linearize the AIs */
	NodeVisitor nodeVisitor = new NodeVisitor(
		new VisitHandler<AiMdReferenceBlock>(AiMdReferenceBlock.class, aiMdReferenceBlock -> {
			rangesMap.put(aiMdReferenceBlock.getHeaderEndLineNumber(),aiMdReferenceBlock.getTextEndLineNumber());
		})
	);
	
	IDocument document;

	ISourceViewer sourceViewer;
	
	public void changeRangeIndication(int editorLine) {
		
		Display.getCurrent().asyncExec(() -> {
			findRangeIndication(editorLine);
		});
	}

	@SneakyThrows(BadLocationException.class)
	public void findRangeIndication(int editorLine) {

		sourceViewer.removeRangeIndication();
		
		/* Parse document in a node */
		Node documentNode = AiMdParser.getParser().parse(document.get());
		rangesMap.clear();
		nodeVisitor.visitChildren(documentNode);

		Entry<Integer, Integer> lineUpEntry = rangesMap.lowerEntry(editorLine);
		if (lineUpEntry == null) {
			return;
		}
		if (editorLine >= lineUpEntry.getValue()) {
			return;
		}
		
		int startOffset = document.getLineOffset(lineUpEntry.getKey() + 1);
		int length = document.getLineOffset(lineUpEntry.getValue()) - startOffset;
		sourceViewer.setRangeIndication(startOffset, length, false); 
	}
}
