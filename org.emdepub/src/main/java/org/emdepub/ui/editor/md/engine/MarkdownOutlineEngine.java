/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.ui.editor.md.engine;

import java.util.ArrayList;

import org.emdepub.activator.R;
import org.emdepub.ui.editor.md.language.MarkdownOutlineNode;

import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.ast.Text;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import com.vladsch.flexmark.util.ast.VisitHandler;
import com.vladsch.flexmark.util.ast.Visitor;

/** Markdown visitors engine */
public class MarkdownOutlineEngine {

	private ArrayList<Heading> linearHeadings = new ArrayList<>();
	private StringBuilder headingStringBuilder = new StringBuilder();

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

	/** Concatenate heading label */
	private NodeVisitor headingTextsNodeVisitor = new NodeVisitor(
		new VisitHandler<Text>(Text.class, new Visitor<Text>() {
			@Override
			public void visit(Text text) {
				String textString = text.getChars().toString().trim();
				//L.p("->" + textString + "<-");
				String separator = "";
				if (headingStringBuilder.length() > 0) {
					separator = " ";
				}
				headingStringBuilder.append(separator + textString);
				headingTextsNodeVisitor.visitChildren(text);
			}
		})
	);
	
	/** Headings outline */
	public MarkdownOutlineNode runOutline(String markdownString) {
		
		/* View, outside of tree */
		MarkdownOutlineNode viewOutlineNode = new MarkdownOutlineNode();

		ArrayList<MarkdownOutlineNode> createdOutlineNodes = new ArrayList<>();

		/* Parse document in a node */
		Node documentNode = MarkdownEditorEngine.getParser().parse(markdownString);
		MarkdownOutlineNode documentOutlineNode = viewOutlineNode.addChildNode();
//			documentOutlineNode.setStart(0);
//			documentOutlineNode.setLength(0);
//			documentOutlineNode.setImage(R.getImage("markdowd"));
//			documentOutlineNode.setLabel("Document");
//			documentOutlineNode.setType("Document");
		documentOutlineNode.setSubType("0");
		createdOutlineNodes.add(documentOutlineNode);
		
		linearHeadings.clear();
		headingsNodeVisitor.visitChildren(documentNode);

		/* Put headings in a hierarchical tree */
		for (Heading heading : linearHeadings) {

			int parentIndex = createdOutlineNodes.size() - 1;
			MarkdownOutlineNode parentOutlineNode = createdOutlineNodes.get(parentIndex);
			int mdHeadingLevel = heading.getLevel();
			while (Integer.parseInt(parentOutlineNode.getSubType()) >= mdHeadingLevel) {
				parentIndex--;
				parentOutlineNode = createdOutlineNodes.get(parentIndex);
			}
			
			MarkdownOutlineNode headingOutlineNode = parentOutlineNode.addChildNode();
			String level = "" + mdHeadingLevel;
			headingOutlineNode.setType("Heading");
			headingOutlineNode.setSubType(level);
			headingOutlineNode.setImage(R.getImage("markdown-header"));
			headingStringBuilder.setLength(0);
			headingTextsNodeVisitor.visitChildren(heading);
			headingOutlineNode.setLabel(headingStringBuilder.toString());
			int offset = heading.getStartOffset();
			headingOutlineNode.setStart(offset);
			headingOutlineNode.setLength(heading.getEndOffset() - offset);
			createdOutlineNodes.add(headingOutlineNode);
		}
		
		return documentOutlineNode;
	}
}
