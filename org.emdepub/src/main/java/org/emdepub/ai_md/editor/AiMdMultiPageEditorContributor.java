/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.ai_md.editor;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.StatusLineContributionItem;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.emdepub.ai_md.editor.formatter.AiMdFormatter;
import org.emdepub.ai_md.engine.AiMdEngine;
import org.emdepub.common.resources.CR;
import org.emdepub.common.ui.UI;

import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;

/** Markdown multi-page editor contributor to menu, tool bar, status bar */
@FieldDefaults(level=AccessLevel.PRIVATE)
public class AiMdMultiPageEditorContributor extends MultiPageEditorActionBarContributor {

	/** Replace document selection */
	private abstract class ReplaceDocumentSelection {

		@SneakyThrows(BadLocationException.class)
		public ReplaceDocumentSelection() {
			super();

			TextSelection textSelection = (TextSelection) aiMdSourceTextEditor.getSelectionProvider().getSelection();
			String replacedSelection = replace(textSelection.getText());
			IDocument document = aiMdSourceTextEditor.getDocumentProvider().getDocument(aiMdSourceTextEditor.getEditorInput());
			document.replace(textSelection.getOffset(), textSelection.getLength(), replacedSelection);
		}
		
		public abstract String replace(String selection);
	}

	/** The actual editor */
	AiMdMultiPageEditor aiMdMultiPageEditor;
	
	//AiMdPreferencesMdFormatter mdPreferences;
	
	AiMdTextEditor aiMdSourceTextEditor;
	
	/** Contributed menu */
	IMenuManager aiMdMenuManager;
	/** Contributed tool bar */
	IToolBarManager aiMdToolBarManager;
	/** Contributed status line */
	IStatusLineManager aiMdStatusLineManager;

	/* First page */

	/** Browser hover link */
	private static StatusLineContributionItem statusLineLinkField;

	/** Source position */
	private static StatusLineContributionItem statusLinePositionField;

	/* Second page */
	
	/** Word wrap */
	Action wordWrapAction;
	String wordWrapActionId;

	/** Show whitespace characters */
	Action showWhitespaceCharactersAction;
	String showWhitespaceCharactersActionId;

	/** Named separator */
	Separator editSeparatorAction = new Separator();
	String editSeparatorActionId = "org.emdepub.ui.editor.ai-md.action.editSeparatorAction";

	/** Format Markdown text */
	Action formatMarkdownAction;
	String formatMarkdownActionId = "org.emdepub.ui.editor.ai-md.action.formatMarkdownAction";

	/** Format Markdown text */
	Action repairBrokenTextAction;
	String repairBrokenTextActionId = "org.emdepub.ui.editor.ai-md.action.repairBrokenTextAction";
	
	/** Named separator */
	Separator formatSeparatorAction = new Separator();
	String formatSeparatorActionId = "org.emdepub.ui.editor.ai-md.action.formatSeparatorAction";

	/** Bold Markdown text format */
	Action boldFormatAction;
	String boldFormatActionId = "org.emdepub.ui.editor.ai-md.action.boldFormatAction";

	/** Italic Markdown text format */
	Action italicFormatAction;
	String italicFormatActionId = "org.emdepub.ui.editor.ai-md.action.italicFormatAction";

	/** Named separator */
	Separator aiSeparatorAction = new Separator();
	String aiSeparatorActionId = "org.emdepub.ui.editor.ai-md.action.aiSeparatorAction";

	/** Split view */
	Action aiSplitViewAction;
	final String aiSplitViewActionId = "org.emdepub.ui.editor.ai-md.action.aiSplitViewAction";

	/** Collapse */
	Action aiCollapseHeadersAction;
	String aiCollapseHeadersActionId = "org.emdepub.ui.editor.ai-md.action.aiCollapseHeadersAction";

	/** Generate AI */
	Action aiGenerateAction;
	String aiGenerateActionId = "org.emdepub.ui.editor.ai-md.action.aiGenerateAction";

	/** Generate HTML */
	Action htmlGenerateAction;
	String htmlGenerateActionId = "org.emdepub.ui.editor.ai-md.action.htmlGenerateAction";

	/** Eclipse actions */
	private void createActions() {
		
		
		/* Second page */
		
		/* Format Selected Markdown Source Text */
		formatMarkdownAction = UI.ActionFactory.create(formatMarkdownActionId, "Format Markdown source", "Format selected Markdown source text",
				CR.getImageDescriptor("markdown-action-format-md"), () -> {
			new ReplaceDocumentSelection() {
				@Override
				public String replace(String selection) {
					return AiMdFormatter.formatMarkdown(selection, aiMdMultiPageEditor.getMdPreferences());
				}
			};
		});

		/* Format Selected Markdown Source Text */
		repairBrokenTextAction = UI.ActionFactory.create(repairBrokenTextActionId, "Repair broken text paragraphs", "Repair broken text to re-create paragraphs",
				CR.getImageDescriptor("markdown-action-repair-paragraph"), () -> {
			new ReplaceDocumentSelection() {
				@Override
				public String replace(String selection) {
					return AiMdFormatter.repairBrokenText(selection);
				}
			};
		});

		/** Bold Markdown format */
		boldFormatAction = UI.ActionFactory.create(boldFormatActionId, "Bold format selected text", "Markdown Bold format selected text",
			CR.getImageDescriptor("PD_Toolbar_bold"), () -> {
				new ReplaceDocumentSelection() {
					@Override
					public String replace(String selection) {
						String selectionTrim = selection.trim();
						if (selectionTrim.startsWith("**") && selectionTrim.endsWith("**")) {
							int startPos = selection.indexOf("**");
							int endPos = selection.lastIndexOf("**");
							if (startPos < endPos) {
								return selection.substring(0, startPos) + selection.substring(startPos + 2,  endPos) + selection.substring(endPos + 2);
							}
						}
						return "**" + selection + "**";
					}
				};
		});
		
		/** Italic Markdown format */
		italicFormatAction = UI.ActionFactory.create(italicFormatActionId, "Italic format selected text", "Markdown Italic format selected text",
				CR.getImageDescriptor("PD_Toolbar_italic"), () -> {
			new ReplaceDocumentSelection() {
				@Override
				public String replace(String selection) {
					String selectionTrim = selection.trim();
					if (selectionTrim.startsWith("*") && selectionTrim.endsWith("*")) {
						int startPos = selection.indexOf("*");
						int endPos = selection.lastIndexOf("*");
						if (startPos < endPos) {
							return selection.substring(0, startPos) + selection.substring(startPos + 1,  endPos) + selection.substring(endPos + 1);
						}
					}
					return "*" + selection + "*";
				}
			};
		});

		/** Split view */
		aiSplitViewAction = UI.ActionFactory.create(aiSplitViewActionId, "Split view", "Split view", CR.getImageDescriptor("split_vertical"), () -> {
			boolean isEditorSplitView = !aiMdMultiPageEditor.isEditorSplitView();
			aiMdMultiPageEditor.setEditorSplitView(isEditorSplitView);
			aiSplitViewAction.setChecked(isEditorSplitView);
			
			aiMdMultiPageEditor.changeSplitView(isEditorSplitView);
		});
		/* Initial */
		aiSplitViewAction.setChecked(true);

		/** Collapse headers */
		aiCollapseHeadersAction = UI.ActionFactory.create(aiCollapseHeadersActionId, "Collapse all AI headers", "Collapse all AI text headers",
				CR.getImageDescriptor("collapseall"), () -> {
			aiMdSourceTextEditor.collapseAllReferenceFoldingAnnotations();
		});

		/** Generate AI */
		aiGenerateAction = UI.ActionFactory.create(aiGenerateActionId, "Generate", "Generate", CR.getImageDescriptor("run"), () -> {

			AiMdEngine.generateMdFromAiMd(aiMdSourceTextEditor.getDocument().get(), aiMdSourceTextEditor.getCursorPositionString(), aiMdMultiPageEditor.findTargetFileName());
		});
		
		/** Generate HTML */
		htmlGenerateAction = UI.ActionFactory.create(aiGenerateActionId, "Generate HTML", "Generate HTML", CR.getImageDescriptor("run"), () -> {

			aiMdMultiPageEditor.viewHtmlFromAiMd();
		});
	}

	/** Initial, fix contribution */
	@Override
	public void contributeToMenu(IMenuManager menuManager) {
		aiMdMenuManager = new MenuManager("AI MD");
		menuManager.prependToGroup(IWorkbenchActionConstants.MB_ADDITIONS, aiMdMenuManager);
//		aiMdMenuManager.add(aiGenerateAction);
//		aiMdMenuManager.add(new Separator());
	}

	/** Initial, fix contribution */
	@Override
	public void contributeToToolBar(IToolBarManager toolBarManager) {
		aiMdToolBarManager = toolBarManager;
//		aiMdToolBarManager.add(aiGenerateAction);
//		aiMdToolBarManager.add(new Separator());
	}
	
	/** Initial, fix contribution */
	@Override
	public void contributeToStatusLine(IStatusLineManager statusLineManager) {
		aiMdStatusLineManager = statusLineManager;
		//statusLineManager.add(statusLineLinkField);
		super.contributeToStatusLine(statusLineManager);
	}

	/** Creates a multi-page contributor */
	public AiMdMultiPageEditorContributor() {
		super();

		createActions();
		
		statusLinePositionField = new StatusLineContributionItem("statusLinePositionField", 80);
		statusLinePositionField.setText("0");
		
		statusLineLinkField = new StatusLineContributionItem("statusLineLinkField", 120);
		statusLineLinkField.setText("");

	}
	
	/** The editor class is available here */
	@Override
	public void setActiveEditor(IEditorPart editorPart) {

		if (editorPart == null) {
			return;
		}
		
		aiMdMultiPageEditor = (AiMdMultiPageEditor) editorPart;
		
		aiMdSourceTextEditor = aiMdMultiPageEditor.getAiMdTextEditor();
//		aiMdPreferences = aiMdMultiPageEditor.getPreferences();
		
		/** Word wrap */
		wordWrapAction = (Action) aiMdSourceTextEditor.getAction(ITextEditorActionConstants.WORD_WRAP);
		wordWrapAction.setImageDescriptor(CR.getImageDescriptor("wordwrap"));
		wordWrapActionId = wordWrapAction.getId();

		/** Show whitespace characters */
		showWhitespaceCharactersAction = (Action) aiMdSourceTextEditor.getAction(ITextEditorActionConstants.SHOW_WHITESPACE_CHARACTERS);
		showWhitespaceCharactersAction.setImageDescriptor(CR.getImageDescriptor("show_whitespace_chars"));
		showWhitespaceCharactersActionId = showWhitespaceCharactersAction.getId();
		
		super.setActiveEditor(aiMdMultiPageEditor);
	}
	
	@Override
	public void setActivePage(IEditorPart editorPart) {
		
		IActionBars actionBars = getActionBars();
		if ((aiMdMultiPageEditor == null) || (actionBars == null)) {
			return;
		}

		aiMdToolBarManager.remove(wordWrapActionId);
		aiMdToolBarManager.remove(showWhitespaceCharactersActionId);
		
		aiMdToolBarManager.remove(editSeparatorActionId);

		aiMdMenuManager.remove(formatMarkdownActionId);
		aiMdToolBarManager.remove(formatMarkdownActionId);
		
		aiMdMenuManager.remove(repairBrokenTextActionId);
		aiMdToolBarManager.remove(repairBrokenTextActionId);

		aiMdToolBarManager.remove(formatSeparatorActionId);
		
		aiMdToolBarManager.remove(boldFormatActionId);
		aiMdToolBarManager.remove(italicFormatActionId);

		aiMdToolBarManager.remove(aiSeparatorActionId);
		
		aiMdToolBarManager.remove(aiSplitViewActionId);
		aiMdToolBarManager.remove(aiCollapseHeadersActionId);
		aiMdToolBarManager.remove(aiGenerateActionId);
		aiMdToolBarManager.remove(htmlGenerateActionId);
		
		/* Status line */
		aiMdStatusLineManager.add(statusLinePositionField);
		statusLinePositionField.setText(aiMdSourceTextEditor.getCursorPositionString());
		aiMdStatusLineManager.add(statusLineLinkField);
		
		/* Update */
		aiMdToolBarManager.update(true);

		if (aiMdMultiPageEditor.getActivePage() == aiMdMultiPageEditor.getAiMdPreferencesEditorPageIndex()) {
			/* Update */
			aiMdToolBarManager.update(true);
			aiMdStatusLineManager.update(true);
		}
		if (aiMdMultiPageEditor.getActivePage() == aiMdMultiPageEditor.getAiMdTextEditorPageIndex()) {

			aiMdToolBarManager.add(wordWrapAction);
			aiMdToolBarManager.add(showWhitespaceCharactersAction);
			
			aiMdToolBarManager.add(editSeparatorAction);

			aiMdMenuManager.add(formatMarkdownAction);
			aiMdToolBarManager.add(formatMarkdownAction);

			aiMdMenuManager.add(repairBrokenTextAction);
			aiMdToolBarManager.add(repairBrokenTextAction);

			aiMdToolBarManager.add(formatSeparatorAction);

			aiMdToolBarManager.add(boldFormatAction);
			aiMdToolBarManager.add(italicFormatAction);
			
			aiMdToolBarManager.add(aiSeparatorAction);
			
			aiMdToolBarManager.add(aiSplitViewAction);
			aiMdToolBarManager.add(aiCollapseHeadersAction);
			aiMdToolBarManager.add(aiGenerateAction);
			aiMdToolBarManager.add(htmlGenerateAction);

			
			/* Update */
			aiMdToolBarManager.update(true);
			aiMdStatusLineManager.update(true);
		}
	}

	public static StatusLineContributionItem getStatusLineLinkField() {
		return statusLineLinkField;
	}
	
	public static StatusLineContributionItem getStatusLinePositionField() {
		return statusLinePositionField;
	}
}
