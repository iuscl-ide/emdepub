package org.emdepub.ai_md.repo.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AiMdRepoReferenceTextAction {

	String textActionName;
	
	String sourceText;
	
	String resultText;
}
