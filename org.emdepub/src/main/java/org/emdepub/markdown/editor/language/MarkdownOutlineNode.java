/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.markdown.editor.language;

import java.util.ArrayList;

import org.eclipse.swt.graphics.Image;

/** The data for outline tree view nodes */
public class MarkdownOutlineNode {
	
	public static final MarkdownOutlineNode[] NO_CHILDREN = new MarkdownOutlineNode[0];
	
	private String label;
	private Image image;

	private MarkdownOutlineNode parentOutlineNode;
	private ArrayList<MarkdownOutlineNode> childOutlineNodes;
	
	private String type;
	private String subType;

	private int start = 0;
	private int length = 0;
	
	/** To create the document */
	public MarkdownOutlineNode() { /* root */ }	
	
	/** Internal for child */
	private MarkdownOutlineNode(MarkdownOutlineNode parentOutlineNode) {
		this.parentOutlineNode = parentOutlineNode;
	}
	
	/** To create from parser */
	public MarkdownOutlineNode addChildNode() {

		MarkdownOutlineNode child = new MarkdownOutlineNode(this);
		if (childOutlineNodes == null) {
			childOutlineNodes = new ArrayList<>();
		}
		childOutlineNodes.add(child);
		
		return child;
	}
	
	/** Children, as array */
	public MarkdownOutlineNode[] findChildOutlineNodes() {
		
		if (childOutlineNodes == null) {
			return NO_CHILDREN;
		}
		return (MarkdownOutlineNode[]) childOutlineNodes.toArray(new MarkdownOutlineNode[childOutlineNodes.size()]);
	}

	/** the label */
	public String getLabel() {
		return label;
	}

	/** label the label to set */
	public void setLabel(String label) {
		this.label = label;
	}

	/** the image */
	public Image getImage() {
		return image;
	}

	/** image the image to set */
	public void setImage(Image image) {
		this.image = image;
	}

	/** the type */
	public String getType() {
		return type;
	}

	/** type the type to set */
	public void setType(String type) {
		this.type = type;
	}

	/** the subType */
	public String getSubType() {
		return subType;
	}

	/** subType the subType to set */
	public void setSubType(String subType) {
		this.subType = subType;
	}

	/** the parentOutlineNode */
	public MarkdownOutlineNode getParentOutlineNode() {
		return parentOutlineNode;
	}

	/** the start */
	public int getStart() {
		return start;
	}

	/** start the start to set */
	public void setStart(int start) {
		this.start = start;
	}

	/** the length */
	public int getLength() {
		return length;
	}

	/** length the length to set */
	public void setLength(int length) {
		this.length = length;
	}
}
