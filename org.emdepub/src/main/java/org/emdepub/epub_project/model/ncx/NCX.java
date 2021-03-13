package org.emdepub.epub_project.model.ncx;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "ncx")
public class NCX {

	@JacksonXmlProperty(isAttribute = true)
	public String xmlns = "http://www.daisy.org/z3986/2005/ncx/";

	@JacksonXmlProperty(isAttribute = true)
	public String version = "2005-1";
	
	public NCX_docTitle docTitle = new NCX_docTitle();

	public NCX_docAuthor docAuthor = new NCX_docAuthor();

	public NCX_navMap navMap = new NCX_navMap();
}
