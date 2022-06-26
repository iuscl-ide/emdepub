/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.toml.editor.language;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.emdepub.toml.editor.engine.TomlContentAssistEngine;

/** TOML content assist processor */
public class TomlContentAssistProcessor implements IContentAssistProcessor {

	/** Engine, Flexmark */
	private TomlContentAssistEngine tomlContentAssistEngine;
	
	/** One per editor */
	public TomlContentAssistProcessor() {
		super();
		
		tomlContentAssistEngine = new TomlContentAssistEngine();
	}

	/** Trough here */
	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer textViewer, int offset) {
		
		Document document = (Document) textViewer.getDocument();
		String tomlString = document.get();
		String lineDelimiter = document.getDefaultLineDelimiter();
		
		return tomlContentAssistEngine.runContentAssist(tomlString, offset, lineDelimiter);
	}

	/** Local information, ignored */
	@Override
	public IContextInformation[] computeContextInformation(ITextViewer textViewer, int offset) { return null; }

	/** Only the list */
	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {

		return tomlContentAssistEngine.getAutoActivationCharacters();
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
		
		return tomlContentAssistEngine.getErrorMessage();
	}

}
