package org.emdepub.epub_project.model.ncx;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class NCX_navPoint {

	@JacksonXmlProperty(isAttribute = true, localName = "class")
	public String _class;
	
	@JacksonXmlProperty(isAttribute = true)
	public String id;

	@JacksonXmlProperty(isAttribute = true)
	public String playOrder;
	
	public NCX_navPoint_navLabel navLabel = new NCX_navPoint_navLabel();
	
	public NCX_navPoint_content content = new NCX_navPoint_content();
}
