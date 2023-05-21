package org.emdepub.ai_md.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level=AccessLevel.PRIVATE)
public class AIMdReferenceText {

	String id;
	
	int referenceLine;
	int textStartLine;
	int textEndLine;
}
