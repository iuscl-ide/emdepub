/* EMDEPUB Eclipse Plugin - emdepub.org */
package org.emdepub.common.resources;

import java.util.LinkedHashMap;

import org.emdepub.common.editor.language.content_assist.CommonCompletionProposal;
import org.emdepub.markdown.editor.engine.MarkdownCompletionProposal.MarkdownCompletionProposalKey;

/** For TOML */
public class MarkdownResSupport {

	private LinkedHashMap<MarkdownCompletionProposalKey, CommonCompletionProposal> proposals = new LinkedHashMap<>();

	public LinkedHashMap<MarkdownCompletionProposalKey, CommonCompletionProposal> getProposals() {
		return proposals;
	}
}
