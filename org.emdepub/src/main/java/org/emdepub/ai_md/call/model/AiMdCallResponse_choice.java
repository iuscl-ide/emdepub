package org.emdepub.ai_md.call.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AiMdCallResponse_choice {

	AiMdCallResponse_message message;
	
	String finish_reason;
	
	int index;
}
