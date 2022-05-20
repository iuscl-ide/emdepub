package org.emdepub;

import com.vladsch.flexmark.util.ast.BlankLineBreakNode;
import com.vladsch.flexmark.util.ast.DoNotCollectText;
import com.vladsch.flexmark.util.ast.LineBreakNode;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import com.vladsch.flexmark.util.ast.SpaceInsertingSequenceBuilder;

import com.vladsch.flexmark.util.ast.TextContainer;
import com.vladsch.flexmark.util.ast.Visitor;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import java.util.function.BiConsumer;
import org.jetbrains.annotations.NotNull;

public class TextLayoutCollectingVisitor {

	private final NodeVisitor nodeVisitor = new NodeVisitor() {
		
		public void processNode(@NotNull Node node, boolean withChildren, @NotNull BiConsumer<Node, Visitor<Node>> processor) {
			
			System.out.println(node);
			
			if (!node.isOrDescendantOfType(new Class[] { DoNotCollectText.class }))
				if (node instanceof TextContainer) {
					TextLayoutCollectingVisitor.this.out.setLastNode(node);
					if (((TextContainer) node).collectText(TextLayoutCollectingVisitor.this.out,
							TextLayoutCollectingVisitor.this.flags, TextLayoutCollectingVisitor.this.nodeVisitor)) {
						if (node instanceof BlankLineBreakNode && TextLayoutCollectingVisitor.this.out.isNotEmpty()) {
							TextLayoutCollectingVisitor.this.out.appendEol();
						}
						processChildren(node, processor);
						if (node instanceof LineBreakNode && TextLayoutCollectingVisitor.this.out.needEol()) {
							TextLayoutCollectingVisitor.this.out.appendEol();
						}
					}
				} else {
					TextLayoutCollectingVisitor.this.out.setLastNode(node);
					if (node instanceof BlankLineBreakNode && TextLayoutCollectingVisitor.this.out.isNotEmpty()) {
						TextLayoutCollectingVisitor.this.out.appendEol();
					}
					processChildren(node, processor);
					if (node instanceof LineBreakNode && TextLayoutCollectingVisitor.this.out.needEol()) {
						TextLayoutCollectingVisitor.this.out.appendEol();
					}
				}
		}
	};

	private SpaceInsertingSequenceBuilder out;
	private int flags;

	public String getText() {
		return this.out.toString();
	}

	public BasedSequence getSequence() {
		return this.out.toSequence();
	}

	public void collect(Node node) {
		collect(node, 0);
	}

	public String collectAndGetText(Node node) {
		return collectAndGetText(node, 0);
	}

	public BasedSequence collectAndGetSequence(Node node) {
		return collectAndGetSequence(node, 0);
	}

	public void collect(Node node, int flags) {
		this.out = SpaceInsertingSequenceBuilder.emptyBuilder(node.getChars(), flags);
		this.flags = flags;
		this.nodeVisitor.visit(node);
	}

	public String collectAndGetText(Node node, int flags) {
		collect(node, flags);
		return this.out.toString();
	}

	public BasedSequence collectAndGetSequence(Node node, int flags) {
		collect(node, flags);
		return this.out.toSequence();
	}
}
