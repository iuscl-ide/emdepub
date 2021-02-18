/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.ui.editor.md;

import org.eclipse.ui.internal.genericeditor.ExtensionBasedTextEditor;

/** Markdown text editor based on the new generic editor */
@SuppressWarnings("restriction")
public class MarkdownTextEditor extends ExtensionBasedTextEditor {

	public MarkdownTextEditor() {
		super();
	}

	/** For status bar */
	public String getCursorPositionString() {
		
		return this.getCursorPosition();
	}
	
	/** For status bar */
	@Override
	protected void handleCursorPositionChanged() {
		
		MarkdownEditorContributor.getStatusLinePositionField().setText(this.getCursorPositionString());

		super.handleCursorPositionChanged();
	}
}
