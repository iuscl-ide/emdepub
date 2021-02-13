/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.ui.editor.md.engine;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.eclipse.jface.text.Position;
import org.emdepub.activator.R;
import org.emdepub.ui.editor.md.language.MarkdownOutlineNode;

import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.ast.Text;
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
		Node documentNode = MarkdownEditorEngine.getParser().parse(markdownString);
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

		
		
//		/* Put headings in a hierarchical tree */
//		for (Heading heading : linearHeadings) {
//
//
//			
//			int parentIndex = createdOutlineNodes.size() - 1;
//			MarkdownOutlineNode parentOutlineNode = createdOutlineNodes.get(parentIndex);
//			int mdHeadingLevel = heading.getLevel();
//			while (Integer.parseInt(parentOutlineNode.getSubType()) >= mdHeadingLevel) {
//				parentIndex--;
//				parentOutlineNode = createdOutlineNodes.get(parentIndex);
//			}
//			
//			MarkdownOutlineNode headingOutlineNode = parentOutlineNode.addChildNode();
//			String level = "" + mdHeadingLevel;
//			headingOutlineNode.setType("Heading");
//			headingOutlineNode.setSubType(level);
//			headingOutlineNode.setImage(R.getImage("markdown-header"));
//			headingStringBuilder.setLength(0);
//			headingTextsNodeVisitor.visitChildren(heading);
//			headingOutlineNode.setLabel(headingStringBuilder.toString());
//			int offset = heading.getStartOffset();
//			headingOutlineNode.setStart(offset);
//			headingOutlineNode.setLength(heading.getEndOffset() - offset);
//			createdOutlineNodes.add(headingOutlineNode);
//		}
		
		return headerPositions;
	}
	
}
