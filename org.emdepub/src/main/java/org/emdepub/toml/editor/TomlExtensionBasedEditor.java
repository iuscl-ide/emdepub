/* EMDEPUB Eclipse Plugin - emdepub.org */
package org.emdepub.toml.editor;

import org.eclipse.ui.internal.genericeditor.ExtensionBasedTextEditor;

/** TOML editor based on the new generic editor */
@SuppressWarnings("restriction")
public class TomlExtensionBasedEditor extends ExtensionBasedTextEditor {

	public final static String verifyTomlResultOK = "OK on last verify";
	public final static String verifyTomlResultVoid = "Not verified yet";
	
	private String verifyTomlResultString = verifyTomlResultVoid;
	private String verifyTomlResultLineCol = "(0 : 0)";

	public TomlExtensionBasedEditor() {
		super();
		
		setSourceViewerConfiguration(new TomlExtensionBasedTextViewerConfiguration(this, null));
	}

	/** For status bar */
	public String getCursorPositionText() {
		
		return this.getCursorPosition();
	}

	/** For status bar */
	@Override
	protected void handleCursorPositionChanged() {
		
		TomlExtensionBasedEditorContributor.getStatusLinePositionField().setText(getCursorPositionText());

		super.handleCursorPositionChanged();
	}
	
	public String getVerifyTomlResultString() {
		return verifyTomlResultString;
	}

	public void setVerifyTomlResultString(String verifyTomlResultString) {
		this.verifyTomlResultString = verifyTomlResultString;
	}

	public String getVerifyTomlResultLineCol() {
		return verifyTomlResultLineCol;
	}

	public void setVerifyTomlResultLineCol(String verifyTomlResultLineCol) {
		this.verifyTomlResultLineCol = verifyTomlResultLineCol;
	}
}
