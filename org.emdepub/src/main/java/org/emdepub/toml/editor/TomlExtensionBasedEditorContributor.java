/* EMDEPUB Eclipse Plugin - emdepub.org */
package org.emdepub.toml.editor;

import java.util.StringJoiner;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineContributionItem;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.EditorActionBarContributor;
import org.emdepub.common.resources.CR;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.toml.TomlMapper;
import com.fasterxml.jackson.dataformat.toml.TomlStreamReadException;

import lombok.SneakyThrows;

/** TOML editor contributor to menu, tool bar, status bar */
public class TomlExtensionBasedEditorContributor extends EditorActionBarContributor {

//	/** Contributed menu */
//	private IMenuManager tomlMenuManager;
//	/** Contributed tool bar */
//	private IToolBarManager tomlToolBarManager;
//	/** Contributed status line */
//	private IStatusLineManager tomlStatusLineManager;

	/** Source position */
	private static StatusLineContributionItem statusLinePositionField;

	/** Verify */
	private static StatusLineContributionItem statusLineVerifyField;

	private TomlExtensionBasedEditor tomlSourceTextEditor;

	/** Verify TOML */
	private Action verifyTomlAction;
	private final static String verifyTomlActionId = "org.emdepub.ui.editor.toml.action.verifyToml";

	/** Comment TOML */
	private Action commentTomlAction;
	private final static String commentTomlActionId = "org.emdepub.ui.editor.toml.action.commentToml";

	public TomlExtensionBasedEditorContributor() {
		super();

		createActions();

		statusLinePositionField = new StatusLineContributionItem("statusLinePositionField", 25);
		statusLinePositionField.setText("0");

		statusLineVerifyField = new StatusLineContributionItem("statusLineVerifyField", 120);
		statusLineVerifyField.setText(TomlExtensionBasedEditor.verifyTomlResultVoid);
	}

	/** Initial, fix contribution */
	@Override
	public void contributeToMenu(IMenuManager menuManager) {
		IMenuManager tomlMenuManager = new MenuManager("TOML");
		menuManager.prependToGroup(IWorkbenchActionConstants.MB_ADDITIONS, tomlMenuManager);
		tomlMenuManager.add(verifyTomlAction);
		tomlMenuManager.add(commentTomlAction);
	}

	/** Initial, fix contribution */
	@Override
	public void contributeToToolBar(IToolBarManager toolBarManager) {
//		tomlToolBarManager = toolBarManager;
		toolBarManager.add(verifyTomlAction);
		toolBarManager.add(commentTomlAction);
	}

	/** Initial, fix contribution */
	@Override
	public void contributeToStatusLine(IStatusLineManager statusLineManager) {
//		tomlStatusLineManager = statusLineManager;
		statusLineManager.add(statusLinePositionField);
		statusLineManager.add(statusLineVerifyField);
		super.contributeToStatusLine(statusLineManager);
	}

	@Override
	public void setActiveEditor(IEditorPart editorPart) {

		if (editorPart == null) {
			return;
		}

		tomlSourceTextEditor = (TomlExtensionBasedEditor) editorPart;

		statusLinePositionField.setText(tomlSourceTextEditor.getCursorPositionText());

		if (tomlSourceTextEditor.getVerifyTomlResultString().equals(TomlExtensionBasedEditor.verifyTomlResultOK)
				|| tomlSourceTextEditor.getVerifyTomlResultString()
						.equals(TomlExtensionBasedEditor.verifyTomlResultVoid)) {
			statusLineVerifyField.setText(tomlSourceTextEditor.getVerifyTomlResultString());
		} else {
			statusLineVerifyField.setText(tomlSourceTextEditor.getVerifyTomlResultString() + " "
					+ tomlSourceTextEditor.getVerifyTomlResultLineCol());
		}
	}

	/** Action to be access also from page activation */
	@SneakyThrows({ JsonMappingException.class, JsonProcessingException.class })
	public void verifyToml() {

		TomlMapper tomlMapper = new TomlMapper();
		try {
			IDocument document = tomlSourceTextEditor.getDocumentProvider()
					.getDocument(tomlSourceTextEditor.getEditorInput());
			tomlMapper.readTree(document.get());
			tomlSourceTextEditor.setVerifyTomlResultString(TomlExtensionBasedEditor.verifyTomlResultOK);
			statusLineVerifyField.setText(tomlSourceTextEditor.getVerifyTomlResultString());
		} catch (TomlStreamReadException tomlStreamReadException) {
			tomlSourceTextEditor.selectAndReveal((int) tomlStreamReadException.getLocation().getCharOffset(), 0);
			tomlSourceTextEditor.setVerifyTomlResultString(tomlStreamReadException.getOriginalMessage());
			tomlSourceTextEditor.setVerifyTomlResultLineCol("(" + tomlStreamReadException.getLocation().getLineNr()
					+ " : " + tomlStreamReadException.getLocation().getColumnNr() + ")");
			statusLineVerifyField.setText(tomlSourceTextEditor.getVerifyTomlResultString());
		}
	}

	/** Action to comment */
	@SneakyThrows(BadLocationException.class)
	public void commentToml() {

		TextSelection textSelection = (TextSelection) tomlSourceTextEditor.getSelectionProvider().getSelection();

		String selection = textSelection.getText();
		String sep = "";
		if (selection.contains("\r\n")) {
			sep = "\r\n";
		} else if (selection.contains("\n")) {
			sep = "\n";
		} else {
			sep = "\r";
		}

		boolean selectionEndsWithSep = selection.endsWith(sep);
		if (selectionEndsWithSep) {
			selection = selection.substring(0, selection.length() - sep.length());
		}

		String[] lines = selection.split(sep, -1);

		boolean allLinesCommented = true;
		for (String line : lines) {
			if (!line.startsWith("# ")) {
				allLinesCommented = false;
				break;
			}
		}

		StringJoiner joiner = new StringJoiner(sep);
		int length = selection.length();

		if (allLinesCommented) {
			/* Uncomment */
			for (String line : lines) {
				joiner.add(line.substring(2));
				length = length - 2;
			}
		} else {
			/* Comment */
			for (String line : lines) {
				joiner.add("# " + line);
				length = length + 2;
			}
		}

		String replacement = joiner.toString();
		if (selectionEndsWithSep) {
			replacement = replacement + sep;
			length = length + sep.length();
		}

		IDocument document = tomlSourceTextEditor.getDocumentProvider()
				.getDocument(tomlSourceTextEditor.getEditorInput());

		document.replace(textSelection.getOffset(), textSelection.getLength(), replacement);

		textSelection = new TextSelection(document, textSelection.getOffset(), length);

		tomlSourceTextEditor.getSelectionProvider().setSelection(textSelection);
	}

	/** Eclipse actions */
	private void createActions() {

		/* Try to load TOML */
		verifyTomlAction = new Action() {
			public void run() {
				verifyToml();
			}
		};
		verifyTomlAction.setId(verifyTomlActionId);
		verifyTomlAction.setText("Verify TOML");
		verifyTomlAction.setToolTipText("Verify TOML file (try to load)");
		verifyTomlAction.setImageDescriptor(CR.getImageDescriptor("toml-action-verify-file"));

		/* Comment / Uncomment TOML */
		commentTomlAction = new Action() {
			public void run() {
				commentToml();
			}
		};
		commentTomlAction.setId(commentTomlActionId);
		commentTomlAction.setText("Comment / Uncomment TOML");
		commentTomlAction.setToolTipText("Comment / Uncomment TOML");
		commentTomlAction.setImageDescriptor(CR.getImageDescriptor("toml-action-comment"));
	}

	public static StatusLineContributionItem getStatusLinePositionField() {
		return statusLinePositionField;
	}
}
