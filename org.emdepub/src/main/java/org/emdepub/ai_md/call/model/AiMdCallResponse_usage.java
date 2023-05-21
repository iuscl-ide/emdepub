package org.emdepub.ai_md.call.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AiMdCallResponse_usage {

	int prompt_tokens;
	
	int completion_tokens;
	
	int total_tokens;
}
