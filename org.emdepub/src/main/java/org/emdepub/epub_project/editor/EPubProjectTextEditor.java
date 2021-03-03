/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.epub_project.editor;

import org.eclipse.ui.internal.genericeditor.ExtensionBasedTextEditor;

/** EPubProject text editor based on the new generic editor */
@SuppressWarnings("restriction")
public class EPubProjectTextEditor extends ExtensionBasedTextEditor {

	public EPubProjectTextEditor() {
		super();
	}

	/** For status bar */
	public String getCursorPositionString() {
		
		return this.getCursorPosition();
	}
	
	/** For status bar */
	@Override
	protected void handleCursorPositionChanged() {
		
		EPubProjectEditorContributor.getStatusLinePositionField().setText(this.getCursorPositionString());

		super.handleCursorPositionChanged();
	}
}
