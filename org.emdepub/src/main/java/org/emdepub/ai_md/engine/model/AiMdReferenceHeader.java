package org.emdepub.ai_md.engine.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AiMdReferenceHeader {

	String id;
	
	String textAction;
}
