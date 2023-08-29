/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.ai_md.editor;

import java.nio.file.Path;
import java.nio.file.Paths;

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
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.emdepub.ai_md.editor.formatter.AiMdFormatter;
import org.emdepub.ai_md.engine.AiMdEngine;
import org.emdepub.common.resources.CR;
import org.emdepub.common.ui.UI;
import org.emdepub.common.utils.CU;
import org.emdepub.markdown.editor.preferences.MarkdownPreferences;

import lombok.SneakyThrows;

/** Markdown multi-page editor contributor to menu, tool bar, status bar */
public class AiMdEditorContributor extends MultiPageEditorActionBarContributor {

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
	private AiMdEditor aiMdMultiPageEditor;
	private AiMdTextEditor aiMdSourceTextEditor;
	private MarkdownPreferences aiMdPreferences;
	
	/** Contributed menu */
	private IMenuManager aiMdMenuManager;
	/** Contributed tool bar */
	private IToolBarManager aiMdToolBarManager;
	/** Contributed status line */
	private IStatusLineManager aiMdStatusLineManager;

	/* First page */

	/** Browser hover link */
	private static StatusLineContributionItem statusLineLinkField;

	/** Source position */
	private static StatusLineContributionItem statusLinePositionField;

	/* Second page */
	
	/** Word wrap */
	private Action wordWrapAction;
	private String wordWrapActionId;

	/** Show whitespace characters */
	private Action showWhitespaceCharactersAction;
	private String showWhitespaceCharactersActionId;

	/** Named separator */
	private Separator editSeparatorAction = new Separator();
	private String editSeparatorActionId = "org.emdepub.ui.editor.markdown.action.editSeparatorAction";

	/** Format Markdown text */
	private Action formatMarkdownAction;
	private String formatMarkdownActionId = "org.emdepub.ui.editor.markdown.action.formatMarkdownAction";

	/** Special Markdown text format */
	private Action formatOptionsAction;
	private String formatOptionsActionId = "org.emdepub.ui.editor.markdown.action.specialFormatAction";

	/** Format Markdown text */
	private Action repairBrokenTextAction;
	private String repairBrokenTextActionId = "org.emdepub.ui.editor.markdown.action.repairBrokenTextAction";
	
	/** Named separator */
	private Separator formatSeparatorAction = new Separator();
	private String formatSeparatorActionId = "org.emdepub.ui.editor.markdown.action.formatSeparatorAction";

	/** Bold Markdown text format */
	private Action boldFormatAction;
	private String boldFormatActionId = "org.emdepub.ui.editor.markdown.action.boldFormatAction";

	/** Italic Markdown text format */
	private Action italicFormatAction;
	private String italicFormatActionId = "org.emdepub.ui.editor.markdown.action.italicFormatAction";

	/** Named separator */
	private Separator aiSeparatorAction = new Separator();
	private String aiSeparatorActionId = "org.emdepub.ui.editor.markdown.action.aiSeparatorAction";

	/** Split view */
	private Action aiSplitViewAction;
	private final String aiSplitViewActionId = "org.emdepub.ui.editor.aiMd.action.aiSplitViewAction";

	/** Collapse */
	private Action aiCollapseHeadersAction;
	private String aiCollapseHeadersActionId = "org.emdepub.ui.editor.aiMd.action.aiCollapseHeadersAction";

	/** Generate */
	private Action aiGenerateAction;
	private String aiGenerateActionId = "org.emdepub.ui.editor.aiMd.action.aiGenerateAction";

	/** Eclipse actions */
	private void createActions() {
		
		
		/* Second page */
		
		/* Format Selected Markdown Source Text */
		formatMarkdownAction = UI.ActionFactory.create(formatMarkdownActionId, "Format Markdown source", "Format selected Markdown source text",
				CR.getImageDescriptor("markdown-action-format-md"), () -> {
			new ReplaceDocumentSelection() {
				@Override
				public String replace(String selection) {
					return AiMdFormatter.formatMarkdown(selection, aiMdPreferences);
				}
			};
		});

		/** Special Markdown format */
		formatOptionsAction = UI.ActionFactory.create(formatOptionsActionId, "Markdown format options", "Markdown format source text options",
				CR.getImageDescriptor("markdown-action-create-80"), () -> {
		
//			Shell ideShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
//			MarkdownFormatSourceTextWizard aiMdFormatSourceTextWizard = new MarkdownFormatSourceTextWizard(aiMdMultiPageEditor);
//			WizardDialog wizardDialog = new WizardDialog(ideShell, aiMdFormatSourceTextWizard);
//			wizardDialog.open();    	
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

		/** Generate */
		aiGenerateAction = UI.ActionFactory.create(aiGenerateActionId, "Generate", "Generate", CR.getImageDescriptor("run"), () -> {

			String aiMdFileName = ((FileEditorInput) aiMdMultiPageEditor.getEditorInput()).getPath().makeAbsolute().toOSString();
			String fileTargetFileName = CU.findFileNameWithoutExtension(aiMdFileName) + ".md";
			
//			FileEditorInput fileEditorInput = (FileEditorInput) aiMdMultiPageEditor.getEditorInput();
//			Path fileEditorFilePath = Paths.get(fileEditorInput.getPath().makeAbsolute().toOSString());
//			String fileTargetFileName = fileEditorFilePath.getFileName().toString().replace(".ai-md", ".md");

			Path targetFolderPath = aiMdMultiPageEditor.getAiMdProject().getTargetFolderPath();
			String targetFilePathName = Paths.get(targetFolderPath.toAbsolutePath().toString(), fileTargetFileName).toAbsolutePath().toString();
			
			AiMdEngine.generateMdFromAiMd(aiMdSourceTextEditor.getDocument().get(), aiMdSourceTextEditor.getCursorPositionString(), targetFilePathName);
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
	public AiMdEditorContributor() {
		super();

		createActions();
		
		statusLineLinkField = new StatusLineContributionItem("statusLineLinkField", 120);
		statusLineLinkField.setText("");
		
		statusLinePositionField = new StatusLineContributionItem("statusLinePositionField", 120);
		statusLinePositionField.setText("0");
	}
	
	/** The editor class is available here */
	@Override
	public void setActiveEditor(IEditorPart editorPart) {

		if (editorPart == null) {
			return;
		}
		
		aiMdMultiPageEditor = (AiMdEditor) editorPart;
		aiMdSourceTextEditor = aiMdMultiPageEditor.getAiMdTextEditor();
		aiMdPreferences = aiMdMultiPageEditor.getPreferences();
		
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
		
		aiMdMenuManager.remove(formatOptionsActionId);
		aiMdToolBarManager.remove(formatOptionsActionId);

		aiMdMenuManager.remove(repairBrokenTextActionId);
		aiMdToolBarManager.remove(repairBrokenTextActionId);

		aiMdToolBarManager.remove(formatSeparatorActionId);
		
		aiMdToolBarManager.remove(boldFormatActionId);
		aiMdToolBarManager.remove(italicFormatActionId);

		aiMdToolBarManager.remove(aiSeparatorActionId);
		
		aiMdToolBarManager.remove(aiSplitViewActionId);
		aiMdToolBarManager.remove(aiCollapseHeadersActionId);
		aiMdToolBarManager.remove(aiGenerateActionId);
		
		/* Status line */
		aiMdStatusLineManager.remove(statusLineLinkField);
		aiMdStatusLineManager.remove(statusLinePositionField);
		
		/* Update */
		aiMdToolBarManager.update(true);

		if (aiMdMultiPageEditor.getActivePage() == aiMdMultiPageEditor.getAiMdProjectEditorPageIndex()) {
			
			aiMdStatusLineManager.add(statusLineLinkField);

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

			aiMdMenuManager.add(formatOptionsAction);
			aiMdToolBarManager.add(formatOptionsAction);

			aiMdMenuManager.add(repairBrokenTextAction);
			aiMdToolBarManager.add(repairBrokenTextAction);

			aiMdToolBarManager.add(formatSeparatorAction);

			aiMdToolBarManager.add(boldFormatAction);
			aiMdToolBarManager.add(italicFormatAction);
			
			aiMdToolBarManager.add(aiSeparatorAction);
			
			aiMdToolBarManager.add(aiSplitViewAction);
			aiMdToolBarManager.add(aiCollapseHeadersAction);
			aiMdToolBarManager.add(aiGenerateAction);

			
			aiMdStatusLineManager.add(statusLinePositionField);

			statusLinePositionField.setText(aiMdSourceTextEditor.getCursorPositionString());
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
