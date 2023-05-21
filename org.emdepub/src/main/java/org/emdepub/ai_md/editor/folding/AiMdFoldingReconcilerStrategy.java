/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.ai_md.editor.folding;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.emdepub.ai_md.parser.AiMdParser;
import org.emdepub.ai_md.parser.ext.references.AiMdReferenceBlock;

import com.vladsch.flexmark.ast.FencedCodeBlock;
import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.ast.IndentedCodeBlock;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeCollectingVisitor;

/** AI Markdown folding reconciler strategy */
public class AiMdFoldingReconcilerStrategy implements IReconcilingStrategy, IReconcilingStrategyExtension {

	private ArrayList<Heading> linearHeadings = new ArrayList<>();
	private ArrayList<FencedCodeBlock> linearFencedCodeBlocks = new ArrayList<>();
	private ArrayList<IndentedCodeBlock> indentedCodeBlocks = new ArrayList<>();

	private ArrayList<AiMdReferenceBlock> aiMdReferenceBlocks = new ArrayList<>();
	                                                     
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

		if (document.get().equals(oldDocument)) {
			return;
		}
		oldDocument = document.get();
		
		long st = System.currentTimeMillis();

		List<Position> newPositions = getNewPositionsOfAnnotations();

		List<Position> oldPositionsToRemove = new ArrayList<>();
		List<Annotation> oldAnnotationsToRemove = new ArrayList<>();

		for (int index = 0; index < oldPositions.size(); index++) {
			Position oldPosition = oldPositions.get(index);
			
			if (!newPositions.contains(oldPosition)) {
				projectionViewer.getProjectionAnnotationModel().removeAnnotation(oldAnnotations.get(index));
				
				oldPositionsToRemove.add(oldPosition);
				oldAnnotationsToRemove.add(oldAnnotations.get(index));
			}
			else {
				newPositions.remove(oldPosition);
				//System.out.println("oldPosition f " + oldPosition);
			}
		}
		oldPositions.removeAll(oldPositionsToRemove);
		oldAnnotations.removeAll(oldAnnotationsToRemove);

		for (Position newPosition : newPositions) {
			
			Annotation newAnnotation = null;
			if (newPosition instanceof AiMdReferenceHeaderFoldingPosition) {
				newAnnotation = new AiMdReferenceHeaderFoldingProjectionAnnotation();
			}
			else {
				newAnnotation = new ProjectionAnnotation();	
			}
			
			projectionViewer.getProjectionAnnotationModel().addAnnotation(newAnnotation, newPosition);
			oldPositions.add(newPosition);
			oldAnnotations.add(newAnnotation);
		}
		
		System.out.println("initialReconcile end " + (System.currentTimeMillis() - st));
	}

	@Override
	public void setProgressMonitor(IProgressMonitor monitor) { /* ILB */ }
	
	/** Here for whole document */
	private List<Position> getNewPositionsOfAnnotations() {

		String markdownString = document.get();
		int lineDelimiterLength = ((Document) document).getDefaultLineDelimiter().length();
		
		ArrayList<Position> positions = new ArrayList<>();
		
		/* Parse document in a node */
		Node documentNode = AiMdParser.getParser().parse(markdownString);
		
		NodeCollectingVisitor nodeCollectingVisitor = new NodeCollectingVisitor(Set.of(Heading.class, FencedCodeBlock.class, IndentedCodeBlock.class, AiMdReferenceBlock.class));
		nodeCollectingVisitor.collect(documentNode);

		linearHeadings.clear();
		linearFencedCodeBlocks.clear();
		indentedCodeBlocks.clear();
		aiMdReferenceBlocks.clear();

		for (Node node : nodeCollectingVisitor.getSubClassingBag().getItems()) {
			if (node instanceof Heading) {
				linearHeadings.add((Heading) node);
			}
			else if (node instanceof FencedCodeBlock) {
				linearFencedCodeBlocks.add((FencedCodeBlock) node);
			}
			else if (node instanceof IndentedCodeBlock) {
				indentedCodeBlocks.add((IndentedCodeBlock) node);
			}
			else {
				aiMdReferenceBlocks.add((AiMdReferenceBlock) node);
			}
		}

		int headingsSize = linearHeadings.size();
		if (headingsSize > 0) {
	    	for (int i = 0; i < headingsSize - 1; i++) {
	    		
	    		Heading headingStart = linearHeadings.get(i);
	    		int positionStart = headingStart.getStartOffset();
	    		int levelStart = headingStart.getLevel();
	    		boolean found = false;
	        	for (int j = i + 1; j < headingsSize; j++) {
	        		Heading headingEnd = linearHeadings.get(j);
	        		int positionEnd = headingEnd.getStartOffset();
	        		if (headingEnd.getLevel() <= levelStart) {
	        			positions.add(new Position(positionStart, positionEnd - positionStart));
	        			found = true;
	        			break;
	        		}
	        	}
	        	if (!found) {
	        		positions.add(new Position(positionStart, markdownString.length() - (positionStart)));	
	        	}
	    	}
	    	int lastHeadingStart = linearHeadings.get(headingsSize - 1).getStartOffset();
	    	positions.add(new Position(lastHeadingStart, markdownString.length() - lastHeadingStart));
		}

    	for (FencedCodeBlock fencedCodeBlock : linearFencedCodeBlocks) {
    		int fencedCodeBlockStart = fencedCodeBlock.getStartOffset();
    		positions.add(new Position(fencedCodeBlockStart, fencedCodeBlock.getEndOffset() - (fencedCodeBlockStart - lineDelimiterLength)));
    	}

    	for (IndentedCodeBlock indentedCodeBlock : indentedCodeBlocks) {
    		int indentedCodeBlockStart = indentedCodeBlock.getStartOffset();
    		positions.add(new Position(indentedCodeBlockStart, indentedCodeBlock.getEndOffset() - (indentedCodeBlockStart - lineDelimiterLength)));
    	}

    	for (AiMdReferenceBlock aiMdReferenceBlock : aiMdReferenceBlocks) {
    		int aiMdReferenceBlockStart = aiMdReferenceBlock.getStartOffset();
    		//System.out.println(aiMdReferenceBlock.getStartOffset() + " -- " + aiMdReferenceBlock.getHeaderEndBasedSequence().getStartOffset() + " he");
    		positions.add(new AiMdReferenceHeaderFoldingPosition(aiMdReferenceBlockStart,
    				(aiMdReferenceBlock.getHeaderEndBasedSequence().getStartOffset() - aiMdReferenceBlockStart) + lineDelimiterLength + 1));
    	}
    	
		return positions;
	}

}