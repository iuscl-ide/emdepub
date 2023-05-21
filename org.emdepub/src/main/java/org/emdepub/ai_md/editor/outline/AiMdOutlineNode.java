/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.ai_md.editor.outline;

import java.util.ArrayList;

import org.eclipse.swt.graphics.Image;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/** The data for outline tree view nodes */
@FieldDefaults(level=AccessLevel.PRIVATE)
@Getter
@Setter
public class AiMdOutlineNode {
	
	public static final AiMdOutlineNode[] NO_CHILDREN = new AiMdOutlineNode[0];
	
	String label;
	Image image;

	final AiMdOutlineNode parentOutlineNode;
	ArrayList<AiMdOutlineNode> childOutlineNodes;
	
	String type;
	String subType;

	int start = 0;
	int length = 0;
	
	/** To create the document */
	public AiMdOutlineNode() {
		/* root */
		parentOutlineNode = null;
	}	
	
	/** Internal for child */
	private AiMdOutlineNode(AiMdOutlineNode parentOutlineNode) {
		this.parentOutlineNode = parentOutlineNode;
	}
	
	/** To create from parser */
	public AiMdOutlineNode addChildNode() {

		AiMdOutlineNode child = new AiMdOutlineNode(this);
		if (childOutlineNodes == null) {
			childOutlineNodes = new ArrayList<>();
		}
		childOutlineNodes.add(child);
		
		return child;
	}
	
	/** Children, as array */
	public AiMdOutlineNode[] findChildOutlineNodes() {
		
		if (childOutlineNodes == null) {
			return NO_CHILDREN;
		}
		return (AiMdOutlineNode[]) childOutlineNodes.toArray(new AiMdOutlineNode[childOutlineNodes.size()]);
	}
}
