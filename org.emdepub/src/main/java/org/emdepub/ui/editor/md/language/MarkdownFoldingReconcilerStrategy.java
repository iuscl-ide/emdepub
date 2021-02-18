/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.ui.editor.md.language;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.emdepub.ui.editor.md.engine.MarkdownFoldingEngine;

/** Markdown folding reconciler strategy */
public class MarkdownFoldingReconcilerStrategy implements IReconcilingStrategy, IReconcilingStrategyExtension {

	private IDocument document;
	private String oldDocument;
	private ProjectionViewer projectionViewer;
	private List<Annotation> oldAnnotations = new ArrayList<>();
	private List<Position> oldPositions = new ArrayList<>();

	@Override
	public void setDocument(IDocument document) {
		this.document = document;
	}

	public void setProjectionViewer(ProjectionViewer projectionViewer) {
		this.projectionViewer = projectionViewer;
	}

	/** Folding */
	@Override
	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		initialReconcile();
	}

	/** Folding */
	@Override
	public void reconcile(IRegion partition) {
		initialReconcile();
	}

	/** Here for whole document */
	@Override
	public void initialReconcile() {

		if (document.get().equals(oldDocument))
			return;
		oldDocument = document.get();

		List<Position> positions = getNewPositionsOfAnnotations();

		List<Position> positionsToRemove = new ArrayList<>();
		List<Annotation> annotationToRemove = new ArrayList<>();

		for (Position position : oldPositions) {
			if (!positions.contains(position)) {
				projectionViewer.getProjectionAnnotationModel()
						.removeAnnotation(oldAnnotations.get(oldPositions.indexOf(position)));
				positionsToRemove.add(position);
				annotationToRemove.add(oldAnnotations.get(oldPositions.indexOf(position)));
			} else {
				positions.remove(position);
			}
		}
		oldPositions.removeAll(positionsToRemove);
		oldAnnotations.removeAll(annotationToRemove);

		for (Position position : positions) {
			Annotation annotation = new ProjectionAnnotation();
			projectionViewer.getProjectionAnnotationModel().addAnnotation(annotation, position);
			oldPositions.add(position);
			oldAnnotations.add(annotation);
		}
	}

	/** Here for whole document */
	private List<Position> getNewPositionsOfAnnotations() {

		MarkdownFoldingEngine markdownFoldingEngine = new MarkdownFoldingEngine();

		return markdownFoldingEngine.runFolding(document.get());
	}

	@Override
	public void setProgressMonitor(IProgressMonitor monitor) {
	}

}