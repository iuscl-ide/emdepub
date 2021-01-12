package org.emdepub.ui.editor.md;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.emdepub.activator.L;
import org.emdepub.ui.editor.md.prefs.MarkdownHtmlGeneratorPrefs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;


public class MarkdownEditor extends FormEditor {

	/** The browser will display the Markdown HTML */
	private Browser viewerBrowser;
	private int browserViewerPageIndex;

	private ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);;

	
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
            			"window.mdUI.mdContent = '" + getMarkdownText() + "'; " +
            			"window.mdUI.cssName = '" + "github-markdown" + "'; " +
            			"window.mdUI.cssHighlightName = '" + "highlight/googlecode" + "'; " +
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
		System.out.println("addPages");
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

	private String findFilePathName() {
		
		IFile iFile = (IFile) markdownTextEditor.getEditorInput().getAdapter(IFile.class);
		return iFile.getLocation().toOSString();
	}

	private File findPrefsFile() {
		
		return new File(findFilePathName() + ".prefs.json");
	}

	public void savePrefs() {
		
		try {
			objectMapper.writeValue(findPrefsFile(), markdownHtmlGeneratorPrefs);
		} catch (IOException ioException) {
			L.e("savePrefs", ioException);
		}
	}
	
	public void exportAsHtml() {
		
    	String htmlText = (String) viewerBrowser.evaluate("window.mdUI = {}; " +
    			"window.mdUI.mdContent = '" + getMarkdownText() + "'; " +
    			"window.mdUI.cssName = '" + "github-markdown" + "'; " +
    			"window.mdUI.cssHighlightName = '" + "highlight/googlecode" + "'; " +
    			"window.mdUI.localBaseHref = '" + findLocalBaseHref() + "'; " +
    			"return exportHTML();");

    	System.out.println(htmlText);
		
//		try {
//			objectMapper.writeValue(findPrefsFile(), markdownHtmlGeneratorPrefs);
//		} catch (IOException ioException) {
//			L.e("exportAsHtml", ioException);
//		}
	}
	
	/** Get the Markdown as text */
	public String getMarkdownText() {
		
		String mdText = markdownTextEditor.getDocumentProvider().getDocument(markdownTextEditor.getEditorInput()).get();
		
		return new String(Base64.getEncoder().encode(mdText.getBytes()));
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

//	/**
//	 * @param sourcePage The sourcePage to set.
//	 */
//	public void setSourcePage(ITextEditor sourcePage) {
//		this.markdownTextEditor = sourcePage;
//		this.sourcePageIndex = pages.indexOf(sourcePage);
//	}
	
	
}
