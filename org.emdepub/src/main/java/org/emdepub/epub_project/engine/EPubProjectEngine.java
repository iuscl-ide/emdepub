package org.emdepub.epub_project.engine;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.emdepub.activator.F;
import org.emdepub.activator.L;
import org.emdepub.activator.R;
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

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;

public class EPubProjectEngine {

	/** Convenient call */
	public static <T> T c(Callable<T> theFunction) {
		try {
			return theFunction.call();
		} catch (Exception exception) {
			L.e("Exception", exception);
			throw new RuntimeException(exception);
		}
	}

	private static final String s = F.s;

	private static final String ePub_mimetype = R.getTextResourceAsString("texts/epub-mimetype");
	private static final String ePub_container = R.getTextResourceAsString("texts/epub-container.xml");

	private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

	private static final XmlMapper xmlMapper = new XmlMapper();

	/** Generate ePub */
	public static void generateBook(EPubProjectEditor ePubProjectEditor) {
		
		EPUB_project ePubProject = ePubProjectEditor.getEPubProject();
		
		Path targetCommonFolderName = Paths.get(ePubProject.targetFolderNameWithFullPath);
		
		String book = F.getFileNameWithoutExtension(ePubProjectEditor.getSourceEPubProjectFilePathAndName());
		Path tempBookRootFolder = c(() -> Files.createTempDirectory(targetCommonFolderName, "temp-ePub-book_" + book + "_"));
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			F.deleteFolderAndItsContents(tempBookRootFolder.toString());
		}));

		/* mimetype */
		F.saveStringToFile(ePub_mimetype, Paths.get(tempBookRootFolder.toString(), "mimetype").toString());
		
		/* META-INF/container.xml */
		Path tempBookMetaInfoFolder = Paths.get(tempBookRootFolder.toString(), "META-INF");
		F.createFoldersIfNotExists(tempBookMetaInfoFolder.toString());
		String container = ePub_container.replace("{{opf}}", ePubProject.opfFileNameWithRelativePath);
		F.saveStringToFile(container, Paths.get(tempBookMetaInfoFolder.toString(), "container.xml").toString());
		
		
		/* OPF */
		Path opfFile = Paths.get(tempBookRootFolder.toString(), ePubProject.opfFileNameWithRelativePath);
		F.createFoldersIfNotExists(F.getFileFolder(opfFile.toString()));
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
			if (F.isEmpty(projectManifestItem.itemFileRelativePath)) {
				opfManifestItem.href = projectManifestItem.itemFileName;
			}
			else {
				opfManifestItem.href = projectManifestItem.itemFileRelativePath.replace(s, "/") + "/" + projectManifestItem.itemFileName;
			}
			opfManifestItem.id = projectManifestItem.itemFileId;
			opfManifestItem.media_type = projectManifestItem.itemFileMediaType;
			if (!F.isEmpty(projectManifestItem.itemFileProperties)) {
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
		
		String ncxXml = c(() -> xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(ncx));
		F.saveStringToFile(ncxXml, Paths.get(tempBookRootFolderManifest.toString(), tocFileNameWithoutPath).toString());

		
		/* Spine */
		opfPackage.spine = new OPF_package_spine();
		opfPackage.spine.itemref = new ArrayList<>();
		opfPackage.spine.toc = tocFileManifestID;
		
		for (EPUB_project_spine_item projectSpineItem : ePubProject.spineItems) {

			OPF_package_spine_itemref opfSpineItem = new OPF_package_spine_itemref();
			opfSpineItem.idref = projectSpineItem.itemManifestItemFileId;
		
			opfPackage.spine.itemref.add(opfSpineItem);
		}
		
		
		String opfXml = c(() -> xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(opfPackage));
		F.saveStringToFile(opfXml, opfFile.toString());
		
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
				F.createFoldersIfNotExists(targetFolderPath.toString());
			}
		}
		for (EPUB_project_manifest_item manifestItem : ePubProject.manifestItems) {
			Path sourcePath = Paths.get(rootFolderManifest, manifestItem.itemFileRelativePath, manifestItem.itemFileName);
			Path targetPath = Paths.get(tempBookRootFolderManifest.toString(), manifestItem.itemFileRelativePath, manifestItem.itemFileName);
			F.copyFile(sourcePath.toString(), targetPath.toString());
		}
		
		/* ZIP */
		ZipFile bookZipFile = new ZipFile(Paths.get(ePubProject.targetFolderNameWithFullPath, book + ".epub").toString());
		ZipParameters zipParameters = new ZipParameters();
		zipParameters.setIncludeRootFolder(false);
		c(() -> { bookZipFile.addFolder(tempBookRootFolder.toFile(), zipParameters); return null; });
		
		F.deleteFolderAndItsContents(tempBookRootFolder.toString());
	}
}
