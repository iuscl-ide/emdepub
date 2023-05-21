/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.ai_md.editor.language.folding;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.tm4e.core.model.TMModel;
import org.eclipse.tm4e.ui.TMUIPlugin;
import org.emdepub.activator.L;
import org.emdepub.ai_md.editor.AiMdTextEditor;
import org.emdepub.markdown.editor.engine.MarkdownFoldingEngine;

import lombok.SneakyThrows;

/** AI Markdown folding reconciler strategy */
public class AiMdFoldingReconcilerStrategy implements IReconcilingStrategy, IReconcilingStrategyExtension {

	public static final String BLOCK_HEADING_LINE = "punctuation.definition.markup.heading.markdown";
	
	public static final String BLOCK_FENCED_CODE_LINE = "block.punctuation.definition.markdown.markup.fenced_code";
	                                                     
	private IDocument document;
	private ProjectionViewer projectionViewer;

//	private List<Annotation> oldAnnotations = new ArrayList<>();
//	private List<Position> oldPositions = new ArrayList<>();

	@Override
	public void setDocument(IDocument document) {
		this.document = document;
	}

	public void setProjectionViewer(ProjectionViewer projectionViewer) {
		this.projectionViewer = projectionViewer;
	}

	/** Folding */
	@Override
	@SneakyThrows(BadLocationException.class)
	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {

//		initialReconcile();
		
		//partialReconcile(0, document.getLength());
		
		int beginLine = document.getLineOfOffset(subRegion.getOffset());
		int endLine = document.getLineOfOffset(subRegion.getOffset() + subRegion.getLength());

		System.out.println("beginLine " + beginLine + " " + endLine);
		
		TMModel tmDocumentModel = (TMModel) TMUIPlugin.getTMModelManager().connect(document);
		final int numberOfLines = tmDocumentModel.getNumberOfLines();
		
		int lineUp = beginLine;
		int lineUpFound = -1;
		do {
			String lineUpType = tmDocumentModel.getLineTokens(lineUp).get(0).type;
//			L.i(lineUpType);
			if ((lineUpType.contains("punctuation") && lineUpType.contains("heading")) ||
					lineUpType.equals(AiMdTextEditor.BLOCK_REFERENCE_BEGIN)) {
				lineUpFound = lineUp;
				break;
			}
			lineUp--;
		} while (lineUp > -1);
		
		if (lineUpFound == -1) {
			lineUpFound = beginLine;
		}
		
		int lineDown = endLine;
		int lineDownFound = -1;
		do {
			String lineDownType = tmDocumentModel.getLineTokens(lineDown).get(0).type;
//			L.i(lineDownType);
			if ((lineDownType.contains("punctuation") && lineDownType.contains("heading")) ||
					lineDownType.equals(AiMdTextEditor.BLOCK_REFERENCE_END)) {
				lineDownFound = lineDown;
				break;
			}
			lineDown++;
		} while (lineDown < numberOfLines);
		
		if (lineDownFound == -1) {
			lineDownFound = endLine;
		}

		int startOffset = document.getLineOffset(lineUpFound);
		int length = document.getLineOffset(lineDownFound) - startOffset;

		System.out.println("reconcile " + startOffset + " " + length);

		ProjectionAnnotationModel projectionAnnotationModel = projectionViewer.getProjectionAnnotationModel();
		
		Iterator<Annotation> projectionAnnotationModelAnnotationIterator = projectionAnnotationModel.
				getAnnotationIterator(startOffset, length, true, true);
		
		while (projectionAnnotationModelAnnotationIterator.hasNext()) {
			ProjectionAnnotation projectionAnnotation = (ProjectionAnnotation) projectionAnnotationModelAnnotationIterator.next();
//			removeAnnotations.add(projectionAnnotation);
			Position position = projectionAnnotationModel.getPosition(projectionAnnotation);
//			if (startOffset > position.getOffset()) {
//				startOffset = position.getOffset();
//			}
//			if (endOffset < position.getOffset() + position.getLength()) {
//				endOffset = position.getOffset() + position.getLength();
//			}
			System.out.println("projectionAnnotation " + position.getOffset() + " " + (position.getOffset() + position.getLength()));
		}
		
		
		
		
//		ProjectionAnnotationModel projectionAnnotationModel = projectionViewer.getProjectionAnnotationModel();
//		projectionAnnotationModel.removeAllAnnotations();
//		
//		partialReconcile(0, document.getLength());

//		ProjectionAnnotationModel projectionAnnotationModel = projectionViewer.getProjectionAnnotationModel();
//		
////		System.out.println("subRegion " + subRegion);
//		
//		List<Annotation> removeAnnotations = new ArrayList<>();
//		int startOffset = subRegion.getOffset(); 
//		int endOffset = subRegion.getOffset() + subRegion.getLength();
//
//		Iterator<Annotation> projectionAnnotationModelAnnotationIterator = null;
//		if (dirtyRegion.getType().equals(DirtyRegion.REMOVE)) {
//			projectionAnnotationModelAnnotationIterator = projectionAnnotationModel.getAnnotationIterator(subRegion.getOffset() - 1, 2, true, true);
//		}
//		else if (dirtyRegion.getType().equals(DirtyRegion.INSERT)) {
//			projectionAnnotationModelAnnotationIterator = projectionAnnotationModel.getAnnotationIterator(subRegion.getOffset() - 1, subRegion.getLength() + 1, true, true);
//		}
//		
//		while (projectionAnnotationModelAnnotationIterator.hasNext()) {
//			ProjectionAnnotation projectionAnnotation = (ProjectionAnnotation) projectionAnnotationModelAnnotationIterator.next();
//			removeAnnotations.add(projectionAnnotation);
//			Position position = projectionAnnotationModel.getPosition(projectionAnnotation);
//			if (startOffset > position.getOffset()) {
//				startOffset = position.getOffset();
//			}
//			if (endOffset < position.getOffset() + position.getLength()) {
//				endOffset = position.getOffset() + position.getLength();
//			}
////			System.out.println("projectionAnnotation " + position.getOffset() + " " + (position.getOffset() + position.getLength()));
//		}
//
////		Iterator<Annotation> projectionAnnotationModelAnnotationIterator2 = projectionAnnotationModel.getAnnotationIterator();
////		while (projectionAnnotationModelAnnotationIterator2.hasNext()) {
////			ProjectionAnnotation projectionAnnotation = (ProjectionAnnotation) projectionAnnotationModelAnnotationIterator2.next();
////			System.out.println("projectionAnnotation2 " + projectionAnnotationModel.getPosition(projectionAnnotation));
////		}
//		
//		for (Annotation annotation : removeAnnotations) {
//			projectionAnnotationModel.removeAnnotation(annotation);	
//		}
//		
////		Iterator<Annotation> projectionAnnotationModelAnnotationIterator3 = projectionAnnotationModel.getAnnotationIterator();
////		while (projectionAnnotationModelAnnotationIterator3.hasNext()) {
////			ProjectionAnnotation projectionAnnotation = (ProjectionAnnotation) projectionAnnotationModelAnnotationIterator3.next();
////			System.out.println("projectionAnnotation3 " + projectionAnnotationModel.getPosition(projectionAnnotation));
////		}
//		
//		partialReconcile(startOffset, endOffset);
	}

	/** Folding */
	@Override
	public void reconcile(IRegion partition) {
		
		L.e("Knowledge exception", new Exception("AiMdFoldingReconcilerStrategy reconcile(IRegion partition): should not enter here"));
	}

	/** Here for whole document */
	@Override
	public void initialReconcile() {
		
//		List<Position> newPositions = getNewPositionsOfAnnotations();
//
//		List<Position> oldPositionsToRemove = new ArrayList<>();
//		List<Annotation> oldAnnotationsToRemove = new ArrayList<>();
//
//		for (int index = 0; index < oldPositions.size(); index++) {
//			Position oldPosition = oldPositions.get(index);
//			
//			if (!newPositions.contains(oldPosition)) {
//				projectionViewer.getProjectionAnnotationModel().removeAnnotation(oldAnnotations.get(index));
//				
//				oldPositionsToRemove.add(oldPosition);
//				oldAnnotationsToRemove.add(oldAnnotations.get(index));
//			}
//			else {
//				newPositions.remove(oldPosition);
//				//System.out.println("oldPosition f " + oldPosition);
//			}
//		}
//		oldPositions.removeAll(oldPositionsToRemove);
//		oldAnnotations.removeAll(oldAnnotationsToRemove);
//
//		for (Position newPosition : newPositions) {
//			Annotation newAnnotation = new ProjectionAnnotation();
//			projectionViewer.getProjectionAnnotationModel().addAnnotation(newAnnotation, newPosition);
//			oldPositions.add(newPosition);
//			oldAnnotations.add(newAnnotation);
//		}
		
		partialReconcile(0, document.getLength());
	}

	@Override
	public void setProgressMonitor(IProgressMonitor monitor) { /* ILB */ }
	
	/** Here for part of document */
	@SneakyThrows(BadLocationException.class)
	public void partialReconcile(int startOffset, int endOffset) {

//		Iterator<org.eclipse.jface.text.source.Annotation> projectionAnnotationModelAnnotationIterator = projectionViewer.getProjectionAnnotationModel().getAnnotationIterator();
//		
//		while (projectionAnnotationModelAnnotationIterator.hasNext()) {
//			ProjectionAnnotation projectionAnnotation = (ProjectionAnnotation) projectionAnnotationModelAnnotationIterator.next();
//			if (!projectionAnnotation.isCollapsed()) {
//				projectionViewer.getProjectionAnnotationModel().removeAnnotation(projectionAnnotation);	
//			}
//		}
	

		
		//projectionViewer.getProjectionAnnotationModel().removeAllAnnotations();
		
		//long st = System.currentTimeMillis();
		//System.out.println("partialReconcile f " + startOffset + " - " + endOffset);
		
		int startLineIndex = document.getLineOfOffset(startOffset);
		int endLineIndex = document.getLineOfOffset(endOffset);
		
		ProjectionAnnotationModel projectionAnnotationModel = projectionViewer.getProjectionAnnotationModel();
		
		/** Here for whole document */
		TMModel tmDocumentModel = (TMModel) TMUIPlugin.getTMModelManager().connect(document);
		
//		long st = System.currentTimeMillis();
		int startHeadingLineIndex = -1;
		int startReferenceHeaderLineIndex = -1;
		int startFencedCodeLineIndex = -1;
		for (int lineIndex = startLineIndex; lineIndex < endLineIndex; lineIndex++) {
//			List<TMToken> lineTokens = tmDocumentModel.getLineTokens(lineIndex);
//			System.out.println(lineTokens);

			while (tmDocumentModel.getLineTokens(lineIndex).size() == 0) {
				System.out.println("size " + lineIndex);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException interruptedException) { }
			}

			String lineType = tmDocumentModel.getLineTokens(lineIndex).get(0).type;
//			System.out.println(lineType);
			switch (lineType) {
			case AiMdTextEditor.BLOCK_REFERENCE_BEGIN:
				startReferenceHeaderLineIndex = lineIndex;
				break;
			case AiMdTextEditor.BLOCK_HEADER_END:
				if (startReferenceHeaderLineIndex > -1) {
					int startReferenceHeaderOffset = document.getLineOffset(startReferenceHeaderLineIndex);
					Position position = new Position(startReferenceHeaderOffset, document.getLineOffset(lineIndex + 1) - startReferenceHeaderOffset);
					projectionAnnotationModel.addAnnotation(new AiMdReferenceHeaderFoldingProjectionAnnotation(), position);
				}
				startReferenceHeaderLineIndex = -1;
				break;
			case AiMdTextEditor.BLOCK_REFERENCE_END:
				startReferenceHeaderLineIndex = -1;
				break;
			case BLOCK_FENCED_CODE_LINE:
				if (startFencedCodeLineIndex > -1) {
					int startFencedCodeOffset = document.getLineOffset(startFencedCodeLineIndex);
					Position position = new Position(startFencedCodeOffset, document.getLineOffset(lineIndex + 1) - startFencedCodeOffset);
					projectionAnnotationModel.addAnnotation(new ProjectionAnnotation(), position);
					startFencedCodeLineIndex = -1;
				}
				else {
					startFencedCodeLineIndex = lineIndex;
				}
				break;
			default:
				if (lineType.contains("punctuation") && lineType.contains("heading")) {
					if (startHeadingLineIndex > -1) {
						int startHeadingOffset = document.getLineOffset(startHeadingLineIndex);
						Position position = new Position(startHeadingOffset, document.getLineOffset(lineIndex) - startHeadingOffset);
						projectionAnnotationModel.addAnnotation(new ProjectionAnnotation(), position);
					}
					startHeadingLineIndex = lineIndex;
				}
				break;
			}
		}

		/* Last heading */
		if (startHeadingLineIndex > -1) {
			int startHeadingOffset = document.getLineOffset(startHeadingLineIndex);
			Position position = new Position(startHeadingOffset, endOffset - startHeadingOffset);
			projectionAnnotationModel.addAnnotation(new ProjectionAnnotation(), position);
		}
		
//		long st = System.currentTimeMillis();
		List<Integer> cach = new ArrayList<>(endLineIndex - startLineIndex);
		for (int lineIndex = startLineIndex; lineIndex < endLineIndex; lineIndex++) {
			
			String lineType = tmDocumentModel.getLineTokens(lineIndex).get(0).type;
			
			cach.add(10);
//			for (int lineIndex2 = lineIndex; lineIndex2 < lineIndex + 10; lineIndex2++) {
//				String lineType2 = tmDocumentModel.getLineTokens(lineIndex2).get(0).type;
//				//String lineType = tmDocumentModel.getLineTokens(lineIndex).get(0).type;
//			}
		}
		
		long st = System.currentTimeMillis();
		for (int lineIndex = startLineIndex; lineIndex < endLineIndex - 10002; lineIndex++) {
			String lineType = cach.get(lineIndex) + "";
			for (int lineIndex2 = lineIndex; lineIndex2 < lineIndex + 10000; lineIndex2++) {
				String lineType2 = cach.get(lineIndex2) + "";
				//String lineType = tmDocumentModel.getLineTokens(lineIndex).get(0).type;
			}
		}

		
		System.out.println("partialReconcile end " + (System.currentTimeMillis() - st));
	}
	
//	/** Here for whole document */
//	@SneakyThrows(BadLocationException.class)
//	private List<Position> getNewPositionsOfAnnotations() {
//		long st = System.currentTimeMillis();
//		List<Position> positions = new ArrayList<>();
//		
//		int startOffset = 0;
//		int endOffset = document.getLength();
//		
//		int startLineIndex = document.getLineOfOffset(startOffset);
//		int endLineIndex = document.getLineOfOffset(endOffset);
//		
//		//ProjectionAnnotationModel projectionAnnotationModel = projectionViewer.getProjectionAnnotationModel();
//		
//		/** Here for whole document */
//		TMModel tmDocumentModel = (TMModel) TMUIPlugin.getTMModelManager().connect(document);
//		
////		long st = System.currentTimeMillis();
//		int startHeadingLineIndex = -1;
//		int startReferenceHeaderLineIndex = -1;
//		int startFencedCodeLineIndex = -1;
//		for (int lineIndex = startLineIndex; lineIndex < endLineIndex; lineIndex++) {
////			List<TMToken> lineTokens = tmDocumentModel.getLineTokens(lineIndex);
////			System.out.println(lineTokens);
//
//			while (tmDocumentModel.getLineTokens(lineIndex).size() == 0) {
//				System.out.println("size " + lineIndex);
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException interruptedException) { }
//			}
//
//			String lineType = tmDocumentModel.getLineTokens(lineIndex).get(0).type;
////			System.out.println(lineType);
//			switch (lineType) {
//			case AiMdTextEditor.BLOCK_REFERENCE_BEGIN:
//				startReferenceHeaderLineIndex = lineIndex;
//				break;
//			case AiMdTextEditor.BLOCK_HEADER_END:
//				if (startReferenceHeaderLineIndex > -1) {
//					int startReferenceHeaderOffset = document.getLineOffset(startReferenceHeaderLineIndex);
//					Position position = new Position(startReferenceHeaderOffset, document.getLineOffset(lineIndex + 1) - startReferenceHeaderOffset);
//					positions.add(position);
//					//projectionAnnotationModel.addAnnotation(new AiMdReferenceHeaderFoldingProjectionAnnotation(), position);
//				}
//				startReferenceHeaderLineIndex = -1;
//				break;
//			case AiMdTextEditor.BLOCK_REFERENCE_END:
//				startReferenceHeaderLineIndex = -1;
//				break;
//			case BLOCK_FENCED_CODE_LINE:
//				if (startFencedCodeLineIndex > -1) {
//					int startFencedCodeOffset = document.getLineOffset(startFencedCodeLineIndex);
//					Position position = new Position(startFencedCodeOffset, document.getLineOffset(lineIndex + 1) - startFencedCodeOffset);
//					positions.add(position);
//					//projectionAnnotationModel.addAnnotation(new ProjectionAnnotation(), position);
//					startFencedCodeLineIndex = -1;
//				}
//				else {
//					startFencedCodeLineIndex = lineIndex;
//				}
//				break;
//			default:
//				if (lineType.contains("punctuation") && lineType.contains("heading")) {
//					if (startHeadingLineIndex > -1) {
//						int startHeadingOffset = document.getLineOffset(startHeadingLineIndex);
//						Position position = new Position(startHeadingOffset, document.getLineOffset(lineIndex) - startHeadingOffset);
//						positions.add(position);
//						//projectionAnnotationModel.addAnnotation(new ProjectionAnnotation(), position);
//					}
//					startHeadingLineIndex = lineIndex;
//				}
//				break;
//			}
//		}
//
//		/* Last heading */
//		if (startHeadingLineIndex > -1) {
//			int startHeadingOffset = document.getLineOffset(startHeadingLineIndex);
//			Position position = new Position(startHeadingOffset, endOffset - startHeadingOffset);
//			positions.add(position);
////			projectionAnnotationModel.addAnnotation(new ProjectionAnnotation(), position);
//		}
//		System.out.println("getNewPositionsOfAnnotations end " + (System.currentTimeMillis() - st));
//		return positions;
//	}

}