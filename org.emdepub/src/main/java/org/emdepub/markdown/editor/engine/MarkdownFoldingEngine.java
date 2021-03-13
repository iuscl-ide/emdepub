/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.markdown.editor.engine;

import java.util.ArrayList;

import org.eclipse.jface.text.Position;

import com.vladsch.flexmark.ast.FencedCodeBlock;
import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.ast.IndentedCodeBlock;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import com.vladsch.flexmark.util.ast.VisitHandler;
import com.vladsch.flexmark.util.ast.Visitor;

/** Markdown visitors engine */
public class MarkdownFoldingEngine {

	private ArrayList<Heading> linearHeadings = new ArrayList<>();
	private ArrayList<FencedCodeBlock> linearFencedCodeBlocks = new ArrayList<>();
	private ArrayList<IndentedCodeBlock> indentedCodeBlocks = new ArrayList<>();

	/** Linearize the headings */
	private NodeVisitor nodeVisitor = new NodeVisitor(
		new VisitHandler<Heading>(Heading.class, new Visitor<Heading>() {
			@Override
			public void visit(Heading heading) {
				linearHeadings.add(heading);
				nodeVisitor.visitChildren(heading);
			}
		}),
		new VisitHandler<FencedCodeBlock>(FencedCodeBlock.class, new Visitor<FencedCodeBlock>() {
			@Override
			public void visit(FencedCodeBlock fencedCodeBlock) {
				linearFencedCodeBlocks.add(fencedCodeBlock);
			}
		}),
		new VisitHandler<IndentedCodeBlock>(IndentedCodeBlock.class, new Visitor<IndentedCodeBlock>() {
			@Override
			public void visit(IndentedCodeBlock indentedCodeBlock) {
				indentedCodeBlocks.add(indentedCodeBlock);
			}
		})		
	);
	
	/** Headings outline */
	public ArrayList<Position> runFolding(String markdownString, int lineDelimiterLength) {
		
		ArrayList<Position> positions = new ArrayList<>();
		
		/* Parse document in a node */
		Node documentNode = MarkdownFormatterEngine.getParser().parse(markdownString);
		linearHeadings.clear();
		linearFencedCodeBlocks.clear();
		indentedCodeBlocks.clear();
		nodeVisitor.visitChildren(documentNode);

		int headingsSize = linearHeadings.size();
		if (headingsSize > 0) {
	    	for (int i = 0; i < headingsSize - 1; i++) {
	    		
	    		Heading headingStart = linearHeadings.get(i);
	    		int positionStart = headingStart.getStartOffset();
	    		int levelStart = headingStart.getLevel();
	    		boolean found = false;
	        	for (int j = i + 1; j < headingsSize; j++) {
	        		Heading headingEnd = linearHeadings.get(j);
	        		int positionEnd = headingEnd.getStartOffset();
	        		if (headingEnd.getLevel() <= levelStart) {
	        			positions.add(new Position(positionStart, positionEnd - positionStart));
	        			found = true;
	        			break;
	        		}
	        	}
	        	if (!found) {
	        		positions.add(new Position(positionStart, markdownString.length() - (positionStart)));	
	        	}
	    	}
	    	int lastHeadingStart = linearHeadings.get(headingsSize - 1).getStartOffset();
	    	positions.add(new Position(lastHeadingStart, markdownString.length() - lastHeadingStart));
		}

    	for (FencedCodeBlock fencedCodeBlock : linearFencedCodeBlocks) {
    		int fencedCodeBlockStart = fencedCodeBlock.getStartOffset();
    		positions.add(new Position(fencedCodeBlockStart, fencedCodeBlock.getEndOffset() - (fencedCodeBlockStart - lineDelimiterLength)));
    	}

    	for (IndentedCodeBlock indentedCodeBlock : indentedCodeBlocks) {
    		int indentedCodeBlockStart = indentedCodeBlock.getStartOffset();
    		positions.add(new Position(indentedCodeBlockStart, indentedCodeBlock.getEndOffset() - (indentedCodeBlockStart - lineDelimiterLength)));
    	}

		return positions;
	}
}
