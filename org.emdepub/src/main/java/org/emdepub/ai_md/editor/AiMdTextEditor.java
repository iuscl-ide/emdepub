/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.ai_md.editor;

import java.lang.reflect.Field;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.internal.genericeditor.ExtensionBasedTextEditor;
import org.eclipse.ui.internal.genericeditor.GenericEditorContentAssistant;
import org.emdepub.activator.L;
import org.emdepub.ai_md.editor.folding.AiMdReferenceHeaderFoldingProjectionAnnotation;
import org.emdepub.ai_md.editor.range_indication.AiMdRangeIndicationStrategy;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;

/** EPubProject text editor based on the new generic editor */
@FieldDefaults(level=AccessLevel.PRIVATE)
@SuppressWarnings("restriction")
public class AiMdTextEditor extends ExtensionBasedTextEditor {
	
	public static final String BLOCK_REFERENCE_BEGIN = "ai_md_reference_block.text.reference_begin";
	public static final String BLOCK_HEADER_END = "ai_md_reference_block.text.header_end";
	public static final String BLOCK_REFERENCE_END = "ai_md_reference_block.text.reference_end";
	//public static final String BLOCK_HEADER_BEGIN_LINE = "ai_md_reference_block.text.header_begin";
	public static final String BLOCK_TEXT_LINE = "ai_md_reference_block.text";

	public static final String ID = "id";
	
	static Field fContentAssistant_Field;
	
	static {
		try {
			fContentAssistant_Field = SourceViewer.class.getDeclaredField("fContentAssistant");
			fContentAssistant_Field.setAccessible(true);
		} catch (NoSuchFieldException | SecurityException exception) {
			L.e("Reflection error for: fContentAssistant", exception);
		}
	}

	public static final String MARKER_REFERENCE_BEGIN = "org.emdepub.ai-md.reference.begin.marker";
	public static final String MARKER_REFERENCE_TEXT_BEGIN = "org.emdepub.ai-md.reference.text.begin.marker";
	public static final String MARKER_REFERENCE_TEXT_END = "org.emdepub.ai-md.reference.text.end.marker";

//	public static final String MARKER_REFERENCE_BEGIN = "org.emdepub.ai-md.reference.line.text.marker";
//	public static final String MARKER_REFERENCE_TEXT_BEGIN = "org.emdepub.ai-md.reference.start.line.text.marker";
//	public static final String MARKER_REFERENCE_TEXT_END = "org.emdepub.ai-md.reference.end.line.text.marker";

	@Getter
	IResource resource;
	@Getter
	IDocument document;
	ISourceViewer sourceViewer;
	
//	final Map<String, IMarker> referenceLineMarkers = new LinkedHashMap<>();
//	final Map<String, IMarker> referenceStartLineMarkers = new LinkedHashMap<>();
//	final Map<String, IMarker> referenceEndLineMarkers = new LinkedHashMap<>();
	
//	@Getter
//	AIMdReferences aiMdReferences = new AIMdReferences();

	AiMdRangeIndicationStrategy aiMdRangeIndicationStrategy;
	
	@Override
	@SneakyThrows(IllegalAccessException.class)
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		
		resource = this.getEditorInput().getAdapter(IFile.class);
		document = this.getDocumentProvider().getDocument(this.getEditorInput());
		sourceViewer = this.getSourceViewer();
		
		aiMdRangeIndicationStrategy = new AiMdRangeIndicationStrategy(document, sourceViewer);
		
		GenericEditorContentAssistant genericEditorContentAssistant = (GenericEditorContentAssistant) fContentAssistant_Field.get(sourceViewer);
		genericEditorContentAssistant.enableAutoActivateCompletionOnType(false);
	}
	
	/** For status bar, used also from exterior */
	public String getCursorPositionString() {
		
		return this.getCursorPosition();
	}
	
	/** For status bar */
	@Override
	protected void handleCursorPositionChanged() {

//		L.i("handleCursorPositionChanged " + this.getCursorPosition());
		
		AiMdEditorContributor.getStatusLinePositionField().setText(this.getCursorPosition());

		super.handleCursorPositionChanged();
		
		updateRangeIndication();
	}

	public ISourceViewer findSourceViewer() {
		
		return sourceViewer; 
	}

	public void updateRangeIndication() {

		aiMdRangeIndicationStrategy.changeRangeIndication(Integer.valueOf(this.getCursorPosition().split(":")[0].trim().strip()) - 1);
		
//		sourceViewer.removeRangeIndication();
//
//		int line = Integer.valueOf(this.getCursorPosition().split(":")[0].trim().strip()) - 1;
//
//		TMModel tmDocumentModel = (TMModel) TMUIPlugin.getTMModelManager().connect(document);
//		final int numberOfLines = tmDocumentModel.getNumberOfLines();
////		List<TMToken> tokens = tmDocumentModel.getLineTokens(lineIndex);
//		if (!tmDocumentModel.getLineTokens(line).get(0).type.equals(BLOCK_TEXT_LINE)) {
//			return;
//		}
//		
//		int lineUp = line;
//		int lineUpFound = -1;
//		do {
//			String lineUpType = tmDocumentModel.getLineTokens(lineUp).get(0).type;
////			L.i(lineUpType);
//			if (lineUpType.equals(BLOCK_HEADER_END)) {
//				lineUpFound = lineUp;
//				break;
//			}
//			if (lineUpType.equals(BLOCK_REFERENCE_BEGIN) || lineUpType.equals(BLOCK_REFERENCE_END)) {
//				break;
//			}
//			lineUp--;
//		} while (lineUp > -1);
//		
//		if (lineUpFound == -1) {
//			return;
//		}
//		
//		int lineDown = line;
//		int lineDownFound = -1;
//		do {
//			String lineDownType = tmDocumentModel.getLineTokens(lineDown).get(0).type;
////			L.i(lineDownType);
//			if (lineDownType.equals(BLOCK_REFERENCE_END)) {
//				lineDownFound = lineDown;
//				break;
//			}
//			if (lineDownType.equals(BLOCK_REFERENCE_BEGIN) || lineDownType.equals(BLOCK_HEADER_END)) {
//				break;
//			}
//			lineDown++;
//		} while (lineDown < numberOfLines);
//		
//		if (lineDownFound == -1) {
//			return;
//		}
//
//		int startOffset = document.getLineOffset(lineUpFound + 1);
//		int length = document.getLineOffset(lineDownFound) - startOffset;
//		sourceViewer.setRangeIndication(startOffset, length, false);

		
//		IMarker referenceStartMarkers[] = resource.findMarkers(REFERENCE_START_LINE, false, IResource.DEPTH_INFINITE);
//		IMarker referenceEndMarkers[] = resource.findMarkers(REFERENCE_END_LINE, false, IResource.DEPTH_INFINITE);
//		
//		ArrayList<Integer> referenceStarts = new ArrayList<>(Arrays.stream(referenceStartMarkers).
//				map(marker -> MarkerUtilities.getLineNumber(marker)).collect(Collectors.toList()));
//		Collections.sort(referenceStarts);
//		ArrayList<Integer> referenceEnds = new ArrayList<>(Arrays.stream(referenceEndMarkers).
//				map(marker -> MarkerUtilities.getLineNumber(marker)).collect(Collectors.toList()));
//		Collections.sort(referenceEnds);
//		
//		
//		for (int index = 0; index < Math.min(referenceStarts.size(), referenceEnds.size()); index++) {
//			if ((referenceStarts.get(index) < line) && (referenceEnds.get(index) > line)) {
//			
//				int startOffset = document.getLineOffset(referenceStarts.get(index));
//				int length = document.getLineOffset(referenceEnds.get(index) - 1) - startOffset;
//				sourceViewer.setRangeIndication(startOffset, length, false);
//				
//				break;
//			}
//		}
		
//		int startOffset = document.getLineOffset(treeSet.floor(line));
//		int length = document.getLineOffset(treeSet.ceiling(line) - 1) - startOffset;
//		L.i("updateRangeIndication " + line + treeSet.floor(line) + " " + treeSet.ceiling(line) + "  " + startOffset + " " + length);
//		this.findSourceViewer().setRangeIndication(startOffset, length, false);
		
		
//		for (AIMdReferenceText aiMdReferenceText : aiMdReferences.getReferences()) {
//			if ((aiMdReferenceText.getTextStartLine() < line) && (aiMdReferenceText.getTextEndLine() > line)) {
//				
//				int startOffset = document.getLineOffset(aiMdReferenceText.getTextStartLine());
//				int length = document.getLineOffset(aiMdReferenceText.getTextEndLine() - 1) - startOffset;
//				this.findSourceViewer().setRangeIndication(startOffset, length, false);
//				
//				break;
//			}
//		}
	}
	
	public void collapseAllReferenceFoldingAnnotations() {
		
		ProjectionAnnotationModel projectionAnnotationModel = ((ProjectionViewer) this.getSourceViewer()).getProjectionAnnotationModel();
		
		Iterator<Annotation> projectionAnnotationIterator = projectionAnnotationModel.getAnnotationIterator();
		while (projectionAnnotationIterator.hasNext()) {
			ProjectionAnnotation projectionAnnotation = (ProjectionAnnotation) projectionAnnotationIterator.next();
			if (projectionAnnotation instanceof AiMdReferenceHeaderFoldingProjectionAnnotation) {
				projectionAnnotationModel.collapse(projectionAnnotation);
			}
		}
	}
	
//	@SneakyThrows(CoreException.class)
	public void initReferences() {
		
//		resource = this.getEditorInput().getAdapter(IFile.class);
//		document = this.getDocumentProvider().getDocument(this.getEditorInput());
		
//	    IMarker[] markers;
//	    markers = resource.findMarkers(REFERENCE_LINE, true, IResource.DEPTH_INFINITE);
//	    for (IMarker marker : markers) {
//	    	marker.delete();
//	    } 
//	    markers = resource.findMarkers(REFERENCE_START_LINE, true, IResource.DEPTH_INFINITE);
//	    for (IMarker marker : markers) {
//	    	marker.delete();
//	    } 
//	    markers = resource.findMarkers(REFERENCE_END_LINE, true, IResource.DEPTH_INFINITE);
//	    for (IMarker marker : markers) {
//	    	marker.delete();
//	    } 
//		
//		aiMdReferences = AIMdEngine.generateReferences(document.get());
//		
//		for (AIMdReferenceText reference : aiMdReferences.getReferences()) {
//
//			String id = reference.getId();
//			
//		    HashMap<String, Object> markerAttributesMap = new HashMap<>();
//		    markerAttributesMap.put(ID, id);
//		    MarkerUtilities.setLineNumber(markerAttributesMap, reference.getReferenceLine());
//		    MarkerUtilities.setMessage(markerAttributesMap, id);
//		    MarkerUtilities.createMarker(resource, markerAttributesMap, REFERENCE_LINE);
//		    
////		    if (reference.getTextStartLine() != reference.getReferenceLine()) {
//		    	markerAttributesMap = new HashMap<>();
//			    markerAttributesMap.put(ID, id);
//		    	MarkerUtilities.setLineNumber(markerAttributesMap, reference.getTextStartLine());
//			    MarkerUtilities.setMessage(markerAttributesMap, reference.getId());
//			    MarkerUtilities.createMarker(resource, markerAttributesMap, REFERENCE_START_LINE);
////		    }
//		    
//		    markerAttributesMap = new HashMap<>();
//		    markerAttributesMap.put(ID, id);
//		    MarkerUtilities.setLineNumber(markerAttributesMap, reference.getTextEndLine());
//		    MarkerUtilities.setMessage(markerAttributesMap, reference.getId());
//		    MarkerUtilities.createMarker(resource, markerAttributesMap, REFERENCE_END_LINE);
//		}
//
//		referenceLineMarkers.clear();
//		for (IMarker marker : resource.findMarkers(REFERENCE_LINE, true, IResource.DEPTH_INFINITE)) {
//	    	referenceLineMarkers.put((String) marker.getAttribute(ID), marker);
//	    } 
//		referenceStartLineMarkers.clear();
//		for (IMarker marker : resource.findMarkers(REFERENCE_START_LINE, true, IResource.DEPTH_INFINITE)) {
//			referenceStartLineMarkers.put((String) marker.getAttribute(ID), marker);
//	    } 
//		referenceEndLineMarkers.clear();
//		for (IMarker marker : resource.findMarkers(REFERENCE_END_LINE, true, IResource.DEPTH_INFINITE)) {
//			referenceEndLineMarkers.put((String) marker.getAttribute(ID), marker);
//	    } 
	}
	
//	@SneakyThrows(CoreException.class)
//	public void reconcileAnnotationsWithReferences() {
//		
//	    IMarker[] referenceMarkers = resource.findMarkers("org.emdepub.ai_md.reference.line.text.marker", true, IResource.DEPTH_INFINITE);
//	    for (IMarker referenceMarker : referenceMarkers) {
//	    	
//	    	String id = MarkerUtilities.getMessage(referenceMarker);
//	    	
//	    	for (AIMdReferenceText reference : aiMdReferences.getReferences()) {
//	    		
//	    	}
//	    } 
//	}




	
//	@Override
//	@SneakyThrows
//	protected void rulerContextMenuAboutToShow(IMenuManager menu) {
//		
//		L.i("rulerContextMenuAboutToShow");
//		IFile iFile = (IFile) this.getEditorInput().getAdapter(IFile.class);
//		
//		IMarker marker = iFile.createMarker("vtclparsermarker");
//		 marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
//		 marker.setAttribute(IMarker.MESSAGE, "message");
//		 marker.setAttribute(IMarker.LINE_NUMBER, 10);
//		
//		//createCoolMarker(iFile);
//		// TODO Auto-generated method stub
//		super.rulerContextMenuAboutToShow(menu);
//	}
//
//	@Override
//	protected IVerticalRuler createVerticalRuler() {
//		
//	    IVerticalRuler ruler =  super.createVerticalRuler();
//	    CompositeRuler compositeRuler = (CompositeRuler) ruler;
//	    
//	    AnnotationRulerColumn column1 = new AnnotationRulerColumn(100, new DefaultMarkerAnnotationAccess());
//	    compositeRuler.addDecorator(0, column1);
//	    //ruler2.addDecorator(2, createLineNumberRulerColumn());
//	    //column1.addAnnotationType("MARKER");
//	    column1.addAnnotationType("my.annotationType");
//	    
//	    compositeRuler.createControl(parent, textViewer)
//	    
//	    
//	    
//	    
//	    
//	    
////	    column1.addVerticalRulerListener(listener);
//	    return ruler;
//
//	};
	
//	@SneakyThrows
//	public IMarker createCoolMarker(IResource resource) {
//		
//      IMarker marker = resource.createMarker(MarkerAnnotation.TYPE_UNKNOWN);
//      //marker.setAttribute("coolFactor", "ULTRA");
//      return marker;
//	}
		
//		//AnnotationModel annotationModel = new AnnotationModel();
//		gptAnnotationRulerColumn.setModel(annotationModel);
//		
//		
//		verticalRuler.addDecorator(1000, gptAnnotationRulerColumn);
//		
//		return verticalRuler;
//	}
	
	
}
