/* EMDEPUB Eclipse Plugin - emdepub.org */
package org.emdepub.toml.editor;

import org.eclipse.ui.internal.genericeditor.ExtensionBasedTextEditor;

/** TOML editor based on the new generic editor */
@SuppressWarnings("restriction")
public class TomlExtensionBasedEditor extends ExtensionBasedTextEditor {

	public TomlExtensionBasedEditor() {
		super();
	}

	/** For status bar */
	public String getCursorPositionString() {
		
		return this.getCursorPosition();
	}
	
	/** For status bar */
	@Override
	protected void handleCursorPositionChanged() {
		
		TomlExtensionBasedEditorContributor.getStatusLinePositionField().setText(this.getCursorPositionString());

		super.handleCursorPositionChanged();
	}
}
