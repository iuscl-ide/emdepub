package org.emdepub.ui.editor.md;

import java.io.File;
import java.util.Base64;
import java.util.LinkedHashMap;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.IDE;
import org.emdepub.activator.F;
import org.emdepub.activator.L;
import org.emdepub.activator.R;
import org.emdepub.activator.S;
import org.emdepub.md.ui.wizards.MarkdownExportAsHtmlWizard.MarkdownExportType;
import org.emdepub.ui.editor.md.prefs.MarkdownHtmlGeneratorPrefs;
import org.emdepub.ui.editor.md.prefs.MarkdownHtmlGeneratorPrefs.FormatCodeStyle;
import org.emdepub.ui.editor.md.prefs.MarkdownHtmlGeneratorPrefs.FormatOption;
import org.emdepub.ui.editor.md.prefs.MarkdownHtmlGeneratorPrefs.FormatStyle;


public class MarkdownEditor extends FormEditor {

	private static final String s = F.sep();
	private static final String e = F.enter();
	
	public static final LinkedHashMap<FormatStyle, String> formatStylesCss = new LinkedHashMap<>();
	static {
		formatStylesCss.put(FormatStyle.None, "");
		formatStylesCss.put(FormatStyle.GitHub, "github-markdown");
		formatStylesCss.put(FormatStyle.Google, "google-like-markdown");
		formatStylesCss.put(FormatStyle.BitBucket, "bitbucket-markdown");
	}

	public static final LinkedHashMap<FormatOption, Boolean> formatOptions = new LinkedHashMap<>();
	static {
		formatOptions.put(FormatOption.FixedContentWidth, true);
		formatOptions.put(FormatOption.JustifiedParagraphs, true);
		formatOptions.put(FormatOption.CenterHeaders, false);
	}

	public static final LinkedHashMap<FormatCodeStyle, String> formatCodeStylesCss = new LinkedHashMap<>();
	static {
		formatCodeStylesCss.put(FormatCodeStyle.None, "");
		formatCodeStylesCss.put(FormatCodeStyle.Agate, "agate");
		formatCodeStylesCss.put(FormatCodeStyle.Androidstudio, "androidstudio");
		formatCodeStylesCss.put(FormatCodeStyle.ArduinoLight, "arduino-light");
		formatCodeStylesCss.put(FormatCodeStyle.Arta, "arta");
		formatCodeStylesCss.put(FormatCodeStyle.Ascetic, "ascetic");
		formatCodeStylesCss.put(FormatCodeStyle.AtelierCaveDark, "atelier-cave-dark");
		formatCodeStylesCss.put(FormatCodeStyle.AtelierCaveLight, "atelier-cave-light");
		formatCodeStylesCss.put(FormatCodeStyle.AtelierDuneDark, "atelier-dune-dark");
		formatCodeStylesCss.put(FormatCodeStyle.AtelierDuneLight, "atelier-dune-light");
		formatCodeStylesCss.put(FormatCodeStyle.AtelierEstuaryDark, "atelier-estuary-dark");
		formatCodeStylesCss.put(FormatCodeStyle.AtelierEstuaryLight, "atelier-estuary-light");
		formatCodeStylesCss.put(FormatCodeStyle.AtelierForestDark, "atelier-forest-dark");
		formatCodeStylesCss.put(FormatCodeStyle.AtelierForestLight, "atelier-forest-light");
		formatCodeStylesCss.put(FormatCodeStyle.AtelierHeathDark, "atelier-heath-dark");
		formatCodeStylesCss.put(FormatCodeStyle.AtelierHeathLight, "atelier-heath-light");
		formatCodeStylesCss.put(FormatCodeStyle.AtelierLakesideDark, "atelier-lakeside-dark");
		formatCodeStylesCss.put(FormatCodeStyle.AtelierLakesideLight, "atelier-lakeside-light");
		formatCodeStylesCss.put(FormatCodeStyle.AtelierPlateauDark, "atelier-plateau-dark");
		formatCodeStylesCss.put(FormatCodeStyle.AtelierPlateauLight, "atelier-plateau-light");
		formatCodeStylesCss.put(FormatCodeStyle.AtelierSavannaDark, "atelier-savanna-dark");
		formatCodeStylesCss.put(FormatCodeStyle.AtelierSavannaLight, "atelier-savanna-light");
		formatCodeStylesCss.put(FormatCodeStyle.AtelierSeasideDark, "atelier-seaside-dark");
		formatCodeStylesCss.put(FormatCodeStyle.AtelierSeasideLight, "atelier-seaside-light");
		formatCodeStylesCss.put(FormatCodeStyle.AtelierSulphurpoolDark, "atelier-sulphurpool-dark");
		formatCodeStylesCss.put(FormatCodeStyle.AtelierSulphurpoolLight, "atelier-sulphurpool-light");
		formatCodeStylesCss.put(FormatCodeStyle.AtomOneDark, "atom-one-dark");
		formatCodeStylesCss.put(FormatCodeStyle.AtomOneLight, "atom-one-light");
		formatCodeStylesCss.put(FormatCodeStyle.BrownPaper, "brown-paper");
		formatCodeStylesCss.put(FormatCodeStyle.CodepenEmbed, "codepen-embed");
		formatCodeStylesCss.put(FormatCodeStyle.ColorBrewer, "color-brewer");
		formatCodeStylesCss.put(FormatCodeStyle.Darcula, "darcula");
		formatCodeStylesCss.put(FormatCodeStyle.Dark, "dark");
		formatCodeStylesCss.put(FormatCodeStyle.Darkula, "darkula");
		formatCodeStylesCss.put(FormatCodeStyle.Default, "default");
		formatCodeStylesCss.put(FormatCodeStyle.Docco, "docco");
		formatCodeStylesCss.put(FormatCodeStyle.Dracula, "dracula");
		formatCodeStylesCss.put(FormatCodeStyle.Far, "far");
		formatCodeStylesCss.put(FormatCodeStyle.Foundation, "foundation");
		formatCodeStylesCss.put(FormatCodeStyle.GithubGist, "github-gist");
		formatCodeStylesCss.put(FormatCodeStyle.Github, "github");
		formatCodeStylesCss.put(FormatCodeStyle.Googlecode, "googlecode");
		formatCodeStylesCss.put(FormatCodeStyle.Grayscale, "grayscale");
		formatCodeStylesCss.put(FormatCodeStyle.GruvboxDark, "gruvbox-dark");
		formatCodeStylesCss.put(FormatCodeStyle.GruvboxLight, "gruvbox-light");
		formatCodeStylesCss.put(FormatCodeStyle.Hopscotch, "hopscotch");
		formatCodeStylesCss.put(FormatCodeStyle.Hybrid, "hybrid");
		formatCodeStylesCss.put(FormatCodeStyle.Idea, "idea");
		formatCodeStylesCss.put(FormatCodeStyle.IrBlack, "ir-black");
		formatCodeStylesCss.put(FormatCodeStyle.KimbieDark, "kimbie.dark");
		formatCodeStylesCss.put(FormatCodeStyle.KimbieLight, "kimbie.light");
		formatCodeStylesCss.put(FormatCodeStyle.Magula, "magula");
		formatCodeStylesCss.put(FormatCodeStyle.MonoBlue, "mono-blue");
		formatCodeStylesCss.put(FormatCodeStyle.MonokaiSublime, "monokai-sublime");
		formatCodeStylesCss.put(FormatCodeStyle.Monokai, "monokai");
		formatCodeStylesCss.put(FormatCodeStyle.Obsidian, "obsidian");
		formatCodeStylesCss.put(FormatCodeStyle.Ocean, "ocean");
		formatCodeStylesCss.put(FormatCodeStyle.ParaisoDark, "paraiso-dark");
		formatCodeStylesCss.put(FormatCodeStyle.ParaisoLight, "paraiso-light");
		formatCodeStylesCss.put(FormatCodeStyle.Pojoaque, "pojoaque");
		formatCodeStylesCss.put(FormatCodeStyle.Purebasic, "purebasic");
		formatCodeStylesCss.put(FormatCodeStyle.Qtcreator_dark, "qtcreator_dark");
		formatCodeStylesCss.put(FormatCodeStyle.Qtcreator_light, "qtcreator_light");
		formatCodeStylesCss.put(FormatCodeStyle.Railscasts, "railscasts");
		formatCodeStylesCss.put(FormatCodeStyle.Rainbow, "rainbow");
		formatCodeStylesCss.put(FormatCodeStyle.Routeros, "routeros");
		formatCodeStylesCss.put(FormatCodeStyle.SchoolBook, "school-book");
		formatCodeStylesCss.put(FormatCodeStyle.SolarizedDark, "solarized-dark");
		formatCodeStylesCss.put(FormatCodeStyle.SolarizedLight, "solarized-light");
		formatCodeStylesCss.put(FormatCodeStyle.Sunburst, "sunburst");
		formatCodeStylesCss.put(FormatCodeStyle.TomorrowNightBlue, "tomorrow-night-blue");
		formatCodeStylesCss.put(FormatCodeStyle.TomorrowNightBright, "tomorrow-night-bright");
		formatCodeStylesCss.put(FormatCodeStyle.TomorrowNightEighties, "tomorrow-night-eighties");
		formatCodeStylesCss.put(FormatCodeStyle.TomorrowNight, "tomorrow-night");
		formatCodeStylesCss.put(FormatCodeStyle.Tomorrow, "tomorrow");
		formatCodeStylesCss.put(FormatCodeStyle.Vs, "vs");
		formatCodeStylesCss.put(FormatCodeStyle.Vs2015, "vs2015");
		formatCodeStylesCss.put(FormatCodeStyle.Xcode, "xcode");
		formatCodeStylesCss.put(FormatCodeStyle.Xt256, "xt256");
		formatCodeStylesCss.put(FormatCodeStyle.Zenburn, "zenburn");
	}
	
	private static final String mdExportTemplate = R.getTextResourceAsString("texts/md-export-template.html");
	private static final String mdFixedContentWidthCss = R.getTextResourceAsString("texts/md-fixed-content-width.css"); 
	private static final String mdJustifiedParagraphsCss = R.getTextResourceAsString("texts/md-justified-paragraphs.css"); 
	private static final String mdCenterHeadersCss = R.getTextResourceAsString("texts/md-center-headers.css"); 
	
	/** The browser will display the Markdown HTML */
	private Browser viewerBrowser;
	private int browserViewerPageIndex;

	//private ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

	
	/** The Markdown editor */
	private MarkdownTextEditor markdownTextEditor;
	private int markdownTextEditorPageIndex;
	private MarkdownHtmlGeneratorPrefs markdownHtmlGeneratorPrefs;

//	private IEditorPart markdownTextEditorPart;
//	private IDocument sourceDocument;

	/** Creates the viewer page of the forms editor */
	private void createBrowserViewerPage() {
		
		Composite viewerPageComposite = new Composite(getContainer(), SWT.NONE);
	    
	    GridLayout viewerPageGridLayout = new GridLayout();
	    viewerPageGridLayout.marginWidth = 1;
	    viewerPageGridLayout.marginHeight = 1;
	    viewerPageGridLayout.verticalSpacing = 0;
	    viewerPageGridLayout.horizontalSpacing = 0;
	    viewerPageGridLayout.numColumns = 1;
	    viewerPageComposite.setLayout(viewerPageGridLayout);
		
		viewerBrowser = new Browser(viewerPageComposite, SWT.NONE);
		viewerBrowser.setJavascriptEnabled(true);
		viewerBrowser.setText("html");
		viewerBrowser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		viewerBrowser.addStatusTextListener(new StatusTextListener() {
			
			@Override
			public void changed(StatusTextEvent statusTextEvent) {
				//MarkdownSemanticEPEditorContributor.getLinkField().setText(statusTextEvent.text);
			}
		});

		viewerBrowser.addProgressListener(new ProgressListener() {
            public void changed(ProgressEvent progressEvent) {
            	/* ILB */
            }
            public void completed(ProgressEvent progressEvent) {
            
            	viewerBrowser.execute("window.mdUI = {}; " +
            			"window.mdUI.mdContent = '" + getBase64MarkdownText() + "'; " +
            			"window.mdUI.formatOptionsCss = '" + getBase64FormatOptionsCss() + "'; " +
            			"window.mdUI.cssName = '" + formatStylesCss.get(markdownHtmlGeneratorPrefs.getFormatStyle()) + "'; " +
            			"window.mdUI.cssHighlightName = '" + "highlight/" +  formatCodeStylesCss.get(markdownHtmlGeneratorPrefs.getFormatCodeStyle()) + "'; " +
            			"window.mdUI.localBaseHref = '" + findLocalBaseHref() + "'; " +
            			"reload();");
            }
        });
		
		browserViewerPageIndex = addPage(viewerPageComposite);
		setPageText(browserViewerPageIndex, "Display");
	}

	/** Creates Markdown text editor page of the forms editor */
	private void createMarkdownTextEditorPage() {

		markdownHtmlGeneratorPrefs = new MarkdownHtmlGeneratorPrefs();
				
		markdownTextEditor = new MarkdownTextEditor();
		try {
			markdownTextEditorPageIndex = addPage(markdownTextEditor, getEditorInput());
		} catch (PartInitException partInitException) {
			L.e("createMarkdownTextEditorPage", partInitException);
		}
		
		this.setPartName(markdownTextEditor.getTitle());
		
		setPageText(markdownTextEditorPageIndex, "Markdown Editor");
		//setSourcePage(markdownTextEditor);
		//sourceDocument = markdownTextEditor.getDocumentProvider().getDocument(markdownTextEditor.getEditorInput());
	}

	/** Create  */
	@Override
	protected void addPages() {
		//System.out.println("addPages");
		createBrowserViewerPage();
		
		createMarkdownTextEditorPage();
		
		
//		if (markdownTextEditor == null) {
//			markdownTextEditor = new ExtensionBasedTextEditor();
//			ExtensionBasedTextEditor editor = (ExtensionBasedTextEditor) markdownTextEditor;
//
//			int dex;
//			try {
//				dex = addPage(markdownTextEditor, getEditorInput());
//				setPageText(dex, editor.getTitle());
//				setSourcePage(markdownTextEditor);
//				sourceDocument = editor.getDocumentProvider().getDocument(editor.getEditorInput());
//			} catch (PartInitException ex) {
//				// log.error(ex.getMessage(), ex);
//			}
//		}
		// TODO Auto-generated method stub

	}
	
	private String findLocalBaseHref() {
		
		IFile iFile = (IFile) markdownTextEditor.getEditorInput().getAdapter(IFile.class);
		File file = iFile.getLocation().toFile();
		String rootPath = file.getPath().substring(0, file.getPath().length() - file.getName().length()).replace("\\", "/");
		
		return "file:///" + rootPath;
	}

	public String getFilePathAndName() {
		
		IFile iFile = (IFile) markdownTextEditor.getEditorInput().getAdapter(IFile.class);
		return iFile.getLocation().toOSString();
	}

//	private File findPrefsFile() {
//		
//		return new File(getFilePathAndName() + ".prefs.json");
//	}

	public void savePrefs() {
		
		S.saveObjectToJsonFileName(markdownHtmlGeneratorPrefs, getFilePathAndName() + ".prefs.json");
	}
	
	public void exportAsHtml(MarkdownExportType markdownExportType, String exportName, String exportLocation) {

    	String htmlText = (String) viewerBrowser.evaluate("window.mdUI = {}; " +
			"window.mdUI.mdContent = '" + getBase64MarkdownText() + "'; " +
			"window.mdUI.cssHighlightName = '" + "highlight/" +  formatCodeStylesCss.get(markdownHtmlGeneratorPrefs.getFormatCodeStyle()) + "'; " +
			"window.mdUI.localBaseHref = null; " +
			"return exportHTML();");

    	if (markdownExportType == MarkdownExportType.ExportFileOnly) {
    		String exportFileNameWithPath = exportLocation + s + exportName + ".html";
    		F.saveStringToFile(htmlText, exportFileNameWithPath);
    		openExternalFile(exportFileNameWithPath);
    		
//    		HashMap<String , Object> ser =  new HashMap<>();
//    		ser.put("a", "str");
//    		ser.put("b", 1);
//    		LinkedHashMap<String, Object> ser = markdownHtmlGeneratorPrefs.getSerialization();
//    		S.saveObjectToJsonFileName(ser, "C:\\Iustin\\Programming\\_emdepub\\tools\\exports\\ser.json");
//    		LinkedHashMap<String, Object> ser2 = new LinkedHashMap<>();
//    		ser2 = S.loadObjectFromJsonFileName("C:\\Iustin\\Programming\\_emdepub\\tools\\exports\\ser.json", LinkedHashMap.class);
//    		markdownHtmlGeneratorPrefs.setSerialization(ser2);
    	}
    	else {
    		String exportFolderNameWithPath = exportLocation + s + exportName;
    		F.deleteFolder(exportFolderNameWithPath);
    		F.createFoldersIfNotExists(exportFolderNameWithPath);
    		
    		String mdExport = mdExportTemplate;
    		
    		mdExport = mdExport.replace("{{export-content}}", htmlText);
    		
    		mdExport = mdExport.replace("{{export-title}}", exportName);

    		
    		/* CSS */
    		String mdViewerFolderNameWithPath = "C:/Iustin/Programming/_emdepub/repositories/emdepub/org.emdepub/code-language/viewers/markdown";
    		String exportCssFolderNameWithPath = exportFolderNameWithPath + s + "css"; 
    		F.createFoldersIfNotExists(exportCssFolderNameWithPath);
    		String exportCssHighlightFolderNameWithPath = exportCssFolderNameWithPath + s + "highlight"; 
    		F.createFoldersIfNotExists(exportCssHighlightFolderNameWithPath);

    		String exportFormatStylesCss = formatStylesCss.get(markdownHtmlGeneratorPrefs.getFormatStyle()) + ".css";
    		F.copyFile(mdViewerFolderNameWithPath + s + "styles" + s + exportFormatStylesCss,
    				exportCssFolderNameWithPath + s + exportFormatStylesCss);
    		
    		String formatOptionsCss = getFormatOptionsCss();
    		if (formatOptionsCss.trim().length() > 0) {
    			formatOptionsCss = e + "<style>" + formatOptionsCss + e + "</style>" + e;
    		}

    		String exportFormatCodeStylesCss = formatCodeStylesCss.get(markdownHtmlGeneratorPrefs.getFormatCodeStyle()) + ".css";
    		F.copyFile(mdViewerFolderNameWithPath + s + "styles/highlight" + s + exportFormatCodeStylesCss,
    				exportCssHighlightFolderNameWithPath + s + exportFormatCodeStylesCss);
    		
    		mdExport = mdExport.replace("{{export-css}}",
				formatOptionsCss +
				e + "<link rel=\"stylesheet\" href=\"css/" + exportFormatStylesCss + "\">" + e +
				e + "<link rel=\"stylesheet\" href=\"css/highlight/" + exportFormatCodeStylesCss + "\">" + e);
			

    		String exportFileNameWithPath = exportFolderNameWithPath + s + exportName + ".html";
    		F.saveStringToFile(mdExport, exportFileNameWithPath);

    		
//    		switch (markdownHtmlGeneratorPrefs.getFormatStyle()) {
//			case BitBucket:
//				
//				break;
//			case GitHub:
//				mdExportTemplate = mdExportTemplate.replace("{{export-content}}", "<link rel=\"stylesheet\" href=\"css/ui-fonts.css\">");
//				break;
//			case Google:
//				mdExportTemplate = mdExportTemplate.replace("{{export-content}}", "");
//				break;
//			case None:
//					mdExportTemplate = mdExportTemplate.replace("{{export-content}}", "");
//				break;
//			default:
//				throw new IllegalArgumentException("Unexpected value: " + markdownHtmlGeneratorPrefs.getFormatStyle());
//			}
    		
    		
    		openExternalFile(exportFileNameWithPath);
    	}
	}
	
	/** Get the Markdown as text */
	public String getBase64MarkdownText() {
		
		String mdText = markdownTextEditor.getDocumentProvider().getDocument(markdownTextEditor.getEditorInput()).get();
		
		return new String(Base64.getEncoder().encode(mdText.getBytes()));
	}

	/** Get the Markdown as text */
	public String getFormatOptionsCss() {
		
		String formatOptionsCss = "";
		String e = F.enter();
		
		if (markdownHtmlGeneratorPrefs.getFormatOptions().get(FormatOption.FixedContentWidth)) {
			formatOptionsCss = formatOptionsCss + e + mdFixedContentWidthCss;
		}

		if (markdownHtmlGeneratorPrefs.getFormatOptions().get(FormatOption.JustifiedParagraphs)) {
			formatOptionsCss = formatOptionsCss + e + mdJustifiedParagraphsCss;
		}

		if (markdownHtmlGeneratorPrefs.getFormatOptions().get(FormatOption.CenterHeaders)) {
			formatOptionsCss = formatOptionsCss + e + mdCenterHeadersCss;
		}
		
		return formatOptionsCss;
	}

	/** Get the Markdown as text */
	public String getBase64FormatOptionsCss() {
		
		String formatOptionsCss = getFormatOptionsCss();

		return new String(Base64.getEncoder().encode(formatOptionsCss.getBytes()));
	}

	/** External file in IDE */
	private void openExternalFile(String fileNameWithPath) {

		IFileStore fileStore = EFS.getLocalFileSystem().getStore(new Path(fileNameWithPath));
		// fileStore = fileStore.getChild(names[i]);
		if (!fileStore.fetchInfo().isDirectory() && fileStore.fetchInfo().exists()) {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			try {
//				IEditorRegistry editorRegistry = PlatformUI.getWorkbench().getEditorRegistry();
//				IEditorDescriptor genericEditorDescriptor = null;
//				for (IEditorDescriptor editorDescriptor : editorRegistry.getEditors(fileNameWithPath)) {
//					if (editorDescriptor.getId().equals("org.eclipse.ui.genericeditor.GenericEditor")) {
//						genericEditorDescriptor = editorDescriptor;
//					}
//					System.out.println(editorDescriptor.toString());
//				}
//				
//				IContentType contentType = Platform.getContentTypeManager().getContentType("org.eclipse.wildwebdeveloper.html");
//						
//						//findContentTypeFor(fileNameWithPath);
//				
//				IEditorInput editorInput = new FileStoreEditorInput(fileStore);
//				
//				//org.eclipse.wildwebdeveloper.html
//				
//				if (genericEditorDescriptor != null) {
//					IDE.overrideDefaultEditorAssociation(editorInput, contentType, genericEditorDescriptor);
//					
//					//overrideDefaultEditorAssociation(fileNameWithPath, contentType, genericEditorDescriptor);
//				}
				
				
				//IDE.overrideDefaultEditorAssociation(fileNameWithPath, null, null)
				
				//
				IDE.openEditor(page, new FileStoreEditorInput(fileStore), "org.eclipse.ui.genericeditor.GenericEditor");
				//IDE.openEditorOnFileStore(page, fileStore);
			} catch (PartInitException partInitException) {
				L.e("openExternalFile", partInitException);
			}
		}
	}
	
	/** Put in browser what is in editor */
	public void refresh() {
		//System.out.println("refresh " + getMarkdownText());
		
		viewerBrowser.setUrl("file://C:/Iustin/Programming/_emdepub/repositories/emdepub/org.emdepub/code-language/viewers/markdown/index-md.html");
	}

	/** Refreshes contents of viewer page when it is activated */
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);
		
		if (newPageIndex == browserViewerPageIndex) {
			refresh();
		}
//		if (newPageIndex == preferencesPageIndex) {
//			preferencesUI.setInitialFocus();
//		}
	}
	
	/** Saves the forms editor's document */
	@Override
	public void doSave(IProgressMonitor progressMonitor) {
		
		markdownTextEditor.doSave(progressMonitor);
	}

	/** Saves the forms editor's document as another file */
	@Override
	public void doSaveAs() {

		markdownTextEditor.doSaveAs();
		this.setPartName(markdownTextEditor.getTitle());
		setInput(markdownTextEditor.getEditorInput());
	}
	
//	/** Saves the multi-page editor's document as another file */
//	public void doSaveAs() {
//		textEditorPart.doSaveAs();
//		setPageText(textEditorPageIndex, textEditorPart.getTitle());
//		setInput(textEditorPart.getEditorInput());
//	}
	
	@Override
	public boolean isSaveAsAllowed() {

		return true;
	}

	public MarkdownHtmlGeneratorPrefs getMarkdownHtmlGeneratorPrefs() {
		return markdownHtmlGeneratorPrefs;
	}

	public MarkdownTextEditor getMarkdownTextEditor() {
		return markdownTextEditor;
	}

	public int getBrowserViewerPageIndex() {
		return browserViewerPageIndex;
	}

	public int getMarkdownTextEditorPageIndex() {
		return markdownTextEditorPageIndex;
	}

//	/**
//	 * @param sourcePage The sourcePage to set.
//	 */
//	public void setSourcePage(ITextEditor sourcePage) {
//		this.markdownTextEditor = sourcePage;
//		this.sourcePageIndex = pages.indexOf(sourcePage);
//	}

	
	
}
