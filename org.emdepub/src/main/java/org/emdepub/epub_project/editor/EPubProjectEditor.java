/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.epub_project.editor;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.emdepub.activator.F;
import org.emdepub.activator.R;
import org.emdepub.activator.UI;
import org.emdepub.epub_project.editor.wizard.EPubProjectGenerateIDsWizard;
import org.emdepub.epub_project.editor.wizard.EPubProjectModifyManifestItemWizard;
import org.emdepub.epub_project.editor.wizard.EPubProjectModifyTocItemWizard;
import org.emdepub.epub_project.editor.wizard.EPubProjectSplitInChaptersWizard;
import org.emdepub.epub_project.engine.EPubProjectEngine;
import org.emdepub.epub_project.model.EPUB_project;
import org.emdepub.epub_project.model.EPUB_project_manifest_item;
import org.emdepub.epub_project.model.EPUB_project_spine_item;
import org.emdepub.epub_project.model.EPUB_project_toc_item;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;

/** EPubProject multi-page editor */

public class EPubProjectEditor extends FormEditor {

	/* Columns */
	public static final int itemFileNameIndex = 0;
	public static final int itemFileRelativePathIndex = 1;
	public static final int itemFileIdIndex = 2;
	public static final int itemFileMediaTypeIndex = 3;
	public static final int itemFilePropertiesIndex = 4;

	public static final int itemSpineNameIndex = 0;
	public static final int itemSpineRelativePathIndex = 1;
	public static final int itemSpineIdIndex = 2;

	public static final int itemTocTextIndex = 0;
	public static final int itemTocSrcIndex = 1;

	
	/** Sort order */
	public static final Collator COLLATOR = Collator.getInstance(Locale.ENGLISH);
	
	public class ManifestItemsComparator implements Comparator<EPUB_project_manifest_item> {
		 
		private final boolean ascCompare[] = { true, true, true, true, true };
		private final ArrayList<Comparator<EPUB_project_manifest_item>> defaultComparators = new ArrayList<>();
	    private final ArrayList<Comparator<EPUB_project_manifest_item>> listComparators = new ArrayList<>();
	 
	    public ManifestItemsComparator() {
	    	defaultComparators.add(new Comparator<EPUB_project_manifest_item>() {
				@Override
				public int compare(EPUB_project_manifest_item o1, EPUB_project_manifest_item o2) {
					return ascCompare[0] ? COLLATOR.compare(o1.itemFileName, o2.itemFileName) :
						COLLATOR.compare(o2.itemFileName, o1.itemFileName);
				}
			});
	    	defaultComparators.add(new Comparator<EPUB_project_manifest_item>() {
				@Override
				public int compare(EPUB_project_manifest_item o1, EPUB_project_manifest_item o2) {
					return ascCompare[1] ? o1.itemFileRelativePath.compareTo(o2.itemFileRelativePath) :
						o2.itemFileRelativePath.compareTo(o1.itemFileRelativePath);
				}
			});
	    	defaultComparators.add(new Comparator<EPUB_project_manifest_item>() {
				@Override
				public int compare(EPUB_project_manifest_item o1, EPUB_project_manifest_item o2) {
					return ascCompare[2] ? o1.itemFileId.compareTo(o2.itemFileId) :
						o2.itemFileId.compareTo(o1.itemFileId);
				}
			});
	    	defaultComparators.add(new Comparator<EPUB_project_manifest_item>() {
				@Override
				public int compare(EPUB_project_manifest_item o1, EPUB_project_manifest_item o2) {
					return ascCompare[3] ? o1.itemFileMediaType.compareTo(o2.itemFileMediaType) :
						o2.itemFileMediaType.compareTo(o1.itemFileMediaType);
				}
			});
	    	defaultComparators.add(new Comparator<EPUB_project_manifest_item>() {
				@Override
				public int compare(EPUB_project_manifest_item o1, EPUB_project_manifest_item o2) {
					return ascCompare[4] ? o1.itemFileProperties.compareTo(o2.itemFileProperties) :
						o2.itemFileProperties.compareTo(o1.itemFileProperties);
				}
			});
	    }

	    public void setSortDirection(int index, boolean asc) {
	    			
   			ascCompare[itemFileNameIndex] = true;
	    	ascCompare[itemFileRelativePathIndex] = true;
	    	ascCompare[itemFileIdIndex] = true;
	    	ascCompare[itemFileMediaTypeIndex] = true;
	    	ascCompare[itemFilePropertiesIndex] = true;

	    	if (!asc) {
	    		ascCompare[index] = false;
	    	}
	    }

	    public void setSortOrder(int index1, int index2, int index3, int index4, int index5) {
	    	
	    	listComparators.clear();
	    	listComparators.add(defaultComparators.get(index1));
	    	listComparators.add(defaultComparators.get(index2));
	    	listComparators.add(defaultComparators.get(index3));
	    	listComparators.add(defaultComparators.get(index4));
	    	listComparators.add(defaultComparators.get(index5));
	    }
	 
	    @Override
	    public int compare(EPUB_project_manifest_item item1, EPUB_project_manifest_item item2) {
	        for (Comparator<EPUB_project_manifest_item> comparator : listComparators) {
	            int result = comparator.compare(item1, item2);
	            if (result != 0) {
	                return result;
	            }
	        }
	        return 0;
	    }
	}
	
	private ArrayList<Path> linearFileNamesWithPath = new ArrayList<>();
	
	public class LinearizeFileVisitor extends SimpleFileVisitor<Path> {
		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			linearFileNamesWithPath.add(file);

			return super.visitFile(file, attrs);
		}
	}
	
	private static final String s = F.s;
//	private static final String e = F.enter();

	
	private EPUB_project ePubProject;
	private String projectFileNameWithPath;
	
	private final ObjectMapper objectMapper = new ObjectMapper();
	
//	private final String defaultNamespace = "http://www.idpf.org/2007/opf";
//	private final Map<String, String> otherNamespaces = Collections.singletonMap("dc", "http://purl.org/dc/elements/1.1/");
//	private final XmlMapper xmlMapper = new XmlMapper(engine.new NamespaceXmlFactory(defaultNamespace, otherNamespaces));
	
	private FileControl rootFolderFileControl;
	private FileControl targetFolderFileControl;
	private TextControl opfFileTextControl;
	
	private TextControl metadataIdentifierTextControl;
	private TextControl metadataTitle;
	private TextControl metadataAuthors;
	private TextControl metadataCover;
	private TextControl metadataDescription;

		
	private Grid opfManifestGrid;
	private Font gridFont;
	private Font gridFontItalic;
	private Point opfManifestGridLastSize = new Point(0, 0);
	private int opfManifestGridSortColumnIndex = 0;
	private boolean opfManifestGridSortColumnAsc = true;
	private ArrayList<EPUB_project_manifest_item> gridManifestItems = new ArrayList<>();
	private ManifestItemsComparator manifestItemsComparator = new ManifestItemsComparator();

	private Grid opfSpineGrid;
	private Point opfSpineGridLastSize = new Point(0, 0);
	private ArrayList<EPUB_project_spine_item> gridSpineItems = new ArrayList<>();

	private Grid tocGrid;
	private Point tocGridLastSize = new Point(0, 0);
	private ArrayList<EPUB_project_toc_item> gridTocItems = new ArrayList<>();
	
	private Document document;
	
	private LinearizeFileVisitor linearizeFileVisitor = new LinearizeFileVisitor();
	
	/** Root folder */
	private String applicationRootFolder = "C:/";
	
	/** File control */
	private interface FileControl {
		
		public void reload();
		public String getCompleteFileName();
	}

	/** Text control */
	private interface TextControl {
		
		public void reload();
		public String getText();
	}
	
	/** The forms will display the EPub Project */
	private int ePubProjectFormsPageIndex;
	
	/** The EPubProject editor */
	private EPubProjectTextEditor ePubProjectTextEditor;
	private int ePubProjectTextEditorPageIndex;

	/** Creates the viewer page of the forms editor */
	@SneakyThrows({JsonMappingException.class, JsonProcessingException.class})
	private void createEPubProjectEditorPage() {
		
		/* load */
		projectFileNameWithPath = getSourceEPubProjectFilePathAndName();
		String json = F.loadFileInString(projectFileNameWithPath);
		if (F.isEmpty(json)) {
			ePubProject = new EPUB_project();
			
		}
		else {
			ePubProject = objectMapper.readValue(json, EPUB_project.class);	
		}
		//ePubProject.metadata_identifier = UUID.randomUUID().toString();
//		final int indentWidth = 24;
		final int labelWidth = 75;
//		final int buttonWidth = 92;
		
		final int marginSpacing = 7;
		final int verticalSpacing = 7;
		final int horizontalSpacing = 7;
		
		UI ui = new UI(true, Display.getCurrent());
		
		/* Page */
		Composite opfPageComposite = getContainer();
		FormToolkit formsToolkit = new FormToolkit(opfPageComposite.getDisplay());
		/* Form */
		Form opfForm = formsToolkit.createForm(opfPageComposite);
		String fileName = F.getFileName(projectFileNameWithPath);   //fileNameWithPath.substring(F.getFileFolderName(fileNameWithPath).length() + 1);
		opfForm.setText("\"" + fileName + "\" " + this.getTitle());
		opfForm.setImage(R.getImage("project"));
//		opfForm.setToolBarVerticalAlignment(SWT.BOTTOM);
		formsToolkit.decorateFormHeading(opfForm);

		/* Form Composite */
		Composite opfFormComposite = opfForm.getBody();
		GridLayout opfFormCompositeGridLayout = ui.createMarginsVerticalSpacingGridLayout(marginSpacing, verticalSpacing);
		opfFormComposite.setLayout(opfFormCompositeGridLayout);
		
		/* Top */
		Composite topComposite = new Composite(opfFormComposite, SWT.NULL);
		topComposite.setLayoutData(ui.createFillHorizontalGridData());
		GridLayout topCompositeGridLayout = ui.createColumnsSpacingGridLayout(2, horizontalSpacing * 2);
		topCompositeGridLayout.makeColumnsEqualWidth = true;
		topComposite.setLayout(topCompositeGridLayout);
		
		
		/* General Section */
		Section opfGeneralSection = formsToolkit.createSection(topComposite, Section.EXPANDED | Section.TWISTIE | Section.DESCRIPTION | Section.TITLE_BAR);
		opfGeneralSection.setLayoutData(ui.createTopAlignedFillHorizontalGridData());
		opfGeneralSection.setLayout(ui.createGridLayout());
		opfGeneralSection.setText("General");
		opfGeneralSection.setDescription("ePub project settings");

		ToolBarManager generalToolBarManager = new ToolBarManager(SWT.FLAT);
		ToolBar generalToolBar = generalToolBarManager.createControl(opfGeneralSection);
		
		/* Run */
		generalToolBarManager.add(UI.ActionFactory.create("org.emdepub.epub_project.editor.generalRunAction",
			"Run", "Generate ePub book", R.getImageDescriptor("run"), () -> {
				generateBook();
			}));

		/* Split */
		generalToolBarManager.add(UI.ActionFactory.create("org.emdepub.epub_project.editor.generalSplitAction",
			"Split", "Split a file in separate files (for chapters)", R.getImageDescriptor("XML_file"), () -> {
				splitSourceFile();
			}));

		generalToolBarManager.update(true);
		opfGeneralSection.setTextClient(generalToolBar);

		
		/* General Section Composite */
		Composite opfGeneralSectionComposite = formsToolkit.createComposite(opfGeneralSection, SWT.NULL);
		opfGeneralSectionComposite.setLayoutData(ui.createFillBothGridData());
		opfGeneralSectionComposite.setLayout(ui.createMarginTopVerticalSpacingGridLayout(verticalSpacing, verticalSpacing));
		opfGeneralSection.setClient(opfGeneralSectionComposite);
		
		rootFolderFileControl = addFileControl(ui, opfGeneralSectionComposite, "Root folder", labelWidth, null, true, "rootFolderNameWithFullPath");
		targetFolderFileControl = addFileControl(ui, opfGeneralSectionComposite, "Target folder", labelWidth, null, true, "targetFolderNameWithFullPath");
		opfFileTextControl = addTextControl(ui, opfGeneralSectionComposite, "OPF file name with relative path", null, 1, "opfFileNameWithRelativePath");
		
		/* Metadata Section */
		Section opfMetadataSection = formsToolkit.createSection(topComposite, Section.EXPANDED | Section.TWISTIE | Section.DESCRIPTION | Section.TITLE_BAR);
		opfMetadataSection.setLayoutData(ui.createTopAlignedFillHorizontalGridData());
		opfMetadataSection.setLayout(ui.createGridLayout());
		opfMetadataSection.setText("Metadata");
		opfMetadataSection.setDescription("ePub book properties");

		ToolBarManager metadataToolBarManager = new ToolBarManager(SWT.FLAT);
		ToolBar metadataToolBar = metadataToolBarManager.createControl(opfMetadataSection);
		
		/* Generate identifier UUID */
		metadataToolBarManager.add(UI.ActionFactory.create("org.emdepub.epub_project.editor.generateIdentifierUuidAction",
			"Generate identifier UUID", "Generate identifier UUID", R.getImageDescriptor("asterisk"), () -> {
				ePubProject.metadata_identifier = "urn:uuid:" + UUID.randomUUID().toString();
				metadataIdentifierTextControl.reload();
			}));
		
		metadataToolBarManager.update(true);
		opfMetadataSection.setTextClient(metadataToolBar);

		
		/* Metadata Section Composite */
		Composite opfMetadataSectionComposite = formsToolkit.createComposite(opfMetadataSection, SWT.NULL);
		opfMetadataSectionComposite.setLayoutData(ui.createFillBothGridData());
		opfMetadataSectionComposite.setLayout(ui.createMarginTopVerticalSpacingGridLayout(verticalSpacing, verticalSpacing));
		opfMetadataSection.setClient(opfMetadataSectionComposite);
		
		metadataIdentifierTextControl = addTextControl(ui, opfMetadataSectionComposite, "Identifier", labelWidth, 1, "metadata_identifier");
		metadataTitle = addTextControl(ui, opfMetadataSectionComposite, "Title", labelWidth, 1, "metadata_title");
		metadataAuthors = addTextControl(ui, opfMetadataSectionComposite, "Author(s)", labelWidth, 1, "metadata_authors");
		metadataCover = addTextControl(ui, opfMetadataSectionComposite, "Cover ID", labelWidth, 1, "metadata_cover");
		metadataDescription = addTextControl(ui, opfMetadataSectionComposite, "Description", labelWidth, 4, "metadata_description");

		
		/* Manifest Section */
		Section opfManifestSection = formsToolkit.createSection(opfFormComposite, Section.DESCRIPTION | Section.TITLE_BAR);
		opfManifestSection.setLayoutData(ui.createFillBothGridData());
		opfManifestSection.setLayout(ui.createGridLayout());
		opfManifestSection.setText("Manifest"); //$NON-NLS-1$
		opfManifestSection.setDescription("All the files to be included in the ePub book: documents, images, styles"); //$NON-NLS-1$
		
		ToolBarManager manifestToolBarManager = new ToolBarManager(SWT.FLAT);
		ToolBar manifestToolBar = manifestToolBarManager.createControl(opfManifestSection);
		
		/* Refresh manifest */
		manifestToolBarManager.add(UI.ActionFactory.create("org.emdepub.epub_project.editor.manifestRefreshAction",
			"Refresh manifest", "Refresh manifest from root files and folders", R.getImageDescriptor("refresh"), () -> {
				refreshManifest();
			}));
		
		/* Generate manifest IDs */
		manifestToolBarManager.add(UI.ActionFactory.create("org.emdepub.epub_project.editor.manifestGenerateIDsAction",
			"Generate manifest IDs", "Generate manifest IDs", R.getImageDescriptor("XSDIdentityConstraintDefinitionKey"), () -> {
				editManifestIDPreferences();
			}));

		/* Modify manifest item */
		manifestToolBarManager.add(UI.ActionFactory.create("org.emdepub.epub_project.editor.modifyManifestItemAction",
			"Manifest item properties", "Edit manifest item properties", R.getImageDescriptor("edit_template"), () -> {
				if (opfManifestGrid.getItemCount() == 0) {
					MessageDialog.openError(Display.getCurrent().getActiveShell(), "The manifest is empty", "Fill the manifest and select an item");
					return;
				}
				int gridItemIndex = opfManifestGrid.getSelectionIndex();
				if (gridItemIndex == -1) {
					MessageDialog.openError(Display.getCurrent().getActiveShell(), "No manifest item is selected", "Select an item");
					return;
				}
				GridItem gridItem = opfManifestGrid.getItem(gridItemIndex);
				modifyManifestItem(gridItem);
			}));
		
		manifestToolBarManager.update(true);
		opfManifestSection.setTextClient(manifestToolBar);

		/* Manifest Section Composite */
		Composite opfManifestSectionComposite = formsToolkit.createComposite(opfManifestSection, SWT.NULL);
		//ui.addDebug(opfManifestSectionComposite);
		opfManifestSectionComposite.setLayoutData(ui.createFillBothGridData());
		opfManifestSectionComposite.setLayout(ui.createMarginTopVerticalSpacingGridLayout(verticalSpacing, verticalSpacing));
		opfManifestSection.setClient(opfManifestSectionComposite);
		
		opfManifestGrid = new Grid(opfManifestSectionComposite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		gridFontItalic = opfManifestGrid.getFont();
		/* Italic */
		gridFontItalic = ui.newFontAttributes(opfManifestGrid.getFont(), SWT.ITALIC);

		opfManifestGrid.setLayoutData(ui.createFillBothGridData());
		
		opfManifestGrid.setHeaderVisible(true);
		//opfManifestGrid.setItemHeight(24);
		opfManifestGrid.setFileList(true);
		int opfManifestGridHorizontalScrollCompensate = opfManifestGrid.getVerticalBar().getSize().x;
		opfManifestGrid.setHorizontalScrollCompensate(opfManifestGridHorizontalScrollCompensate);
		
		opfManifestGrid.addControlListener((UI.OnResize) resizeEvent -> {
			
			Point filesListGridNewSize = opfManifestGrid.getSize();
			//L.p("resize " + filesListGridNewSize);
			if (opfManifestGridLastSize.equals(filesListGridNewSize)) {
				return;
			}
			if (opfManifestGridLastSize.x == filesListGridNewSize.x) {
				return;
			}
			opfManifestGridLastSize = filesListGridNewSize;
			int newWidth = filesListGridNewSize.x - (opfManifestGridHorizontalScrollCompensate + 4);
			
			opfManifestGrid.getColumn(itemFileNameIndex).setWidth((newWidth * 24) / 100);
			opfManifestGrid.getColumn(itemFileRelativePathIndex).setWidth((newWidth * 24) / 100);
			opfManifestGrid.getColumn(itemFileIdIndex).setWidth((newWidth * 24) / 100);
			opfManifestGrid.getColumn(itemFileMediaTypeIndex).setWidth((newWidth * 14) / 100);
			int actualWidth = IntStream.rangeClosed(0, 3).map(index -> opfManifestGrid.getColumn(index).getWidth()).sum();
			opfManifestGrid.getColumn(itemFilePropertiesIndex).setWidth(newWidth - actualWidth);
			
			if (opfManifestGrid.getItemCount() > 0) {
				opfManifestGrid.resetMeasures();
				opfManifestGrid.showSelection();
			}
		});
		
		opfManifestGrid.addMouseListener((UI.OnMouseDoubleClick) mouseEvent -> {
			
			GridItem gridItem = opfManifestGrid.getItem(new Point(mouseEvent.x, mouseEvent.y));
			if (gridItem != null) {
				modifyManifestItem(gridItem);
			}
		});
				
		opfManifestGrid.addTraverseListener(keyTraverseEvent -> {

			if (keyTraverseEvent.detail == SWT.TRAVERSE_RETURN) {
			    /* The user pressed Enter */ 
				if (opfManifestGrid.getItemCount() == 0) {
					MessageDialog.openError(Display.getCurrent().getActiveShell(), "The manifest is empty", "Fill the manifest and select an item");
					return;
				}
				int gridItemIndex = opfManifestGrid.getSelectionIndex();
				if (gridItemIndex == -1) {
					MessageDialog.openError(Display.getCurrent().getActiveShell(), "No manifest item is selected", "Select an item");
					return;
				}
				GridItem gridItem = opfManifestGrid.getItem(gridItemIndex);
				modifyManifestItem(gridItem);
			}
		});

		SelectionListener opfManifestGridColumnSelectionListener = (UI.OnSelection) selectionEvent -> {
				
			ArrayList<GridColumn> filesListGridColumns = new ArrayList<>(Arrays.asList(opfManifestGrid.getColumns()));
			int index = filesListGridColumns.indexOf((GridColumn) selectionEvent.item);
			for(GridColumn gridColumn : opfManifestGrid.getColumns()) {
				gridColumn.setSort(SWT.NONE);
			}
			if (opfManifestGridSortColumnIndex == index) {
				opfManifestGridSortColumnAsc = !opfManifestGridSortColumnAsc;
			}
			else {
				opfManifestGridSortColumnIndex = index;
				opfManifestGridSortColumnAsc = true;
			}
			opfManifestGrid.getColumn(index).setSort(opfManifestGridSortColumnAsc ? SWT.DOWN : SWT.UP);
			sortManifestGrid();
			loadManifestGrid();
		};
		
		ControlListener opfManifestGridColumnControlListener = (UI.OnResize) controlEvent -> {
				
			if (opfManifestGrid.getItemCount() > 0) {
				opfManifestGrid.resetMeasures();
				opfManifestGrid.showSelection();
			}
		};
		
		GridColumn gridColumn = new GridColumn(opfManifestGrid, SWT.NONE);
		gridColumn.setText("File name");
		gridColumn.setMinimumWidth(150);
		gridColumn.setWordWrap(true);
		gridColumn.setSort(SWT.DOWN);
		gridColumn.addSelectionListener(opfManifestGridColumnSelectionListener);
		gridColumn.addControlListener(opfManifestGridColumnControlListener);
		
		gridColumn = new GridColumn(opfManifestGrid, SWT.NONE);
		//gridColumn.setAlignment(SWT.RIGHT);
		gridColumn.setText("File path relative to OPF file");
		gridColumn.setMinimumWidth(150);
		gridColumn.addSelectionListener(opfManifestGridColumnSelectionListener);
		gridColumn.addControlListener(opfManifestGridColumnControlListener);

		gridColumn = new GridColumn(opfManifestGrid, SWT.NONE);
		gridColumn.setText("ID");
		gridColumn.setMinimumWidth(150);
		gridColumn.addSelectionListener(opfManifestGridColumnSelectionListener);
		gridColumn.addControlListener(opfManifestGridColumnControlListener);
		
		gridColumn = new GridColumn(opfManifestGrid, SWT.NONE);
		gridColumn.setText("Media type");
		gridColumn.setMinimumWidth(100);
		gridColumn.addSelectionListener(opfManifestGridColumnSelectionListener);
		gridColumn.addControlListener(opfManifestGridColumnControlListener);
		
		gridColumn = new GridColumn(opfManifestGrid, SWT.NONE);
		//gridColumn.setAlignment(SWT.RIGHT);
		gridColumn.setText("Properties");
		gridColumn.setMinimumWidth(100);
		gridColumn.addSelectionListener(opfManifestGridColumnSelectionListener);
		gridColumn.addControlListener(opfManifestGridColumnControlListener);
		
		gridManifestItems.clear();
		gridManifestItems.addAll(ePubProject.manifestItems);
		
		opfManifestGridSortColumnIndex = 0;
		opfManifestGridSortColumnAsc = true;
		sortManifestGrid();
		loadManifestGrid();

		/* Bottom */
		Composite bottomComposite = new Composite(opfFormComposite, SWT.NULL);
		bottomComposite.setLayoutData(ui.createFillHorizontalGridData());
		GridLayout bottomCompositeGridLayout = ui.createColumnsSpacingGridLayout(2, horizontalSpacing * 2);
		bottomCompositeGridLayout.makeColumnsEqualWidth = true;
		bottomComposite.setLayout(bottomCompositeGridLayout);
		
		/* Spine Section */
		Section opfSpineSection = formsToolkit.createSection(bottomComposite, Section.EXPANDED | Section.TWISTIE | Section.DESCRIPTION | Section.TITLE_BAR);
		opfSpineSection.setLayoutData(ui.createFillHorizontalGridData());
		opfSpineSection.setLayout(ui.createGridLayout());
		opfSpineSection.setText("Spine"); //$NON-NLS-1$
		opfSpineSection.setDescription("What documents will be presented to the reader and on which order"); //$NON-NLS-1$

		ToolBarManager spineToolBarManager = new ToolBarManager(SWT.FLAT);
		ToolBar spineToolBar = spineToolBarManager.createControl(opfSpineSection);

		/* Add to spine */
		spineToolBarManager.add(UI.ActionFactory.create("org.emdepub.epub_project.editor.spineAddAction", "Add to spine",
			"Add documents from manifest to spine", R.getImageDescriptor("add"), () -> {
				addToSpine();
			}));
		
		spineToolBarManager.add(UI.ActionFactory.create("org.emdepub.epub_project.editor.spineRemoveAction", "Remove from spine",
			"Remove documents from spine", R.getImageDescriptor("remove"), () -> {
				removeFromSpine();
			}));

		spineToolBarManager.add(UI.ActionFactory.create("org.emdepub.epub_project.editor.spineMoveUpAction", "Move up in spine",
			"Move documents up in spine, to the beginning of the book", R.getImageDescriptor("move-up"), () -> {
				moveInSpine(true);
			}));

		spineToolBarManager.add(UI.ActionFactory.create("org.emdepub.epub_project.editor.spineMoveDownAction", "Move down in spine",
			"Move documents down in spine, to the end of the book", R.getImageDescriptor("move-down"), () -> {
				moveInSpine(false);
			}));
		
		spineToolBarManager.update(true);
		opfSpineSection.setTextClient(spineToolBar);
		
		/* Spine Section Composite */
		Composite opfSpineSectionComposite = formsToolkit.createComposite(opfSpineSection, SWT.NULL);
		opfSpineSectionComposite.setLayoutData(ui.createFillBothGridData());
		opfSpineSectionComposite.setLayout(ui.createMarginTopVerticalSpacingGridLayout(verticalSpacing, verticalSpacing));
		opfSpineSection.setClient(opfSpineSectionComposite);

		opfSpineGrid = new Grid(opfSpineSectionComposite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		gridFontItalic = opfSpineGrid.getFont();
		/* Italic */
		gridFontItalic = ui.newFontAttributes(opfSpineGrid.getFont(), SWT.ITALIC);

		GridData opfSpineGridData = ui.createFillBothGridData();
		opfSpineGridData.minimumHeight = 250;
		opfSpineGrid.setLayoutData(opfSpineGridData);
		
		opfSpineGrid.setHeaderVisible(true);
		//opfSpineGrid.setItemHeight(24);
		opfSpineGrid.setFileList(true);
		int opfSpineGridHorizontalScrollCompensate = opfSpineGrid.getVerticalBar().getSize().x;
		opfSpineGrid.setHorizontalScrollCompensate(opfSpineGridHorizontalScrollCompensate);
		
		/* Spine grid resize */
		opfSpineGrid.addControlListener((UI.OnResize) resizeEvent -> {
			
			Point gridNewSize = opfSpineGrid.getSize();
			if (opfSpineGridLastSize.equals(gridNewSize)) {
				return;
			}
			if (opfSpineGridLastSize.x == gridNewSize.x) {
				return;
			}
			opfSpineGridLastSize = gridNewSize;
			int newWidth = gridNewSize.x - (opfSpineGridHorizontalScrollCompensate + 4);
			
			opfSpineGrid.getColumn(itemSpineNameIndex).setWidth((newWidth * 35) / 100);
			opfSpineGrid.getColumn(itemSpineRelativePathIndex).setWidth((newWidth * 30) / 100);
			
			int actualWidth = opfSpineGrid.getColumn(itemSpineNameIndex).getWidth() +
					opfSpineGrid.getColumn(itemSpineRelativePathIndex).getWidth();
			opfSpineGrid.getColumn(itemSpineIdIndex).setWidth(newWidth - actualWidth);
			
			if (opfSpineGrid.getItemCount() > 0) {
				opfSpineGrid.resetMeasures();
				opfSpineGrid.showSelection();
			}
		});

		/* Spine grid columns resize */
		ControlListener opfSpineGridColumnControlListener = (UI.OnResize) controlEvent -> {
				
			if (opfSpineGrid.getItemCount() > 0) {
				opfSpineGrid.resetMeasures();
				opfSpineGrid.showSelection();
			}
		};

		gridColumn = new GridColumn(opfSpineGrid, SWT.NONE);
		gridColumn.setText("File name");
		gridColumn.setMinimumWidth(150);
		gridColumn.setWordWrap(true);
		gridColumn.addControlListener(opfSpineGridColumnControlListener);
		
		gridColumn = new GridColumn(opfSpineGrid, SWT.NONE);
		//gridColumn.setAlignment(SWT.RIGHT);
		gridColumn.setText("Relative file path");
		gridColumn.setMinimumWidth(150);
		gridColumn.addControlListener(opfSpineGridColumnControlListener);

		gridColumn = new GridColumn(opfSpineGrid, SWT.NONE);
		gridColumn.setText("ID");
		gridColumn.setMinimumWidth(150);
		gridColumn.addControlListener(opfSpineGridColumnControlListener);
		
		gridSpineItems.clear();
		gridSpineItems.addAll(ePubProject.spineItems);
		
		loadSpineGrid();

		
		/* Toc Section */
		Section tocSection = formsToolkit.createSection(bottomComposite, Section.EXPANDED | Section.TWISTIE | Section.DESCRIPTION | Section.TITLE_BAR);
		tocSection.setLayoutData(ui.createFillHorizontalGridData());
		tocSection.setLayout(ui.createGridLayout());
		tocSection.setText("Table of Contents"); //$NON-NLS-1$
		tocSection.setDescription("What document links will be in the table of contents (only ncx and only one level)"); //$NON-NLS-1$

		ToolBarManager tocToolBarManager = new ToolBarManager(SWT.FLAT);
		ToolBar tocToolBar = tocToolBarManager.createControl(tocSection);

		/* Add to toc */
		tocToolBarManager.add(UI.ActionFactory.create("org.emdepub.epub_project.editor.tocAddAction", "Add to toc",
			"Add documents from manifest to toc", R.getImageDescriptor("add"), () -> {
			addToToc();
		}));
		
		tocToolBarManager.add(UI.ActionFactory.create("org.emdepub.epub_project.editor.tocRemoveAction", "Remove from toc",
			"Remove documents from toc", R.getImageDescriptor("remove"), () -> {
			removeFromToc();
		}));

		tocToolBarManager.add(UI.ActionFactory.create("org.emdepub.epub_project.editor.tocMoveUpAction", "Move up in toc",
			"Move documents up in toc, to the beginning of the book", R.getImageDescriptor("move-up"), () -> {
			moveInToc(true);
		}));

		tocToolBarManager.add(UI.ActionFactory.create("org.emdepub.epub_project.editor.tocMoveDownAction", "Move down in toc",
			"Move documents down in toc, to the end of the book", R.getImageDescriptor("move-down"), () -> {
			moveInToc(false);
		}));
		
		/* Modify toc item */
		tocToolBarManager.add(UI.ActionFactory.create("org.emdepub.epub_project.editor.modifyTocItemAction",
			"Edit TOC item", "Edit TOC item", R.getImageDescriptor("edit_template"), () -> {
				
			if (noToc()) {
				return;
			}

			GridItem gridItem = tocGrid.getItem(tocGrid.getSelectionIndex());
			modifyTocItem(gridItem);
		}));

		
		tocToolBarManager.update(true);
		tocSection.setTextClient(tocToolBar);
		
		/* Toc Section Composite */
		Composite tocSectionComposite = formsToolkit.createComposite(tocSection, SWT.NULL);
		tocSectionComposite.setLayoutData(ui.createFillBothGridData());
		tocSectionComposite.setLayout(ui.createMarginTopVerticalSpacingGridLayout(verticalSpacing, verticalSpacing));
		tocSection.setClient(tocSectionComposite);

		tocGrid = new Grid(tocSectionComposite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		gridFontItalic = tocGrid.getFont();
		/* Italic */
		gridFontItalic = ui.newFontAttributes(tocGrid.getFont(), SWT.ITALIC);

		GridData tocGridData = ui.createFillBothGridData();
		tocGridData.minimumHeight = 250;
		tocGrid.setLayoutData(tocGridData);
		
		tocGrid.setHeaderVisible(true);
		//tocGrid.setItemHeight(24);
		tocGrid.setFileList(true);
		int tocGridHorizontalScrollCompensate = tocGrid.getVerticalBar().getSize().x;
		tocGrid.setHorizontalScrollCompensate(tocGridHorizontalScrollCompensate);
		
		/* Toc grid resize */
		tocGrid.addControlListener((UI.OnResize) resizeEvent -> {
			
			Point gridNewSize = tocGrid.getSize();
			if (tocGridLastSize.equals(gridNewSize)) {
				return;
			}
			if (tocGridLastSize.x == gridNewSize.x) {
				return;
			}
			tocGridLastSize = gridNewSize;
			int newWidth = gridNewSize.x - (tocGridHorizontalScrollCompensate + 4);
			
			tocGrid.getColumn(itemTocTextIndex).setWidth((newWidth * 55) / 100);
			
			int actualWidth = tocGrid.getColumn(itemTocTextIndex).getWidth();
			tocGrid.getColumn(itemTocSrcIndex).setWidth(newWidth - actualWidth);
			
			if (tocGrid.getItemCount() > 0) {
				tocGrid.resetMeasures();
				tocGrid.showSelection();
			}
		});

		/* Toc grid columns resize */
		ControlListener tocGridColumnControlListener = (UI.OnResize) controlEvent -> {
				
			if (tocGrid.getItemCount() > 0) {
				tocGrid.resetMeasures();
				tocGrid.showSelection();
			}
		};

		gridColumn = new GridColumn(tocGrid, SWT.NONE);
		gridColumn.setText("Toc entry title");
		gridColumn.setMinimumWidth(150);
		gridColumn.setWordWrap(true);
		gridColumn.addControlListener(tocGridColumnControlListener);
		
		gridColumn = new GridColumn(tocGrid, SWT.NONE);
		//gridColumn.setAlignment(SWT.RIGHT);
		gridColumn.setText("Toc entry HTML src (file or anchor)");
		gridColumn.setMinimumWidth(150);
		gridColumn.addControlListener(tocGridColumnControlListener);
		
		gridTocItems.clear();
		
		loadTocGrid();
	
		ePubProjectFormsPageIndex = addPage(opfForm);
		setPageText(ePubProjectFormsPageIndex, "ePub Project");
	}
	
	/** Creates EPubProject text editor page of the forms editor */
	@SneakyThrows(PartInitException.class)
	private void createEPubProjectTextEditorPage() {

		ePubProjectTextEditor = new EPubProjectTextEditor();
		ePubProjectTextEditorPageIndex = addPage(ePubProjectTextEditor, getEditorInput());
		
		this.setPartName(ePubProjectTextEditor.getTitle());
		
		setPageText(ePubProjectTextEditorPageIndex, ".ePub-project");
		
		document = (Document) ePubProjectTextEditor.getDocumentProvider().getDocument(ePubProjectTextEditor.getEditorInput());
	}
	
	/** Create pages */
	@Override
	protected void addPages() {

		createEPubProjectEditorPage();
		createEPubProjectTextEditorPage();
	}
	
//	/** LocalBaseHref */
//	private String findLocalBaseHref() {
//		
//		IEditorInput editorInput = ePubProjectTextEditor.getEditorInput();
//		
//		if (editorInput instanceof FileEditorInput) {
//			IFile iFile = (IFile) editorInput.getAdapter(IFile.class);
//			File file = iFile.getLocation().toFile();
//			String rootPath = file.getPath().substring(0, file.getPath().length() - file.getName().length()).replace("\\", "/");
//			
//			return "file:///" + rootPath;
//		}
//		else if (editorInput instanceof FileStoreEditorInput) {
//			
//			String rootPath = ((FileStoreEditorInput) editorInput).getURI().toString();
//			rootPath = rootPath.substring(0, rootPath.length() - editorInput.getName().length()).replace("file:/", "file:///");
//			return rootPath;
//		}
//		
//		return editorInput.getName();
//	}

	/** Source EPubProject file path and name */
	@SneakyThrows({UnsupportedEncodingException.class, MalformedURLException.class})
	public String getSourceEPubProjectFilePathAndName() {

		IEditorInput editorInput = getEditorInput();
		
		if (editorInput instanceof FileEditorInput) {
			
			IFile iFile = (IFile) editorInput.getAdapter(IFile.class);
			return iFile.getLocation().toOSString();
		}
		else if (editorInput instanceof FileStoreEditorInput) {
		
			URI urlPath = ((FileStoreEditorInput) editorInput).getURI();
			return new File(URLDecoder.decode(urlPath.toURL().getPath(), "UTF-8")).getAbsolutePath();
		}
		
		return editorInput.getName();
	}

	/** Used in many places */
	@SneakyThrows(JsonProcessingException.class)
	public void serialize() {

		document.set(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(ePubProject));
	}

	/** Refreshes contents of viewer page when it is activated */
	@SneakyThrows(JsonProcessingException.class)
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);
		
		if (newPageIndex == ePubProjectFormsPageIndex) {
			
			if (F.isEmpty(document.get())) {
				return;
			}
			
			ePubProject = objectMapper.readValue(document.get(), EPUB_project.class);

			rootFolderFileControl.reload();
			targetFolderFileControl.reload();
			opfFileTextControl.reload();

			metadataIdentifierTextControl.reload();
			metadataTitle.reload();
			metadataAuthors.reload();
			metadataCover.reload();
			metadataDescription.reload();
			
			gridManifestItems.clear();
			gridManifestItems.addAll(ePubProject.manifestItems);
//			opfManifestGridSortColumnIndex = 0;
//			opfManifestGridSortColumnAsc = true;
			sortManifestGrid();
			loadManifestGrid();

			gridSpineItems.clear();
			gridSpineItems.addAll(ePubProject.spineItems);
			loadSpineGrid();

			gridTocItems.clear();
			gridTocItems.addAll(ePubProject.tocItems);
			loadTocGrid();
		}
	}

	/** Saves the forms editor's document */
	@Override
	public void doSave(IProgressMonitor progressMonitor) {
		
		ePubProjectTextEditor.doSave(progressMonitor);
	}

	/** Saves the forms editor's document as another file */
	@Override
	public void doSaveAs() {

		ePubProjectTextEditor.doSaveAs();
		this.setPartName(ePubProjectTextEditor.getTitle());
		setInput(ePubProjectTextEditor.getEditorInput());
	}

	/** Select and reveal EPubProject text editor page */
	public void activateEPubProjectTextEditorPage() {
		if (this.getActivePage() != ePubProjectTextEditorPageIndex) {
			this.setActiveEditor(getEditor(ePubProjectTextEditorPageIndex));
		}
	}

	/** Saves the forms editor's document */
	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	public EPubProjectTextEditor getEPubProjectTextEditor() {
		return ePubProjectTextEditor;
	}

	public int getEPubProjectFormsPageIndex() {
		return ePubProjectFormsPageIndex;
	}

	public int getEPubProjectTextEditorPageIndex() {
		return ePubProjectTextEditorPageIndex;
	}

	/** Create file control */
	@SneakyThrows({NoSuchFieldException.class, IllegalAccessException.class})
	private FileControl addFileControl(UI ui, Composite parentComposite, String labelText, Integer labelWidth, String fileType, boolean isFolder, String fieldName) {
		
		final Composite fileComposite = new Composite(parentComposite, SWT.NONE);
		//ui.addDebug(fileComposite);
	    fileComposite.setLayoutData(ui.createFillHorizontalGridData());
		fileComposite.setLayout(ui.createColumnsSpacingGridLayout(3, UI.sep));
		
		final Label fileLabel = new Label(fileComposite, SWT.NONE);
		fileLabel.setLayoutData(labelWidth == null ? ui.createGridData() : ui.createWidthGridData(labelWidth));
		fileLabel.setText(labelText);

		final Field field = ePubProject.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		
		final Text fileText = new Text(fileComposite, SWT.SINGLE | SWT.BORDER);
		String fileTextValue = (String) field.get(ePubProject);
		fileText.setText(F.isEmpty(fileTextValue) ? "" : fileTextValue);
		fileText.setLayoutData(ui.createFillHorizontalGridData());

		final Button fileButton = new Button(fileComposite, SWT.NONE);
		fileButton.setText("Browse");
		fileButton.setLayoutData(ui.createWidthGridData(80));
		
		/* File name */
		fileText.addFocusListener(new FocusAdapter() {
			@Override
			@SneakyThrows(IllegalAccessException.class)
			public void focusLost(FocusEvent focusEvent) {
				String focusLostFileName = fileText.getText().trim();
				if (!focusLostFileName.equalsIgnoreCase((String) field.get(ePubProject))) {
					field.set(ePubProject, focusLostFileName);
					serialize();
				}
			}
		});
		
		/* Browse */
		fileButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {

				String oldFileName = fileText.getText();
				String newFileName = null;
				if (isFolder) {
					DirectoryDialog directoryDialog = new DirectoryDialog(parentComposite.getShell(), SWT.NONE);
					directoryDialog.setText(labelText);
					if (oldFileName.trim().length() == 0) {
						directoryDialog.setFilterPath(applicationRootFolder);	
					}
					else {
						directoryDialog.setFilterPath(oldFileName);	
					}
					newFileName = directoryDialog.open();
				}
				else {
					FileDialog fileDialog = new FileDialog(parentComposite.getShell(), SWT.NONE);
					fileDialog.setText(labelText);
					if (oldFileName.trim().length() == 0) {
						fileDialog.setFileName(applicationRootFolder);	
					}
					else {
						fileDialog.setFileName(oldFileName);	
					}
					
//					if (fileType.equalsIgnoreCase("csv")) {
//						fileDialog.setFilterExtensions(filterExtensionsCsv);
//						fileDialog.setFilterNames(filterNamesCsv);
//					}
//
//					if (fileType.equalsIgnoreCase("json")) {
//						fileDialog.setFilterExtensions(filterExtensionsJson);
//						fileDialog.setFilterNames(filterNamesJson);
//					}
//
//					if (fileType.equalsIgnoreCase("liquid")) {
//						fileDialog.setFilterExtensions(filterExtensionsLiquid);
//						fileDialog.setFilterNames(filterNamesLiquid);
//					}
					
					newFileName = fileDialog.open();
				}
				
				if ((newFileName != null) && (!newFileName.equalsIgnoreCase(oldFileName))) {
					fileText.setText(newFileName);
					fileText.setFocus();
				}
			}
		});
		
		
		return new FileControl() {
			@Override
			@SneakyThrows(IllegalAccessException.class)
			public void reload() {
				String fileTextValue = (String) field.get(ePubProject);
				fileText.setText(F.isEmpty(fileTextValue) ? "" : fileTextValue);
				//fileText.setLayoutData(ui.createFillHorizontalGridData());
			}
			@Override
			public String getCompleteFileName() {
				return fileText.getText();
			}
		};
	}

	/** Create text control */
	@SneakyThrows({NoSuchFieldException.class, IllegalAccessException.class})
	private TextControl addTextControl(UI ui, Composite parentComposite, String labelText, Integer labelWidth, int lines, String fieldName) {
		
		final Composite textComposite = new Composite(parentComposite, SWT.NONE);
		//ui.addDebug(fileComposite);
	    textComposite.setLayoutData(ui.createFillHorizontalGridData());
		textComposite.setLayout(ui.createColumnsSpacingGridLayout(2, UI.sep));
		
		final Label textLabel = new Label(textComposite, SWT.NONE);
		GridData textLabelGridData = null;
		if (labelWidth == null) {
			textLabelGridData = lines > 1 ? ui.createTopAlignedGridData() : ui.createGridData();
		}
		else {
			textLabelGridData = lines > 1 ? ui.createWidthTopAlignedGridData(labelWidth) : ui.createWidthGridData(labelWidth);
		}
		textLabel.setLayoutData(textLabelGridData);
		textLabel.setText(labelText);

		final Field field = ePubProject.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		
		final Text text = new Text(textComposite, (lines > 1 ? SWT.MULTI | SWT.V_SCROLL | SWT.WRAP : SWT.SINGLE) | SWT.BORDER );
		String textValue = (String) field.get(ePubProject);
		text.setText(F.isEmpty(textValue) ? "" : textValue);
		GridData textGridData = ui.createFillHorizontalGridData();
		if (lines > 1) {
			textGridData.minimumHeight = lines * text.getLineHeight();
			textGridData.heightHint = lines * text.getLineHeight();
		}
		text.setLayoutData(textGridData);
		
		/* Text */
		text.addFocusListener(new FocusAdapter() {
			@Override
			@SneakyThrows(IllegalAccessException.class)
			public void focusLost(FocusEvent focusEvent) {
				String focusLostText = text.getText().trim();
				if (!focusLostText.equalsIgnoreCase((String) field.get(ePubProject))) {
					field.set(ePubProject, focusLostText);
					serialize();
				}
			}
		});
		
		return new TextControl() {
			@Override
			@SneakyThrows(IllegalAccessException.class)
			public void reload() {
				String textValue = (String) field.get(ePubProject);
				text.setText(F.isEmpty(textValue) ? "" : textValue);
			}
			@Override
			public String getText() {
				return text.getText();
			}
		};
	}

	/** Refresh manifest */
	@SneakyThrows(IOException.class)
	public void refreshManifest() {
		
		if (F.isEmpty(ePubProject.rootFolderNameWithFullPath)) {
			
			MessageDialog.openError(Display.getCurrent().getActiveShell(),
				"Refresh manifest cannot continue", "Root folder name is empty");
				return;
			//L.e("Refresh manifest", new Throwable("Root folder name is empty"));
		}
		
		Path rootFolderPath = Paths.get(ePubProject.rootFolderNameWithFullPath);
		
		Path opfFolderPath = Paths.get(rootFolderPath.toString(), ePubProject.opfFileNameWithRelativePath).getParent();
		
		linearFileNamesWithPath.clear();
		Files.walkFileTree(rootFolderPath, linearizeFileVisitor);
		
		ePubProject.manifestItems.clear();
		
		LinkedHashMap<String, EPUB_project_manifest_item> oldManifestItems = new LinkedHashMap<>();
		gridManifestItems.stream().forEach(gridManifestItem ->
			oldManifestItems.put(gridManifestItem.itemFileRelativePath + s + gridManifestItem.itemFileName, gridManifestItem));
		
		for (Path file: linearFileNamesWithPath) {

			Path relativePath = opfFolderPath.relativize(file);
			Path relativeParentFolder = relativePath.getParent();
			
			EPUB_project_manifest_item manifest_item = new EPUB_project_manifest_item();
			manifest_item.itemFileName = relativePath.getFileName().toString();
			manifest_item.itemFileRelativePath = relativeParentFolder == null ? "" : relativeParentFolder.toString();

			manifest_item.itemFileId = "";

			String mediaType = Files.probeContentType(file);
			manifest_item.itemFileMediaType = mediaType == null ? "unk" : mediaType; 
			
			manifest_item.itemFileProperties = "";

			String key = manifest_item.itemFileRelativePath + s + manifest_item.itemFileName;
			if (oldManifestItems.containsKey(key)) {
				
				EPUB_project_manifest_item oldManifestItem = oldManifestItems.get(key);
				
				if (!F.isEmpty(oldManifestItem.itemFileManualId)) {
					manifest_item.itemFileManualId = oldManifestItem.itemFileManualId;
				}
				if (!F.isEmpty(oldManifestItem.itemFileId)) {
					manifest_item.itemFileId = oldManifestItem.itemFileId;
				}
				if (!F.isEmpty(oldManifestItem.itemFileMediaType)) {
					manifest_item.itemFileMediaType = oldManifestItem.itemFileMediaType;
				}
				if (!F.isEmpty(oldManifestItem.itemFileProperties)) {
					manifest_item.itemFileProperties = oldManifestItem.itemFileProperties;
				}
			}
			
			ePubProject.manifestItems.add(manifest_item);
	    }
		gridManifestItems.clear();
		gridManifestItems.addAll(ePubProject.manifestItems);	

		opfManifestGrid.getColumn(itemFileNameIndex).setSort(SWT.DOWN);
		opfManifestGridSortColumnIndex = 0;
		opfManifestGridSortColumnAsc = true;
		sortManifestGrid();
		loadManifestGrid();
	}

	/** Generate manifest IDs action */
	public void editManifestIDPreferences() {
		
		Shell ideShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		EPubProjectGenerateIDsWizard ePubProjectGenerateIDsWizard = new EPubProjectGenerateIDsWizard(this);
		WizardDialog wizardDialog = new WizardDialog(ideShell, ePubProjectGenerateIDsWizard);
		wizardDialog.open();
	}

	/** Generate manifest IDs */
	public void generateManifestIDs() {
		
		String id = ePubProject.manifestIDPrefix;
		
		if (ePubProject.manifestIDGuid) {
			for (EPUB_project_manifest_item gridManifestItem : gridManifestItems) {
				if (F.isEmpty(gridManifestItem.itemFileManualId)) {
					gridManifestItem.itemFileId =  id + UUID.randomUUID().toString();	
				}
			}
		}
		else {
			int index = 1;
			for (EPUB_project_manifest_item gridManifestItem : gridManifestItems) {
				if (F.isEmpty(gridManifestItem.itemFileManualId)) {
					String count = "" + index;
					gridManifestItem.itemFileId =  id + "00000".substring(count.length()) + count;
					index++;
				}
			}
		}
		
		sortManifestGrid();
		loadManifestGrid();
		
		serialize();
	}

	/** Modify manifest item action */
	public void modifyManifestItem(GridItem gridItem) {
		
		Shell ideShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		EPubProjectModifyManifestItemWizard ePubProjectModifyManifestItemWizard = new EPubProjectModifyManifestItemWizard(this, gridItem);
		WizardDialog wizardDialog = new WizardDialog(ideShell, ePubProjectModifyManifestItemWizard);
		wizardDialog.open();
	}

	public void sortManifestGrid() {
		
		switch (opfManifestGridSortColumnIndex) {
			case itemFileNameIndex: {
				manifestItemsComparator.setSortOrder(itemFileNameIndex, itemFileRelativePathIndex, itemFileIdIndex, itemFileMediaTypeIndex, itemFilePropertiesIndex);	
				manifestItemsComparator.setSortDirection(itemFileNameIndex, opfManifestGridSortColumnAsc);
				break;
			}
			case itemFileRelativePathIndex: {
				manifestItemsComparator.setSortOrder(itemFileRelativePathIndex, itemFileNameIndex, itemFileIdIndex, itemFileMediaTypeIndex, itemFilePropertiesIndex);	
				manifestItemsComparator.setSortDirection(itemFileRelativePathIndex, opfManifestGridSortColumnAsc);
				break;
			}
			case itemFileIdIndex: {
				manifestItemsComparator.setSortOrder(itemFileIdIndex, itemFileNameIndex, itemFileRelativePathIndex, itemFileMediaTypeIndex, itemFilePropertiesIndex);	
				manifestItemsComparator.setSortDirection(itemFileIdIndex, opfManifestGridSortColumnAsc);
				break;
			}
			case itemFileMediaTypeIndex: {
				manifestItemsComparator.setSortOrder(itemFileMediaTypeIndex, itemFileNameIndex, itemFileRelativePathIndex, itemFileIdIndex, itemFilePropertiesIndex);	
				manifestItemsComparator.setSortDirection(itemFileMediaTypeIndex, opfManifestGridSortColumnAsc);
				break;
			}
			case itemFilePropertiesIndex: {
				manifestItemsComparator.setSortOrder(itemFilePropertiesIndex, itemFileNameIndex, itemFileRelativePathIndex, itemFileIdIndex, itemFileMediaTypeIndex);	
				manifestItemsComparator.setSortDirection(itemFilePropertiesIndex, opfManifestGridSortColumnAsc);
				break;
			}
		}
		
		Collections.sort(gridManifestItems, manifestItemsComparator);
	}
	
	/** Load grid from gridManifestItems */
	public void loadManifestGrid() {

		opfManifestGrid.disposeAllItems();
		
		for (EPUB_project_manifest_item manifest_item : gridManifestItems) {
			
			GridItem gridItem = new GridItem(opfManifestGrid, SWT.NONE);

			String mediaType = manifest_item.itemFileMediaType; 
			if (F.isEmpty(mediaType)) {
				gridItem.setImage(R.getImage("unknown"));
			}
			else {
				if (mediaType.contains("html")) {
					gridItem.setImage(R.getImage("html"));
				}
				else if (mediaType.contains("image")) {
					gridItem.setImage(R.getImage("tag-image"));
				}
				else if (mediaType.contains("css")) {
					gridItem.setImage(R.getImage("stylesheet"));
				}
				else if (mediaType.contains("text")) {
					gridItem.setImage(R.getImage("unknown"));
				}
				else {
					gridItem.setImage(R.getImage("fileType_filter"));
				}
			}
			
			gridItem.setText(itemFileNameIndex, manifest_item.itemFileName);
			gridItem.setText(itemFileRelativePathIndex, manifest_item.itemFileRelativePath);
			
			gridItem.setFont(itemFileIdIndex, F.isEmpty(manifest_item.itemFileManualId) ? gridFont : gridFontItalic);
			gridItem.setText(itemFileIdIndex, manifest_item.itemFileId);
			
			gridItem.setText(itemFileMediaTypeIndex, manifest_item.itemFileMediaType);
			gridItem.setText(itemFilePropertiesIndex, manifest_item.itemFileProperties);

			gridItem.setData("manifest_item", manifest_item);
		}
	}

	/** Load grid from gridSpineItems */
	public void loadSpineGrid() {
		
		opfSpineGrid.disposeAllItems();
		
		LinkedHashMap<String, EPUB_project_manifest_item> idManifestItems = new LinkedHashMap<>();
		gridManifestItems.stream().forEach(gridManifestItem -> idManifestItems.put(gridManifestItem.itemFileId, gridManifestItem));
		
		for (EPUB_project_spine_item gridSpineItem : gridSpineItems) {
			
			EPUB_project_manifest_item manifestItem = idManifestItems.get(gridSpineItem.itemManifestItemFileId);
			if (manifestItem != null) {

				GridItem gridItem = new GridItem(opfSpineGrid, SWT.NONE);
				
				gridItem.setText(itemSpineNameIndex, manifestItem.itemFileName);
				gridItem.setText(itemSpineRelativePathIndex, manifestItem.itemFileRelativePath);
				gridItem.setText(itemSpineIdIndex, gridSpineItem.itemManifestItemFileId);
				
				gridItem.setData("spine_item", gridSpineItem);
			}
		}
	}

	/** Add items to gridSpineItems from manifest */
	public void addToSpine() {
		
		if (noManifest()) {
			return;
		}
		
		int spineSelectionIndex = opfSpineGrid.getSelectionIndex();
		if (spineSelectionIndex == -1) {
			spineSelectionIndex = 0;
		}
		int spireFirstSelectionIndex = spineSelectionIndex;
		LinkedHashMap<String, EPUB_project_spine_item> oldSpineItems = new LinkedHashMap<>();
		gridSpineItems.stream().forEach(gridSpineItem -> oldSpineItems.put(gridSpineItem.itemManifestItemFileId, gridSpineItem));
		
		for (GridItem manifestGridItem : opfManifestGrid.getSelection()) {
			
			EPUB_project_manifest_item manifestItem = (EPUB_project_manifest_item) manifestGridItem.getData("manifest_item");
			String id = manifestItem.itemFileId;
			if (!oldSpineItems.containsKey(id)) {
				
				EPUB_project_spine_item spineItem = new EPUB_project_spine_item();
				spineItem.itemManifestItemFileId = id;
				gridSpineItems.add(spineSelectionIndex, spineItem);
				spineSelectionIndex++;
			}
		}
		
		loadSpineGrid();
		opfSpineGrid.showItem(opfSpineGrid.getItem(spireFirstSelectionIndex));
		
		ePubProject.spineItems.clear();
		ePubProject.spineItems.addAll(gridSpineItems);
		
		serialize();
	}

	/** Remove documents from spine */
	public void removeFromSpine() {

		if (noSpine()) {
			return;
		}

		int firstItemIndex = opfSpineGrid.getTopIndex();
		
		for (GridItem spineGridItem : opfSpineGrid.getSelection()) {
			
			EPUB_project_spine_item gridSpineItem = (EPUB_project_spine_item) spineGridItem.getData("spine_item");
			gridSpineItems.remove(gridSpineItem);
		}
		
		loadSpineGrid();
		int gridSpineItemsCount = opfSpineGrid.getItemCount();
		if (gridSpineItemsCount > 0) {
			if (firstItemIndex >= gridSpineItemsCount) {
				firstItemIndex = 0;
			}
			opfSpineGrid.showItem(opfSpineGrid.getItem(firstItemIndex));
		}

		ePubProject.spineItems.clear();
		ePubProject.spineItems.addAll(gridSpineItems);

		serialize();
	}

	/** Move documents up in spine */
	public void moveInSpine(boolean up) {

		if (noSpine()) {
			return;
		}

		ArrayList<Integer> newSelectionIndexes = new ArrayList<>();
		
		ArrayList<Integer> oldSelectionIndexes = Arrays.stream(opfSpineGrid.getSelection()).
			map(gridItem -> opfSpineGrid.getIndexOfItem(gridItem)).
			collect(Collectors.toCollection(ArrayList<Integer>::new));
		Collections.sort(oldSelectionIndexes);

		if (up) {
			if(oldSelectionIndexes.get(0) == 0) {
				return;
			}
		}
		else {
			if(oldSelectionIndexes.get(oldSelectionIndexes.size() - 1) == opfSpineGrid.getItemCount() - 1) {
				return;
			}
		}
		
		ArrayList<GridItem> sortedSelectionGridItems = oldSelectionIndexes.stream().
			map(index -> opfSpineGrid.getItem(index)).
			collect(Collectors.toCollection(ArrayList<GridItem>::new));
//		GridItem firstSelectedGridItem = sortedSelectionGridItems.get(0);
//		GridItem lastSelectedGridItem = sortedSelectionGridItems.get(sortedSelectionGridItems.size() - 1);
		
		if (up) {
			for (GridItem spineGridItem : sortedSelectionGridItems) {
				EPUB_project_spine_item spineItem = (EPUB_project_spine_item) spineGridItem.getData("spine_item");
				int index = gridSpineItems.indexOf(spineItem);
				gridSpineItems.remove(index);
				index = index - 1;
				gridSpineItems.add(index, spineItem);
				newSelectionIndexes.add(index);
			}
		}
		else {
			Collections.reverse(sortedSelectionGridItems);
			for (GridItem spineGridItem : sortedSelectionGridItems) {
				EPUB_project_spine_item spineItem = (EPUB_project_spine_item) spineGridItem.getData("spine_item");
				int index = gridSpineItems.indexOf(spineItem);
				gridSpineItems.remove(index);
				index = index + 1;
				gridSpineItems.add(index, spineItem);
				newSelectionIndexes.add(index);
			}
		}
		
		loadSpineGrid();
		opfSpineGrid.setSelection(newSelectionIndexes.stream().mapToInt(i -> i).toArray());
		if (up) {
			opfSpineGrid.showItemAtTop(opfSpineGrid.getItem(newSelectionIndexes.get(0)));
		}
		else {
			opfSpineGrid.showItem(opfSpineGrid.getItem(newSelectionIndexes.get(0)));
		}

		ePubProject.spineItems.clear();
		ePubProject.spineItems.addAll(gridSpineItems);

		serialize();
	}

	/** Generate ePub */
	public void generateBook() {
		EPubProjectEngine.generateBook(this);
	}

	/** Generate ePub */
	public void splitSourceFile() {

		Shell ideShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		EPubProjectSplitInChaptersWizard ePubProjectSplitInChaptersWizard = new EPubProjectSplitInChaptersWizard(this);
		WizardDialog wizardDialog = new WizardDialog(ideShell, ePubProjectSplitInChaptersWizard);
		wizardDialog.open();
	}
	
	/** In engine ? */
	public void splitFileInFiles() {
		
		String source = F.loadFileInString(ePubProject.splitSourceFileNameWithFullPath);
		
		String[] targets = source.split(ePubProject.splitStartExpression);
		
		int count = targets.length;
		
		int counterLength = ("" + count).length();
		String counterMax = "0000000000".substring(0, counterLength);
		
		for (int index = 1; index < count; index ++) {
			String counter = "" + index;
			counter = counterMax.substring(0, counterLength - counter.length()) + counter;
			F.saveStringToFile(targets[index], ePubProject.splitTargetFileNamesWithFullPath.replaceAll("\\*", counter));
		}
	}
	
	/** Toc items */
	public void loadTocGrid() {

		tocGrid.disposeAllItems();
		
		for (EPUB_project_toc_item gridTocItem : gridTocItems) {
			GridItem gridItem = new GridItem(tocGrid, SWT.NONE);
			
			gridItem.setText(itemTocTextIndex, gridTocItem.itemText);
			gridItem.setText(itemTocSrcIndex, gridTocItem.itemSrc);
			
			gridItem.setData("toc_item", gridTocItem);
		}
	}

	/** Toc items */
	public void addToToc() {
		
		if (noManifest()) {
			return;
		}
		
		int tocSelectionIndex = tocGrid.getSelectionIndex();
		if (tocSelectionIndex == -1) {
			tocSelectionIndex = 0;
		}
		int tocFirstSelectionIndex = tocSelectionIndex;
		
		for (GridItem manifestGridItem : opfManifestGrid.getSelection()) {
			
			EPUB_project_manifest_item manifestItem = (EPUB_project_manifest_item) manifestGridItem.getData("manifest_item");
				
			EPUB_project_toc_item tocItem = new EPUB_project_toc_item();
			tocItem.itemText = F.getFileNameWithoutExtension(manifestItem.itemFileName);
			tocItem.itemSrc = manifestItem.itemFileRelativePath.replace(s, "/") + "/" + manifestItem.itemFileName;
			gridTocItems.add(tocSelectionIndex, tocItem);
			tocSelectionIndex++;
		}
		
		loadTocGrid();
		tocGrid.showItem(tocGrid.getItem(tocFirstSelectionIndex));
		
		ePubProject.tocItems.clear();
		ePubProject.tocItems.addAll(gridTocItems);
		
		serialize();
		
	}

	/** Toc items */
	public void removeFromToc() {

		if (noToc()) {
			return;
		}

		int firstItemIndex = tocGrid.getTopIndex();
		for (GridItem tocGridItem : tocGrid.getSelection()) {
			EPUB_project_toc_item gridTocItem = (EPUB_project_toc_item) tocGridItem.getData("toc_item");
			gridTocItems.remove(gridTocItem);
		}
		loadTocGrid();
		int gridTocItemsCount = tocGrid.getItemCount();
		if (gridTocItemsCount > 0) {
			if (firstItemIndex >= gridTocItemsCount) {
				firstItemIndex = 0;
			}
			tocGrid.showItem(tocGrid.getItem(firstItemIndex));
		}
		ePubProject.tocItems.clear();
		ePubProject.tocItems.addAll(gridTocItems);

		serialize();
	}

	/** Toc items */
	public void moveInToc(boolean up) {

		if (noToc()) {
			return;
		}

		ArrayList<Integer> newSelectionIndexes = new ArrayList<>();
		ArrayList<Integer> oldSelectionIndexes = Arrays.stream(tocGrid.getSelection()).
			map(gridItem -> tocGrid.getIndexOfItem(gridItem)).
			collect(Collectors.toCollection(ArrayList<Integer>::new));
		Collections.sort(oldSelectionIndexes);
		if (up) {
			if(oldSelectionIndexes.get(0) == 0) {
				return;
			}
		}
		else {
			if(oldSelectionIndexes.get(oldSelectionIndexes.size() - 1) == tocGrid.getItemCount() - 1) {
				return;
			}
		}
		ArrayList<GridItem> sortedSelectionGridItems = oldSelectionIndexes.stream().
			map(index -> tocGrid.getItem(index)).
			collect(Collectors.toCollection(ArrayList<GridItem>::new));
		if (up) {
			for (GridItem tocGridItem : sortedSelectionGridItems) {
				EPUB_project_toc_item tocItem = (EPUB_project_toc_item) tocGridItem.getData("toc_item");
				int index = gridTocItems.indexOf(tocItem);
				gridTocItems.remove(index);
				index = index - 1;
				gridTocItems.add(index, tocItem);
				newSelectionIndexes.add(index);
			}
		}
		else {
			Collections.reverse(sortedSelectionGridItems);
			for (GridItem tocGridItem : sortedSelectionGridItems) {
				EPUB_project_toc_item tocItem = (EPUB_project_toc_item) tocGridItem.getData("toc_item");
				int index = gridTocItems.indexOf(tocItem);
				gridTocItems.remove(index);
				index = index + 1;
				gridTocItems.add(index, tocItem);
				newSelectionIndexes.add(index);
			}
		}
		loadTocGrid();
		tocGrid.setSelection(newSelectionIndexes.stream().mapToInt(i -> i).toArray());
		if (up) {
			tocGrid.showItemAtTop(tocGrid.getItem(newSelectionIndexes.get(0)));
		}
		else {
			tocGrid.showItem(tocGrid.getItem(newSelectionIndexes.get(0)));
		}
		ePubProject.tocItems.clear();
		ePubProject.tocItems.addAll(gridTocItems);

		serialize();
	}

	/** Modify TOC item */
	private void modifyTocItem(GridItem gridItem) {
	
		Shell ideShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		EPubProjectModifyTocItemWizard ePubProjectModifyTocItemWizard = new EPubProjectModifyTocItemWizard(this, gridItem);
		WizardDialog wizardDialog = new WizardDialog(ideShell, ePubProjectModifyTocItemWizard);
		wizardDialog.open();
	}
	
	/** Manifest has no items or no selection */
	private boolean noManifest() {

		if (opfManifestGrid.getItemCount() == 0) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "The manifest is empty", "Fill the manifest and select at least an item");
			return true;
		}
		int gridItemIndex = opfManifestGrid.getSelectionIndex();
		if (gridItemIndex == -1) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "No manifest item is selected", "Select at least an item");
			return true;
		}

		return false;
	}

	/** Spine has no items or no selection */
	private boolean noSpine() {

		if (opfSpineGrid.getItemCount() == 0) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "The spine is empty", "Add to spine and select at least an item");
			return true;
		}
		int gridItemIndex = opfSpineGrid.getSelectionIndex();
		if (gridItemIndex == -1) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "No spine item is selected", "Select at least an item");
			return true;
		}

		return false;
	}

	/** Toc has no items or no selection */
	private boolean noToc() {

		if (tocGrid.getItemCount() == 0) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "The toc is empty", "Add to toc and select at least an item");
			return true;
		}
		int gridItemIndex = tocGrid.getSelectionIndex();
		if (gridItemIndex == -1) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "No toc item is selected", "Select at least an item");
			return true;
		}

		return false;
	}
	
	
	public EPUB_project getEPubProject() {
		return ePubProject;
	}

	public Font getGridFont() {
		return gridFont;
	}

	public Font getGridFontItalic() {
		return gridFontItalic;
	}
}
