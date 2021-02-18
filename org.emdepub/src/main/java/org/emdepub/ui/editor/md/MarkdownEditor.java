/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.ui.editor.md;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Base64;
import java.util.LinkedHashMap;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.emdepub.activator.F;
import org.emdepub.activator.L;
import org.emdepub.activator.R;
import org.emdepub.activator.UI;
import org.emdepub.md.ui.wizards.MarkdownExportAsHtmlWizard.MarkdownExportType;
import org.emdepub.ui.editor.md.engine.MarkdownFormatterEngine;
import org.emdepub.ui.editor.md.language.MarkdownOutlinePage;
import org.emdepub.ui.editor.md.prefs.MarkdownPreferences;
import org.emdepub.ui.editor.md.prefs.MarkdownPreferences.DisplayFormatCodeStyles;
import org.emdepub.ui.editor.md.prefs.MarkdownPreferences.DisplayFormatStyles;
import org.emdepub.ui.editor.md.prefs.MarkdownPreferences.PreferenceNames;

/** Markdown multi-page editor */
public class MarkdownEditor extends FormEditor {

	private static final String s = F.sep();
	private static final String e = F.enter();
	
	public static final LinkedHashMap<DisplayFormatStyles, String> formatStylesCss = new LinkedHashMap<>();
	static {
		formatStylesCss.put(DisplayFormatStyles.None, null);
		formatStylesCss.put(DisplayFormatStyles.GitHub, "github-markdown");
		formatStylesCss.put(DisplayFormatStyles.GoogleLike, "google-like-markdown");
		formatStylesCss.put(DisplayFormatStyles.SemanticUILike, "semantic-ui-like-markdown");
		formatStylesCss.put(DisplayFormatStyles.Custom, "stylesheet");
	}

	public static final LinkedHashMap<PreferenceNames, Boolean> formatOptions = new LinkedHashMap<>();
	static {
		formatOptions.put(PreferenceNames.DisplayFixedContentWidth, true);
		formatOptions.put(PreferenceNames.DisplayJustifiedParagraphs, true);
		formatOptions.put(PreferenceNames.DisplayCenterHeaders, false);
	}

	public static final LinkedHashMap<DisplayFormatCodeStyles, String> formatCodeStylesCss = new LinkedHashMap<>();
	static {
		formatCodeStylesCss.put(DisplayFormatCodeStyles.None, null);
		formatCodeStylesCss.put(DisplayFormatCodeStyles.Agate, "agate");
		formatCodeStylesCss.put(DisplayFormatCodeStyles.Androidstudio, "androidstudio");
		formatCodeStylesCss.put(DisplayFormatCodeStyles.ArduinoLight, "arduino-light");
		formatCodeStylesCss.put(DisplayFormatCodeStyles.Dark, "dark");
		formatCodeStylesCss.put(DisplayFormatCodeStyles.Default, "default");
		formatCodeStylesCss.put(DisplayFormatCodeStyles.Far, "far");
		formatCodeStylesCss.put(DisplayFormatCodeStyles.Foundation, "foundation");
		formatCodeStylesCss.put(DisplayFormatCodeStyles.GithubGist, "github-gist");
		formatCodeStylesCss.put(DisplayFormatCodeStyles.Github, "github");
		formatCodeStylesCss.put(DisplayFormatCodeStyles.Googlecode, "googlecode");
		formatCodeStylesCss.put(DisplayFormatCodeStyles.Grayscale, "grayscale");
		formatCodeStylesCss.put(DisplayFormatCodeStyles.GruvboxDark, "gruvbox-dark");
		formatCodeStylesCss.put(DisplayFormatCodeStyles.GruvboxLight, "gruvbox-light");
		formatCodeStylesCss.put(DisplayFormatCodeStyles.Hybrid, "hybrid");
		formatCodeStylesCss.put(DisplayFormatCodeStyles.Idea, "idea");
		formatCodeStylesCss.put(DisplayFormatCodeStyles.IrBlack, "ir-black");
		formatCodeStylesCss.put(DisplayFormatCodeStyles.Magula, "magula");
		formatCodeStylesCss.put(DisplayFormatCodeStyles.Purebasic, "purebasic");
		formatCodeStylesCss.put(DisplayFormatCodeStyles.Railscasts, "railscasts");
		formatCodeStylesCss.put(DisplayFormatCodeStyles.Sunburst, "sunburst");
		formatCodeStylesCss.put(DisplayFormatCodeStyles.Vs, "vs");
		formatCodeStylesCss.put(DisplayFormatCodeStyles.Vs2015, "vs2015");
		formatCodeStylesCss.put(DisplayFormatCodeStyles.VSCode, "vscode");
		formatCodeStylesCss.put(DisplayFormatCodeStyles.Xcode, "xcode");
		formatCodeStylesCss.put(DisplayFormatCodeStyles.Custom, "custom");
	}
	
	public static final LinkedHashMap<DisplayFormatCodeStyles, String> formatCodeStylesPre = new LinkedHashMap<>();
	static {
		formatCodeStylesPre.put(DisplayFormatCodeStyles.None, null);
		formatCodeStylesPre.put(DisplayFormatCodeStyles.Agate, "background: rgb(51, 51, 51);color: white;");
		formatCodeStylesPre.put(DisplayFormatCodeStyles.Androidstudio, "background: rgb(40, 43, 46);color: rgb(169, 183, 198);");
		formatCodeStylesPre.put(DisplayFormatCodeStyles.ArduinoLight, "background: rgb(255, 255, 255);color: rgb(67, 79, 84);");
		formatCodeStylesPre.put(DisplayFormatCodeStyles.Dark, "background: rgb(68, 68, 68);color: rgb(221, 221, 221);");
		formatCodeStylesPre.put(DisplayFormatCodeStyles.Default, "background: rgb(240, 240, 240);color: rgb(68, 68, 68);");
		formatCodeStylesPre.put(DisplayFormatCodeStyles.Far, "background: rgb(0, 0, 128);color: rgb(0, 255, 255);");
		formatCodeStylesPre.put(DisplayFormatCodeStyles.Foundation, "background: rgb(238, 238, 238);color: black;");
		formatCodeStylesPre.put(DisplayFormatCodeStyles.GithubGist, "background: white;color: rgb(51, 51, 51);");
		formatCodeStylesPre.put(DisplayFormatCodeStyles.Github, "background: rgb(248, 248, 248);color: rgb(51, 51, 51);");
		formatCodeStylesPre.put(DisplayFormatCodeStyles.Googlecode, "background: white;color: black;");
		formatCodeStylesPre.put(DisplayFormatCodeStyles.Grayscale, "background: rgb(255, 255, 255);color: rgb(51, 51, 51);");
		formatCodeStylesPre.put(DisplayFormatCodeStyles.GruvboxDark, "background: rgb(40, 40, 40);color: rgb(235, 219, 178);");
		formatCodeStylesPre.put(DisplayFormatCodeStyles.GruvboxLight, "background: rgb(251, 241, 199);color: rgb(60, 56, 54);");
		formatCodeStylesPre.put(DisplayFormatCodeStyles.Hybrid, "background: rgb(29, 31, 33);color: rgb(197, 200, 198);");
		formatCodeStylesPre.put(DisplayFormatCodeStyles.Idea, "background: rgb(255, 255, 255);color: rgb(0, 0, 0);");
		formatCodeStylesPre.put(DisplayFormatCodeStyles.IrBlack, "background: rgb(0, 0, 0);color: rgb(248, 248, 248);");
		formatCodeStylesPre.put(DisplayFormatCodeStyles.Magula, "color: black;");
		formatCodeStylesPre.put(DisplayFormatCodeStyles.Purebasic, "background: rgb(255, 255, 223);color: rgb(0, 0, 0);");
		formatCodeStylesPre.put(DisplayFormatCodeStyles.Railscasts, "background: rgb(35, 35, 35);color: rgb(230, 225, 220);");
		formatCodeStylesPre.put(DisplayFormatCodeStyles.Sunburst, "background: rgb(0, 0, 0);color: rgb(248, 248, 248);");
		formatCodeStylesPre.put(DisplayFormatCodeStyles.Vs, "background: white;color: black;");
		formatCodeStylesPre.put(DisplayFormatCodeStyles.Vs2015, "background: rgb(30, 30, 30);color: rgb(220, 220, 220);");
		formatCodeStylesPre.put(DisplayFormatCodeStyles.VSCode, "background: black; color: lemonchiffon;");
		formatCodeStylesPre.put(DisplayFormatCodeStyles.Xcode, "background: rgb(255, 255, 255);color: black;");
		formatCodeStylesPre.put(DisplayFormatCodeStyles.Custom, "background: white;color: black;");
	}
	
	private static final String mdExportTemplate = R.getTextResourceAsString("texts/md-export-template.html");
	private static final String mdFixedContentWidthCss = R.getTextResourceAsString("texts/md-fixed-content-width.css"); 
	private static final String mdJustifiedParagraphsCss = R.getTextResourceAsString("texts/md-justified-paragraphs.css"); 
	private static final String mdCenterHeadersCss = R.getTextResourceAsString("texts/md-center-headers.css"); 
	
	
	private static final String mdViewerFolderNameWithPath = "C:/Iustin/Programming/_emdepub/repositories/emdepub/org.emdepub/code-language/viewers/markdown";

	
	/** The browser will display the Markdown HTML */
	private Browser viewerBrowser;
	private int browserViewerPageIndex;

	//private ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

	
	/** The Markdown editor */
	private MarkdownTextEditor markdownTextEditor;
	private int markdownTextEditorPageIndex;
	private MarkdownPreferences markdownPreferences;
	private String markdownPreferencesPropertiesFileNameWithPath;
	private MarkdownOutlinePage markdownOutlinePage;
//	private IEditorPart markdownTextEditorPart;
//	private IDocument sourceDocument;

	/** Creates the viewer page of the forms editor */
	private void createBrowserViewerPage() {
		
		UI ui = new UI(false, Display.getCurrent());
		
		Composite viewerPageComposite = new Composite(getContainer(), SWT.NONE);
		viewerPageComposite.setLayout(ui.createMarginsGridLayout(1));
	
		viewerBrowser = new Browser(viewerPageComposite, SWT.NONE);
		viewerBrowser.setJavascriptEnabled(true);
		viewerBrowser.setText("");
		viewerBrowser.setLayoutData(ui.createFillBothGridData());
		
		/** Status bar */
		viewerBrowser.addStatusTextListener(new StatusTextListener() {
			@Override
			public void changed(StatusTextEvent statusTextEvent) {
				
				MarkdownEditorContributor.getStatusLineLinkField().setText(statusTextEvent.text);
			}
		});

		/** Progress */
		viewerBrowser.addProgressListener(new ProgressListener() {
            public void changed(ProgressEvent progressEvent) { }
            public void completed(ProgressEvent progressEvent) {
            
            	String formatStyleCss = formatStylesCss.get(markdownPreferences.get(PreferenceNames.DisplayFormatStyle));
            	String formatCodeStyleCss = formatCodeStylesCss.get(markdownPreferences.get(PreferenceNames.DisplayFormatCodeStyle));
            	
            	viewerBrowser.execute("window.markdownSettings = {}; " +
        			"window.markdownSettings.markdownText = '" + getBase64MarkdownText() + "'; " +
        			"window.markdownSettings.formatOptionsCss = '" + getBase64FormatOptionsCss() + "'; " +
        			(formatStyleCss == null ? "" : "window.markdownSettings.cssName = '" + formatStyleCss + "'; ") +
        			(formatCodeStyleCss == null ? "" : "window.markdownSettings.cssHighlightName = '" + "highlight/" +  formatCodeStyleCss + "'; ") +
        			"window.markdownSettings.localBaseHref = '" + findLocalBaseHref() + "'; " +
        			"reload();");
            }
        });
		
		browserViewerPageIndex = addPage(viewerPageComposite);
		setPageText(browserViewerPageIndex, "Display");
	}

	/** Creates Markdown text editor page of the forms editor */
	private void createMarkdownTextEditorPage() {

		markdownPreferences = new MarkdownPreferences();
		
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
		
		markdownPreferencesPropertiesFileNameWithPath = getSourceMarkdownFilePathAndName() + ".prefs";
		markdownPreferences.loadProperties(markdownPreferencesPropertiesFileNameWithPath);
		
		markdownOutlinePage = new MarkdownOutlinePage(this);

//		if (getEditorInput() != null)
//				markdownOutlinePage.setInput(getEditorInput());
//		}

	}

	/** Save from here */
	public void saveMarkdownPreferences() {
		
		markdownPreferences.saveProperties(markdownPreferencesPropertiesFileNameWithPath);
	}
	
	/** Create pages */
	@Override
	protected void addPages() {

		createBrowserViewerPage();
		createMarkdownTextEditorPage();
	}
	
	/** LocalBaseHref */
	private String findLocalBaseHref() {
		
		IEditorInput editorInput = markdownTextEditor.getEditorInput();
		
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

	/** Source Markdown file path and name */
	public String getSourceMarkdownFilePathAndName() {

		IEditorInput editorInput = markdownTextEditor.getEditorInput();
		
		if (editorInput instanceof FileEditorInput) {
			
			IFile iFile = (IFile) editorInput.getAdapter(IFile.class);
			return iFile.getLocation().toOSString();
		}
		else if (editorInput instanceof FileStoreEditorInput) {
		
			URI urlPath = ((FileStoreEditorInput) editorInput).getURI();
			try {
				return new File(urlPath.toURL().getPath()).getAbsolutePath();
			} catch (MalformedURLException malformedURLException) {
				L.e("getSourceMarkdownFilePathAndName", malformedURLException);
			}
		}
		
		return editorInput.getName();
	}
	
	/** Export */
	public void exportAsHtml(MarkdownExportType markdownExportType, String exportName, String exportLocation) {

		String formatStyleCss = formatStylesCss.get(markdownPreferences.get(PreferenceNames.DisplayFormatStyle));
		String formatCodeStyleCss = formatCodeStylesCss.get(markdownPreferences.get(PreferenceNames.DisplayFormatCodeStyle));
		
    	String htmlText = (String) viewerBrowser.evaluate("window.markdownSettings = {}; " +
			"window.markdownSettings.markdownText = '" + getBase64MarkdownText() + "'; " +
			(formatCodeStyleCss == null ? "" : "window.markdownSettings.cssHighlightName = '" + "highlight/" +  formatCodeStyleCss + "'; ") +
			"window.markdownSettings.localBaseHref = null; " +
			"return createHTML();");

    	if (markdownExportType == MarkdownExportType.ExportFileOnly) {
    		String exportFileNameWithPath = exportLocation + s + exportName + ".html";
    		
    		closeExternalFile(exportFileNameWithPath);
    		
    		F.saveStringToFile(htmlText, exportFileNameWithPath);
    		openExternalFile(exportFileNameWithPath);
    	}
    	else {
    		String exportFolderNameWithPath = exportLocation + s + exportName;
    		String exportFileNameWithPath = exportFolderNameWithPath + s + exportName + ".html";

    		closeExternalFile(exportFileNameWithPath);
    		
    		F.deleteFolder(exportFolderNameWithPath);
    		F.createFoldersIfNotExists(exportFolderNameWithPath);
    		
    		String markdownExport = mdExportTemplate;
    		markdownExport = markdownExport.replace("{{export-content}}", htmlText);
    		markdownExport = markdownExport.replace("{{export-title}}", exportName);

    		String exportCss = "";
    		
    		/* CSS */
    		if (formatStyleCss != null) {
    			String exportCssFolderNameWithPath = exportFolderNameWithPath + s + "css";
    			F.createFoldersIfNotExists(exportCssFolderNameWithPath);
        		String exportFormatStylesCss = formatStyleCss + ".css";
        		F.copyFile(mdViewerFolderNameWithPath + s + "styles" + s + exportFormatStylesCss,
        			exportCssFolderNameWithPath + s + exportFormatStylesCss);
        		exportCss = exportCss + e + "<link rel=\"stylesheet\" href=\"css/" + exportFormatStylesCss + "\">" + e;
    		}
    		
    		/* CSS Highlight */
    		if (formatCodeStyleCss != null) {
        		String exportCssHighlightFolderNameWithPath = exportFolderNameWithPath + s + "css" + s + "highlight";
    			F.createFoldersIfNotExists(exportCssHighlightFolderNameWithPath);
        		String exportFormatCodeStylesCss = formatCodeStyleCss + ".css";
        		F.copyFile(mdViewerFolderNameWithPath + s + "styles/highlight" + s + exportFormatCodeStylesCss,
       				exportCssHighlightFolderNameWithPath + s + exportFormatCodeStylesCss);
        		exportCss = exportCss + e + "<link rel=\"stylesheet\" href=\"css/highlight/" + exportFormatCodeStylesCss + "\">" + e;
    		}

    		/* CSS options */
    		String formatOptionsCss = getFormatOptionsCss();
    		if (formatOptionsCss.trim().length() > 0) {
    			formatOptionsCss = e + "<style>" + formatOptionsCss + e + "</style>" + e;
    			exportCss = exportCss + e + formatOptionsCss + e;
    		}
    		
    		markdownExport = markdownExport.replace("{{export-css}}", exportCss);
			

    		F.saveStringToFile(markdownExport, exportFileNameWithPath);
    		
    		openExternalFile(exportFileNameWithPath);
    	}
	}
	
	/** Special formatting */
	public void doSpecialFormatting(MarkdownPreferences markdownPreferences) {
		
		Document document = (Document) markdownTextEditor.getDocumentProvider().getDocument(markdownTextEditor.getEditorInput());
//		String enter = document.getDefaultLineDelimiter();
		TextSelection textSelection = (TextSelection) markdownTextEditor.getSelectionProvider().getSelection();
		
//		String selection = "";
//		if (formattingOptions.get(SpecialFormattingOptions.ApplyToSelection)) {
//			selection = textSelection.getText();
//		}
//		else {
//			selection = document.get();
//		}
		String selection = textSelection.getText();
		if (selection.length() == 0) {
			return;
		}

		String formattedSelection = MarkdownFormatterEngine.formatMarkdown(selection, markdownPreferences);

		try {
			document.replace(textSelection.getOffset(), textSelection.getLength(), formattedSelection);
		}
		catch (BadLocationException badLocationException) {
			L.e("BadLocationException in doSpecialFormatting", badLocationException);
		}

//		if (formattingOptions.get(SpecialFormattingOptions.ApplyToSelection)) {
//			try {
//				document.replace(textSelection.getOffset(), textSelection.getLength(), formattedSelection);
//			}
//			catch (BadLocationException badLocationException) {
//				L.e("BadLocationException in doSpecialFormatting", badLocationException);
//			}
//		}
//		else {
//			document.set(formattedSelection);
//		}
	}
	
	/** Get the Markdown as Base64 text */
	public String getBase64MarkdownText() {
		
		String mdText = markdownTextEditor.getDocumentProvider().getDocument(markdownTextEditor.getEditorInput()).get();
		
		return new String(Base64.getEncoder().encode(mdText.getBytes()));
	}

	/** Get the options CSSs */
	public String getFormatOptionsCss() {
		
		String formatOptionsCss = "";
		String e = F.enter();
		
		if (markdownPreferences.<Boolean>get(PreferenceNames.DisplayFixedContentWidth)) {
			formatOptionsCss = formatOptionsCss + e + mdFixedContentWidthCss;
		}

		if (markdownPreferences.<Boolean>get(PreferenceNames.DisplayJustifiedParagraphs)) {
			formatOptionsCss = formatOptionsCss + e + mdJustifiedParagraphsCss;
		}

		if (markdownPreferences.<Boolean>get(PreferenceNames.DisplayCenterHeaders)) {
			formatOptionsCss = formatOptionsCss + e + mdCenterHeadersCss;
		}
		
		String formatCodeStylePre = formatCodeStylesPre.get(markdownPreferences.get(PreferenceNames.DisplayFormatCodeStyle));
		if (formatCodeStylePre != null) {
			String formatCodeStylesCssBackground = "article.markdown-body pre {" + e + formatCodeStylePre + e + "}";
			formatOptionsCss = formatOptionsCss + e + formatCodeStylesCssBackground;
		}
		
		/* Kept here to fill the lists */
//		for (FormatCodeStyle formatCodeStyle : FormatCodeStyle.values()) {
//
//			String formatCodeStylesCssFileName = formatCodeStylesCss.get(formatCodeStyle) + ".css";
//			String formatCodeStylesCssFileNameWithPath = mdViewerFolderNameWithPath + s + "styles/highlight" + s + formatCodeStylesCssFileName;
//
//			CSSOMParser cssomParser = new CSSOMParser(new SACParserCSS3());
//			InputSource inputSource;
//			try {
//				inputSource = new InputSource(new FileReader(formatCodeStylesCssFileNameWithPath));
//				CSSStyleSheet cssStyleSheet = cssomParser.parseStyleSheet(inputSource, null, null);
//				
//				
//				CSSRuleList rules = cssStyleSheet.getCssRules();
//				for (int i = 0; i < rules.getLength(); i++) {
//				    final CSSRule rule = rules.item(i);
//				    
//				    if (rule instanceof CSSStyleRule) {
//					    CSSStyleRule cssStyleRule = (CSSStyleRule) rule;
//					    //System.out.println(cssStyleRule.getSelectorText());
//					    if (cssStyleRule.getSelectorText().equals(".hljs") || cssStyleRule.getSelectorText().contains(".hljs,")) {
//					    
//					    	String pre = "";
//					    	String background = cssStyleRule.getStyle().getPropertyValue("background");
//					    	if (!background.equals("")) {
//					    		pre = pre + "background: " + background + ";";
//					    	}
//					    	String color = cssStyleRule.getStyle().getPropertyValue("color");
//					    	if (!color.equals("")) {
//					    		pre = pre + "color: " + color + ";";
//					    	}
//					    	
//					    	System.out.println("formatCodeStylesPre.put(FormatCodeStyle." + formatCodeStyle + ", \"" + pre + "\");");
//					    }
//				    }
//				}
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//		}
		
		return formatOptionsCss;
	}

	/** Get the options as Base64 text */
	public String getBase64FormatOptionsCss() {
		
		String formatOptionsCss = getFormatOptionsCss();

		return new String(Base64.getEncoder().encode(formatOptionsCss.getBytes()));
	}

	/** Open external file in IDE */
	private void openExternalFile(String fileNameWithPath) {

		IFileStore fileStore = EFS.getLocalFileSystem().getStore(new Path(fileNameWithPath));
		// fileStore = fileStore.getChild(names[i]);
		if (!fileStore.fetchInfo().isDirectory() && fileStore.fetchInfo().exists()) {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			try {
				IDE.openEditor(page, new FileStoreEditorInput(fileStore), "org.eclipse.ui.genericeditor.GenericEditor");
			} catch (PartInitException partInitException) {
				L.e("openExternalFile", partInitException);
			}
		}
	}

	/** Close external file opened in IDE */
	private void closeExternalFile(String fileNameWithPath) {

		String fileName = fileNameWithPath.substring(F.getFileFolderName(fileNameWithPath).length() + 1);
		
		IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
		
		for (IEditorReference editorReference : workbenchPage.getEditorReferences()) {
			if (editorReference.getName().equals(fileName)) {
				try {
					String fullPath = java.nio.file.Path.of(editorReference.getEditorInput().getAdapter(FileStoreEditorInput.class).getURI()).toString();
					if (fullPath.equals(fileNameWithPath)) {
						workbenchPage.closeEditor(editorReference.getEditor(true), true);
					}
				} catch (PartInitException partInitException) {
					/* ILB */
				}
			}
//			System.out.println(java.nio.file.Path.of(editorReference.getEditorInput().getAdapter(FileStoreEditorInput.class).getURI()));
//			System.out.println(editorReference);
		};
	}

	/** Put in browser what is in editor */
	public void refresh() {
		//System.out.println("refresh " + getMarkdownText());
		// TODO where is the index
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

	/** Select and reveal Markdown text editor page */
	public void activateMarkdownTextEditorPage() {
		if (this.getActivePage() != markdownTextEditorPageIndex) {
			this.setActiveEditor(getEditor(markdownTextEditorPageIndex));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAdapter(Class<T> adapter) {
		
		if (IContentOutlinePage.class.equals(adapter)) {
			
//			if (markdownOutlinePage == null) {
//				markdownOutlinePage= new MarkdownOutlinePage(markdownTextEditor.getDocumentProvider(), this);
//				if (getEditorInput() != null)
//					markdownOutlinePage.setInput(getEditorInput());
//			}
			return (T) markdownOutlinePage;
		}
		return super.getAdapter(adapter);
	}

	
//	/** Saves the multi-page editor's document as another file */
//	public void doSaveAs() {
//		textEditorPart.doSaveAs();
//		setPageText(textEditorPageIndex, textEditorPart.getTitle());
//		setInput(textEditorPart.getEditorInput());
//	}
	
	/** Saves the forms editor's document */
	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	public MarkdownPreferences getPreferences() {
		return markdownPreferences;
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

	public MarkdownOutlinePage getMarkdownOutlinePage() {
		return markdownOutlinePage;
	}

//	/**
//	 * @param sourcePage The sourcePage to set.
//	 */
//	public void setSourcePage(ITextEditor sourcePage) {
//		this.markdownTextEditor = sourcePage;
//		this.sourcePageIndex = pages.indexOf(sourcePage);
//	}
}
