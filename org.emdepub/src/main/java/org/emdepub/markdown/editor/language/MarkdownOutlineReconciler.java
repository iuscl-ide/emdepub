/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.markdown.editor.language;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.reconciler.Reconciler;

/** Markdown outline reconciler */
public class MarkdownOutlineReconciler extends Reconciler {

	public MarkdownOutlineReconciler() {
		this.setReconcilingStrategy(new MarkdownOutlineReconcilerStrategy(), IDocument.DEFAULT_CONTENT_TYPE);
	}
}