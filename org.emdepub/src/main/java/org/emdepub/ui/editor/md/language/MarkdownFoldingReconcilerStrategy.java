/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.ui.editor.md.language;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
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
import org.emdepub.ui.editor.md.engine.MarkdownOutlineEngine;

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

    @Override
    public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
        initialReconcile();
    }

    @Override
    public void reconcile(IRegion partition) {
        initialReconcile();
    }

    @Override
    public void initialReconcile() {
		if(document.get().equals(oldDocument)) return;
		oldDocument = document.get();

		List<Position> positions = getNewPositionsOfAnnotations();

		List<Position> positionsToRemove = new ArrayList<>();
		List<Annotation> annotationToRemove = new ArrayList<>();

		for (Position position : oldPositions) {
			if(!positions.contains(position)) {
				projectionViewer.getProjectionAnnotationModel().removeAnnotation(oldAnnotations.get(oldPositions.indexOf(position)));
				positionsToRemove.add(position);
				annotationToRemove.add(oldAnnotations.get(oldPositions.indexOf(position)));
			}else {
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

    private static enum SearchingFor {
         START_OF_TAG, START_OF_WORD, END_OF_WORD, END_OF_LINE
    }

    private static final ArrayList<Pattern> patterns = new ArrayList<>();
    static {
//    	patterns.add(Pattern.compile("^\\s*#\\s+\\S", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE));
//    	patterns.add(Pattern.compile("^\\s*##\\s+\\S", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE));	
//    	patterns.add(Pattern.compile("^\\s*###\\s+\\S", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE));	
//    	patterns.add(Pattern.compile("^\\s*####\\s+\\S", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE));	
//    	patterns.add(Pattern.compile("^\\s*#####\\s+\\S", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE));	
//    	patterns.add(Pattern.compile("^\\s*######\\s+\\S", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE));	

    	patterns.add(Pattern.compile("^#\\s+\\S", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE));
    	patterns.add(Pattern.compile("^##\\s+\\S", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE));	
    	patterns.add(Pattern.compile("^###\\s+\\S", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE));	
    	patterns.add(Pattern.compile("^####\\s+\\S", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE));	
    	patterns.add(Pattern.compile("^#####\\s+\\S", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE));	
    	patterns.add(Pattern.compile("^######\\s+\\S", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE));	
    }
    
    /** Here for whole document */
    private List<Position> getNewPositionsOfAnnotations(){
        
//    	ArrayList<Position> headerPositions = new ArrayList<>();
//
//    	String wholeDocument = document.get();
//    	LinkedHashMap<Integer, Integer> startsLevels = new LinkedHashMap<>();
//
//   		for (int index = 0; index < 6; index++) {
//            Matcher matcher = patterns.get(index).matcher(wholeDocument);
//            boolean matchFound = matcher.find();
//            while (matchFound) {
//            	startsLevels.put(matcher.start(), index);
//            	matchFound = matcher.find();
//            }
//    	};
//    	if (startsLevels.size() > 0) {
//        	ArrayList<Integer> starts = new ArrayList<Integer>(startsLevels.keySet());
//        	int startsLength = starts.size();
//        	Collections.sort(starts);
//        	for (int i = 0; i < startsLength - 1; i++) {
//        		int positionStart = starts.get(i);
//        		boolean found = false;
//            	for (int j = i + 1; j < startsLength; j++) {
//            		int positionEnd = starts.get(j);
//            		if (startsLevels.get(positionEnd) <= startsLevels.get(positionStart)) {
//            			headerPositions.add(new Position(positionStart, positionEnd - positionStart));
//            			found = true;
//            			break;
//            		}
//            	}
//            	if (!found) {
//            		headerPositions.add(new Position(positionStart, wholeDocument.length() - (positionStart)));	
//            	}
//        	}
//        	headerPositions.add(new Position(starts.get(startsLength - 1), wholeDocument.length() - (starts.get(startsLength - 1))));
//
//        	//return headerPositions;
//    	}
//    	
//		List<Position> positions = new ArrayList<>();
//		Map<String, Integer> startOfAnnotation = new HashMap<>();
//		SearchingFor searchingFor = SearchingFor.START_OF_TAG;
//
//		int characters = document.getLength();
//		int currentCharIndex = 0;
//
//		int wordStartIndex = 0;
//		int sectionStartIndex = 0;
//		String word = "";
//
//		try {
//			while (currentCharIndex < characters) {
//				char currentChar = document.getChar(currentCharIndex);
//				switch (searchingFor) {
//				case START_OF_TAG:
//					if (currentChar == '<') {
//						char nextChar = document.getChar(currentCharIndex + 1);
//						if (nextChar != '?') {
//							sectionStartIndex = currentCharIndex;
//							searchingFor = SearchingFor.START_OF_WORD;
//						}
//					}
//					break;
//				case START_OF_WORD:
//					if (Character.isLetter(currentChar)) {
//						wordStartIndex = currentCharIndex;
//						searchingFor = SearchingFor.END_OF_WORD;
//					}
//					break;
//				case END_OF_WORD:
//					if (!Character.isLetter(currentChar)) {
//						word = document.get(wordStartIndex, currentCharIndex - wordStartIndex);
//						if (startOfAnnotation.containsKey(word)) {
//							searchingFor = SearchingFor.END_OF_LINE;
//						} else {
//							startOfAnnotation.put(word, sectionStartIndex);
//							searchingFor = SearchingFor.START_OF_TAG;
//						}
//					}
//					break;
//				case END_OF_LINE:
//					if (currentChar == '\n') {
//						int start = startOfAnnotation.get(word);
//						if (document.getLineOfOffset(start) != document.getLineOfOffset(currentCharIndex)) {
//							positions.add(new Position(start, currentCharIndex + 1 - start));
//						}
//						startOfAnnotation.remove(word);
//						searchingFor = SearchingFor.START_OF_TAG;
//					}
//					break;
//				}
//				currentCharIndex++;
//			}
//		} catch (BadLocationException badLocationException) { /* skip the remainder of file due to error */ }
//        
//        positions.addAll(headerPositions);
        
    	MarkdownFoldingEngine markdownFoldingEngine = new MarkdownFoldingEngine();
		
		return markdownFoldingEngine.runFolding(document.get());
    }

    @Override
    public void setProgressMonitor(IProgressMonitor monitor) { }

}