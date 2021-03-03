package org.emdepub.epub_project.model.opf;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "package")
public class OPF_package {

	@JacksonXmlProperty(isAttribute = true)
	public String xmlns = "http://www.idpf.org/2007/opf";
	
	@JacksonXmlProperty(isAttribute = true, localName = "unique-identifier")
	public String uniqueIdentifier;

	@JacksonXmlProperty(isAttribute = true)
	public String version;

	public OPF_package_metadata metadata= new OPF_package_metadata();
	
	@JacksonXmlElementWrapper(useWrapping = true, localName = "manifest")
	public List<OPF_package_manifest_item> item;
	
	public OPF_package_spine spine;

	@JacksonXmlElementWrapper(useWrapping = true, localName = "guide")
	public List<OPF_package_guide_reference> reference;
}
