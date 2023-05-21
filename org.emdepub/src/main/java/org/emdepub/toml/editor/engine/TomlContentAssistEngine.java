/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.toml.editor.engine;

import java.util.LinkedHashMap;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.emdepub.common.resources.CR;
import org.emdepub.toml.editor.engine.TomlCompletionProposal.TomlCompletionProposalKey;

/** TOML visitors engine */
public class TomlContentAssistEngine {

	/** For now */
	private static final char[] NO_CHARS = new char[0];

	/** Default proposals */
	private static final LinkedHashMap<TomlCompletionProposalKey, TomlCompletionProposal> defaultProposals;
	private static final TomlCompletionProposal[] completionProposals;
	
	/** Fill default proposals */
	static {
		defaultProposals = CR.getTomlCompletionProposals();
		completionProposals = new TomlCompletionProposal[defaultProposals.size()];
	}

	/** Find offset proposals */
	public ICompletionProposal[] runContentAssist(String markdownString, int offset, String lineDelimiter) {

		int index = 0;
		int lineDelimiterChars = lineDelimiter.length();
		for (TomlCompletionProposal completionProposal : defaultProposals.values()) {
			completionProposal.setReplacementString(completionProposal.getReplacementString().replaceAll("\\CR", lineDelimiter));
			completionProposal.setReplacementOffset(offset);
			completionProposal.setCursorPosition(completionProposal.getCursorPositionChars() +
					completionProposal.getCursorPositionLineDelimiters() * lineDelimiterChars);
			completionProposals[index] = completionProposal;
			index++;
		}
		
		return completionProposals;
	}
	
	/** For now, then #, | */
	public char[] getAutoActivationCharacters() {
	
		return NO_CHARS;
	}
	
	/** ? */
	public String getErrorMessage() {
		
		return "Content assist error";
	}
}
