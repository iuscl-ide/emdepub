package org.emdepub.epub_project.model.opf;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

public class OPF_package_metadata_meta {

	@JacksonXmlProperty(isAttribute = true, localName = "property")
	public String property_;

	@JacksonXmlProperty(isAttribute = true)
	public String refines;

	@JacksonXmlProperty(isAttribute = true)
	public String content;

	@JacksonXmlProperty(isAttribute = true)
	public String name;
	
	
	@JacksonXmlText
	public String text;
}
