package org.emdepub.epub_project.model.opf;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class OPF_package_spine {

	@JacksonXmlProperty(isAttribute = true)
	public String toc;

	@JacksonXmlElementWrapper(useWrapping = false)
	public List<OPF_package_spine_itemref> itemref;
}
