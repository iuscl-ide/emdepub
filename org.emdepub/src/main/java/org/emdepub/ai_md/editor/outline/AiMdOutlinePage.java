/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.ai_md.editor.outline;

import java.util.ArrayList;
import java.util.Set;
import java.util.StringJoiner;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.emdepub.ai_md.editor.AiMdMultiPageEditor;
import org.emdepub.ai_md.editor.AiMdTextEditor;
import org.emdepub.ai_md.parser.AiMdParser;
import org.emdepub.common.resources.CR;
import org.emdepub.common.utils.CU;
import org.jetbrains.annotations.NotNull;

import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.ast.Text;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeCollectingVisitor;
import com.vladsch.flexmark.util.collection.OrderedSet;

/** The outline view with its tree view */
public class AiMdOutlinePage extends ContentOutlinePage {

	/** Tree labels */
	public class OutlineLabelProvider extends LabelProvider {

		public String getText(Object element) {
			return ((AiMdOutlineNode)element).getLabel();
		}

		public Image getImage(Object element) {
			return ((AiMdOutlineNode)element).getImage();
		}
	}

	/** Tree nodes */
	public class OutlineContentProvider implements ITreeContentProvider {

		public Object[] getChildren(Object parentElement) {
			return ((AiMdOutlineNode)parentElement).findChildOutlineNodes();
		}

		public Object getParent(Object element) {
			return ((AiMdOutlineNode)element).getParentOutlineNode();
		}

		public boolean hasChildren(Object element) {
			return getChildren(element).length > 0;
		}

		public Object[] getElements(Object inputElement) {
			return getChildren(inputElement);
		}

		public void dispose() { }

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { /* ? */ }		
	}

	/** Editor */
	private AiMdMultiPageEditor aiMdEditor;
	private AiMdTextEditor aiMdTextEditor;
	private Document document;
	
	/** The view */
	public AiMdOutlinePage(AiMdMultiPageEditor aiMdEditor) {
		super();
		
		this.aiMdEditor = aiMdEditor;
		this.aiMdTextEditor = aiMdEditor.getAiMdTextEditor();
		this.document = (Document) aiMdTextEditor.getDocumentProvider().getDocument(aiMdTextEditor.getEditorInput());
	}

	/** Parse and visit */
	private AiMdOutlineNode createOutlineFromDocument() {

		/* View, outside of tree */
		AiMdOutlineNode viewOutlineNode = new AiMdOutlineNode();

		ArrayList<AiMdOutlineNode> createdOutlineNodes = new ArrayList<>();

		/* Parse document in a node */
		Node documentNode = AiMdParser.getParser().parse(document.get());
		AiMdOutlineNode documentOutlineNode = viewOutlineNode.addChildNode();
		documentOutlineNode.setSubType("0");
		createdOutlineNodes.add(documentOutlineNode);
		
		NodeCollectingVisitor nodeCollectingVisitor = new NodeCollectingVisitor(Set.of(Heading.class));
		nodeCollectingVisitor.collect(documentNode);
		@NotNull OrderedSet<Node> linearHeadings = nodeCollectingVisitor.getSubClassingBag().getItems();

		/* Put headings in a hierarchical tree */
		for (Node headingNode : linearHeadings) {
			Heading heading = (Heading) headingNode;

			int parentIndex = createdOutlineNodes.size() - 1;
			AiMdOutlineNode parentOutlineNode = createdOutlineNodes.get(parentIndex);
			int markdownHeadingLevel = heading.getLevel();
			while (Integer.parseInt(parentOutlineNode.getSubType()) >= markdownHeadingLevel) {
				parentIndex--;
				parentOutlineNode = createdOutlineNodes.get(parentIndex);
			}
			
			AiMdOutlineNode headingOutlineNode = parentOutlineNode.addChildNode();
			String level = "" + markdownHeadingLevel;
			headingOutlineNode.setType("Heading");
			headingOutlineNode.setSubType(level);
			headingOutlineNode.setImage(CR.getImage("markdown-header"));
			
			NodeCollectingVisitor textNodeCollectingVisitor = new NodeCollectingVisitor(Set.of(Text.class));
			textNodeCollectingVisitor.collect(heading);
			@NotNull OrderedSet<Node> linearTexts = textNodeCollectingVisitor.getSubClassingBag().getItems();
			StringJoiner stringJoiner = new StringJoiner(CU.SP);
			linearTexts.forEach(node -> stringJoiner.add(((Text) node).getChars().toString().trim()));
			headingOutlineNode.setLabel(stringJoiner.toString());

			int offset = heading.getStartOffset();
			headingOutlineNode.setStart(offset);
			headingOutlineNode.setLength(heading.getEndOffset() - offset);
			//headingOutlineNode.setLength(0);
			createdOutlineNodes.add(headingOutlineNode);
		}
		
		return documentOutlineNode;
	}
	
	/** Customize the content */
	public void createControl(Composite parent) {
		super.createControl(parent);
		
		TreeViewer tree = getTreeViewer();
		tree.setAutoExpandLevel(7);
		tree.setContentProvider(new OutlineContentProvider());
		tree.setLabelProvider(new OutlineLabelProvider());
		tree.setInput(createOutlineFromDocument());
		
		tree.addSelectionChangedListener(event -> {
			Object selected = ((IStructuredSelection) event.getSelection()).getFirstElement();
			if (selected == null) {
				return;
			}
			AiMdOutlineNode selectedOutlineNode = (AiMdOutlineNode)selected;
			aiMdEditor.activateAiMdTextEditorPage();
			aiMdTextEditor.selectAndReveal(selectedOutlineNode.getStart(), selectedOutlineNode.getLength());
		});
	}
	
	/** When modified */
	public void updateOulineView() {
		
		final TreeViewer tree = getTreeViewer();
		if ((tree != null) && (!tree.getControl().isDisposed())) {
			tree.getControl().getDisplay().asyncExec(() -> {
				tree.setInput(createOutlineFromDocument());
			});
		}
	}
}
