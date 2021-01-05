package org.emdepub.ui.editor.md;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.internal.genericeditor.ExtensionBasedTextEditor;
import org.eclipse.ui.texteditor.ITextEditor;

public class MarkdownEditor extends FormEditor {

	// private static final org.osgi.service.log.Logger log =
	// LoggerFactory.getLogger(MarkdownEditor.class);

	/** The browser will display the html markdown */
	private Browser viewerBrowser;
	
	private int browserViewerPageIndex;


	private IDocument sourceDocument;

	private int sourcePageIndex;

	ITextEditor sourcePage;

	/** Creates the viewer page of the multi-page editor */
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
		viewerBrowser.setText("html");
		viewerBrowser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		viewerBrowser.addStatusTextListener(new StatusTextListener() {
			
			@Override
			public void changed(StatusTextEvent statusTextEvent) {
				//MarkdownSemanticEPEditorContributor.getLinkField().setText(statusTextEvent.text);
			}
		});

		browserViewerPageIndex = addPage(viewerPageComposite);
		setPageText(browserViewerPageIndex, "Display");
	}

	
	
	@Override
	protected void addPages() {

		createBrowserViewerPage();
		
		if (sourcePage == null) {
			sourcePage = new ExtensionBasedTextEditor();
			ExtensionBasedTextEditor editor = (ExtensionBasedTextEditor) sourcePage;

			int dex;
			try {
				dex = addPage(sourcePage, getEditorInput());
				setPageText(dex, editor.getTitle());
				setSourcePage(sourcePage);
				sourceDocument = editor.getDocumentProvider().getDocument(editor.getEditorInput());
			} catch (PartInitException ex) {
				// log.error(ex.getMessage(), ex);
			}
		}
		// TODO Auto-generated method stub

	}

	@Override
	public void doSave(IProgressMonitor arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @param sourcePage The sourcePage to set.
	 */
	public void setSourcePage(ITextEditor sourcePage) {
		this.sourcePage = sourcePage;
		this.sourcePageIndex = pages.indexOf(sourcePage);
	}
}
