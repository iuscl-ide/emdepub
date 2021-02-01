/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.activator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Serialization, files */
public class S {

	/** Jackson */
	private static final ObjectMapper jsonEngine = new ObjectMapper();

	/** JSON string to object */
	public static <T> T loadObjectFromJsonString(String jsonString, Class<T> clazz) {
		
		try {
			return (T) jsonEngine.readValue(jsonString, clazz);	
		}
		catch (JsonMappingException jsonMappingException) {
			L.e("loadObjectFromJsonString, jsonString: " + jsonString, jsonMappingException);
			throw new E(jsonMappingException);
		}
		catch (JsonProcessingException jsonProcessingException) {
			L.e("loadObjectFromJsonString, jsonString: " + jsonString, jsonProcessingException);
			throw new E(jsonProcessingException);
		}
	}

	/** Object to JSON string */
	public static <T> String saveObjectToJsonString(T t) {
		
		try {
//			FilterProvider filters = new SimpleFilterProvider().addFilter("myFilter", MarkdownHtmlGeneratorPrefs.propertyFilter);
//			return jsonEngine.writerWithDefaultPrettyPrinter().with(filters).writeValueAsString(t);
			return jsonEngine.writerWithDefaultPrettyPrinter().writeValueAsString(t);
		}
		catch (JsonProcessingException jsonProcessingException) {
			L.e("saveObjectToJsonString, t: " + t, jsonProcessingException);
			throw new E(jsonProcessingException);
		}
	}

	/** JSON file to object */
	public static <T> T loadObjectFromJsonFileName(String jsonFileName, Class<T> clazz) {
		
		return loadObjectFromJsonString(F.loadFileInString(jsonFileName), clazz);
	}

	/** Object to JSON file */
	public static <T> void saveObjectToJsonFileName(T t, String jsonFileName) {
		
		F.saveStringToFile(saveObjectToJsonString(t), jsonFileName);
	}
}
