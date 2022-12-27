/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.markdown.editor.engine;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import lombok.SneakyThrows;

/** Markdown CompletionProposal */
public final class MarkdownCompletionProposal implements ICompletionProposal {

	/** Default proposal keys */
	public static enum MarkdownCompletionProposalKey { 
		BOLD_TEXT, ITALIC_TEXT, BOLD_ITALIC_TEXT,
		STRIKETROUGH_TEXT, QUOTED_TEXT,
		INLINE_CODE, FENCED_CODE_BLOCK, INDENTED_CODE_BLOCK,
		HEADING_LEVEL_1, HEADING_LEVEL_2, HEADING_LEVEL_3, HEADING_LEVEL_4, HEADING_LEVEL_5, HEADING_LEVEL_6,
		UNORDERED_LIST, ORDERED_LIST, DEFINITION_LIST,
		LINK, IMAGE,
		HORIZONTAL_RULE};

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

	/** To create from TOML */
	public MarkdownCompletionProposal() {
		super();
	}

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
	@SneakyThrows(BadLocationException.class)
	public void apply(IDocument document) {
		document.replace(this.replacementOffset, this.replacementLength, this.replacementString);
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
