package org.emdepub.epub_project.model.ncx;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

public class NCX_navMap {

	@JacksonXmlElementWrapper(useWrapping = false)
	public List<NCX_navPoint> navPoint = new ArrayList<>();
}
