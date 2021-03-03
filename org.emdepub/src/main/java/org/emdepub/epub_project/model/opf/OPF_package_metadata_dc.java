package org.emdepub.epub_project.model.opf;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

public class OPF_package_metadata_dc {

	@JacksonXmlProperty(isAttribute = true)
	public String id;

	@JacksonXmlText
	public String text;
}
