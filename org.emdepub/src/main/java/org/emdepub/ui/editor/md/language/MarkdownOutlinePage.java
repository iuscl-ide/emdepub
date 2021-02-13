/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.ui.editor.md.language;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.emdepub.ui.editor.md.MarkdownEditor;
import org.emdepub.ui.editor.md.MarkdownTextEditor;
import org.emdepub.ui.editor.md.engine.MarkdownEditorEngine;

/** The outline view with its tree view */
public class MarkdownOutlinePage extends ContentOutlinePage {

	/** Tree labels */
	public class OutlineLabelProvider extends LabelProvider {

		public String getText(Object element) {
			return ((MarkdownOutlineNode)element).getLabel();
		}

		public Image getImage(Object element) {
			return ((MarkdownOutlineNode)element).getImage();
		}
	}

	/** Tree nodes */
	public class OutlineContentProvider implements ITreeContentProvider {

		public Object[] getChildren(Object parentElement) {
			return ((MarkdownOutlineNode)parentElement).findChildOutlineNodes();
		}

		public Object getParent(Object element) {
			return ((MarkdownOutlineNode)element).getParentOutlineNode();
		}

		public boolean hasChildren(Object element) {
			return getChildren(element).length > 0;
		}

		public Object[] getElements(Object inputElement) {
			return getChildren(inputElement);
		}

		public void dispose() { }

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { /* ? */ }		
	}

	/** Editor */
	private MarkdownEditor markdownEditor;
	private MarkdownTextEditor markdownTextEditor;
	private Document document;
	
	/** The view */
	public MarkdownOutlinePage(MarkdownEditor markdownEditor) {
		super();
		
		this.markdownEditor = markdownEditor;
		this.markdownTextEditor = markdownEditor.getMarkdownTextEditor();
		this.document = (Document) markdownTextEditor.getDocumentProvider().getDocument(markdownTextEditor.getEditorInput());
	}

	/** Parse and visit */
	private MarkdownOutlineNode createOutlineFromDocument() {
		
		return MarkdownEditorEngine.updateDocumentOutline(document.get());
	}
	
	/** Customize the content */
	public void createControl(Composite parent) {
		super.createControl(parent);
		
		TreeViewer tree = getTreeViewer();
		tree.setAutoExpandLevel(7);
		tree.setContentProvider(new OutlineContentProvider());
		tree.setLabelProvider(new OutlineLabelProvider());
		tree.setInput(createOutlineFromDocument());
		
		tree.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				Object selected = ((IStructuredSelection) event.getSelection()).getFirstElement();
				if (selected == null) {
					return;
				}
				MarkdownOutlineNode selectedOutlineNode = (MarkdownOutlineNode)selected;
				markdownEditor.activateMarkdownTextEditorPage();
				markdownTextEditor.selectAndReveal(selectedOutlineNode.getStart(), selectedOutlineNode.getLength());
			}
		});
	}
	
	/** When modified */
	public void updateOulineView() {
		
		final TreeViewer tree = getTreeViewer();
		if ((tree != null) && (!tree.getControl().isDisposed())) {
			tree.getControl().getDisplay().asyncExec(new Runnable() {
				public void run() {
					tree.setInput(createOutlineFromDocument());
				};
			});
		}
	}
}
