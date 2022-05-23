/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.toml.editor;

import java.io.IOException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.EditorActionBarContributor;
import org.emdepub.activator.L;
import org.emdepub.activator.R;

import com.fasterxml.jackson.dataformat.toml.TomlMapper;

/** Markdown multi-page editor contributor to menu, tool bar, status bar */
public class TomlExtensionBasedEditorContributor extends EditorActionBarContributor {

	/** Contributed menu */
	private IMenuManager tomlMenuManager;
	/** Contributed tool bar */
	private IToolBarManager tomlToolBarManager;
//	/** Contributed status line */
//	private IStatusLineManager tomlStatusLineManager;
	
	TomlExtensionBasedEditor tomlSourceTextEditor;
	
	/** Verify TOML */
	private Action verifyTomlAction;
	private final static String verifyTomlActionId = "org.emdepub.ui.editor.toml.action.verifyToml";
	
	public TomlExtensionBasedEditorContributor() {
		super();
		
		createActions();
	}

	/** Initial, fix contribution */
	@Override
	public void contributeToMenu(IMenuManager menuManager) {
		tomlMenuManager = new MenuManager("TOML");
		menuManager.prependToGroup(IWorkbenchActionConstants.MB_ADDITIONS, tomlMenuManager);
		tomlMenuManager.add(verifyTomlAction);
		tomlMenuManager.add(new Separator());
	}

	/** Initial, fix contribution */
	@Override
	public void contributeToToolBar(IToolBarManager toolBarManager) {
		tomlToolBarManager = toolBarManager;
		tomlToolBarManager.add(verifyTomlAction);
		tomlToolBarManager.add(new Separator());
	}
	
	@Override
	public void setActiveEditor(IEditorPart editorPart) {

		if (editorPart == null) {
			return;
		}
		
		tomlSourceTextEditor = (TomlExtensionBasedEditor) editorPart;
	}
	
	/** Eclipse actions */
	private void createActions() {
		
		/* Export Markdown Document as HTML */
		verifyTomlAction = new Action() {
			public void run() {
				/* Rule fields */
				TomlMapper tomlMapper = new TomlMapper();
				try {
					IDocument document = tomlSourceTextEditor.getDocumentProvider().getDocument(tomlSourceTextEditor.getEditorInput());
					
//					TextSelection textSelection = (TextSelection) tomlSourceTextEditor.getSelectionProvider().getSelection();
					System.out.println(document.get());
					tomlMapper.readTree(document.get());
				} catch (IOException ioException) {
					L.e("Load from TOML resource", ioException);
				}
			}
		};
		verifyTomlAction.setId(verifyTomlActionId);
		verifyTomlAction.setText("verifyTomlActionId file");
		verifyTomlAction.setToolTipText("verifyTomlActionId");
		verifyTomlAction.setImageDescriptor(R.getImageDescriptor("html"));
	}
}
