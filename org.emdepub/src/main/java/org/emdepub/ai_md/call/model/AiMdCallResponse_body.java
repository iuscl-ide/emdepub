package org.emdepub.ai_md.call.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AiMdCallResponse_body {

	String id;
	
	String object;
	
	long created;
	
	String model;
	
	AiMdCallResponse_usage usage;
	
	List<AiMdCallResponse_choice> choices = new ArrayList<>();
}
