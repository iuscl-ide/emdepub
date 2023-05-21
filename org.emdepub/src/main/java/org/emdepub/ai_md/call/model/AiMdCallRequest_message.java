package org.emdepub.ai_md.call.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AiMdCallRequest_message {

	String role;
	
	String content;
}
