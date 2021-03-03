package org.emdepub.epub_project.model.opf;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class OPF_package_spine_itemref {

	@JacksonXmlProperty(isAttribute = true)
	public String idref;

	@JacksonXmlProperty(isAttribute = true)
	public String linear;
}
