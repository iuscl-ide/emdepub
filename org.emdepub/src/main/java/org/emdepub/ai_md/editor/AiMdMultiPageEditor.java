package org.emdepub.ai_md.editor;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.IStorageDocumentProvider;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.emdepub.activator.Activator;
import org.emdepub.activator.App;
import org.emdepub.activator.L;
import org.emdepub.ai_md.editor.outline.AiMdOutlinePage;
import org.emdepub.ai_md.preferences.AiMdPreferencesAiProducer;
import org.emdepub.ai_md.preferences.AiMdPreferencesAiProducer.AiMdPreferencesAiProducerField;
import org.emdepub.ai_md.preferences.AiMdPreferencesHtmlGenerator;
import org.emdepub.ai_md.preferences.AiMdPreferencesHtmlGenerator.AiMdPreferencesHtmlGeneratorField;
import org.emdepub.ai_md.preferences.AiMdPreferencesHtmlGenerator.CodeStyles;
import org.emdepub.ai_md.preferences.AiMdPreferencesHtmlGenerator.CssStyles;
import org.emdepub.ai_md.preferences.AiMdPreferencesMdFormatter;
import org.emdepub.ai_md.preferences.AiMdPreferencesMdFormatter.AiMdPreferencesMdFormatterField;
import org.emdepub.ai_md.repo.AiMdRepo;
import org.emdepub.ai_md.ui.components.AiMdUiBooleanControl;
import org.emdepub.ai_md.ui.components.AiMdUiControl;
import org.emdepub.ai_md.ui.components.AiMdUiControls;
import org.emdepub.ai_md.ui.components.AiMdUiIntegerControl;
import org.emdepub.common.resources.CR;
import org.emdepub.common.ui.UI;
import org.emdepub.common.utils.CU;

import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level=AccessLevel.PRIVATE)
public class AiMdMultiPageEditor extends FormEditor {

	/* Reflection > */
	private static Field nestedEditors_Field;
	private static Method registerPage_Method;
	private static Method updatePageIndices_Method;
	
	static {
		try {
			nestedEditors_Field = MultiPageEditorPart.class.getDeclaredField("nestedEditors");
			nestedEditors_Field.setAccessible(true);
		} catch (NoSuchFieldException | SecurityException exception) {
			L.e("Reflection error for: nestedEditors_Field", exception);
		}
		
		try {
			registerPage_Method = FormEditor.class.getDeclaredMethod("registerPage", int.class, Object.class);
			registerPage_Method.setAccessible(true);
		} catch (NoSuchMethodException | SecurityException exception) {
			L.e("Reflection error for: registerPage_Method", exception);
		}

		try {
			updatePageIndices_Method = FormEditor.class.getDeclaredMethod("updatePageIndices", int.class);
			updatePageIndices_Method.setAccessible(true);
		} catch (NoSuchMethodException | SecurityException exception) {
			L.e("Reflection error for: updatePageIndices_Method", exception);
		}
	}
	/* < Reflection */

	private static final String htmlFixedContentWidthCss = CR.getTextResourceAsString("texts/html-fixed-content-width.css"); 
	private static final String htmlJustifiedParagraphsCss = CR.getTextResourceAsString("texts/html-justified-paragraphs.css"); 
	private static final String htmlCenterHeadersCss = CR.getTextResourceAsString("texts/html-center-headers.css"); 
	private static final String htmlCenterTablesCss = CR.getTextResourceAsString("texts/html-center-tables.css"); 
	private static final String htmlNoStripedTablesCss = CR.getTextResourceAsString("texts/html-no-striped-tables.css"); 
	
	@Getter
	AiMdPreferencesMdFormatter mdPreferences;
	String mdPreferencesFile;

	@Getter
	AiMdPreferencesHtmlGenerator htmlPreferences;
	String htmlPreferencesFile;

	String projectRootFolder;
	
	String aiPreferencesRootFolderFileName;

	AiMdPreferencesAiProducer aiPreferences;
	String aiPreferencesFile;
	
	@Getter @Setter
	boolean editorSplitView = true;
	
	Composite textEditorPageComposite;
	Browser aiMdViewerBrowser;
	
	@Getter
	int aiMdPreferencesEditorPageIndex;
	
	@Getter
	AiMdTextEditor aiMdTextEditor;
	@Getter
	int aiMdTextEditorPageIndex;

	@Getter
	AiMdOutlinePage aiMdOutlinePage;
	
	@Getter
	AiMdRepo aiMdRepo;
	
	
	String editorFile;

	final UI ui = App.getUi();
	
	final String aiMdHtmlViewerFolderNameWithPath = Activator.getPluginFolder() + CU.S + "viewers" + CU.S + "ai-md";
	final String aiMdHtmlViewerUrl = "file://" + aiMdHtmlViewerFolderNameWithPath + CU.S + "index-ai-md.html";
	
	/** Creates the viewer page of the forms editor */
//	@SneakyThrows(PartInitException.class)
	private void createAiMdPreferencesEditorPage() {
		
		/* Page */
		final Composite containerComposite = getContainer();
		final FormToolkit formsToolkit = new FormToolkit(containerComposite.getDisplay());
		
		/* Form */
		Form containerForm = formsToolkit.createForm(containerComposite);
		containerForm.setText("Preferences for AI MD file: " + this.getTitle());
		containerForm.setImage(CR.getImage("ai-md-multi-page-editor"));
		formsToolkit.decorateFormHeading(containerForm);
		
		/* Form composite */
		Composite containerFormComposite = containerForm.getBody();
		GridLayout containerFormCompositeGridLayout = ui.createGridLayout_Margins_ColumnsSpacing(UI.sep8, 3, UI.sep8);
		containerFormCompositeGridLayout.makeColumnsEqualWidth = true;
		containerFormComposite.setLayout(containerFormCompositeGridLayout);

		/* AI section */
		createAiPreferencesSection(formsToolkit, containerFormComposite); 

		/* MD section */
		createMdPreferencesSection(formsToolkit, containerFormComposite); 
		
		/* HTML section */
		createHtmlPreferencesSection(formsToolkit, containerFormComposite); 

		aiMdPreferencesEditorPageIndex = 1;
		
		addPage(aiMdPreferencesEditorPageIndex, containerForm);
		setPageText(aiMdPreferencesEditorPageIndex, "AI MD Preferences");

	}
	
	private void createAiPreferencesSection(FormToolkit formsToolkit, Composite containerFormComposite) {
		
		Section aiSection = formsToolkit.createSection(containerFormComposite, Section.DESCRIPTION | Section.TITLE_BAR);
		aiSection.setLayoutData(ui.createGridData_FillBoth());
		aiSection.setLayout(ui.createGridLayout());
		aiSection.setText("AI Preferences");
		aiSection.setDescription("AI producer preferences");

		
		/* MD section composite */
		Composite aiSectionComposite = formsToolkit.createComposite(aiSection, SWT.NULL);
		aiSectionComposite.setLayoutData(ui.createGridData_FillBoth());
		aiSectionComposite.setLayout(ui.createGridLayout_MarginTop_VerticalSpacing(UI.sep8, UI.sep8));
		aiSection.setClient(aiSectionComposite);

		List<AiMdUiControl> aiControls = new ArrayList<>(); 

		aiControls.add(AiMdUiControls.addReadOnlyTextControl(ui, aiSectionComposite, 0,
				"Default Preferences File", UI.mediumlarge140,  aiPreferences, AiMdPreferencesAiProducerField.aiPreferencesRootFolderFileName.name(), 4));

		aiControls.add(AiMdUiControls.addFileControl(ui, aiSectionComposite, 0,
			"Target Markdown Folder", UI.mediumlarge140,  aiPreferences, AiMdPreferencesAiProducerField.mdTargetFolder.name(), true));

		AiMdUiControls.loadControls(aiControls);
		
		ToolBarManager aiSectionToolBarManager = new ToolBarManager(SWT.FLAT);
		ToolBar aiSectionToolBar = aiSectionToolBarManager.createControl(aiSection);

		/* Run */
		aiSectionToolBarManager.add(UI.ActionFactory.create("org.eaiepub.ai_ai.editor.aiSection.reset",
			"", "Reset to Default Values", CR.getImageDescriptor("initializ_parameter_context"), () -> {
				
				aiPreferences.reset();
				AiMdUiControls.loadControls(aiControls);
			}));

		aiSectionToolBarManager.add(UI.ActionFactory.create("org.eaiepub.ai_ai.editor.aiSection.reload",
			"", "Reload from Saved Values", CR.getImageDescriptor("reload_nav_16"), () -> {

				aiPreferences.load(aiPreferencesFile);
				AiMdUiControls.loadControls(aiControls);
			}));

		aiSectionToolBarManager.add(UI.ActionFactory.create("org.eaiepub.ai_ai.editor.aiSection.save",
			"", "Save Values", CR.getImageDescriptor("save"), () -> {
				
				aiPreferences.save(aiPreferencesFile);
			}));
		
		aiSectionToolBarManager.update(true);
		aiSection.setTextClient(aiSectionToolBar);
	}

	private void createMdPreferencesSection(FormToolkit formsToolkit, Composite containerFormComposite) {
		
		Section mdSection = formsToolkit.createSection(containerFormComposite, Section.DESCRIPTION | Section.TITLE_BAR);
		mdSection.setLayoutData(ui.createGridData_FillBoth());
		mdSection.setLayout(ui.createGridLayout());
		mdSection.setText("MD Preferences");
		mdSection.setDescription("Markdown formatter preferences");

		
		/* MD section composite */
		Composite mdSectionComposite = formsToolkit.createComposite(mdSection, SWT.NULL);
		mdSectionComposite.setLayoutData(ui.createGridData_FillBoth());
		mdSectionComposite.setLayout(ui.createGridLayout_MarginTop_VerticalSpacing(UI.sep8, UI.sep8));
		mdSection.setClient(mdSectionComposite);

		List<AiMdUiControl> mdControls = new ArrayList<>(); 
		
		final AiMdUiBooleanControl formatRightMarginWrapBooleanControl = AiMdUiControls.addBooleanControl(ui, mdSectionComposite, 1,
			"Format right margin wrap", mdPreferences, AiMdPreferencesMdFormatterField.formatRightMarginWrap.name());
		mdControls.add(formatRightMarginWrapBooleanControl);
		
		final AiMdUiIntegerControl formatRightMarginColumns = AiMdUiControls.addIntegerControl(ui, mdSectionComposite, 2,
			"Format right margin columns", UI.large180, UI.small48, mdPreferences, AiMdPreferencesMdFormatterField.formatRightMarginColumns.name(), null,
			40, 100);
		mdControls.add(formatRightMarginColumns);

		formatRightMarginWrapBooleanControl.addEvent("onChange", () -> {
			formatRightMarginColumns.enable(formatRightMarginWrapBooleanControl.getBoolean());
		});
		
		mdControls.add(AiMdUiControls.addBooleanControl(ui, mdSectionComposite, 1,
			"Format collapse line whitespace", mdPreferences, AiMdPreferencesMdFormatterField.formatCollapseLineWhitespace.name()));

		mdControls.add(AiMdUiControls.addBooleanControl(ui, mdSectionComposite, 1,
			"Format collapse empty lines", mdPreferences, AiMdPreferencesMdFormatterField.formatCollapseEmptyLines.name()));

		mdControls.add(AiMdUiControls.addBooleanControl(ui, mdSectionComposite, 1,
			"Format collapse trailing empty lines", mdPreferences, AiMdPreferencesMdFormatterField.formatCollapseTrailingEmptyLines.name()));

		AiMdUiControls.loadControls(mdControls);
		
		ToolBarManager mdSectionToolBarManager = new ToolBarManager(SWT.FLAT);
		ToolBar mdSectionToolBar = mdSectionToolBarManager.createControl(mdSection);

		/* Run */
		mdSectionToolBarManager.add(UI.ActionFactory.create("org.emdepub.ai_md.editor.mdSection.reset",
			"", "Reset to Default Values", CR.getImageDescriptor("initializ_parameter_context"), () -> {
				
				mdPreferences.reset();
				AiMdUiControls.loadControls(mdControls);
			}));

		mdSectionToolBarManager.add(UI.ActionFactory.create("org.emdepub.ai_md.editor.mdSection.reload",
			"", "Reload from Saved Values", CR.getImageDescriptor("reload_nav_16"), () -> {

				mdPreferences.load(mdPreferencesFile);
				AiMdUiControls.loadControls(mdControls);
			}));

		mdSectionToolBarManager.add(UI.ActionFactory.create("org.emdepub.ai_md.editor.mdSection.save",
			"", "Save Values", CR.getImageDescriptor("save"), () -> {
				
				mdPreferences.save(mdPreferencesFile);
			}));
		
		mdSectionToolBarManager.update(true);
		mdSection.setTextClient(mdSectionToolBar);
	}

	private void createHtmlPreferencesSection(FormToolkit formsToolkit, Composite containerFormComposite) {
		
		Section htmlSection = formsToolkit.createSection(containerFormComposite, Section.DESCRIPTION | Section.TITLE_BAR);
		htmlSection.setLayoutData(ui.createGridData_FillBoth());
		htmlSection.setLayout(ui.createGridLayout());
		htmlSection.setText("HTML Preferences");
		htmlSection.setDescription("HTML generator preferences");

		
		/* HTML section composite */
		Composite htmlSectionComposite = formsToolkit.createComposite(htmlSection, SWT.NULL);
		htmlSectionComposite.setLayoutData(ui.createGridData_FillBoth());
		htmlSectionComposite.setLayout(ui.createGridLayout_MarginTop_VerticalSpacing(UI.sep8, UI.sep8));
		htmlSection.setClient(htmlSectionComposite);

		final List<AiMdUiControl> htmlControls = new ArrayList<>();
		
		htmlControls.add(AiMdUiControls.addComboControl(ui, htmlSectionComposite, 1, "CSS style", UI.large180, UI.large180, htmlPreferences,
				AiMdPreferencesHtmlGeneratorField.cssStyle.name(), AiMdPreferencesHtmlGenerator.cssStyleNames));

		htmlControls.add(AiMdUiControls.addComboControl(ui, htmlSectionComposite, 1, "Code style", UI.large180, UI.large180, htmlPreferences,
				AiMdPreferencesHtmlGeneratorField.codeStyle.name(), AiMdPreferencesHtmlGenerator.codeStyleNames));

		htmlControls.add(AiMdUiControls.addBooleanControl(ui, htmlSectionComposite, 1,
				"Fixed content width", htmlPreferences, AiMdPreferencesHtmlGeneratorField.fixedContentWidth.name()));

		htmlControls.add(AiMdUiControls.addBooleanControl(ui, htmlSectionComposite, 1,
				"Justified paragraphs", htmlPreferences, AiMdPreferencesHtmlGeneratorField.justifiedParagraphs.name()));

		htmlControls.add(AiMdUiControls.addBooleanControl(ui, htmlSectionComposite, 1,
				"Center headers", htmlPreferences, AiMdPreferencesHtmlGeneratorField.centerHeaders.name()));

		htmlControls.add(AiMdUiControls.addBooleanControl(ui, htmlSectionComposite, 1,
				"Center tables", htmlPreferences, AiMdPreferencesHtmlGeneratorField.centerTables.name()));

		htmlControls.add(AiMdUiControls.addIntegerControl(ui, htmlSectionComposite, 1,
				"Percent width tables", UI.large180, UI.small48, htmlPreferences, AiMdPreferencesHtmlGeneratorField.percentWidthTables.name(), null,
				0, 100));

		htmlControls.add(AiMdUiControls.addBooleanControl(ui, htmlSectionComposite, 1,
				"Not striped tables", htmlPreferences, AiMdPreferencesHtmlGeneratorField.noStripedTables.name()));

		AiMdUiControls.loadControls(htmlControls);
		
		ToolBarManager htmlSectionToolBarManager = new ToolBarManager(SWT.FLAT);
		ToolBar htmlSectionToolBar = htmlSectionToolBarManager.createControl(htmlSection);

		/* Run */
		htmlSectionToolBarManager.add(UI.ActionFactory.create("org.emdepub.ai_md.editor.htmlSection.reset",
			"", "Reset to Default Values", CR.getImageDescriptor("initializ_parameter_context"), () -> {
				
				htmlPreferences.reset();
				AiMdUiControls.loadControls(htmlControls);
			}));

		htmlSectionToolBarManager.add(UI.ActionFactory.create("org.emdepub.ai_md.editor.htmlSection.reload",
			"", "Reload from Saved Values", CR.getImageDescriptor("reload_nav_16"), () -> {

				htmlPreferences.load(htmlPreferencesFile);
				AiMdUiControls.loadControls(htmlControls);
			}));

		htmlSectionToolBarManager.add(UI.ActionFactory.create("org.emdepub.ai_md.editor.htmlSection.save",
			"", "Save Values", CR.getImageDescriptor("save"), () -> {
				
				htmlPreferences.save(htmlPreferencesFile);
			}));
		
		htmlSectionToolBarManager.update(true);
		htmlSection.setTextClient(htmlSectionToolBar);
	}
	
	/** Creates EPubProject text editor page of the forms editor */
	@SneakyThrows({PartInitException.class, IllegalAccessException.class, InvocationTargetException.class})
	private void createAiMdTextEditorPage() {

		aiMdTextEditorPageIndex = 0;

		aiMdTextEditor = new AiMdTextEditor();

		
		String aiMdFileName = ((FileEditorInput) getEditorInput()).getPath().makeAbsolute().toOSString();
		aiMdRepo = AiMdRepo.loadOrCreateRepo(aiMdFileName);
		
		/* addPage > */

		final IEditorSite editorSite = this.createSite(aiMdTextEditor);
		aiMdTextEditor.init(editorSite, getEditorInput());

		aiMdTextEditor.addPropertyListener((source, propertyId) -> this.handlePropertyChange(propertyId));

		@SuppressWarnings("unchecked")
		ArrayList<IEditorPart> nestedEditors = (ArrayList<IEditorPart>) nestedEditors_Field.get(this);
		nestedEditors.add(aiMdTextEditor);
//      registerPage(aiMdTextEditorPageIndex, aiMdTextEditor);
//      updatePageIndices(aiMdTextEditorPageIndex + 1);
		registerPage_Method.invoke(this, aiMdTextEditorPageIndex, aiMdTextEditor);
		updatePageIndices_Method.invoke(this, aiMdTextEditorPageIndex + 1);
		
		textEditorPageComposite = ui.createComposite(this.getContainer());
//		pageComposite.setLayoutData(ui.createGridData_FillBoth());
		GridLayout splitViewGridLayout = ui.createGridLayout_ColumnsSpacing(2, UI.sep8);
		splitViewGridLayout.makeColumnsEqualWidth = true;
		textEditorPageComposite.setLayout(splitViewGridLayout);
		
		final Item item = (Item) this.createItem(aiMdTextEditorPageIndex, (Control) textEditorPageComposite);
		item.setData((Object) aiMdTextEditor);
		
		final Composite textEditorComposite = ui.createComposite(textEditorPageComposite);
		textEditorComposite.setLayoutData(ui.createGridData_FillBoth());
		textEditorComposite.setLayout((Layout) new FillLayout());
		aiMdTextEditor.createPartControl(textEditorComposite);

		aiMdViewerBrowser = new Browser(textEditorPageComposite, SWT.NONE);
		aiMdViewerBrowser.setLayoutData(ui.createGridData_FillBoth());

		aiMdViewerBrowser.setJavascriptEnabled(true);
		aiMdViewerBrowser.setText("");

		/** Status bar */
		aiMdViewerBrowser.addStatusTextListener(new StatusTextListener() {
			@Override
			public void changed(StatusTextEvent statusTextEvent) {
				
				AiMdMultiPageEditorContributor.getStatusLineLinkField().setText(statusTextEvent.text);
			}
		});

		/** Progress */
		aiMdViewerBrowser.addProgressListener(new ProgressListener() {
			@Override
            public void changed(ProgressEvent progressEvent) { }
			@Override
            public void completed(ProgressEvent progressEvent) {
            
            	String cssStyleClass = AiMdPreferencesHtmlGenerator.cssStyleClasses.get(CssStyles.valueOf(htmlPreferences.getCssStyle())); 
            	String codeStyleClass = AiMdPreferencesHtmlGenerator.codeStyleClasses.get(CodeStyles.valueOf(htmlPreferences.getCodeStyle()));
            	
            	aiMdViewerBrowser.execute("window.markdownSettings = {}; " +
        			"window.markdownSettings.markdownText = '" + getBase64AiMdText() + "'; " +
        			"window.markdownSettings.formatOptionsCss = '" + getBase64HtmlOptionsCss() + "'; " +
        			(CU.isEmpty(cssStyleClass) ? "" : "window.markdownSettings.cssName = '" + cssStyleClass + "'; ") +
        			(CU.isEmpty(codeStyleClass) ? "" : "window.markdownSettings.cssHighlightName = '" + "highlight/" +  codeStyleClass + "'; ") +
        			"window.markdownSettings.localBaseHref = '" + findLocalBaseHref() + "'; " +
        			"reload();");
            }
        });

		
		
//		aiMdTextEditorPageIndex = addPage(aiMdTextEditor, getEditorInput());
		/* < addPage */

		IDocumentProvider documentProvider = aiMdTextEditor.getDocumentProvider();
		if (documentProvider instanceof IStorageDocumentProvider) {
			IStorageDocumentProvider storageDocumentProvider = (IStorageDocumentProvider) documentProvider;
			/* Make sure the file is seen as UTF-8. */
			storageDocumentProvider.setEncoding(aiMdTextEditor.getEditorInput(), "utf-8");
			aiMdTextEditor.doRevertToSaved();
		}

		this.setPartName(aiMdTextEditor.getTitle());

		setPageText(aiMdTextEditorPageIndex, "AI MD Editor");

//		document = (Document) aiMdTextEditor.getDocumentProvider().getDocument(aiMdTextEditor.getEditorInput());

//		aiMdTextEditor.initReferences();

		aiMdOutlinePage = new AiMdOutlinePage(this);
	}
	
	/** Create pages */
	@Override
	@SneakyThrows(IOException.class)
	protected void addPages() {

		editorFile = ((FileEditorInput) getEditorInput()).getPath().makeAbsolute().toOSString();
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot workspaceRoot = workspace.getRoot();
		IProject[] projects = workspaceRoot.getProjects();
		for (IProject project : projects) {
			String projectFolder = project.getLocation().toOSString();
			if (Paths.get(editorFile).startsWith(Paths.get(projectFolder))) {
				projectRootFolder = projectFolder;	
			}
		}		
		
		mdPreferencesFile = CU.findFileFolder(editorFile) + CU.S + CU.findFileNameWithoutExtension(editorFile) + ".ai-md.md-preferences.yaml";
		mdPreferences = AiMdPreferencesMdFormatter.create();
		mdPreferences.load(mdPreferencesFile);

		htmlPreferencesFile = CU.findFileFolder(editorFile) + CU.S + CU.findFileNameWithoutExtension(editorFile) + ".ai-md.html-preferences.yaml";
		htmlPreferences = AiMdPreferencesHtmlGenerator.create();
		htmlPreferences.load(htmlPreferencesFile);


		Path fileEditorFilePath = Paths.get(editorFile);
		Path fileEditorFolderPath = fileEditorFilePath.getParent().getParent();	
		
		Path rootAiPreferencesPath = null;
		while (rootAiPreferencesPath == null) {

			@Cleanup DirectoryStream<Path> directoryStream = Files.newDirectoryStream(fileEditorFolderPath);
			for (Path path : directoryStream) {
	            if (!Files.isDirectory(path)) {
					if (path.toAbsolutePath().toString().endsWith(".ai-preferences.yaml")) {
						rootAiPreferencesPath = path;
						break;
					}
	            }
	        }
			if (rootAiPreferencesPath == null) {
				if (fileEditorFolderPath.toString().equals(projectRootFolder)) {
					break;
				}
				fileEditorFolderPath = fileEditorFolderPath.getParent();
			}
		}
		if (rootAiPreferencesPath == null) {
			L.e("No project", new Throwable("No .ai-preferences.yaml project"));
		}

		aiPreferencesRootFolderFileName = rootAiPreferencesPath.toString();

		aiPreferencesFile = CU.findFileFolder(editorFile) + CU.S + CU.findFileNameWithoutExtension(editorFile) + ".ai-md.ai-preferences.yaml";
		aiPreferences = AiMdPreferencesAiProducer.create(aiPreferencesRootFolderFileName);
		aiPreferences.load(aiPreferencesFile);
		
		createAiMdTextEditorPage();

		createAiMdPreferencesEditorPage();
	}

	/** Saves the forms editor's document */
	@Override
	public void doSave(IProgressMonitor progressMonitor) {
		
		aiMdTextEditor.doSave(progressMonitor);
	}

	/** Saves the forms editor's document as another file */
	@Override
	public void doSaveAs() {

		aiMdTextEditor.doSaveAs();
		this.setPartName(aiMdTextEditor.getTitle());
		setInput(aiMdTextEditor.getEditorInput());
	}

	/** Select and reveal GptProject text editor page */
	public void activateAiMdTextEditorPage() {
		if (this.getActivePage() != aiMdTextEditorPageIndex) {
			this.setActiveEditor(getEditor(aiMdTextEditorPageIndex));
		}
	}

	/** Saves the forms editor's document */
	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	/** Put in browser what is in editor */
	public void refresh() {
		
		aiMdViewerBrowser.setUrl(aiMdHtmlViewerUrl);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAdapter(Class<T> adapter) {
		
		if (IContentOutlinePage.class.equals(adapter)) {
			return (T) aiMdOutlinePage;
		}
		return super.getAdapter(adapter);
	}

	public void changeSplitView(boolean splitView) {
		
		editorSplitView = splitView;

		GridLayout textEditorPageCompositeGridLayout = (GridLayout) textEditorPageComposite.getLayout();

		if (editorSplitView) {
			textEditorPageCompositeGridLayout.horizontalSpacing = UI.sep8;
			textEditorPageCompositeGridLayout.makeColumnsEqualWidth = true;

			((GridData) aiMdViewerBrowser.getLayoutData()).exclude = false;
			aiMdViewerBrowser.setVisible(true);
		}
		else {
			textEditorPageCompositeGridLayout.horizontalSpacing = UI.sep0;
			textEditorPageCompositeGridLayout.makeColumnsEqualWidth = false;

			((GridData) aiMdViewerBrowser.getLayoutData()).exclude = true;
			aiMdViewerBrowser.setVisible(false);
		}
		textEditorPageComposite.layout();
	}
	
	/** Get the Markdown as Base64 text */
	public String getBase64AiMdText() {
		
		//String aiMdText = aiMdTextEditor.getDocumentProvider().getDocument(aiMdTextEditor.getEditorInput()).get();
		
		String mdText = CU.loadFileInString(findTargetFileName());
		
		return new String(Base64.getEncoder().encode(mdText.getBytes(Charset.forName("UTF-8"))));
	}

	/** Get the options as Base64 text */
	public String getBase64HtmlOptionsCss() {
		
		return new String(Base64.getEncoder().encode(getHtmlOptionsCss().getBytes()));
	}

	/** LocalBaseHref */
	private String findLocalBaseHref() {
		
		IEditorInput editorInput = aiMdTextEditor.getEditorInput();
		
		if (editorInput instanceof FileEditorInput) {
			IFile iFile = (IFile) editorInput.getAdapter(IFile.class);
			File file = iFile.getLocation().toFile();
			String rootPath = file.getPath().substring(0, file.getPath().length() - file.getName().length()).replace("\\", "/");
			
			return "file:///" + rootPath;
		}
		else if (editorInput instanceof FileStoreEditorInput) {
			
			String rootPath = ((FileStoreEditorInput) editorInput).getURI().toString();
			rootPath = rootPath.substring(0, rootPath.length() - editorInput.getName().length()).replace("file:/", "file:///");
			return rootPath;
		}
		
		return editorInput.getName();
	}
	
	public String findTargetFileName() {
		
		String aiMdFileName = ((FileEditorInput) this.getEditorInput()).getPath().makeAbsolute().toOSString();
		String fileTargetFileName = CU.findFileNameWithoutExtension(aiMdFileName) + ".md";
		Path targetFolderPath = Paths.get(aiPreferences.getMdTargetFolder());
		if (!targetFolderPath.isAbsolute()) {
			targetFolderPath = Paths.get(CU.findFileFolder(aiPreferences.getAiPreferencesRootFolderFileName()), targetFolderPath.toString()).toAbsolutePath();
		}
		return Paths.get(targetFolderPath.toAbsolutePath().toString(), fileTargetFileName).toAbsolutePath().toString();
	}

	public void viewHtmlFromAiMd() {
		
		refresh();
	}
	
	/** Get the options CSSs */
	public String getHtmlOptionsCss() {
		
		String htmlOptionsCss = "";
		
		if (htmlPreferences.getFixedContentWidth()) {
			htmlOptionsCss = htmlOptionsCss + CU.E + htmlFixedContentWidthCss;
		}

		if (htmlPreferences.getJustifiedParagraphs()) {
			htmlOptionsCss = htmlOptionsCss + CU.E + htmlJustifiedParagraphsCss;
		}

		if (htmlPreferences.getCenterHeaders()) {
			htmlOptionsCss = htmlOptionsCss + CU.E + htmlCenterHeadersCss;
		}

		if (htmlPreferences.getCenterTables()) {
			htmlOptionsCss = htmlOptionsCss + CU.E + htmlCenterTablesCss;
		}

		if (htmlPreferences.getNoStripedTables()) {
			htmlOptionsCss = htmlOptionsCss + CU.E + htmlNoStripedTablesCss;
		}
		
		String htmlCodeStylePre = AiMdPreferencesHtmlGenerator.codeStylePreValues.get(CodeStyles.valueOf(htmlPreferences.getCodeStyle()));
		if (!CU.isEmpty(htmlCodeStylePre)) {
			String htmlCodeStylesCssBackground = "\t\tarticle.markdown-body pre {" + CU.E + "\t\t\t" + htmlCodeStylePre + CU.E + "\t\t}";
			htmlOptionsCss = htmlOptionsCss + CU.E + htmlCodeStylesCssBackground;
		}

		htmlOptionsCss = htmlOptionsCss + CU.E + "\t\ttable {" + CU.E +
				"\t\t\twidth: " + htmlPreferences.getPercentWidthTables() + "% !important;" + CU.E +
				"\t\t\tdisplay: table !important;" + CU.E +
				"\t\t}";
				
		return htmlOptionsCss;
	}
}
