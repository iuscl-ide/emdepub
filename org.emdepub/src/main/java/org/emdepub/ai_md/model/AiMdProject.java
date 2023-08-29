package org.emdepub.ai_md.model;

import java.nio.file.Path;
import java.nio.file.Paths;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Getter
public class AiMdProject {

	Path rootFolderPath;
	Path projectFilePath;
	
	public Path getTargetFolderPath() {
		
		return Paths.get(rootFolderPath.toAbsolutePath().toString(), "ai-target");
	}
	
}
