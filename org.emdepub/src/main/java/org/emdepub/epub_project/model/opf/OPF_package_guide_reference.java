package org.emdepub.epub_project.model.opf;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class OPF_package_guide_reference {

	@JacksonXmlProperty(isAttribute = true)
	public String href;

	@JacksonXmlProperty(isAttribute = true)
	public String title;

	@JacksonXmlProperty(isAttribute = true)
	public String type;
}
