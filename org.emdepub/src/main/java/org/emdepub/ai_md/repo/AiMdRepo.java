package org.emdepub.ai_md.repo;

import org.emdepub.ai_md.repo.model.AiMdRepoReferences;
import org.emdepub.common.utils.CU;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class AiMdRepo {

	final String repoFileName;
	
	@Getter
	final AiMdRepoReferences repoReferences;
	
	private AiMdRepo(String aiMdFileName) {
		super();
		
		String folderName = CU.findFileFolder(aiMdFileName);
		String fileName = CU.findFileNameWithoutExtension(aiMdFileName);
		
		repoFileName = folderName + CU.S + fileName + ".ai-md.repo.json";
		
		if (CU.findIfFileExists(repoFileName)) {
			repoReferences = CU.jsonDeserializeFromFile(repoFileName, AiMdRepoReferences.class); 
		}
		else {
			repoReferences = new AiMdRepoReferences();
			saveRepo();
		}
	}

	public static AiMdRepo loadOrCreateRepo(String aiMdFileName) {
		
		return new AiMdRepo(aiMdFileName);
	}
	
	public void saveRepo() {
		
		CU.jsonSerializeToFile(repoFileName, repoReferences);
	}
}
