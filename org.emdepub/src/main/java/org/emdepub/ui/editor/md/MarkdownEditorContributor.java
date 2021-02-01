/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.ui.editor.md;

import java.util.LinkedHashMap;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.emdepub.activator.R;
import org.emdepub.md.ui.wizards.MarkdownExportAsHtmlWizard;
import org.emdepub.ui.editor.md.prefs.MarkdownHtmlGeneratorPrefs;
import org.emdepub.ui.editor.md.prefs.MarkdownHtmlGeneratorPrefs.FormatCodeStyle;
import org.emdepub.ui.editor.md.prefs.MarkdownHtmlGeneratorPrefs.FormatStyle;
import org.emdepub.ui.editor.md.prefs.MarkdownHtmlGeneratorPrefs.Pref;

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
//		formatCodeStyles.put(FormatCodeStyle.Arta, "Arta");
//		formatCodeStyles.put(FormatCodeStyle.Ascetic, "Ascetic");
//		formatCodeStyles.put(FormatCodeStyle.AtelierCaveDark, "Atelier Cave Dark");
//		formatCodeStyles.put(FormatCodeStyle.AtelierCaveLight, "Atelier Cave Light");
//		formatCodeStyles.put(FormatCodeStyle.AtelierDuneDark, "Atelier Dune Dark");
//		formatCodeStyles.put(FormatCodeStyle.AtelierDuneLight, "Atelier Dune Light");
//		formatCodeStyles.put(FormatCodeStyle.AtelierEstuaryDark, "Atelier Estuary Dark");
//		formatCodeStyles.put(FormatCodeStyle.AtelierEstuaryLight, "Atelier Estuary Light");
//		formatCodeStyles.put(FormatCodeStyle.AtelierForestDark, "Atelier Forest Dark");
//		formatCodeStyles.put(FormatCodeStyle.AtelierForestLight, "Atelier Forest Light");
//		formatCodeStyles.put(FormatCodeStyle.AtelierHeathDark, "Atelier Heath Dark");
//		formatCodeStyles.put(FormatCodeStyle.AtelierHeathLight, "Atelier Heath Light");
//		formatCodeStyles.put(FormatCodeStyle.AtelierLakesideDark, "Atelier Lakeside Dark");
//		formatCodeStyles.put(FormatCodeStyle.AtelierLakesideLight, "Atelier Lakeside Light");
//		formatCodeStyles.put(FormatCodeStyle.AtelierPlateauDark, "Atelier Plateau Dark");
//		formatCodeStyles.put(FormatCodeStyle.AtelierPlateauLight, "Atelier Plateau Light");
//		formatCodeStyles.put(FormatCodeStyle.AtelierSavannaDark, "Atelier Savanna Dark");
//		formatCodeStyles.put(FormatCodeStyle.AtelierSavannaLight, "Atelier Savanna Light");
//		formatCodeStyles.put(FormatCodeStyle.AtelierSeasideDark, "Atelier Seaside Dark");
//		formatCodeStyles.put(FormatCodeStyle.AtelierSeasideLight, "Atelier Seaside Light");
//		formatCodeStyles.put(FormatCodeStyle.AtelierSulphurpoolDark, "Atelier Sulphurpool Dark");
//		formatCodeStyles.put(FormatCodeStyle.AtelierSulphurpoolLight, "Atelier Sulphurpool Light");
//		formatCodeStyles.put(FormatCodeStyle.AtomOneDark, "Atom One Dark");
//		formatCodeStyles.put(FormatCodeStyle.AtomOneLight, "Atom One Light");
//		formatCodeStyles.put(FormatCodeStyle.BrownPaper, "Brown Paper");
//		formatCodeStyles.put(FormatCodeStyle.CodepenEmbed, "Codepen Embed");
//		formatCodeStyles.put(FormatCodeStyle.ColorBrewer, "Color Brewer");
//		formatCodeStyles.put(FormatCodeStyle.Darcula, "Darcula");
		formatCodeStyles.put(FormatCodeStyle.Dark, "Dark");
//		formatCodeStyles.put(FormatCodeStyle.Darkula, "Darkula");
		formatCodeStyles.put(FormatCodeStyle.Default, "Default");
//		formatCodeStyles.put(FormatCodeStyle.Docco, "Docco");
//		formatCodeStyles.put(FormatCodeStyle.Dracula, "Dracula");
		formatCodeStyles.put(FormatCodeStyle.Far, "Far");
		formatCodeStyles.put(FormatCodeStyle.Foundation, "Foundation");
		formatCodeStyles.put(FormatCodeStyle.GithubGist, "Github Gist");
		formatCodeStyles.put(FormatCodeStyle.Github, "Github");
		formatCodeStyles.put(FormatCodeStyle.Googlecode, "Googlecode");
		formatCodeStyles.put(FormatCodeStyle.Grayscale, "Grayscale");
		formatCodeStyles.put(FormatCodeStyle.GruvboxDark, "Gruvbox Dark");
		formatCodeStyles.put(FormatCodeStyle.GruvboxLight, "Gruvbox Light");
//		formatCodeStyles.put(FormatCodeStyle.Hopscotch, "Hopscotch");
		formatCodeStyles.put(FormatCodeStyle.Hybrid, "Hybrid");
		formatCodeStyles.put(FormatCodeStyle.Idea, "Idea");
		formatCodeStyles.put(FormatCodeStyle.IrBlack, "Ir Black");
//		formatCodeStyles.put(FormatCodeStyle.KimbieDark, "Kimbie.Dark");
//		formatCodeStyles.put(FormatCodeStyle.KimbieLight, "Kimbie.Light");
		formatCodeStyles.put(FormatCodeStyle.Magula, "Magula");
//		formatCodeStyles.put(FormatCodeStyle.MonoBlue, "Mono Blue");
//		formatCodeStyles.put(FormatCodeStyle.MonokaiSublime, "Monokai Sublime");
//		formatCodeStyles.put(FormatCodeStyle.Monokai, "Monokai");
//		formatCodeStyles.put(FormatCodeStyle.Obsidian, "Obsidian");
//		formatCodeStyles.put(FormatCodeStyle.Ocean, "Ocean");
//		formatCodeStyles.put(FormatCodeStyle.ParaisoDark, "Paraiso Dark");
//		formatCodeStyles.put(FormatCodeStyle.ParaisoLight, "Paraiso Light");
//		formatCodeStyles.put(FormatCodeStyle.Pojoaque, "Pojoaque");
		formatCodeStyles.put(FormatCodeStyle.Purebasic, "Purebasic");
//		formatCodeStyles.put(FormatCodeStyle.Qtcreator_dark, "Qtcreator_dark");
//		formatCodeStyles.put(FormatCodeStyle.Qtcreator_light, "Qtcreator_light");
		formatCodeStyles.put(FormatCodeStyle.Railscasts, "Railscasts");
//		formatCodeStyles.put(FormatCodeStyle.Rainbow, "Rainbow");
//		formatCodeStyles.put(FormatCodeStyle.Routeros, "Routeros");
//		formatCodeStyles.put(FormatCodeStyle.SchoolBook, "School Book");
//		formatCodeStyles.put(FormatCodeStyle.SolarizedDark, "Solarized Dark");
//		formatCodeStyles.put(FormatCodeStyle.SolarizedLight, "Solarized Light");
		formatCodeStyles.put(FormatCodeStyle.Sunburst, "Sunburst");
//		formatCodeStyles.put(FormatCodeStyle.TomorrowNightBlue, "Tomorrow Night Blue");
//		formatCodeStyles.put(FormatCodeStyle.TomorrowNightBright, "Tomorrow Night Bright");
//		formatCodeStyles.put(FormatCodeStyle.TomorrowNightEighties, "Tomorrow Night Eighties");
//		formatCodeStyles.put(FormatCodeStyle.TomorrowNight, "Tomorrow Night");
//		formatCodeStyles.put(FormatCodeStyle.Tomorrow, "Tomorrow");
		formatCodeStyles.put(FormatCodeStyle.Vs, "Vs");
		formatCodeStyles.put(FormatCodeStyle.Vs2015, "Vs2015");
		formatCodeStyles.put(FormatCodeStyle.Xcode, "Xcode");
//		formatCodeStyles.put(FormatCodeStyle.Xt256, "Xt256");
//		formatCodeStyles.put(FormatCodeStyle.Zenburn, "Zenburn");
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
	private MenuManager mdMenuManager;
	/** Contributed tool bar */
	private ToolBarManager mdToolBarManager;

	/** Export as one HTML file */
	private Action exportAsHtmlAction;
	private final static String exportAsHtmlActionId = "org.emdepub.ui.editor.md.action.exportAsHtml";

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

		
		formatStyleMenuManager = new MenuManager("Format Style");
		for (FormatStyle formatStyle : FormatStyle.values()) {
			FormatStyleAction action = new FormatStyleAction(formatStyles.get(formatStyle), formatStyle);
			formatStyleMenuManager.add(action);
		};
		
		formatOptionMenuManager = new MenuManager("Format Options");
		
		for (Pref formatOption : Pref.values()) {
			if (MarkdownHtmlGeneratorPrefs.FormatOption.contains(formatOption)) {
				FormatOptionAction action = new FormatOptionAction(formatOptions.get(formatOption), formatOption);
				formatOptionMenuManager.add(action);
			}
		};
		
		formatCodeStyleMenuManager = new MenuManager("Format Code Style");
		for (FormatCodeStyle formatCodeStyle : FormatCodeStyle.values()) {
			FormatCodeStyleAction action = new FormatCodeStyleAction(formatCodeStyles.get(formatCodeStyle), formatCodeStyle);
			formatCodeStyleMenuManager.add(action);
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
		
		
//		mdMenuManager.add(formatStyleMenuManager);
		
//		Action showWordWrapAction = new Action("Show Text Word Wrap", IAction.AS_RADIO_BUTTON) {
//			public void run() {
//	//			MarkdownSemanticEPTextEditor markdownSemanticEPTextEditor = (MarkdownSemanticEPTextEditor) multiPageEditor.getEditor();
//	//			boolean wordWrapEnabled = markdownSemanticEPTextEditor.isWordWrapEnabled();
//	//			wordWrapEnabled = !wordWrapEnabled;
//	//			markdownSemanticEPTextEditor.setWordWrap(wordWrapEnabled);
//			}
//		};
//		showWordWrapAction.setChecked(false);
//		showWordWrapAction.setId("fqq");
//		showWordWrapAction.setText("Show Text Word Wrap");
//		showWordWrapAction.setToolTipText("Show Text Word Wrap");
//		showWordWrapAction.setImageDescriptor(R.getImageDescriptor("md-action-word-wrap"));
//	
//		showWordWrapAction.setChecked(false);
//		
//		//showWordWrapAction.AS_RADIO_BUTTON
//		
//		
//		formatStyleMenuManager.add(showWordWrapAction);
//	
//		showWordWrapAction = new Action("Show Text Word Wrap2", IAction.AS_RADIO_BUTTON) {
//			public void run() {
//		//		MarkdownSemanticEPTextEditor markdownSemanticEPTextEditor = (MarkdownSemanticEPTextEditor) multiPageEditor.getEditor();
//		//		boolean wordWrapEnabled = markdownSemanticEPTextEditor.isWordWrapEnabled();
//		//		wordWrapEnabled = !wordWrapEnabled;
//		//		markdownSemanticEPTextEditor.setWordWrap(wordWrapEnabled);
//			}
//		};
//		showWordWrapAction.setChecked(false);
//		showWordWrapAction.setId("fqq");
//		showWordWrapAction.setText("Show Text Word Wrap2");
//		showWordWrapAction.setToolTipText("Show Text Word Wrap");
//		showWordWrapAction.setImageDescriptor(R.getImageDescriptor("md-action-word-wrap"));
//		
//		showWordWrapAction.setChecked(true);
//		
//		formatStyleMenuManager.add(showWordWrapAction);
	
			
			
			
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

		mdMenuManager.remove(formatStyleMenuManager);
		mdToolBarManager.remove(formatStyleDropDownMenuActionId);

		mdMenuManager.remove(formatOptionMenuManager);
		mdToolBarManager.remove(formatOptionDropDownMenuActionId);

		mdMenuManager.remove(formatCodeStyleMenuManager);
		mdToolBarManager.remove(formatCodeStyleDropDownMenuActionId);

		/* Update */
		mdToolBarManager.update(true);

		if (markdownMultiPageEditor.getActivePage() == markdownMultiPageEditor.getBrowserViewerPageIndex()) {
			
			mdMenuManager.add(formatStyleMenuManager);
			mdToolBarManager.add(formatStyleDropDownMenuAction);
			
			mdMenuManager.add(formatOptionMenuManager);
			mdToolBarManager.add(formatOptionDropDownMenuAction);
			
			mdMenuManager.add(formatCodeStyleMenuManager);
			mdToolBarManager.add(formatCodeStyleDropDownMenuAction);
			
			/* Update */
			mdToolBarManager.update(true);
		}
		if (markdownMultiPageEditor.getActivePage() == markdownMultiPageEditor.getMarkdownTextEditorPageIndex()) {
			
//			mdMenuManager.remove(formatStyleMenuManager);
//			mdToolBarManager.remove(formatStyleDropDownMenuActionId);
//			/* Update */
//			mdToolBarManager.update(true);
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

	
}
