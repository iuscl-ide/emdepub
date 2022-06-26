/* EMDEPUB Eclipse Plugin - emdepub.org */
package org.emdepub.toml.editor.engine;

import java.util.LinkedHashMap;

import org.emdepub.toml.editor.engine.TomlCompletionProposal.TomlCompletionProposalKey;

/** For TOML */
public class TomlResSupport {

	private LinkedHashMap<TomlCompletionProposalKey, TomlCompletionProposal> proposals = new LinkedHashMap<>();

	public LinkedHashMap<TomlCompletionProposalKey, TomlCompletionProposal> getProposals() {
		return proposals;
	}
}
