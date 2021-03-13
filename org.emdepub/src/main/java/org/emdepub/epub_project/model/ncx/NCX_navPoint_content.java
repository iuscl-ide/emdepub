package org.emdepub.epub_project.model.ncx;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class NCX_navPoint_content {

	@JacksonXmlProperty(isAttribute = true)
	public String src;
}
