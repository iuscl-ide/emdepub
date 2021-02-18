/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.ui.editor.md.engine;

import java.util.ArrayList;

import org.eclipse.jface.text.Position;

import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import com.vladsch.flexmark.util.ast.VisitHandler;
import com.vladsch.flexmark.util.ast.Visitor;

/** Markdown visitors engine */
public class MarkdownFoldingEngine {

	private ArrayList<Heading> linearHeadings = new ArrayList<>();

	/** Linearize the headings */
	private NodeVisitor headingsNodeVisitor = new NodeVisitor(
		new VisitHandler<Heading>(Heading.class, new Visitor<Heading>() {
			@Override
			public void visit(Heading heading) {
				linearHeadings.add(heading);
				headingsNodeVisitor.visitChildren(heading);
			}
		})
	);
	
	/** Headings outline */
	public ArrayList<Position> runFolding(String markdownString) {
		
		ArrayList<Position> headerPositions = new ArrayList<>();
		
		/* Parse document in a node */
		Node documentNode = MarkdownFormatterEngine.getParser().parse(markdownString);
		linearHeadings.clear();
		headingsNodeVisitor.visitChildren(documentNode);

		int headingsSize = linearHeadings.size();
    	for (int i = 0; i < headingsSize - 1; i++) {
    		
    		Heading headingStart = linearHeadings.get(i);
    		int positionStart = headingStart.getStartOffset();
    		int levelStart = headingStart.getLevel();
    		boolean found = false;
        	for (int j = i + 1; j < headingsSize; j++) {
        		Heading headingEnd = linearHeadings.get(j);
        		int positionEnd = headingEnd.getStartOffset();
        		if (headingEnd.getLevel() <= levelStart) {
        			headerPositions.add(new Position(positionStart, positionEnd - positionStart));
        			found = true;
        			break;
        		}
        	}
        	if (!found) {
        		headerPositions.add(new Position(positionStart, markdownString.length() - (positionStart)));	
        	}
    	}
    	int lastHeadingStart = linearHeadings.get(headingsSize - 1).getStartOffset();
    	headerPositions.add(new Position(lastHeadingStart, markdownString.length() - lastHeadingStart));
		
		return headerPositions;
	}
}
