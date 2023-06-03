package org.emdepub.ai_md.preferences;

import org.emdepub.common.utils.CU;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(Include.NON_NULL)
@FieldNameConstants(asEnum = true, innerTypeName = "AiMdPreferencesAiProducerField", level = AccessLevel.PUBLIC)
public class AiMdPreferencesAiProducer {

	@JsonIgnore
	String aiPreferencesRootFolderFileName;
	
	@JsonProperty("Markdown Target Folder")
	String mdTargetFolder;
	
	@JsonProperty("HTML Target Folder")
	String htmlTargetFolder;
	
	private AiMdPreferencesAiProducer() {
		super();
	}

	public static AiMdPreferencesAiProducer create(String aiPreferencesRootFolderFileName) {
		
		AiMdPreferencesAiProducer aiMdPreferencesAiProducer = new AiMdPreferencesAiProducer();
		aiMdPreferencesAiProducer.setAiPreferencesRootFolderFileName(aiPreferencesRootFolderFileName);
		aiMdPreferencesAiProducer.reset();
		
		return aiMdPreferencesAiProducer;
	}

	public String findDefaultAiPreferencesRootFolderFileName() {
		return aiPreferencesRootFolderFileName;
	}

	public String findDefaultMdTargetFolder() {
		
		if (CU.findIfFileExists(aiPreferencesRootFolderFileName)) {
			AiMdPreferencesAiProducer deserialized = CU.yamlDeserializeFromFile(aiPreferencesRootFolderFileName, AiMdPreferencesAiProducer.class);
			
			return deserialized.getMdTargetFolder();
		}
		
		return "ai-target";
	}

	public String findDefaultHtmlTargetFolder() {

		if (CU.findIfFileExists(aiPreferencesRootFolderFileName)) {
			AiMdPreferencesAiProducer deserialized = CU.yamlDeserializeFromFile(aiPreferencesRootFolderFileName, AiMdPreferencesAiProducer.class);
			
			return deserialized.getHtmlTargetFolder();
		}
		
		return "ai-target";
	}

	public void reset() {

		AiMdPreferences.reset(this, AiMdPreferencesAiProducerField.values());
	}

	public void load(String preferencesFileName) {
		
		if (!CU.findIfFileExists(preferencesFileName)) {
			reset();

			return;
		}
		
		AiMdPreferencesAiProducer deserialized = CU.yamlDeserializeFromFile(preferencesFileName, AiMdPreferencesAiProducer.class);

		AiMdPreferences.load(this, AiMdPreferencesAiProducerField.values(), deserialized);
	}
	
	public void save(String preferencesFileName) {
		
		AiMdPreferencesAiProducer serialized = new AiMdPreferencesAiProducer();
		
		AiMdPreferences.save(this, AiMdPreferencesAiProducerField.values(), serialized, preferencesFileName);
	}
}
