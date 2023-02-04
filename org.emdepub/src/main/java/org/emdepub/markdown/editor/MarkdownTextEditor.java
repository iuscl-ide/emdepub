/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.markdown.editor;

import org.eclipse.ui.internal.genericeditor.ExtensionBasedTextEditor;

/** Markdown text editor based on the new generic editor */
@SuppressWarnings("restriction")
public class MarkdownTextEditor extends ExtensionBasedTextEditor {

	public MarkdownTextEditor() {
		super();
		
		setSourceViewerConfiguration(new MarkdownExtensionBasedTextViewerConfiguration(this, null));
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
