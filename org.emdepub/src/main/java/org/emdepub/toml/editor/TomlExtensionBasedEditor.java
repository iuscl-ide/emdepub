/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.toml.editor;

import org.eclipse.ui.internal.genericeditor.ExtensionBasedTextEditor;

/** Markdown text editor based on the new generic editor */
@SuppressWarnings("restriction")
public class TomlExtensionBasedEditor extends ExtensionBasedTextEditor {

	public TomlExtensionBasedEditor() {
		super();
	}

	/** For status bar */
	public String getCursorPositionString() {
		
		return this.getCursorPosition();
	}
}
