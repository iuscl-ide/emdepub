package org.emdepub.ai_md.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(makeFinal=true, level=AccessLevel.PRIVATE)
public class AIMdReferences {

	List<AIMdReferenceText> references = new ArrayList<>(); 
}
