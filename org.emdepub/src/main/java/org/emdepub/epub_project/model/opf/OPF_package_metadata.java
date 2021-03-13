package org.emdepub.epub_project.model.opf;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "metadata")
public class OPF_package_metadata {

	@JacksonXmlProperty(isAttribute = true, localName = "xmlns:dc")
	public String xmlns_dc = "http://purl.org/dc/elements/1.1/";
	
	@JacksonXmlProperty(isAttribute = true, localName = "xmlns:opf")
	public String xmlns_opf = "http://www.idpf.org/2007/opf";

	@JacksonXmlProperty(localName = "dc:identifier")
	public OPF_package_metadata_dc identifier = new OPF_package_metadata_dc();
//	@JsonSetter(value = "identifier")
//	public void setFromXml_identifier(OPF_package_metadata_dc identifier) {
//		this.identifier = identifier;
//	}
	
	@JacksonXmlProperty(localName = "dc:title")
	public OPF_package_metadata_dc title = new OPF_package_metadata_dc();
//	@JsonSetter(value = "title")
//	public void setFromXml_title(OPF_package_metadata_dc title) {
//		this.title = title;
//	}

	@JacksonXmlElementWrapper(useWrapping = false)
	@JacksonXmlProperty(localName = "dc:language")
	public List<OPF_package_metadata_dc> language = new ArrayList<>();
//	@JsonSetter(value = "language")
//	public void setFromXml_language(OPF_package_metadata_dc language) {
//		this.language.add(language);
//	}

	@JacksonXmlProperty(localName = "dc:date")
	public OPF_package_metadata_dc date = new OPF_package_metadata_dc();
//	@JsonSetter(value = "date")
//	public void setFromXml_date(OPF_package_metadata_dc date) {
//		this.date = date;
//	}

	@JacksonXmlElementWrapper(useWrapping = false)
	@JacksonXmlProperty(localName = "dc:creator")
	public List<OPF_package_metadata_dc> creator = new ArrayList<>();
//	@JsonSetter(value = "creator")
//	public void setFromXml_creator(OPF_package_metadata_dc creator) {
//		this.creator.add(creator);
//	}

	@JacksonXmlProperty(localName = "dc:publisher")
	public OPF_package_metadata_dc publisher = new OPF_package_metadata_dc();
//	@JsonSetter(value = "publisher")
//	public void setFromXml_publisher(OPF_package_metadata_dc publisher) {
//		this.publisher = publisher;
//	}

	@JacksonXmlElementWrapper(useWrapping = false)
	public final List<OPF_package_metadata_meta> meta = new ArrayList<>();
//	@JsonSetter(value = "meta")
//	public void setFromXml_meta(OPF_package_metadata_meta meta) {
//		this.meta.add(meta);
//	}
	
	@JacksonXmlProperty(localName = "dc:description")
	public OPF_package_metadata_dc description = new OPF_package_metadata_dc();
//	@JsonSetter(value = "description")
//	public void setFromXml_description(OPF_package_metadata_dc description) {
//		this.description = description;
//	}
}
