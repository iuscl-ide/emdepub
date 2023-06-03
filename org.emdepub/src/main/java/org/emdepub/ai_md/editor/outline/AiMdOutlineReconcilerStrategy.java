/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.ai_md.editor.outline;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.emdepub.ai_md.editor.AiMdMultiPageEditor;
import org.emdepub.ai_md.editor.AiMdTextEditor;

/** AiMd outline strategy */
public class AiMdOutlineReconcilerStrategy implements IReconcilingStrategy, IReconcilingStrategyExtension {

	private IDocument document;
	private AiMdOutlinePage aiMdOutlinePage;
	
    @Override
    public void setDocument(IDocument document) {
        this.document = document;
    }

    @Override
    public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
    	initialReconcile();
    }

    @Override
    public void reconcile(IRegion partition) {
    	initialReconcile();
    }
    
	/** Actual outline reconcile */
	@Override
	public void initialReconcile() {

		if (aiMdOutlinePage == null) {
			Display.getDefault().syncExec(new Runnable() {
				
			    @Override
			    public void run() {

			    	IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
					IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
					for (IEditorReference editorReference : workbenchPage.getEditorReferences()) {
						IEditorPart editorPart = editorReference.getEditor(true);
						if (editorPart instanceof AiMdMultiPageEditor) {
							AiMdMultiPageEditor aiMdEditor = (AiMdMultiPageEditor) editorPart;
							AiMdTextEditor aiMdTextEditor = aiMdEditor.getAiMdTextEditor();
							Document editorReferenceDocument = (Document) aiMdTextEditor.getDocumentProvider().getDocument(aiMdTextEditor.getEditorInput());
							if (editorReferenceDocument == document) {
								aiMdOutlinePage = aiMdEditor.getAiMdOutlinePage();
								break;
							}
						}
					}
			    }
			});
		}
		
		aiMdOutlinePage.updateOulineView();
	}

    @Override
    public void setProgressMonitor(IProgressMonitor monitor) { /* no progress monitor used */ }
    
    /* LSPEclipseUtils, how Eclipse is doing it */
    public static ITextEditor getActiveTextEditor() {
	
    	IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if(editorPart instanceof ITextEditor) {
			return (ITextEditor) editorPart;
		} else if (editorPart instanceof MultiPageEditorPart) {
			MultiPageEditorPart multiPageEditorPart = (MultiPageEditorPart) editorPart;
			Object page = multiPageEditorPart.getSelectedPage();
			if (page instanceof ITextEditor) {
				return (ITextEditor) page;
			}
		}
		return null;
	}
}