/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.ui.editor.md;

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
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.emdepub.activator.L;
import org.emdepub.activator.R;
import org.emdepub.md.ui.wizards.MarkdownExportAsHtmlWizard;
import org.emdepub.md.ui.wizards.MarkdownFormatSourceTextWizard;
import org.emdepub.ui.editor.md.engine.MarkdownEditorEngine;
import org.emdepub.ui.editor.md.prefs.MarkdownPreferences;
import org.emdepub.ui.editor.md.prefs.MarkdownPreferences.DisplayFormatCodeStyles;
import org.emdepub.ui.editor.md.prefs.MarkdownPreferences.DisplayFormatStyles;
import org.emdepub.ui.editor.md.prefs.MarkdownPreferences.PreferenceNames;

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
		formatCodeStyles.put(DisplayFormatCodeStyles.Xcode, "Xcode");
		formatCodeStyles.put(DisplayFormatCodeStyles.Custom, "Custom");
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
	private MenuManager markdownMenuManager;
	/** Contributed tool bar */
	private ToolBarManager markdownToolBarManager;

	/** Export as one HTML file */
	private Action exportAsHtmlAction;
	private final static String exportAsHtmlActionId = "org.emdepub.ui.editor.md.action.exportAsHtml";

	/* First page */
	
	private MenuManager formatStyleMenuManager;
	private Action formatStyleDropDownMenuAction;
	private final static String formatStyleDropDownMenuActionId = "org.emdepub.ui.editor.md.action.formatStyleDropDownMenu";
	private Menu formatStyleDropDownMenu;

	private MenuManager formatOptionMenuManager;
	private Action formatOptionDropDownMenuAction;
	private final static String formatOptionDropDownMenuActionId = "org.emdepub.ui.editor.md.action.formatOptionDropDownMenu";
	private Menu formatOptionDropDownMenu;

	private MenuManager formatCodeStyleMenuManager;
	private Action formatCodeStyleDropDownMenuAction;
	private final static String formatCodeStyleDropDownMenuActionId = "org.emdepub.ui.editor.md.action.formatCodeStyleDropDownMenu";
	private Menu formatCodeStyleDropDownMenu;

	/** Browser hover link */
	private static StatusLineContributionItem statusLineLinkField;
	
	/* Second page */
	
	/** Word wrap */
	private Action wordWrapAction;
	private String wordWrapActionId;

	/** Show whitespace characters */
	private Action showWhitespaceCharactersAction;
	private String showWhitespaceCharactersActionId;

	/** Named separator */
	private Separator editSeparatorAction = new Separator();
	private String editSeparatorActionId = "org.emdepub.ui.editor.md.action.editSeparatorAction";
	
	/** Format Markdown text */
	private Action formatMarkdownAction;
	private String formatMarkdownActionId = "org.emdepub.ui.editor.md.action.formatMarkdownAction";

	/** Special Markdown text format */
	private Action specialFormatAction;
	private String specialFormatActionId = "org.emdepub.ui.editor.md.action.specialFormatAction";
	
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
				TextSelection textSelection = (TextSelection) markdownSourceTextEditor.getSelectionProvider().getSelection();
				String selection = textSelection.getText();
//				L.p(selection);
				String formattedSelection = MarkdownEditorEngine.formatMarkdown(selection, markdownPreferences);
//				L.p(formattedSelection);
				IDocument document = markdownSourceTextEditor.getDocumentProvider().getDocument(markdownSourceTextEditor.getEditorInput());
				try {
					document.replace(textSelection.getOffset(), textSelection.getLength(), formattedSelection);
				}
				catch (BadLocationException badLocationException) {
					L.e("BadLocationException in formatMarkdownAction", badLocationException);
				}
			}
		};
		formatMarkdownAction.setId(formatMarkdownActionId);
		formatMarkdownAction.setText("Format Markdown source");
		formatMarkdownAction.setToolTipText("Format selected Markdown source text");
		formatMarkdownAction.setImageDescriptor(R.getImageDescriptor("markdown-action-format-md"));
		
		/** Special Markdown format */
		specialFormatAction = new Action() {
			public void run() {
				Shell ideShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				MarkdownFormatSourceTextWizard markdownFormatSourceTextWizard = new MarkdownFormatSourceTextWizard(markdownMultiPageEditor);
				WizardDialog wizardDialog = new WizardDialog(ideShell, markdownFormatSourceTextWizard);
				wizardDialog.open();    	
			}
		};
		specialFormatAction.setId(specialFormatActionId);
		specialFormatAction.setText("Markdown format options");
		specialFormatAction.setToolTipText("Markdown format source text options");
		specialFormatAction.setImageDescriptor(R.getImageDescriptor("markdown-action-repair-paragraph"));
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
		markdownToolBarManager = (ToolBarManager) toolBarManager;
		markdownToolBarManager.add(exportAsHtmlAction);
		markdownToolBarManager.add(new Separator());
	}
	
	/** Initial, fix contribution */
	@Override
	public void contributeToStatusLine(IStatusLineManager statusLineManager) {
		statusLineManager.add(statusLineLinkField);
		super.contributeToStatusLine(statusLineManager);
	}

	/** Creates a multi-page contributor */
	public MarkdownEditorContributor() {
		super();

		createActions();
		
		statusLineLinkField = new StatusLineContributionItem("statusLineLinkField", 120);
		statusLineLinkField.setText("");
		
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
		
		if (markdownMultiPageEditor == null) {
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
		
		markdownMenuManager.remove(specialFormatActionId);
		markdownToolBarManager.remove(specialFormatActionId);
		
		/* Update */
		markdownToolBarManager.update(true);

		if (markdownMultiPageEditor.getActivePage() == markdownMultiPageEditor.getBrowserViewerPageIndex()) {
			
			markdownMenuManager.add(formatStyleMenuManager);
			markdownToolBarManager.add(formatStyleDropDownMenuAction);
			
			markdownMenuManager.add(formatOptionMenuManager);
			markdownToolBarManager.add(formatOptionDropDownMenuAction);
			
			markdownMenuManager.add(formatCodeStyleMenuManager);
			markdownToolBarManager.add(formatCodeStyleDropDownMenuAction);
			
			/* Update */
			markdownToolBarManager.update(true);
		}
		if (markdownMultiPageEditor.getActivePage() == markdownMultiPageEditor.getMarkdownTextEditorPageIndex()) {

			markdownToolBarManager.add(wordWrapAction);
			markdownToolBarManager.add(showWhitespaceCharactersAction);
			
			markdownToolBarManager.add(editSeparatorAction);

			markdownMenuManager.add(formatMarkdownAction);
			markdownToolBarManager.add(formatMarkdownAction);

			markdownMenuManager.add(specialFormatAction);
			markdownToolBarManager.add(specialFormatAction);
			
			/* Update */
			markdownToolBarManager.update(true);
		}
	}

	public static StatusLineContributionItem getStatusLineLinkField() {
		return statusLineLinkField;
	}
}
