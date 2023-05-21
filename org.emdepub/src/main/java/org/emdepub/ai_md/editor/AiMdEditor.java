package org.emdepub.ai_md.editor;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.text.Document;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.IStorageDocumentProvider;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.emdepub.activator.L;
import org.emdepub.ai_md.editor.outline.AiMdOutlinePage;
import org.emdepub.ai_md.model.AiMdProject;
import org.emdepub.common.resources.CR;
import org.emdepub.common.ui.UI;
import org.emdepub.markdown.editor.preferences.MarkdownPreferences;

import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level=AccessLevel.PRIVATE)
public class AiMdEditor extends FormEditor {

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
		GridLayout opfFormCompositeGridLayout = ui.createMarginsColumnsSpacingGridLayout(UI.sep, 2, UI.sep);
		opfFormComposite.setLayout(opfFormCompositeGridLayout);
		
		/* General Section */
		Section gptSourceSection = formsToolkit.createSection(opfFormComposite, Section.DESCRIPTION | Section.TITLE_BAR);
		gptSourceSection.setLayoutData(ui.createFillBothGridData());
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
		gptSourceSectionComposite.setLayoutData(ui.createFillBothGridData());
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
		Section gptGeneratedSection = formsToolkit.createSection(opfFormComposite, Section.EXPANDED | Section.TWISTIE | Section.DESCRIPTION | Section.TITLE_BAR);
		gptGeneratedSection.setLayoutData(ui.createTopAlignedFillHorizontalGridData());
		gptGeneratedSection.setLayout(ui.createGridLayout());
		gptGeneratedSection.setText("Metadata");
		gptGeneratedSection.setDescription("ePub book properties");

		ToolBarManager metadataToolBarManager = new ToolBarManager(SWT.FLAT);
		ToolBar metadataToolBar = metadataToolBarManager.createControl(gptGeneratedSection);
		
////		/* Generate identifier UUID */
////		metadataToolBarManager.add(UI.ActionFactory.create("org.emdepub.epub_project.editor.generateIdentifierUuidAction",
////			"Generate identifier UUID", "Generate identifier UUID", CR.getImageDescriptor("asterisk"), () -> {
////				ePubProject.metadata_identifier = "urn:uuid:" + UUID.randomUUID().toString();
////				metadataIdentifierTextControl.reload();
////			}));
		
		metadataToolBarManager.update(true);
		gptGeneratedSection.setTextClient(metadataToolBar);


		
		
		aiMdProjectEditorPageIndex = addPage(opfForm);
		setPageText(aiMdProjectEditorPageIndex, "AI Markdown Project / Repository");

	}

	/** Creates EPubProject text editor page of the forms editor */
	@SneakyThrows(PartInitException.class)
	private void createAiMdTextEditorPage() {

//		aiMdTextEditor = new AiMdTextEditor();
//		aiMdTextEditorPageIndex = addPage(aiMdTextEditor, getEditorInput());
//		
//		this.setPartName(aiMdTextEditor.getTitle());
//		
//		setPageText(aiMdTextEditorPageIndex, ".gpt-project");
//		
//		document = (Document) aiMdTextEditor.getDocumentProvider().getDocument(aiMdTextEditor.getEditorInput());
		
		
		
		aiMdTextEditor = new AiMdTextEditor();
		aiMdTextEditorPageIndex = addPage(aiMdTextEditor, getEditorInput());
		
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

		createAiMdProjectEditorPage();
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
		
		//viewerBrowser.setUrl(markdownViewerUrl);
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
	

}
