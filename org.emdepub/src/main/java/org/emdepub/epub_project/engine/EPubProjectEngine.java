package org.emdepub.epub_project.engine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.UUID;

import org.emdepub.common.resources.CR;
import org.emdepub.common.utils.CU;
import org.emdepub.epub_project.editor.EPubProjectEditor;
import org.emdepub.epub_project.model.EPUB_project;
import org.emdepub.epub_project.model.EPUB_project_manifest_item;
import org.emdepub.epub_project.model.EPUB_project_spine_item;
import org.emdepub.epub_project.model.EPUB_project_toc_item;
import org.emdepub.epub_project.model.ncx.NCX;
import org.emdepub.epub_project.model.ncx.NCX_navPoint;
import org.emdepub.epub_project.model.opf.OPF_package;
import org.emdepub.epub_project.model.opf.OPF_package_manifest_item;
import org.emdepub.epub_project.model.opf.OPF_package_metadata;
import org.emdepub.epub_project.model.opf.OPF_package_metadata_dc;
import org.emdepub.epub_project.model.opf.OPF_package_metadata_meta;
import org.emdepub.epub_project.model.opf.OPF_package_spine;
import org.emdepub.epub_project.model.opf.OPF_package_spine_itemref;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import lombok.SneakyThrows;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;

public class EPubProjectEngine {

	private static final String s = CU.S;

	private static final String ePub_mimetype = CR.getTextResourceAsString("texts/epub-mimetype");
	private static final String ePub_container = CR.getTextResourceAsString("texts/epub-container.xml");

	private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

	private static final XmlMapper xmlMapper = new XmlMapper();

	/** Generate ePub */
	@SneakyThrows(IOException.class)
	public static void generateBook(EPubProjectEditor ePubProjectEditor) {
		
		EPUB_project ePubProject = ePubProjectEditor.getEPubProject();
		
		Path targetCommonFolderName = Paths.get(ePubProject.targetFolderNameWithFullPath);
		
		String book = CU.findFileNameWithoutExtension(ePubProjectEditor.getSourceEPubProjectFilePathAndName());
		Path tempBookRootFolder = Files.createTempDirectory(targetCommonFolderName, "temp-ePub-book_" + book + "_");
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			CU.deleteFolderAndItsContents(tempBookRootFolder.toString());
		}));

		/* mimetype */
		CU.saveStringToFile(ePub_mimetype, Paths.get(tempBookRootFolder.toString(), "mimetype").toString());
		
		/* META-INF/container.xml */
		Path tempBookMetaInfoFolder = Paths.get(tempBookRootFolder.toString(), "META-INF");
		CU.createFoldersIfNotExists(tempBookMetaInfoFolder.toString());
		String container = ePub_container.replace("{{opf}}", ePubProject.opfFileNameWithRelativePath);
		CU.saveStringToFile(container, Paths.get(tempBookMetaInfoFolder.toString(), "container.xml").toString());
		
		
		/* OPF */
		Path opfFile = Paths.get(tempBookRootFolder.toString(), ePubProject.opfFileNameWithRelativePath);
		CU.createFoldersIfNotExists(CU.findFileFolder(opfFile.toString()));
		Path tempBookRootFolderManifest = opfFile.getParent();
		
		OPF_package opfPackage = new OPF_package();
		
		/* metadata */
		OPF_package_metadata opfPackageMetadata = new OPF_package_metadata();
		opfPackage.metadata = opfPackageMetadata;

		/* metadata dc */
		OPF_package_metadata_dc date = new OPF_package_metadata_dc();
		date.text = simpleDateFormat.format(new Date());
		opfPackageMetadata.date = date;
		
		OPF_package_metadata_dc identifier = new OPF_package_metadata_dc();
		identifier.id = "uuid-id";
		identifier.text = ePubProject.metadata_identifier;
		opfPackageMetadata.identifier = identifier;
		
		OPF_package_metadata_dc title = new OPF_package_metadata_dc();
		title.id = "title";
		title.text = ePubProject.metadata_title;
		opfPackageMetadata.title = title;

		if (ePubProject.metadata_authors.contains(";")) {
			String authors[] = ePubProject.metadata_authors.split(";");
			int index = 1;
			for (String author : authors) {
				author = author.trim().strip();
				if (author.length() > 0) {
					OPF_package_metadata_dc creator = new OPF_package_metadata_dc();
					creator.id = "author" + index++;
					creator.text = author;
					opfPackageMetadata.creator.add(creator);
				}
			}
		}
		else {
			OPF_package_metadata_dc creator = new OPF_package_metadata_dc();
			creator.id = "author";
			creator.text = ePubProject.metadata_authors;
			opfPackageMetadata.creator.add(creator);
		}

		OPF_package_metadata_dc description = new OPF_package_metadata_dc();
		description.text = ePubProject.metadata_description;
		opfPackageMetadata.description = description;

		/* metadata meta */
		OPF_package_metadata_meta coverMeta = new OPF_package_metadata_meta();
		coverMeta.name = "cover";
		coverMeta.content = ePubProject.metadata_cover;
		opfPackageMetadata.meta.add(coverMeta);

		
		/* Manifest */
		opfPackage.item = new ArrayList<>();
		
		for (EPUB_project_manifest_item projectManifestItem : ePubProject.manifestItems) {
			
			OPF_package_manifest_item opfManifestItem = new OPF_package_manifest_item();
			if (CU.isEmpty(projectManifestItem.itemFileRelativePath)) {
				opfManifestItem.href = projectManifestItem.itemFileName;
			}
			else {
				opfManifestItem.href = projectManifestItem.itemFileRelativePath.replace(s, "/") + "/" + projectManifestItem.itemFileName;
			}
			opfManifestItem.id = projectManifestItem.itemFileId;
			opfManifestItem.media_type = projectManifestItem.itemFileMediaType;
			if (!CU.isEmpty(projectManifestItem.itemFileProperties)) {
				opfManifestItem.properties = projectManifestItem.itemFileProperties;	
			}
			
			opfPackage.item.add(opfManifestItem);
		}


		/* TOC */
		String tocFileNameWithoutPath = "toc.ncx";
		String tocFileManifestID = "ncx_id";
		NCX ncx = new NCX();
		
		ncx.docAuthor.text.text = ePubProject.metadata_authors;
		ncx.docTitle.text.text = ePubProject.metadata_title;
		
		int playOrder = 1;
		for (EPUB_project_toc_item tocItem : ePubProject.tocItems) {
			
			NCX_navPoint navPoint = new NCX_navPoint();
			
			navPoint._class = "chapter";
			navPoint.id = UUID.randomUUID().toString();
			navPoint.playOrder = "" + playOrder++;
			
			navPoint.navLabel.text.text = tocItem.itemText;
			navPoint.content.src = tocItem.itemSrc;
			
			ncx.navMap.navPoint.add(navPoint);
		}
		
		OPF_package_manifest_item opfTocManifestItem = new OPF_package_manifest_item();
		opfTocManifestItem.href = tocFileNameWithoutPath;
		opfTocManifestItem.id = tocFileManifestID;
		opfTocManifestItem.media_type = "application/x-dtbncx+xml";
		opfPackage.item.add(opfTocManifestItem);
		
		String ncxXml = xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(ncx);
		CU.saveStringToFile(ncxXml, Paths.get(tempBookRootFolderManifest.toString(), tocFileNameWithoutPath).toString());

		
		/* Spine */
		opfPackage.spine = new OPF_package_spine();
		opfPackage.spine.itemref = new ArrayList<>();
		opfPackage.spine.toc = tocFileManifestID;
		
		for (EPUB_project_spine_item projectSpineItem : ePubProject.spineItems) {

			OPF_package_spine_itemref opfSpineItem = new OPF_package_spine_itemref();
			opfSpineItem.idref = projectSpineItem.itemManifestItemFileId;
		
			opfPackage.spine.itemref.add(opfSpineItem);
		}
		
		
		String opfXml = xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(opfPackage);
		CU.saveStringToFile(opfXml, opfFile.toString());
		
		/* Copy manifest files */
		Path opfRootRelativePath = Paths.get(ePubProject.opfFileNameWithRelativePath).getParent();
		String opfRootRelativeFolder = opfRootRelativePath == null ? "" : opfRootRelativePath.toString();
		String rootFolderManifest = Paths.get(ePubProject.rootFolderNameWithFullPath, opfRootRelativeFolder).toString();
		
		
		HashSet<String> newRelativeFoldersSet = new HashSet<>();
		for (EPUB_project_manifest_item manifestItem : ePubProject.manifestItems) {
			newRelativeFoldersSet.add(manifestItem.itemFileRelativePath);
		}
		for (String newRelativeFolder : newRelativeFoldersSet) {
			Path targetFolderPath = Paths.get(tempBookRootFolderManifest.toString(), newRelativeFolder);
			if (!Files.exists(targetFolderPath)) {
				CU.createFoldersIfNotExists(targetFolderPath.toString());
			}
		}
		for (EPUB_project_manifest_item manifestItem : ePubProject.manifestItems) {
			Path sourcePath = Paths.get(rootFolderManifest, manifestItem.itemFileRelativePath, manifestItem.itemFileName);
			Path targetPath = Paths.get(tempBookRootFolderManifest.toString(), manifestItem.itemFileRelativePath, manifestItem.itemFileName);
			CU.copyFile(sourcePath.toString(), targetPath.toString());
		}
		
		/* ZIP */
		ZipFile bookZipFile = new ZipFile(Paths.get(ePubProject.targetFolderNameWithFullPath, book + ".epub").toString());
		ZipParameters zipParameters = new ZipParameters();
		zipParameters.setIncludeRootFolder(false);
		bookZipFile.addFolder(tempBookRootFolder.toFile(), zipParameters);
		
		CU.deleteFolderAndItsContents(tempBookRootFolder.toString());
	}
}
