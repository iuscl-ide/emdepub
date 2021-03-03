package org.emdepub.epub_project.model.opf;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class OPF_package_manifest_item {

	@JacksonXmlProperty(isAttribute = true)
	public String href = "item_href";

	@JacksonXmlProperty(isAttribute = true)
	public String id = "item_id";

	@JacksonXmlProperty(isAttribute = true, localName = "media-type")
	public String media_type = "item_media-type";

	@JacksonXmlProperty(isAttribute = true)
	public String properties = "item_properties";
}
