/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.epub_project.editor;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.StatusLineContributionItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;

/** EPubProject multi-page editor contributor to menu, tool bar, status bar */
public class EPubProjectEditorContributor extends MultiPageEditorActionBarContributor {

	/** The actual editor */
	private EPubProjectEditor ePubProjectMultiPageEditor;
	private EPubProjectTextEditor ePubProjectSourceTextEditor;
	
	/** Contributed status line */
	private IStatusLineManager ePubProjectStatusLineManager;

	/** Source position */
	private static StatusLineContributionItem statusLinePositionField;
	
	/** Creates a multi-page contributor */
	public EPubProjectEditorContributor() {
		super();

		statusLinePositionField = new StatusLineContributionItem("statusLinePositionField", 120);
		statusLinePositionField.setText("0");
	}
	
	/** Initial, fix contribution */
	@Override
	public void contributeToStatusLine(IStatusLineManager statusLineManager) {
		ePubProjectStatusLineManager = statusLineManager;
		super.contributeToStatusLine(statusLineManager);
	}

	/** The editor class is available here */
	@Override
	public void setActiveEditor(IEditorPart editorPart) {

		if (editorPart == null) {
			return;
		}
		
		ePubProjectMultiPageEditor = (EPubProjectEditor) editorPart;
		ePubProjectSourceTextEditor = ePubProjectMultiPageEditor.getEPubProjectTextEditor();
	}
	
	@Override
	public void setActivePage(IEditorPart editorPart) {
		
		IActionBars actionBars = getActionBars();
		if ((ePubProjectMultiPageEditor == null) || (actionBars == null)) {
			return;
		}

		/* Status line */
		ePubProjectStatusLineManager.remove(statusLinePositionField);
		/* Update */
		ePubProjectStatusLineManager.update(true);

		if (ePubProjectMultiPageEditor.getActivePage() == ePubProjectMultiPageEditor.getEPubProjectTextEditorPageIndex()) {
			ePubProjectStatusLineManager.add(statusLinePositionField);
			statusLinePositionField.setText(ePubProjectSourceTextEditor.getCursorPositionString());
			/* Update */
			ePubProjectStatusLineManager.update(true);
		}
	}

	/** To be updated from the editor */
	public static StatusLineContributionItem getStatusLinePositionField() {
		return statusLinePositionField;
	}
}
