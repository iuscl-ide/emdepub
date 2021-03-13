package org.emdepub.epub_project.model;

import java.util.ArrayList;

public class EPUB_project {

	public String rootFolderNameWithFullPath;
	public String targetFolderNameWithFullPath;
	public String opfFileNameWithRelativePath;
	
	public String splitStartExpression;
	public String splitSourceFileNameWithFullPath;
	public String splitTargetFileNamesWithFullPath;
	
	public Boolean manifestIDGuid = false;
	public Boolean manifestIDCounter = true;
	public String manifestIDPrefix = "";
	
	public String metadata_identifier = "";
	public String metadata_title = "";
	public String metadata_authors = "";
	public String metadata_cover = "";
	public String metadata_description = "";
	
	public final ArrayList<EPUB_project_manifest_item> manifestItems = new ArrayList<>();
	
	public final ArrayList<EPUB_project_spine_item> spineItems = new ArrayList<>();
	
	public final ArrayList<EPUB_project_toc_item> tocItems = new ArrayList<>();
}
