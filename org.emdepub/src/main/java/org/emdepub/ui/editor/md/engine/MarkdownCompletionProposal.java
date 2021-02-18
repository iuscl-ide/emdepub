/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.ui.editor.md.engine;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

/** Markdown CompletionProposal */
public final class MarkdownCompletionProposal implements ICompletionProposal {

	private String displayString;
	private String replacementString;
	private int replacementOffset;
	private int replacementLength;
	private int cursorPosition;
	private Image image;
	private IContextInformation contextInformation;
	private String additionalProposalInfo;
	private int cursorPositionChars;
	private int cursorPositionLineDelimiters;

	public MarkdownCompletionProposal(String replacementString, int replacementOffset, int replacementLength,
		int cursorPositionChars, int cursorPositionLineDelimiters,
		Image image, String displayString, IContextInformation contextInformation, String additionalProposalInfo) {
		super();

		this.displayString = displayString;
		this.replacementString = replacementString;
		this.replacementOffset = replacementOffset;
		this.replacementLength = replacementLength;
		this.cursorPositionChars = cursorPositionChars;
		this.cursorPositionLineDelimiters = cursorPositionLineDelimiters;
		this.image = image;
		this.contextInformation = contextInformation;
		this.additionalProposalInfo = additionalProposalInfo;
	}

	@Override
	public void apply(IDocument document) {
		try {
			document.replace(this.replacementOffset, this.replacementLength, this.replacementString);
		} catch (BadLocationException badLocationException) {
		}
	}

	@Override
	public Point getSelection(IDocument document) {
		return new Point(this.replacementOffset + this.cursorPosition, 0);
	}

	@Override
	public IContextInformation getContextInformation() {
		return this.contextInformation;
	}

	@Override
	public Image getImage() {
		return this.image;
	}

	@Override
	public String getDisplayString() {
		if (this.displayString != null)
			return this.displayString;
		return this.replacementString;
	}

	@Override
	public String getAdditionalProposalInfo() {
		return this.additionalProposalInfo;
	}

	public String getReplacementString() {
		return replacementString;
	}

	public void setReplacementString(String replacementString) {
		this.replacementString = replacementString;
	}

	public int getReplacementOffset() {
		return replacementOffset;
	}

	public void setReplacementOffset(int replacementOffset) {
		this.replacementOffset = replacementOffset;
	}

	public int getReplacementLength() {
		return replacementLength;
	}

	public void setReplacementLength(int replacementLength) {
		this.replacementLength = replacementLength;
	}

	public int getCursorPosition() {
		return cursorPosition;
	}

	public void setCursorPosition(int cursorPosition) {
		this.cursorPosition = cursorPosition;
	}

	public void setDisplayString(String displayString) {
		this.displayString = displayString;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public void setContextInformation(IContextInformation contextInformation) {
		this.contextInformation = contextInformation;
	}

	public void setAdditionalProposalInfo(String additionalProposalInfo) {
		this.additionalProposalInfo = additionalProposalInfo;
	}

	public int getCursorPositionChars() {
		return cursorPositionChars;
	}

	public void setCursorPositionChars(int cursorPositionChars) {
		this.cursorPositionChars = cursorPositionChars;
	}

	public int getCursorPositionLineDelimiters() {
		return cursorPositionLineDelimiters;
	}

	public void setCursorPositionLineDelimiters(int cursorPositionLineDelimiters) {
		this.cursorPositionLineDelimiters = cursorPositionLineDelimiters;
	}
}
