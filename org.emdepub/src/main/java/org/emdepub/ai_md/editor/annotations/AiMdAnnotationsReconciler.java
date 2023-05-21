package org.emdepub.ai_md.editor.annotations;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.reconciler.Reconciler;

public class AiMdAnnotationsReconciler extends Reconciler {

	public AiMdAnnotationsReconciler() {
		this.setReconcilingStrategy(new AiMdAnnotationsReconcilerStrategy(), IDocument.DEFAULT_CONTENT_TYPE);
	}
}