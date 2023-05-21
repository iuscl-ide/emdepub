/* EMDEPUB Eclipse Plugin - emdepub.org */
package org.emdepub.common.resources;

import java.util.LinkedHashMap;

import org.emdepub.ai_md.editor.content_assist.AiMdContentAssistProcessor.AiMdCompletionProposalKey;
import org.emdepub.common.editor.language.content_assist.CommonCompletionProposal;

/** For TOML */
public class AiMdResSupport {

	private LinkedHashMap<AiMdCompletionProposalKey, CommonCompletionProposal> proposals = new LinkedHashMap<>();

	public LinkedHashMap<AiMdCompletionProposalKey, CommonCompletionProposal> getProposals() {
		return proposals;
	}
}
