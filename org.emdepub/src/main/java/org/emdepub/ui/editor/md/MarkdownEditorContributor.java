package org.emdepub.ui.editor.md;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.emdepub.activator.R;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MarkdownEditorContributor extends MultiPageEditorActionBarContributor {

	/** The actual editor */
	private MarkdownEditor markdownEditor;
	private MarkdownTextEditor markdownTextEditor;
	
	/** Contributed menu */
	private MenuManager mdMenuManager;
	/** Contributed tool bar */
	private ToolBarManager mdToolBarManager;

	/** Export as one HTML file */
	private Action exportAsHtmlAction;
	private String exportAsHtmlActionId = "6104584428303702872L";

//	/** First page */
//	private BrowserViewerPageActionContributor browserViewerPageActionContributor;
//	/** Second page */
//	private TextEditorPageActionContributor textEditorPageActionContributor;

	
	/** Eclipse actions */
	private void createActions() {
		
		/* Export Markdown Document as HTML File */
		exportAsHtmlAction = new Action() {
			public void run() {
				
				markdownEditor.exportAsHtml();
//				ObjectMapper objectMapper = new ObjectMapper();
//				
//				IFile iFile = (IFile) markdownEditor.getEditorInput().getAdapter(IFile.class);
//				String filePathName = iFile.getLocation().toOSString() + ".prefs.json";
//
//				
//				try {
//					objectMapper.writeValue(new File(filePathName), markdownEditor.getMarkdownHtmlGeneratorPrefs());
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				//markdownEditor.getJsonGenerator().
				
				
				//multiPageEditor.exportAsHtml();
			}
		};
		
		exportAsHtmlAction.setId(exportAsHtmlActionId);
		exportAsHtmlAction.setText("Export Markdown as HTML file...");
		exportAsHtmlAction.setToolTipText("Export Markdown Document as HTML File");
		exportAsHtmlAction.setImageDescriptor(R.getImageDescriptor("md-action-exportashtml"));

//		/* Format Selected Markdown Source Text */
//		formatMdAction = new Action() {
//			public void run() {
//				
//				MarkdownSemanticEPTextEditor markdownSemanticEPTextEditor = (MarkdownSemanticEPTextEditor) multiPageEditor.getEditor();
//				
//				TextSelection textSelection = (TextSelection) markdownSemanticEPTextEditor.getSelectionProvider().getSelection();
//				String selection = textSelection.getText();
////				L.p(selection);
//				String formattedSelection = MarkdownSemanticEPEngine.formatMarkdown(selection);
////				L.p(formattedSelection);
//				IDocument document = markdownSemanticEPTextEditor.getDocumentProvider().getDocument(markdownSemanticEPTextEditor.getEditorInput());
//				try {
//					document.replace(textSelection.getOffset(), textSelection.getLength(), formattedSelection);
//				}
//				catch (BadLocationException badLocationException) {
//					L.e("BadLocationException in formatMdAction", badLocationException);
//				}
//			}
//		};
//		
//		formatMdAction.setId(formatMdActionId);
//		formatMdAction.setText("Format Markdown Source");
//		formatMdAction.setToolTipText("Format Selected Markdown Source Text");
//		formatMdAction.setImageDescriptor(R.getImageDescriptor("md-action-format-md"));
//		
//		/** Show word wrap */
//		showWordWrapAction = new Action() {
//			public void run() {
//				MarkdownSemanticEPTextEditor markdownSemanticEPTextEditor = (MarkdownSemanticEPTextEditor) multiPageEditor.getEditor();
//				boolean wordWrapEnabled = markdownSemanticEPTextEditor.isWordWrapEnabled();
//				wordWrapEnabled = !wordWrapEnabled;
//				markdownSemanticEPTextEditor.setWordWrap(wordWrapEnabled);
//			}
//		};
//		showWordWrapAction.setChecked(false);
//		showWordWrapAction.setId(showWordWrapActionId);
//		showWordWrapAction.setText("Show Text Word Wrap");
//		showWordWrapAction.setToolTipText("Show Text Word Wrap");
//		showWordWrapAction.setImageDescriptor(R.getImageDescriptor("md-action-word-wrap"));
//
//		/** Format text to 80 columns */
//		format80ColumnsAction = new Action() {
//			public void run() {
//				MarkdownSemanticEPTextEditor markdownSemanticEPTextEditor = (MarkdownSemanticEPTextEditor) multiPageEditor.getEditor();
//				markdownSemanticEPTextEditor.format80ParagraphContributor();
//			}
//		};
//		format80ColumnsAction.setId(format80ColumnsActionId);
//		format80ColumnsAction.setText("Format to 80 Columns");
//		format80ColumnsAction.setToolTipText("Format Selected Text to 80 Columns");
//		format80ColumnsAction.setImageDescriptor(R.getImageDescriptor("md-action-create-80"));
//
//		/** Repair broken paragraph */
//		repairPragraphAction = new Action() {
//			public void run() {
//				MarkdownSemanticEPTextEditor markdownSemanticEPTextEditor = (MarkdownSemanticEPTextEditor) multiPageEditor.getEditor();
//				markdownSemanticEPTextEditor.repairBrokenParagraphContributor();
//			}
//		};
//		repairPragraphAction.setId(repairPragraphActionId);
//		repairPragraphAction.setText("Repair Broken Paragraphs");
//		repairPragraphAction.setToolTipText("Repair Broken Selected Text Paragraphs");
//		repairPragraphAction.setImageDescriptor(R.getImageDescriptor("md-action-repair-paragraph"));
		
	}

	/** Initial, fix contribution */
	@Override
	public void contributeToMenu(IMenuManager menuManager) {
		mdMenuManager = new MenuManager("Markdown");
		menuManager.prependToGroup(IWorkbenchActionConstants.MB_ADDITIONS, mdMenuManager);
		mdMenuManager.add(exportAsHtmlAction);
		mdMenuManager.add(new Separator());
	}

	/** Initial, fix contribution */
	@Override
	public void contributeToToolBar(IToolBarManager toolBarManager) {
		mdToolBarManager = (ToolBarManager) toolBarManager;
		mdToolBarManager.add(exportAsHtmlAction);
		mdToolBarManager.add(new Separator());
	}
	
	/** Creates a multi-page contributor */
	public MarkdownEditorContributor() {
		super();

		createActions();
//		browserViewerPageActionContributor = new BrowserViewerPageActionContributor();
//		textEditorPageActionContributor = new TextEditorPageActionContributor();
//		
//		linkField = new StatusLineContributionItem("linkField", 120);
//		linkField.setText("");
//
//		positionField = new StatusLineContributionItem("positionField", 16);
//		positionField.setText("0");

//		spellCheckStatusField = new StatusLineContributionItem("spellCheckStatusField", 50);
//		spellCheckStatusField.setText("s");
	}
	
	/** The editor class is available here */
	@Override
	public void setActiveEditor(IEditorPart editorPart) {

		if (editorPart == null) {
			return;
		}
		
		markdownEditor = (MarkdownEditor) editorPart;

		super.setActiveEditor(markdownEditor);
		
//		if (part != null) {
//			multiPageEditor = (MarkdownSemanticEPEditor) part;
//			MarkdownSemanticEPTextEditor markdownSemanticEPTextEditor = (MarkdownSemanticEPTextEditor) multiPageEditor.getEditor();
//			textEditorPageActionContributor.setActiveEditor(markdownSemanticEPTextEditor);
//			positionField.setText(markdownSemanticEPTextEditor.getOffsetStatus());
//			
//			showWordWrapAction.setChecked(markdownSemanticEPTextEditor.isWordWrapEnabled());
//			
//			super.setActiveEditor(multiPageEditor);
//		}
	}
	
	@Override
	public void setActivePage(IEditorPart editorPart) {
		
		if (editorPart == null) {
			return;
		}
		
		markdownTextEditor = (MarkdownTextEditor) editorPart;
		
		// TODO Auto-generated method stub

//		textEditorPageActionContributor.contributeToMenu(bars.getMenuManager());
//		textEditorPageActionContributor.contributeToToolBar(bars.getToolBarManager());

	}

	
	
}
