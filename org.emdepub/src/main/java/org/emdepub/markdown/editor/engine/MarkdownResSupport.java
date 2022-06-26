/* EMDEPUB Eclipse Plugin - emdepub.org */
package org.emdepub.markdown.editor.engine;

import java.util.LinkedHashMap;

import org.emdepub.markdown.editor.engine.MarkdownCompletionProposal.MarkdownCompletionProposalKey;

/** For TOML */
public class MarkdownResSupport {

	private LinkedHashMap<MarkdownCompletionProposalKey, MarkdownCompletionProposal> proposals = new LinkedHashMap<>();

	public LinkedHashMap<MarkdownCompletionProposalKey, MarkdownCompletionProposal> getProposals() {
		return proposals;
	}
}
