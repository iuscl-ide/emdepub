/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.ai_md.editor.outline;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.reconciler.Reconciler;

/** Markdown outline reconciler */
public class AiMdOutlineReconciler extends Reconciler {

	public AiMdOutlineReconciler() {
		this.setReconcilingStrategy(new AiMdOutlineReconcilerStrategy(), IDocument.DEFAULT_CONTENT_TYPE);
	}
}