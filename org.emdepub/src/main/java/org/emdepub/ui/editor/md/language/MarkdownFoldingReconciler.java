/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.ui.editor.md.language;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.reconciler.Reconciler;
import org.eclipse.jface.text.source.projection.ProjectionViewer;

/** Markdown folding reconciler */
public class MarkdownFoldingReconciler extends Reconciler {

    private MarkdownFoldingReconcilerStrategy markdownFoldingReconcilerStrategy;

    public MarkdownFoldingReconciler() {
        markdownFoldingReconcilerStrategy = new MarkdownFoldingReconcilerStrategy();
        this.setReconcilingStrategy(markdownFoldingReconcilerStrategy, IDocument.DEFAULT_CONTENT_TYPE);
    }

    @Override
    public void install(ITextViewer textViewer) {
        super.install(textViewer);
        ProjectionViewer projectionViewer =(ProjectionViewer)textViewer;
        markdownFoldingReconcilerStrategy.setProjectionViewer(projectionViewer);
    }
}