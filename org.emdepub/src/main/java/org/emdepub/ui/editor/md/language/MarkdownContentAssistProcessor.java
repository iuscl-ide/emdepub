/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.ui.editor.md.language;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.emdepub.ui.editor.md.engine.MarkdownContentAssistEngine;

/** Markdown content assist processor */
public class MarkdownContentAssistProcessor implements IContentAssistProcessor {

	/** Engine, Flexmark */
	private MarkdownContentAssistEngine markdownContentAssistEngine;
	
	/** One per editor */
	public MarkdownContentAssistProcessor() {
		super();
		
		markdownContentAssistEngine = new MarkdownContentAssistEngine();
	}

	/** Trough here */
	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer textViewer, int offset) {
		
		Document document = (Document) textViewer.getDocument();
		String markdownString = document.get();
		String lineDelimiter = document.getDefaultLineDelimiter();
		
		return markdownContentAssistEngine.runContentAssist(markdownString, offset, lineDelimiter);
	}

	/** Local information, ignored */
	@Override
	public IContextInformation[] computeContextInformation(ITextViewer textViewer, int offset) { return null; }

	/** Only the list */
	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {

		return markdownContentAssistEngine.getAutoActivationCharacters();
	}

	/** Local information, ignored */
	@Override
	public char[] getContextInformationAutoActivationCharacters() { return null; }

	/** Validation information, ignored */
	@Override
	public IContextInformationValidator getContextInformationValidator() { return null; }

	/** ? */
	@Override
	public String getErrorMessage() {
		
		return markdownContentAssistEngine.getErrorMessage();
	}

}
