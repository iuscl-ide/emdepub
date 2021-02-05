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
import org.emdepub.activator.L;
import org.emdepub.activator.R;
import org.emdepub.md.ui.wizards.MarkdownExportAsHtmlWizard;
import org.emdepub.md.ui.wizards.MarkdownFormatSourceTextWizard;
import org.emdepub.ui.editor.md.engine.MarkdownEditorEngine;
import org.emdepub.ui.editor.md.prefs.MarkdownHtmlGeneratorPrefs;
import org.emdepub.ui.editor.md.prefs.MarkdownHtmlGeneratorPrefs.FormatCodeStyle;
import org.emdepub.ui.editor.md.prefs.MarkdownHtmlGeneratorPrefs.FormatStyle;
import org.emdepub.ui.editor.md.prefs.MarkdownHtmlGeneratorPrefs.Pref;

/** Markdown multi-page editor contributor to menu, tool bar, status bar */
public class MarkdownEditorContributor extends MultiPageEditorActionBarContributor {

	public static final LinkedHashMap<FormatStyle, String> formatStyles = new LinkedHashMap<>();
	static {
		formatStyles.put(FormatStyle.None, "None");
		formatStyles.put(FormatStyle.GitHub, "GitHub");
		formatStyles.put(FormatStyle.GoogleLike, "Google Like");
		formatStyles.put(FormatStyle.SemanticUILike, "Semantic UI Like");
		formatStyles.put(FormatStyle.Custom, "Custom");
	}

	public static final LinkedHashMap<Pref, String> formatOptions = new LinkedHashMap<>();
	static {
		formatOptions.put(Pref.FixedContentWidth, "Fixed Content Width");
		formatOptions.put(Pref.JustifiedParagraphs, "Justified Paragraphs");
		formatOptions.put(Pref.CenterHeaders, "Center Headers");
	}

	public static final LinkedHashMap<FormatCodeStyle, String> formatCodeStyles = new LinkedHashMap<>();
	static {
		formatCodeStyles.put(FormatCodeStyle.None, "None");
		formatCodeStyles.put(FormatCodeStyle.Agate, "Agate");
		formatCodeStyles.put(FormatCodeStyle.Androidstudio, "Android Studio");
		formatCodeStyles.put(FormatCodeStyle.ArduinoLight, "Arduino Light");
		formatCodeStyles.put(FormatCodeStyle.Dark, "Dark");
		formatCodeStyles.put(FormatCodeStyle.Default, "Default");
		formatCodeStyles.put(FormatCodeStyle.Far, "Far");
		formatCodeStyles.put(FormatCodeStyle.Foundation, "Foundation");
		formatCodeStyles.put(FormatCodeStyle.GithubGist, "Github Gist");
		formatCodeStyles.put(FormatCodeStyle.Github, "Github");
		formatCodeStyles.put(FormatCodeStyle.Googlecode, "Googlecode");
		formatCodeStyles.put(FormatCodeStyle.Grayscale, "Grayscale");
		formatCodeStyles.put(FormatCodeStyle.GruvboxDark, "Gruvbox Dark");
		formatCodeStyles.put(FormatCodeStyle.GruvboxLight, "Gruvbox Light");
		formatCodeStyles.put(FormatCodeStyle.Hybrid, "Hybrid");
		formatCodeStyles.put(FormatCodeStyle.Idea, "Idea");
		formatCodeStyles.put(FormatCodeStyle.IrBlack, "Ir Black");
		formatCodeStyles.put(FormatCodeStyle.Magula, "Magula");
		formatCodeStyles.put(FormatCodeStyle.Purebasic, "Purebasic");
		formatCodeStyles.put(FormatCodeStyle.Railscasts, "Railscasts");
		formatCodeStyles.put(FormatCodeStyle.Sunburst, "Sunburst");
		formatCodeStyles.put(FormatCodeStyle.Vs, "Vs");
		formatCodeStyles.put(FormatCodeStyle.Vs2015, "Vs2015");
		formatCodeStyles.put(FormatCodeStyle.Xcode, "Xcode");
		formatCodeStyles.put(FormatCodeStyle.Custom, "Custom");
	}
	
	/** Class */
	private class FormatStyleAction extends Action {
		
		private FormatStyle formatStyle;

		public FormatStyleAction(String text, FormatStyle formatStyle) {
			super(text, IAction.AS_RADIO_BUTTON);
			
			this.formatStyle = formatStyle;
		}

		public FormatStyle getFormatStyle() {
			return formatStyle;
		}

		@Override
		public void run() {
			super.run();
			
			if (this.isChecked()) {
				markdownHtmlGeneratorPrefs.set(Pref.FormatStyle, formatStyle);
				markdownMultiPageEditor.refresh();
				savePrefsProperties();
			}
		}
	}

	/** Class */
	private class FormatOptionAction extends Action {
		
		private Pref formatOption;

		public FormatOptionAction(String text, Pref formatOption) {
			super(text, IAction.AS_CHECK_BOX);
			
			this.formatOption = formatOption;
		}

		public Pref getFormatOption() {
			return formatOption;
		}

		@Override
		public void run() {
			super.run();
			
			Boolean value = markdownHtmlGeneratorPrefs.get(formatOption);
			markdownHtmlGeneratorPrefs.set(formatOption, !value);
			markdownMultiPageEditor.refresh();
			savePrefsProperties();
		}
	}

	/** Class */
	private class FormatCodeStyleAction extends Action {
		
		private FormatCodeStyle formatCodeStyle;

		public FormatCodeStyleAction(String text, FormatCodeStyle formatCodeStyle) {
			super(text, IAction.AS_RADIO_BUTTON);
			
			this.formatCodeStyle = formatCodeStyle;
		}

		public FormatCodeStyle getFormatCodeStyle() {
			return formatCodeStyle;
		}

		@Override
		public void run() {
			super.run();
			
			if (this.isChecked()) {
				markdownHtmlGeneratorPrefs.set(Pref.FormatCodeStyle, formatCodeStyle);
				markdownMultiPageEditor.refresh();
				savePrefsProperties();
			}
		}
	}

	/** The actual editor */
	private MarkdownEditor markdownMultiPageEditor;
	private String prefsPropertiesFileNameWithPath;
	private MarkdownTextEditor markdownSourceTextEditor;
	private MarkdownHtmlGeneratorPrefs markdownHtmlGeneratorPrefs;
	
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
	
	/** Format Markdown text */
	private Action formatMarkdownAction;
	private String formatMarkdownActionId = "org.emdepub.ui.editor.md.action.formatMarkdownAction";

	/** Show word wrap */
	private Action showWordWrapAction;
	private String showWordWrapActionId = "org.emdepub.ui.editor.md.action.showWordWrapAction";

	/** Format text to 80 columns */
	private Action format80ColumnsAction;
	private String format80ColumnsActionId = "org.emdepub.ui.editor.md.action.format80ColumnsAction";

	/** Repair broken paragraph */
	private Action repairPragraphAction;
	private String repairPragraphActionId = "org.emdepub.ui.editor.md.action.repairPragraphAction";

	
//	/** First page */
//	private BrowserViewerPageActionContributor browserViewerPageActionContributor;
//	/** Second page */
//	private TextEditorPageActionContributor textEditorPageActionContributor;
	
	/** Eclipse actions */
	private void createActions() {
		
		/* Export Markdown Document as HTML */
		exportAsHtmlAction = new Action() {
			public void run() {
				
				Shell ideShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				MarkdownExportAsHtmlWizard markdownExportAsHtmlWizard = new MarkdownExportAsHtmlWizard(markdownMultiPageEditor);
				WizardDialog dialog = new WizardDialog(ideShell, markdownExportAsHtmlWizard);
				int result = dialog.open();    	

				
				
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
		exportAsHtmlAction.setImageDescriptor(R.getImageDescriptor("html"));
		
		
		/* First page */
		
		/* Format Style */
		formatStyleMenuManager = new MenuManager("Format Style");
		for (FormatStyle formatStyle : FormatStyle.values()) {
			FormatStyleAction action = new FormatStyleAction(formatStyles.get(formatStyle), formatStyle);
			formatStyleMenuManager.add(action);
		};
		formatStyleDropDownMenuAction = new Action("Format Style", IAction.AS_DROP_DOWN_MENU) {
			public void run() {
				/* ? */
			}
		};
		formatStyleDropDownMenuAction.setId(formatStyleDropDownMenuActionId);
		formatStyleDropDownMenuAction.setToolTipText("Format Styles");
		formatStyleDropDownMenuAction.setImageDescriptor(R.getImageDescriptor("stylesheet"));
		formatStyleDropDownMenuAction.setMenuCreator(new IMenuCreator() {
			@Override
			public Menu getMenu(Menu arg0) {
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
			public void dispose() {
				/* ? */
			}
		});
		
		/* Format Options */
		formatOptionMenuManager = new MenuManager("Format Options");
		for (Pref formatOption : Pref.values()) {
			if (MarkdownHtmlGeneratorPrefs.FormatOption.contains(formatOption)) {
				FormatOptionAction action = new FormatOptionAction(formatOptions.get(formatOption), formatOption);
				formatOptionMenuManager.add(action);
			}
		};
		formatOptionDropDownMenuAction = new Action("Format Option", IAction.AS_DROP_DOWN_MENU) {
			public void run() {
				/* ? */
			}
		};
		formatOptionDropDownMenuAction.setId(formatOptionDropDownMenuActionId);
		formatOptionDropDownMenuAction.setToolTipText("Format Options");
		formatOptionDropDownMenuAction.setImageDescriptor(R.getImageDescriptor("ui_props"));
		formatOptionDropDownMenuAction.setMenuCreator(new IMenuCreator() {
			@Override
			public Menu getMenu(Menu arg0) {
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
			public void dispose() {
				/* ? */
			}
		});
		
		/* Format Code Style */
		formatCodeStyleMenuManager = new MenuManager("Format Code Style");
		for (FormatCodeStyle formatCodeStyle : FormatCodeStyle.values()) {
			FormatCodeStyleAction action = new FormatCodeStyleAction(formatCodeStyles.get(formatCodeStyle), formatCodeStyle);
			formatCodeStyleMenuManager.add(action);
		};
		formatCodeStyleDropDownMenuAction = new Action("Format Code Style", IAction.AS_DROP_DOWN_MENU) {
			public void run() {
				/* ? */
			}
		};
		formatCodeStyleDropDownMenuAction.setId(formatCodeStyleDropDownMenuActionId);
		formatCodeStyleDropDownMenuAction.setToolTipText("Format Code Styles");
		formatCodeStyleDropDownMenuAction.setImageDescriptor(R.getImageDescriptor("PD_Toolbar_source"));
		formatCodeStyleDropDownMenuAction.setMenuCreator(new IMenuCreator() {
			@Override
			public Menu getMenu(Menu arg0) {
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
			public void dispose() {
				/* ? */
			}
		});

		
		/* Second page */
		
		/* Format Selected Markdown Source Text */
		formatMarkdownAction = new Action() {
			public void run() {
				
				Shell ideShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				MarkdownFormatSourceTextWizard markdownFormatSourceTextWizard = new MarkdownFormatSourceTextWizard(markdownMultiPageEditor);
				WizardDialog dialog = new WizardDialog(ideShell, markdownFormatSourceTextWizard);
				int result = dialog.open();    	

				
//				TextSelection textSelection = (TextSelection) markdownSourceTextEditor.getSelectionProvider().getSelection();
//				String selection = textSelection.getText();
////				L.p(selection);
//				String formattedSelection = MarkdownEditorEngine.formatMarkdown(selection);
////				L.p(formattedSelection);
//				IDocument document = markdownSourceTextEditor.getDocumentProvider().getDocument(markdownSourceTextEditor.getEditorInput());
//				try {
//					document.replace(textSelection.getOffset(), textSelection.getLength(), formattedSelection);
//				}
//				catch (BadLocationException badLocationException) {
//					L.e("BadLocationException in formatMdAction", badLocationException);
//				}
			}
		};
		formatMarkdownAction.setId(formatMarkdownActionId);
		formatMarkdownAction.setText("Format Markdown source");
		formatMarkdownAction.setToolTipText("Format selected Markdown source text");
		formatMarkdownAction.setImageDescriptor(R.getImageDescriptor("markdown-action-format-md"));
		
		/** Show word wrap */
		showWordWrapAction = new Action() {
			public void run() {
				boolean wordWrapEnabled = markdownSourceTextEditor.isWordWrapEnabled();
				wordWrapEnabled = !wordWrapEnabled;
				markdownSourceTextEditor.setWordWrap(wordWrapEnabled);
			}
		};
		showWordWrapAction.setChecked(false);
		showWordWrapAction.setId(showWordWrapActionId);
		showWordWrapAction.setText("Set text word wrap");
		showWordWrapAction.setToolTipText("Show text with word wrap");
		showWordWrapAction.setImageDescriptor(R.getImageDescriptor("markdown-action-word-wrap"));

		/** Format text to 80 columns */
		format80ColumnsAction = new Action() {
			public void run() {
				//markdownSourceTextEditor.format80ParagraphContributor();
			}
		};
		format80ColumnsAction.setId(format80ColumnsActionId);
		format80ColumnsAction.setText("Format to 80 columns");
		format80ColumnsAction.setToolTipText("Format selected text to 80 columns");
		format80ColumnsAction.setImageDescriptor(R.getImageDescriptor("markdown-action-create-80"));

		/** Repair broken paragraph */
		repairPragraphAction = new Action() {
			public void run() {
				//markdownSourceTextEditor.repairBrokenParagraphContributor();
			}
		};
		repairPragraphAction.setId(repairPragraphActionId);
		repairPragraphAction.setText("Repair broken paragraphs");
		repairPragraphAction.setToolTipText("Repair broken selected text paragraphs");
		repairPragraphAction.setImageDescriptor(R.getImageDescriptor("markdown-action-repair-paragraph"));
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
		prefsPropertiesFileNameWithPath = markdownMultiPageEditor.getSourceMarkdownFilePathAndName() + ".prefs";
		markdownSourceTextEditor = markdownMultiPageEditor.getMarkdownTextEditor();
		markdownHtmlGeneratorPrefs = markdownMultiPageEditor.getMarkdownHtmlGeneratorPrefs();
		
		super.setActiveEditor(markdownMultiPageEditor);
		
		for (IContributionItem contributionItem : formatStyleMenuManager.getItems()) {
			ActionContributionItem actionContributionItem = (ActionContributionItem) contributionItem;
			FormatStyleAction formatStyleAction = (FormatStyleAction) actionContributionItem.getAction();
			if (formatStyleAction.getFormatStyle() == markdownHtmlGeneratorPrefs.<FormatStyle>get(Pref.FormatStyle)) {
				formatStyleAction.setChecked(true);
			}
			else {
				formatStyleAction.setChecked(false);
			}
		}

		for (IContributionItem contributionItem : formatOptionMenuManager.getItems()) {
			ActionContributionItem actionContributionItem = (ActionContributionItem) contributionItem;
			FormatOptionAction formatOptionAction = (FormatOptionAction) actionContributionItem.getAction();
			formatOptionAction.setChecked(markdownHtmlGeneratorPrefs.<Boolean>get(formatOptionAction.getFormatOption()));
		}

		for (IContributionItem contributionItem : formatCodeStyleMenuManager.getItems()) {
			ActionContributionItem actionContributionItem = (ActionContributionItem) contributionItem;
			FormatCodeStyleAction formatCodeStyleAction = (FormatCodeStyleAction) actionContributionItem.getAction();
			if (formatCodeStyleAction.getFormatCodeStyle() == markdownHtmlGeneratorPrefs.<FormatCodeStyle>get(Pref.FormatCodeStyle)) {
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

		markdownMenuManager.remove(formatMarkdownActionId);
		markdownToolBarManager.remove(formatMarkdownActionId);

		markdownMenuManager.remove(showWordWrapActionId);
		markdownToolBarManager.remove(showWordWrapActionId);

		markdownMenuManager.remove(format80ColumnsActionId);
		markdownToolBarManager.remove(format80ColumnsActionId);

		markdownMenuManager.remove(repairPragraphActionId);
		markdownToolBarManager.remove(repairPragraphActionId);
		
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

			markdownMenuManager.add(formatMarkdownAction);
			markdownToolBarManager.add(formatMarkdownAction);

			markdownMenuManager.add(showWordWrapAction);
			markdownToolBarManager.add(showWordWrapAction);

			markdownMenuManager.add(format80ColumnsAction);
			markdownToolBarManager.add(format80ColumnsAction);

			markdownMenuManager.add(repairPragraphAction);
			markdownToolBarManager.add(repairPragraphAction);

			/* Update */
			markdownToolBarManager.update(true);
		}
	
	
	
	
	//((IContributionManager) ddAction).add(formatStyleMenuManager);
	
//		CommandContributionItemParameter commandContributionItemParameter = new 
//		CommandContributionItem commandContributionItem = new CommandContributionItem(null) 
//		formatStyleMenuManager.add();
//		prependToGroup(IWorkbenchActionConstants.MB_ADDITIONS, mdMenuManager);
//		mdMenuManager.add(exportAsHtmlAction);
//		mdMenuManager.add(new Separator());

		
		//markdownTextEditor = (MarkdownTextEditor) editorPart;
		
		// TODO Auto-generated method stub

//		textEditorPageActionContributor.contributeToMenu(bars.getMenuManager());
//		textEditorPageActionContributor.contributeToToolBar(bars.getToolBarManager());

	}

	/** Save from here */
	private void savePrefsProperties() {
		
		markdownHtmlGeneratorPrefs.saveProperties(prefsPropertiesFileNameWithPath);
	}

	public static StatusLineContributionItem getStatusLineLinkField() {
		return statusLineLinkField;
	}
}
