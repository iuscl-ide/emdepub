/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.common.editor.language.content_assist;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;

/** Common CompletionProposal */
@FieldDefaults(level=AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public final class CommonCompletionProposal implements ICompletionProposal {

	String displayString;
	String replacementString;
	int replacementOffset;
	int replacementLength;
	int cursorPosition;
	Image image;
	IContextInformation contextInformation;
	String additionalProposalInfo;
	int cursorPositionChars;
	int cursorPositionLineDelimiters;

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
	public String getDisplayString() {
		if (this.displayString != null)
			return this.displayString;
		return this.replacementString;
	}
}
