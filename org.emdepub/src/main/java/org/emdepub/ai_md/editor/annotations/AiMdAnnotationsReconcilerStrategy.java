package org.emdepub.ai_md.editor.annotations;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.ui.texteditor.MarkerUtilities;
import org.emdepub.ai_md.editor.AiMdTextEditor;
import org.emdepub.ai_md.parser.AiMdParser;
import org.emdepub.ai_md.parser.ext.references.AiMdReferenceBlock;
import org.jetbrains.annotations.NotNull;

import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeCollectingVisitor;
import com.vladsch.flexmark.util.collection.OrderedSet;

import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class AiMdAnnotationsReconcilerStrategy implements  IReconcilingStrategy, IReconcilingStrategyExtension {

	IDocument document;
	String oldDocument;
	IResource resource;
	
	/** https://www.programcreek.com/java-api-examples/?code=angelozerr%2Ftypescript.java%2Ftypescript.java-master%2Feclipse%2Fts.eclipse.ide.ui%2Fsrc%2Fts%2Feclipse%2Fide%2Fui%2Fcodelens%2FTypeScriptBaseCodeLensProvider.java# */
	private IFile getFile(IDocument document) {
		
		ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager(); /* get the buffer manager */
		ITextFileBuffer buffer = bufferManager.getTextFileBuffer(document);
		IPath location = buffer == null ? null : buffer.getLocation();
		if (location == null) {
			return null;
		}

		return ResourcesPlugin.getWorkspace().getRoot().getFile(location);
	}
	
	@Override
	public void setDocument(IDocument document) {
		this.document = document;
		resource = getFile(document);
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
	@SneakyThrows(CoreException.class)
	public void initialReconcile() {
		
		if (document.get().equals(oldDocument)) {
			return;
		}
		oldDocument = document.get();

		resource.deleteMarkers(AiMdTextEditor.MARKER_REFERENCE_BEGIN, true, IResource.DEPTH_ZERO);
		resource.deleteMarkers(AiMdTextEditor.MARKER_REFERENCE_TEXT_BEGIN, true, IResource.DEPTH_ZERO);
		resource.deleteMarkers(AiMdTextEditor.MARKER_REFERENCE_TEXT_END, true, IResource.DEPTH_ZERO);

		resource.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_ZERO);

		/* Parse document in a node */
		Node documentNode = AiMdParser.getParser().parse(document.get());
		
		NodeCollectingVisitor nodeCollectingVisitor = new NodeCollectingVisitor(Set.of(AiMdReferenceBlock.class));
		nodeCollectingVisitor.collect(documentNode);
		@NotNull OrderedSet<Node> aiMdReferenceNodes = nodeCollectingVisitor.getSubClassingBag().getItems();

    	for (Node aiMdReferenceNode : aiMdReferenceNodes) {
    		AiMdReferenceBlock aiMdReferenceBlock = (AiMdReferenceBlock) aiMdReferenceNode;
    		
			createAnnotation(aiMdReferenceBlock.getHeaderStartLineNumber(), "id", AiMdTextEditor.MARKER_REFERENCE_BEGIN);
			createAnnotation(aiMdReferenceBlock.getHeaderEndLineNumber(), "id", AiMdTextEditor.MARKER_REFERENCE_TEXT_BEGIN);
			createAnnotation(aiMdReferenceBlock.getTextEndLineNumber(), "id", AiMdTextEditor.MARKER_REFERENCE_TEXT_END);
			
			for (Integer problemLine : aiMdReferenceBlock.getHeaderStartInvalidLineNumbers()) {
				createErrorAnnotation(problemLine, "AI header start tag problem");
			}
			for (Integer problemLine : aiMdReferenceBlock.getHeaderEndInvalidLineNumbers()) {
				createErrorAnnotation(problemLine, "AI header end tag problem");
			}
			for (Integer problemLine : aiMdReferenceBlock.getReferenceEndInvalidLineNumbers()) {
				createErrorAnnotation(problemLine, "AI text end tag problem");
			}
    	}
	}

	@Override
	public void setProgressMonitor(IProgressMonitor monitor) { /* ILB */ }

	@SneakyThrows(CoreException.class)
	private void createAnnotation(int lineIndex, String id, String type) {

	    Map<String, Object> markerAttributesMap = new HashMap<>();
	    markerAttributesMap.put(AiMdTextEditor.ID, id);
	    MarkerUtilities.setLineNumber(markerAttributesMap, lineIndex + 1);
	    MarkerUtilities.setMessage(markerAttributesMap, "id");
	    MarkerUtilities.createMarker(resource, markerAttributesMap, type);
	}
	
	@SneakyThrows(CoreException.class)
	private void createErrorAnnotation(int lineIndex, String message) {

	    Map<String, Object> errorMarkerAttributesMap = new HashMap<>();
	    MarkerUtilities.setLineNumber(errorMarkerAttributesMap, lineIndex + 1);
	    MarkerUtilities.setMessage(errorMarkerAttributesMap, message);
	    errorMarkerAttributesMap.put(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
	    MarkerUtilities.createMarker(resource, errorMarkerAttributesMap, IMarker.PROBLEM);
	}
}