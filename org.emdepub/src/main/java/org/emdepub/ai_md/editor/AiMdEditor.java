package org.emdepub.ai_md.editor;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.text.Document;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.IStorageDocumentProvider;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.emdepub.activator.L;
import org.emdepub.ai_md.editor.outline.AiMdOutlinePage;
import org.emdepub.ai_md.model.AiMdProject;
import org.emdepub.ai_md.repo.AiMdRepo;
import org.emdepub.common.resources.CR;
import org.emdepub.common.ui.UI;
import org.emdepub.markdown.editor.preferences.MarkdownPreferences;

import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level=AccessLevel.PRIVATE)
public class AiMdEditor extends FormEditor {

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
	
	@Getter @Setter
	boolean editorSplitView = true;
	
	Composite textEditorPageComposite;
	Browser aiMdViewerBrowser;
	
	@Getter
	AiMdProject aiMdProject;
	
	Document document;
	
	@Getter
	int aiMdProjectEditorPageIndex;
	
	@Getter
	AiMdTextEditor aiMdTextEditor;
	@Getter
	int aiMdTextEditorPageIndex;

	@Getter
	AiMdOutlinePage aiMdOutlinePage;

	
	MarkdownPreferences markdownPreferences;
	
	@Getter
	AiMdRepo aiMdRepo;
	
	UI ui = new UI(false, Display.getDefault());
	
	/** Creates the viewer page of the forms editor */
//	@SneakyThrows(PartInitException.class)
	private void createAiMdProjectEditorPage() {
		
		markdownPreferences = new MarkdownPreferences();
		
//		final int marginSpacing = 7;
//		final int verticalSpacing = 7;
//		final int horizontalSpacing = 7;
		
//		MarkdownEditor markdownEditor = new MarkdownEditor();
//		
//		FormPage formPage = new FormPage("id", "title");
//		formPage.initialize(markdownEditor);
//		
//		aiMdProjectEditorPageIndex = addPage(formPage, getEditorInput());
		
		
		
		UI ui = new UI(true, Display.getCurrent());
//		L.i("createGptProjectEditorPage");
		/* Page */
		Composite opfPageComposite = getContainer();
		FormToolkit formsToolkit = new FormToolkit(opfPageComposite.getDisplay());
		/* Form */
		Form opfForm = formsToolkit.createForm(opfPageComposite);
//		String fileName = CU.getFileName(projectFileNameWithPath);   //fileNameWithPath.substring(CU.getFileFolderName(fileNameWithPath).length() + 1);
		opfForm.setText("\"" + "fileName" + "\" " + this.getTitle());
		opfForm.setImage(CR.getImage("project"));
//		opfForm.setToolBarVerticalAlignment(SWT.BOTTOM);
		formsToolkit.decorateFormHeading(opfForm);

		/* Form Composite */
		Composite opfFormComposite = opfForm.getBody();
		//GridLayout opfFormCompositeGridLayout = ui.createMarginsVerticalSpacingGridLayout(marginSpacing, verticalSpacing);
		GridLayout opfFormCompositeGridLayout = ui.createGridLayout_Margins_ColumnsSpacing(UI.sep8, 2, UI.sep8);
		opfFormComposite.setLayout(opfFormCompositeGridLayout);
		
		/* General Section */
		Section gptSourceSection = formsToolkit.createSection(opfFormComposite, Section.DESCRIPTION | Section.TITLE_BAR);
		gptSourceSection.setLayoutData(ui.createGridData_FillBoth());
		gptSourceSection.setLayout(ui.createGridLayout());
		gptSourceSection.setText("General");
		gptSourceSection.setDescription("ePub project settings");

		ToolBarManager generalToolBarManager = new ToolBarManager(SWT.FLAT);
		ToolBar generalToolBar = generalToolBarManager.createControl(gptSourceSection);
		
		/* Run */
		generalToolBarManager.add(UI.ActionFactory.create("org.emdepub.ai_md.editor.generalRunAction",
			"Run", "Generate ePub book", CR.getImageDescriptor("run"), () -> {
//				this.activateSite();
//				AIMdEngine.generate(this);
//				generateBook();
			}));

		/* Split */
		generalToolBarManager.add(UI.ActionFactory.create("org.emdepub.ai_md.editor.generalSplitAction",
			"Split", "Split a file in separate files (for chapters)", CR.getImageDescriptor("XML_file"), () -> {
//				splitSourceFile();
			}));

		generalToolBarManager.update(true);
		gptSourceSection.setTextClient(generalToolBar);

		
		/* General Section Composite */
		Composite gptSourceSectionComposite = formsToolkit.createComposite(gptSourceSection, SWT.NULL);
		gptSourceSectionComposite.setLayoutData(ui.createGridData_FillBoth());
		gptSourceSectionComposite.setLayout(new FillLayout());
		gptSourceSectionComposite.setBackground(ui.getDisplay().getSystemColor(SWT.COLOR_YELLOW));
		gptSourceSection.setClient(gptSourceSectionComposite);
		
		
//		AiMdTextEditor markdownTextEditor = new AiMdTextEditor();
//		
//		final IEditorSite site = this.createSite(markdownTextEditor);
//		//final IEditorInput input = new FileEditorInput(file);
//		markdownTextEditor.init(site, getEditorInput());
//		
//		markdownTextEditor.createPartControl(gptSourceSectionComposite);
		
		
		
		
		
////		rootFolderFileControl = addFileControl(ui, opfGeneralSectionComposite, "Root folder", labelWidth, null, true, "rootFolderNameWithFullPath");
////		targetFolderFileControl = addFileControl(ui, opfGeneralSectionComposite, "Target folder", labelWidth, null, true, "targetFolderNameWithFullPath");
////		opfFileTextControl = addTextControl(ui, opfGeneralSectionComposite, "OPF file name with relative path", null, 1, "opfFileNameWithRelativePath");
		
		/* Metadata Section */
//		Section gptGeneratedSection = formsToolkit.createSection(opfFormComposite, Section.EXPANDED | Section.TWISTIE | Section.DESCRIPTION | Section.TITLE_BAR);
//		gptGeneratedSection.setLayoutData(ui.createGridData_TopAlignedFillHorizontal());
//		gptGeneratedSection.setLayout(ui.createGridLayout());
//		gptGeneratedSection.setText("Metadata");
//		gptGeneratedSection.setDescription("ePub book properties");

//		ToolBarManager metadataToolBarManager = new ToolBarManager(SWT.FLAT);
//		ToolBar metadataToolBar = metadataToolBarManager.createControl(gptGeneratedSection);
//		
//////		/* Generate identifier UUID */
//////		metadataToolBarManager.add(UI.ActionFactory.create("org.emdepub.epub_project.editor.generateIdentifierUuidAction",
//////			"Generate identifier UUID", "Generate identifier UUID", CR.getImageDescriptor("asterisk"), () -> {
//////				ePubProject.metadata_identifier = "urn:uuid:" + UUID.randomUUID().toString();
//////				metadataIdentifierTextControl.reload();
//////			}));
//		
//		metadataToolBarManager.update(true);
//		gptGeneratedSection.setTextClient(metadataToolBar);


		aiMdProjectEditorPageIndex = 1;
		
		addPage(aiMdProjectEditorPageIndex, opfForm);
		setPageText(aiMdProjectEditorPageIndex, "AI Markdown Project / Repository");

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

		setPageText(aiMdTextEditorPageIndex, "AI Markdown Editor");

		document = (Document) aiMdTextEditor.getDocumentProvider().getDocument(aiMdTextEditor.getEditorInput());

//		aiMdTextEditor.initReferences();

		aiMdOutlinePage = new AiMdOutlinePage(this);
	}
	
	/** Create pages */
	@Override
	@SneakyThrows(IOException.class)
	protected void addPages() {

		FileEditorInput fileEditorInput = (FileEditorInput) getEditorInput();
		Path fileEditorFilePath = Paths.get(fileEditorInput.getPath().makeAbsolute().toOSString());
		Path fileEditorFolderPath = fileEditorFilePath.getParent();	
		
		Path projectFilePath = null;
		while (projectFilePath == null) {

			@Cleanup DirectoryStream<Path> directoryStream = Files.newDirectoryStream(fileEditorFolderPath);
			for (Path path : directoryStream) {
	            if (!Files.isDirectory(path)) {
					if (path.toAbsolutePath().toString().endsWith(".ai.yaml")) {
						projectFilePath = path;
						break;
					}
	            }
	        }
			if (projectFilePath == null) {
				fileEditorFolderPath = fileEditorFolderPath.getParent();
				if (fileEditorFolderPath == null) {
					break;
				}
			}
		}
		if (projectFilePath == null) {
			L.e("No project", new Throwable("No .ai.yaml project"));
		}
		
		aiMdProject = new AiMdProject(projectFilePath.getParent(), projectFilePath);
		
		createAiMdTextEditorPage();

//		createAiMdProjectEditorPage();
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
		
		//aiMdViewerBrowser.setUrl(markdownViewerUrl);
	}

	/** Save from here */
	public void saveMarkdownPreferences() {
		
		//markdownPreferences.saveProperties(markdownPreferencesPropertiesFileNameWithPath);
	}

	public MarkdownPreferences getPreferences() {
		return markdownPreferences;
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
}
