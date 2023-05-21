package org.emdepub.ai_md.call.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AiMdCallRequest_body {

	String model;
	
	List<AiMdCallRequest_message> messages = new ArrayList<>();
}
