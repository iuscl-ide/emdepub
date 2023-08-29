/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.markdown.editor;

import java.util.LinkedHashMap;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.StatusLineContributionItem;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.emdepub.activator.R;
import org.emdepub.markdown.editor.engine.MarkdownFormatterEngine;
import org.emdepub.markdown.editor.preferences.MarkdownPreferences;
import org.emdepub.markdown.editor.preferences.MarkdownPreferences.DisplayFormatCodeStyles;
import org.emdepub.markdown.editor.preferences.MarkdownPreferences.DisplayFormatStyles;
import org.emdepub.markdown.editor.preferences.MarkdownPreferences.PreferenceNames;
import org.emdepub.markdown.editor.wizard.MarkdownExportAsHtmlWizard;
import org.emdepub.markdown.editor.wizard.MarkdownFormatSourceTextWizard;

import lombok.SneakyThrows;

/** Markdown multi-page editor contributor to menu, tool bar, status bar */
public class MarkdownEditorContributor extends MultiPageEditorActionBarContributor {

	public static final LinkedHashMap<DisplayFormatStyles, String> formatStyles = new LinkedHashMap<>();
	static {
		formatStyles.put(DisplayFormatStyles.None, "None");
		formatStyles.put(DisplayFormatStyles.GitHub, "GitHub");
		formatStyles.put(DisplayFormatStyles.GoogleLike, "Google Like");
		formatStyles.put(DisplayFormatStyles.SemanticUILike, "Semantic UI Like");
		formatStyles.put(DisplayFormatStyles.Custom, "Custom");
	}

	public static final LinkedHashMap<PreferenceNames, String> formatOptions = new LinkedHashMap<>();
	static {
		formatOptions.put(PreferenceNames.DisplayFixedContentWidth, "Fixed Content Width");
		formatOptions.put(PreferenceNames.DisplayJustifiedParagraphs, "Justified Paragraphs");
		formatOptions.put(PreferenceNames.DisplayCenterHeaders, "Center Headers");
	}

	public static final LinkedHashMap<DisplayFormatCodeStyles, String> formatCodeStyles = new LinkedHashMap<>();
	static {
		formatCodeStyles.put(DisplayFormatCodeStyles.None, "None");
		formatCodeStyles.put(DisplayFormatCodeStyles.Agate, "Agate");
		formatCodeStyles.put(DisplayFormatCodeStyles.Androidstudio, "Android Studio");
		formatCodeStyles.put(DisplayFormatCodeStyles.ArduinoLight, "Arduino Light");
		formatCodeStyles.put(DisplayFormatCodeStyles.Dark, "Dark");
		formatCodeStyles.put(DisplayFormatCodeStyles.Default, "Default");
		formatCodeStyles.put(DisplayFormatCodeStyles.Far, "Far");
		formatCodeStyles.put(DisplayFormatCodeStyles.Foundation, "Foundation");
		formatCodeStyles.put(DisplayFormatCodeStyles.GithubGist, "Github Gist");
		formatCodeStyles.put(DisplayFormatCodeStyles.Github, "Github");
		formatCodeStyles.put(DisplayFormatCodeStyles.Googlecode, "Googlecode");
		formatCodeStyles.put(DisplayFormatCodeStyles.Grayscale, "Grayscale");
		formatCodeStyles.put(DisplayFormatCodeStyles.GruvboxDark, "Gruvbox Dark");
		formatCodeStyles.put(DisplayFormatCodeStyles.GruvboxLight, "Gruvbox Light");
		formatCodeStyles.put(DisplayFormatCodeStyles.Hybrid, "Hybrid");
		formatCodeStyles.put(DisplayFormatCodeStyles.Idea, "Idea");
		formatCodeStyles.put(DisplayFormatCodeStyles.IrBlack, "Ir Black");
		formatCodeStyles.put(DisplayFormatCodeStyles.Magula, "Magula");
		formatCodeStyles.put(DisplayFormatCodeStyles.Purebasic, "Purebasic");
		formatCodeStyles.put(DisplayFormatCodeStyles.Railscasts, "Railscasts");
		formatCodeStyles.put(DisplayFormatCodeStyles.Sunburst, "Sunburst");
		formatCodeStyles.put(DisplayFormatCodeStyles.Vs, "Vs");
		formatCodeStyles.put(DisplayFormatCodeStyles.Vs2015, "Vs2015");
		formatCodeStyles.put(DisplayFormatCodeStyles.VSCode, "VSCode");
		formatCodeStyles.put(DisplayFormatCodeStyles.Xcode, "Xcode");
		formatCodeStyles.put(DisplayFormatCodeStyles.Custom, "Custom");
	}

	/** Replace document selection */
	private abstract class ReplaceDocumentSelection {

		@SneakyThrows(BadLocationException.class)
		public ReplaceDocumentSelection() {
			super();

			TextSelection textSelection = (TextSelection) markdownSourceTextEditor.getSelectionProvider().getSelection();
			String replacedSelection = replace(textSelection.getText());
			IDocument document = markdownSourceTextEditor.getDocumentProvider().getDocument(markdownSourceTextEditor.getEditorInput());
			document.replace(textSelection.getOffset(), textSelection.getLength(), replacedSelection);
		}
		
		public abstract String replace(String selection);
	}
	
	/** Class */
	private class FormatStyleAction extends Action {
		
		private DisplayFormatStyles formatStyle;

		public FormatStyleAction(String text, DisplayFormatStyles formatStyle) {
			super(text, IAction.AS_RADIO_BUTTON);
			
			this.formatStyle = formatStyle;
		}

		public DisplayFormatStyles getFormatStyle() {
			return formatStyle;
		}

		@Override
		public void run() {
			super.run();
			
			if (this.isChecked()) {
				markdownPreferences.set(PreferenceNames.DisplayFormatStyle, formatStyle);
				markdownMultiPageEditor.refresh();
				markdownMultiPageEditor.saveMarkdownPreferences();
			}
		}
	}

	/** Class */
	private class FormatOptionAction extends Action {
		
		private PreferenceNames formatOption;

		public FormatOptionAction(String text, PreferenceNames formatOption) {
			super(text, IAction.AS_CHECK_BOX);
			
			this.formatOption = formatOption;
		}

		public PreferenceNames getFormatOption() {
			return formatOption;
		}

		@Override
		public void run() {
			super.run();
			
			Boolean value = markdownPreferences.get(formatOption);
			markdownPreferences.set(formatOption, !value);
			markdownMultiPageEditor.refresh();
			markdownMultiPageEditor.saveMarkdownPreferences();
		}
	}

	/** Class */
	private class FormatCodeStyleAction extends Action {
		
		private DisplayFormatCodeStyles formatCodeStyle;

		public FormatCodeStyleAction(String text, DisplayFormatCodeStyles formatCodeStyle) {
			super(text, IAction.AS_RADIO_BUTTON);
			
			this.formatCodeStyle = formatCodeStyle;
		}

		public DisplayFormatCodeStyles getFormatCodeStyle() {
			return formatCodeStyle;
		}

		@Override
		public void run() {
			super.run();
			
			if (this.isChecked()) {
				markdownPreferences.set(PreferenceNames.DisplayFormatCodeStyle, formatCodeStyle);
				markdownMultiPageEditor.refresh();
				markdownMultiPageEditor.saveMarkdownPreferences();
			}
		}
	}

	/** The actual editor */
	private MarkdownEditor markdownMultiPageEditor;
	private MarkdownTextEditor markdownSourceTextEditor;
	private MarkdownPreferences markdownPreferences;
	
	/** Contributed menu */
	private IMenuManager markdownMenuManager;
	/** Contributed tool bar */
	private IToolBarManager markdownToolBarManager;
	/** Contributed status line */
	private IStatusLineManager markdownStatusLineManager;
	

	/** Export as one HTML file */
	private Action exportAsHtmlAction;
	private final static String exportAsHtmlActionId = "org.emdepub.ui.editor.markdown.action.exportAsHtml";

	/* First page */
	
	private MenuManager formatStyleMenuManager;
	private Action formatStyleDropDownMenuAction;
	private final static String formatStyleDropDownMenuActionId = "org.emdepub.ui.editor.markdown.action.formatStyleDropDownMenu";
	private Menu formatStyleDropDownMenu;

	private MenuManager formatOptionMenuManager;
	private Action formatOptionDropDownMenuAction;
	private final static String formatOptionDropDownMenuActionId = "org.emdepub.ui.editor.markdown.action.formatOptionDropDownMenu";
	private Menu formatOptionDropDownMenu;

	private MenuManager formatCodeStyleMenuManager;
	private Action formatCodeStyleDropDownMenuAction;
	private final static String formatCodeStyleDropDownMenuActionId = "org.emdepub.ui.editor.markdown.action.formatCodeStyleDropDownMenu";
	private Menu formatCodeStyleDropDownMenu;

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

	/** Eclipse actions */
	private void createActions() {
		
		/* Export Markdown Document as HTML */
		exportAsHtmlAction = new Action() {
			public void run() {
				Shell ideShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				MarkdownExportAsHtmlWizard markdownExportAsHtmlWizard = new MarkdownExportAsHtmlWizard(markdownMultiPageEditor);
				WizardDialog wizardDialog = new WizardDialog(ideShell, markdownExportAsHtmlWizard);
				wizardDialog.open();
			}
		};
		exportAsHtmlAction.setId(exportAsHtmlActionId);
		exportAsHtmlAction.setText("Export Markdown as HTML file...");
		exportAsHtmlAction.setToolTipText("Export Markdown Document as HTML File");
		exportAsHtmlAction.setImageDescriptor(R.getImageDescriptor("html"));
		
		/* First page */
		
		/* Format Style */
		formatStyleMenuManager = new MenuManager("Format Style");
		for (DisplayFormatStyles formatStyle : DisplayFormatStyles.values()) {
			FormatStyleAction action = new FormatStyleAction(formatStyles.get(formatStyle), formatStyle);
			formatStyleMenuManager.add(action);
		};
		formatStyleDropDownMenuAction = new Action("Format Style", IAction.AS_DROP_DOWN_MENU) {
			public void run() { /* ? */ }
		};
		formatStyleDropDownMenuAction.setId(formatStyleDropDownMenuActionId);
		formatStyleDropDownMenuAction.setToolTipText("Format Styles");
		formatStyleDropDownMenuAction.setImageDescriptor(R.getImageDescriptor("stylesheet"));
		formatStyleDropDownMenuAction.setMenuCreator(new IMenuCreator() {
			@Override
			public Menu getMenu(Menu menu) {
				/* ? */
				return null;
			}
			@Override
			public Menu getMenu(Control control) {
				
				if ((formatStyleDropDownMenu != null) && (!formatStyleDropDownMenu.isDisposed())) {
					formatStyleDropDownMenu.dispose();
				}
				formatStyleDropDownMenu = new Menu(control);
				for (IContributionItem contributionItem : formatStyleMenuManager.getItems()) {
					ActionContributionItem actionContributionItem = (ActionContributionItem) contributionItem;
					ActionContributionItem newActionContributionItem = new ActionContributionItem(actionContributionItem.getAction());
					newActionContributionItem.fill(formatStyleDropDownMenu, -1);
				}
				
				return formatStyleDropDownMenu;
			}
			@Override
			public void dispose() { /* ? */ }
		});
		
		/* Format Options */
		formatOptionMenuManager = new MenuManager("Format Options");
		for (PreferenceNames formatOption : PreferenceNames.values()) {
			if (MarkdownPreferences.DisplayFormatOptions.contains(formatOption)) {
				FormatOptionAction action = new FormatOptionAction(formatOptions.get(formatOption), formatOption);
				formatOptionMenuManager.add(action);
			}
		};
		formatOptionDropDownMenuAction = new Action("Format Option", IAction.AS_DROP_DOWN_MENU) {
			public void run() { /* ? */ }
		};
		formatOptionDropDownMenuAction.setId(formatOptionDropDownMenuActionId);
		formatOptionDropDownMenuAction.setToolTipText("Format Options");
		formatOptionDropDownMenuAction.setImageDescriptor(R.getImageDescriptor("ui_props"));
		formatOptionDropDownMenuAction.setMenuCreator(new IMenuCreator() {
			@Override
			public Menu getMenu(Menu menu) {
				/* ? */
				return null;
			}
			@Override
			public Menu getMenu(Control control) {
				if ((formatOptionDropDownMenu != null) && (!formatOptionDropDownMenu.isDisposed())) {
					formatOptionDropDownMenu.dispose();
				}
				formatOptionDropDownMenu = new Menu(control);
				for (IContributionItem contributionItem : formatOptionMenuManager.getItems()) {
					ActionContributionItem actionContributionItem = (ActionContributionItem) contributionItem;
					ActionContributionItem newActionContributionItem = new ActionContributionItem(actionContributionItem.getAction());
					newActionContributionItem.fill(formatOptionDropDownMenu, -1);
				}
				
				return formatOptionDropDownMenu;
			}
			@Override
			public void dispose() { /* ? */ }
		});
		
		/* Format Code Style */
		formatCodeStyleMenuManager = new MenuManager("Format Code Style");
		for (DisplayFormatCodeStyles formatCodeStyle : DisplayFormatCodeStyles.values()) {
			FormatCodeStyleAction action = new FormatCodeStyleAction(formatCodeStyles.get(formatCodeStyle), formatCodeStyle);
			formatCodeStyleMenuManager.add(action);
		};
		formatCodeStyleDropDownMenuAction = new Action("Format Code Style", IAction.AS_DROP_DOWN_MENU) {
			public void run() { /* ? */ }
		};
		formatCodeStyleDropDownMenuAction.setId(formatCodeStyleDropDownMenuActionId);
		formatCodeStyleDropDownMenuAction.setToolTipText("Format Code Styles");
		formatCodeStyleDropDownMenuAction.setImageDescriptor(R.getImageDescriptor("PD_Toolbar_source"));
		formatCodeStyleDropDownMenuAction.setMenuCreator(new IMenuCreator() {
			@Override
			public Menu getMenu(Menu menu) {
				/* ? */
				return null;
			}
			@Override
			public Menu getMenu(Control control) {
				if ((formatCodeStyleDropDownMenu != null) && (!formatCodeStyleDropDownMenu.isDisposed())) {
					formatCodeStyleDropDownMenu.dispose();
				}
				formatCodeStyleDropDownMenu = new Menu(control);
				for (IContributionItem contributionItem : formatCodeStyleMenuManager.getItems()) {
					ActionContributionItem actionContributionItem = (ActionContributionItem) contributionItem;
					ActionContributionItem newActionContributionItem = new ActionContributionItem(actionContributionItem.getAction());
					newActionContributionItem.fill(formatCodeStyleDropDownMenu, -1);
				}
				
				return formatCodeStyleDropDownMenu;
			}
			@Override
			public void dispose() { /* ? */ }
		});
		
		/* Second page */
		
		/* Format Selected Markdown Source Text */
		formatMarkdownAction = new Action() {
			public void run() {
				new ReplaceDocumentSelection() {
					@Override
					public String replace(String selection) {
						return MarkdownFormatterEngine.formatMarkdown(selection, markdownPreferences);
					}
				};
			}
		};
		formatMarkdownAction.setId(formatMarkdownActionId);
		formatMarkdownAction.setText("Format Markdown source");
		formatMarkdownAction.setToolTipText("Format selected Markdown source text");
		formatMarkdownAction.setImageDescriptor(R.getImageDescriptor("markdown-action-format-md"));

		/** Special Markdown format */
		formatOptionsAction = new Action() {
			public void run() {
				Shell ideShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				MarkdownFormatSourceTextWizard markdownFormatSourceTextWizard = new MarkdownFormatSourceTextWizard(markdownMultiPageEditor);
				WizardDialog wizardDialog = new WizardDialog(ideShell, markdownFormatSourceTextWizard);
				wizardDialog.open();    	
			}
		};
		formatOptionsAction.setId(formatOptionsActionId);
		formatOptionsAction.setText("Markdown format options");
		formatOptionsAction.setToolTipText("Markdown format source text options");
		formatOptionsAction.setImageDescriptor(R.getImageDescriptor("markdown-action-create-80"));

		/* Format Selected Markdown Source Text */
		repairBrokenTextAction = new Action() {
			public void run() {
				new ReplaceDocumentSelection() {
					@Override
					public String replace(String selection) {
						return MarkdownFormatterEngine.repairBrokenText(selection);
					}
				};
			}
		};
		repairBrokenTextAction.setId(repairBrokenTextActionId);
		repairBrokenTextAction.setText("Repair broken text paragraphs");
		repairBrokenTextAction.setToolTipText("Repair broken text to re-create paragraphs");
		repairBrokenTextAction.setImageDescriptor(R.getImageDescriptor("markdown-action-repair-paragraph"));

		/** Bold Markdown format */
		boldFormatAction = new Action() {
			public void run() {
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
			}
		};
		boldFormatAction.setId(boldFormatActionId);
		boldFormatAction.setText("Bold format selected text");
		boldFormatAction.setToolTipText("Markdown Bold format selected text");
		boldFormatAction.setImageDescriptor(R.getImageDescriptor("PD_Toolbar_bold"));
		
		/** Italic Markdown format */
		italicFormatAction = new Action() {
			public void run() {
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
			}
		};
		italicFormatAction.setId(italicFormatActionId);
		italicFormatAction.setText("Italic format selected text");
		italicFormatAction.setToolTipText("Markdown Italic format selected text");
		italicFormatAction.setImageDescriptor(R.getImageDescriptor("PD_Toolbar_italic"));
	}

	/** Initial, fix contribution */
	@Override
	public void contributeToMenu(IMenuManager menuManager) {
		markdownMenuManager = new MenuManager("Markdown");
		menuManager.prependToGroup(IWorkbenchActionConstants.MB_ADDITIONS, markdownMenuManager);
		markdownMenuManager.add(exportAsHtmlAction);
		markdownMenuManager.add(new Separator());
	}

	/** Initial, fix contribution */
	@Override
	public void contributeToToolBar(IToolBarManager toolBarManager) {
		markdownToolBarManager = toolBarManager;
		markdownToolBarManager.add(exportAsHtmlAction);
		markdownToolBarManager.add(new Separator());
	}
	
	/** Initial, fix contribution */
	@Override
	public void contributeToStatusLine(IStatusLineManager statusLineManager) {
		markdownStatusLineManager = statusLineManager;
		//statusLineManager.add(statusLineLinkField);
		super.contributeToStatusLine(statusLineManager);
	}

	/** Creates a multi-page contributor */
	public MarkdownEditorContributor() {
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
		
		markdownMultiPageEditor = (MarkdownEditor) editorPart;
		markdownSourceTextEditor = markdownMultiPageEditor.getMarkdownTextEditor();
		markdownPreferences = markdownMultiPageEditor.getPreferences();
		
		/** Word wrap */
		wordWrapAction = (Action) markdownSourceTextEditor.getAction(ITextEditorActionConstants.WORD_WRAP);
		wordWrapAction.setImageDescriptor(R.getImageDescriptor("wordwrap"));
		wordWrapActionId = wordWrapAction.getId();

		/** Show whitespace characters */
		showWhitespaceCharactersAction = (Action) markdownSourceTextEditor.getAction(ITextEditorActionConstants.SHOW_WHITESPACE_CHARACTERS);
		showWhitespaceCharactersAction.setImageDescriptor(R.getImageDescriptor("show_whitespace_chars"));
		showWhitespaceCharactersActionId = showWhitespaceCharactersAction.getId();
		
		super.setActiveEditor(markdownMultiPageEditor);
		
		for (IContributionItem contributionItem : formatStyleMenuManager.getItems()) {
			ActionContributionItem actionContributionItem = (ActionContributionItem) contributionItem;
			FormatStyleAction formatStyleAction = (FormatStyleAction) actionContributionItem.getAction();
			if (formatStyleAction.getFormatStyle() == markdownPreferences.<DisplayFormatStyles>get(PreferenceNames.DisplayFormatStyle)) {
				formatStyleAction.setChecked(true);
			}
			else {
				formatStyleAction.setChecked(false);
			}
		}

		for (IContributionItem contributionItem : formatOptionMenuManager.getItems()) {
			ActionContributionItem actionContributionItem = (ActionContributionItem) contributionItem;
			FormatOptionAction formatOptionAction = (FormatOptionAction) actionContributionItem.getAction();
			formatOptionAction.setChecked(markdownPreferences.<Boolean>get(formatOptionAction.getFormatOption()));
		}

		for (IContributionItem contributionItem : formatCodeStyleMenuManager.getItems()) {
			ActionContributionItem actionContributionItem = (ActionContributionItem) contributionItem;
			FormatCodeStyleAction formatCodeStyleAction = (FormatCodeStyleAction) actionContributionItem.getAction();
			if (formatCodeStyleAction.getFormatCodeStyle() == markdownPreferences.<DisplayFormatCodeStyles>get(PreferenceNames.DisplayFormatCodeStyle)) {
				formatCodeStyleAction.setChecked(true);
			}
			else {
				formatCodeStyleAction.setChecked(false);
			}
		}
		
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
		
		IActionBars actionBars = getActionBars();
		if ((markdownMultiPageEditor == null) || (actionBars == null)) {
			return;
		}

		markdownMenuManager.remove(formatStyleMenuManager);
		markdownToolBarManager.remove(formatStyleDropDownMenuActionId);

		markdownMenuManager.remove(formatOptionMenuManager);
		markdownToolBarManager.remove(formatOptionDropDownMenuActionId);

		markdownMenuManager.remove(formatCodeStyleMenuManager);
		markdownToolBarManager.remove(formatCodeStyleDropDownMenuActionId);

		markdownToolBarManager.remove(wordWrapActionId);
		markdownToolBarManager.remove(showWhitespaceCharactersActionId);
		
		markdownToolBarManager.remove(editSeparatorActionId);

		markdownMenuManager.remove(formatMarkdownActionId);
		markdownToolBarManager.remove(formatMarkdownActionId);
		
		markdownMenuManager.remove(formatOptionsActionId);
		markdownToolBarManager.remove(formatOptionsActionId);

		markdownMenuManager.remove(repairBrokenTextActionId);
		markdownToolBarManager.remove(repairBrokenTextActionId);

		markdownToolBarManager.remove(formatSeparatorActionId);
		
		markdownToolBarManager.remove(boldFormatActionId);
		markdownToolBarManager.remove(italicFormatActionId);

		/* Status line */
		markdownStatusLineManager.remove(statusLineLinkField);
		markdownStatusLineManager.remove(statusLinePositionField);
		
		/* Update */
		markdownToolBarManager.update(true);

		if (markdownMultiPageEditor.getActivePage() == markdownMultiPageEditor.getMarkdownViewerBrowserPageIndex()) {
			
			markdownMenuManager.add(formatStyleMenuManager);
			markdownToolBarManager.add(formatStyleDropDownMenuAction);
			
			markdownMenuManager.add(formatOptionMenuManager);
			markdownToolBarManager.add(formatOptionDropDownMenuAction);
			
			markdownMenuManager.add(formatCodeStyleMenuManager);
			markdownToolBarManager.add(formatCodeStyleDropDownMenuAction);

			markdownStatusLineManager.add(statusLineLinkField);

			/* Update */
			markdownToolBarManager.update(true);
			markdownStatusLineManager.update(true);
		}
		if (markdownMultiPageEditor.getActivePage() == markdownMultiPageEditor.getMarkdownTextEditorPageIndex()) {

			markdownToolBarManager.add(wordWrapAction);
			markdownToolBarManager.add(showWhitespaceCharactersAction);
			
			markdownToolBarManager.add(editSeparatorAction);

			markdownMenuManager.add(formatMarkdownAction);
			markdownToolBarManager.add(formatMarkdownAction);

			markdownMenuManager.add(formatOptionsAction);
			markdownToolBarManager.add(formatOptionsAction);

			markdownMenuManager.add(repairBrokenTextAction);
			markdownToolBarManager.add(repairBrokenTextAction);

			markdownToolBarManager.add(formatSeparatorAction);

			markdownToolBarManager.add(boldFormatAction);
			markdownToolBarManager.add(italicFormatAction);
			
			markdownStatusLineManager.add(statusLinePositionField);

			statusLinePositionField.setText(markdownSourceTextEditor.getCursorPositionString());
			/* Update */
			markdownToolBarManager.update(true);
			markdownStatusLineManager.update(true);
		}
	}

	public static StatusLineContributionItem getStatusLineLinkField() {
		return statusLineLinkField;
	}
	
	public static StatusLineContributionItem getStatusLinePositionField() {
		return statusLinePositionField;
	}
}
