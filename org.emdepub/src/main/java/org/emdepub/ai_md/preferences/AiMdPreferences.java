package org.emdepub.ai_md.preferences;

import org.emdepub.common.utils.CU;

public class AiMdPreferences {

	public static void reset(Object sourceObject, Enum<?>[] enumFieldValues) {

		for (Enum<?> field : enumFieldValues) {

			String capFieldName = CU.capitalize(field.name());
			
			Object init = CU.val(sourceObject, "findDefault" + capFieldName);
			CU.exec(sourceObject, "set" + capFieldName, init);
		}
	}

	public static void load(Object sourceObject, Enum<?>[] enumFieldValues, Object deserialized) {

		for (Enum<?> field : enumFieldValues) {
			
			String capFieldName = CU.capitalize(field.name());
			
			Object value = CU.val(deserialized, "get" + capFieldName);
			Object init = CU.val(sourceObject, "findDefault" + capFieldName);
			
			if (value == null) {
				CU.exec(sourceObject, "set" + capFieldName, init);
			}
			else {
				CU.exec(sourceObject, "set" + capFieldName, value);
			}
		}
	}
	
	public static void save(Object sourceObject, Enum<?>[] enumFieldValues, Object serialized, String preferencesFileName) {
		
		boolean exists = false;
		
		for (Enum<?> field : enumFieldValues) {
			
			String capFieldName = CU.capitalize(field.name());
			
			Object value = CU.val(sourceObject, "get" + capFieldName);
			Object init = CU.val(sourceObject, "findDefault" + capFieldName);
			
			if (!value.equals(init)) {
				CU.exec(serialized, "set" + capFieldName, value);
				exists = true;
			}
		}

		if (!exists) {
			CU.deleteFile(preferencesFileName);
		}
		else {
			CU.yamlSerializeToFile(preferencesFileName, serialized);	
		}
	}
}
