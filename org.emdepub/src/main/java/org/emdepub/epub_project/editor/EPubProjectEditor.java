/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.epub_project.editor;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
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
import java.util.concurrent.Callable;
import java.util.stream.IntStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.emdepub.activator.F;
import org.emdepub.activator.L;
import org.emdepub.activator.R;
import org.emdepub.activator.UI;
import org.emdepub.epub_project.editor.wizard.EPubProjectGenerateIDsWizard;
import org.emdepub.epub_project.editor.wizard.EPubProjectModifyManifestItemWizard;
import org.emdepub.epub_project.model.EPUB_project;
import org.emdepub.epub_project.model.EPUB_project_manifest_item;

import com.fasterxml.jackson.databind.ObjectMapper;

/** EPubProject multi-page editor */
public class EPubProjectEditor extends FormEditor {

	/** Convenient call */
	public static <T> T r(Callable<T> theFunction) {
		try {
			return theFunction.call();
		} catch (Exception exception) {
			L.e("Exception", exception);
			throw new RuntimeException(exception);
		}
	}
	
	/* Columns */
	public static final int itemFileNameIndex = 0;
	public static final int itemFileRelativePathIndex = 1;
	public static final int itemFileIdIndex = 2;
	public static final int itemFileMediaTypeIndex = 3;
	public static final int itemFilePropertiesIndex = 4;

	/** Sort order */
	public static final Collator COLLATOR = Collator.getInstance(Locale.ENGLISH);
	
	public class ManifestItemsComparator implements Comparator<EPUB_project_manifest_item> {
		 
		private final ArrayList<Comparator<EPUB_project_manifest_item>> defaultComparators = new ArrayList<>();
	    private final ArrayList<Comparator<EPUB_project_manifest_item>> listComparators = new ArrayList<>();
	 
	    public ManifestItemsComparator() {
	    	defaultComparators.add(new Comparator<EPUB_project_manifest_item>() {
				@Override
				public int compare(EPUB_project_manifest_item o1, EPUB_project_manifest_item o2) {
					return COLLATOR.compare(o1.itemFileName, o2.itemFileName);
				}
			});
	    	defaultComparators.add(new Comparator<EPUB_project_manifest_item>() {
				@Override
				public int compare(EPUB_project_manifest_item o1, EPUB_project_manifest_item o2) {
					return o1.itemFileRelativePath.compareTo(o2.itemFileRelativePath);
				}
			});
	    	defaultComparators.add(new Comparator<EPUB_project_manifest_item>() {
				@Override
				public int compare(EPUB_project_manifest_item o1, EPUB_project_manifest_item o2) {
					return o1.itemFileId.compareTo(o2.itemFileId);
				}
			});
	    	defaultComparators.add(new Comparator<EPUB_project_manifest_item>() {
				@Override
				public int compare(EPUB_project_manifest_item o1, EPUB_project_manifest_item o2) {
					return o1.itemFileMediaType.compareTo(o2.itemFileMediaType);
				}
			});
	    	defaultComparators.add(new Comparator<EPUB_project_manifest_item>() {
				@Override
				public int compare(EPUB_project_manifest_item o1, EPUB_project_manifest_item o2) {
					return o1.itemFileProperties.compareTo(o2.itemFileProperties);
				}
			});
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
	

	
	private static final String s = F.sep();
//	private static final String e = F.enter();

	private EPUB_project project_ePub;
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	private FileControl rootFolderFileControl;
	private FileControl targetFolderFileControl;
	private FileControl opfFileFileControl;
	
	private Grid opfManifestGrid;
	private Font gridFont;
	private Font gridFontItalic;
	private Point opfManifestGridLastSize = new Point(0, 0);
	private int opfManifestGridSortColumnIndex = 0;
	private boolean opfManifestGridSortColumnAsc = true;
	private ArrayList<EPUB_project_manifest_item> gridManifestItems = new ArrayList<>();
	private ManifestItemsComparator manifestItemsComparator = new ManifestItemsComparator();
	
	private Document document;
	
	private LinearizeFileVisitor linearizeFileVisitor = new LinearizeFileVisitor();
	
	/** Root folder */
	private String applicationRootFolder = "C:/";
	
	/** File control */
	private interface FileControl {
		
		public void reload();
		public String getCompleteFileName();
	}

//	private FileControl rootFolderFileControl;
//	private FileControl targetFolderFileControl;
//	private FileControl opfFileFileControl;

	
//	private static final String ePubProjectExportTemplate = R.getTextResourceAsString("texts/ePubProject-export-template.html");
	
//	private static final String ePubProjectViewerFolderNameWithPath = Activator.getPluginFolder().replace("\\", "/") + "/viewers/ePubProject";
	
	
	/** The forms will display the EPub Project */
	private int ePubProjectFormsPageIndex;
	
	/** The EPubProject editor */
	private EPubProjectTextEditor ePubProjectTextEditor;
	private int ePubProjectTextEditorPageIndex;

	/** Creates the viewer page of the forms editor */
	private void createEPubProjectEditorPage() {
		
		/* load */
		String json = F.loadFileInString(getSourceEPubProjectFilePathAndName());
		project_ePub = r(() -> objectMapper.readValue(json, EPUB_project.class));
		
//		final int indentWidth = 24;
//		final int labelWidth = 90;
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
		String fileNameWithPath = getSourceEPubProjectFilePathAndName();
		String fileName = fileNameWithPath.substring(F.getFileFolderName(fileNameWithPath).length() + 1);
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
		topComposite.setLayout(ui.createColumnsSpacingGridLayout(2, horizontalSpacing * 3));
		
		/* General Section */
		Section opfGeneralSection = formsToolkit.createSection(topComposite, Section.EXPANDED | Section.TWISTIE | Section.DESCRIPTION | Section.TITLE_BAR);
		opfGeneralSection.setLayoutData(ui.createFillHorizontalGridData());
		opfGeneralSection.setLayout(ui.createGridLayout());
		opfGeneralSection.setText("General"); //$NON-NLS-1$
		opfGeneralSection.setDescription("ePub project settings"); //$NON-NLS-1$

		/* General Section Composite */
		Composite opfGeneralSectionComposite = formsToolkit.createComposite(opfGeneralSection, SWT.NULL);
		opfGeneralSectionComposite.setLayoutData(ui.createFillBothGridData());
		opfGeneralSectionComposite.setLayout(ui.createMarginTopVerticalSpacingGridLayout(verticalSpacing, verticalSpacing));
		opfGeneralSection.setClient(opfGeneralSectionComposite);
		
		rootFolderFileControl = addFileControl(ui, opfGeneralSectionComposite, "Root folder", null, true, "rootFolderNameWithFullPath");
		targetFolderFileControl = addFileControl(ui, opfGeneralSectionComposite, "Target folder", null, true, "targetFolderNameWithFullPath");
		opfFileFileControl = addFileControl(ui, opfGeneralSectionComposite, "OPF file name with relative path", null, false, "opfFileNameWithRelativePath");
		
		/* Manifest Section */
		Section opfManifestSection = formsToolkit.createSection(opfFormComposite, Section.DESCRIPTION | Section.TITLE_BAR);
		opfManifestSection.setLayoutData(ui.createFillBothGridData());
		opfManifestSection.setLayout(ui.createGridLayout());
		opfManifestSection.setText("Manifest"); //$NON-NLS-1$
		opfManifestSection.setDescription("All the files to be included in the ePub book: documents, images, styles"); //$NON-NLS-1$
		
		ToolBarManager manifestToolBarManager = new ToolBarManager(SWT.FLAT);
		ToolBar manifestToolBar = manifestToolBarManager.createControl(opfManifestSection);
		
		/* Refresh manifest */
		Action manifestRefreshAction = new Action() {
			public void run() {
				refreshManifest();
			}
		};
		manifestRefreshAction.setId("org.emdepub.epub_project.editor.manifestRefreshAction");
		manifestRefreshAction.setText("Refresh manifest");
		manifestRefreshAction.setToolTipText("Refresh manifest from root files and folders");
		manifestRefreshAction.setImageDescriptor(R.getImageDescriptor("refresh_nav"));

		manifestToolBarManager.add(manifestRefreshAction);
		
		/* Generate manifest IDs */
		Action manifestGenerateIDsAction = new Action() {
			public void run() {
				editManifestIDPreferences();
			}
		};
		manifestGenerateIDsAction.setId("org.emdepub.epub_project.editor.manifestGenerateIDsAction");
		manifestGenerateIDsAction.setText("Generate manifest IDs");
		manifestGenerateIDsAction.setToolTipText("Generate manifest IDs");
		manifestGenerateIDsAction.setImageDescriptor(R.getImageDescriptor("classf_generate"));

		manifestToolBarManager.add(manifestGenerateIDsAction);

		/* Modify manifest item */
		Action modifyManifestItemAction = new Action() {
			public void run() {
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
		};
		modifyManifestItemAction.setId("org.emdepub.epub_project.editor.modifyManifestItemAction");
		modifyManifestItemAction.setText("Manifest item properties");
		modifyManifestItemAction.setToolTipText("Edit manifest item properties");
		modifyManifestItemAction.setImageDescriptor(R.getImageDescriptor("show_properties_view"));

		manifestToolBarManager.add(modifyManifestItemAction);
		
		
		
		
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
		int horizontalScrollCompensate = opfManifestGrid.getVerticalBar().getSize().x;
		opfManifestGrid.setHorizontalScrollCompensate(horizontalScrollCompensate);
		
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
			int newWidth = filesListGridNewSize.x - (horizontalScrollCompensate + 4);
			
			opfManifestGrid.getColumn(0).setWidth((newWidth * 24) / 100);
			opfManifestGrid.getColumn(1).setWidth((newWidth * 24) / 100);
			opfManifestGrid.getColumn(2).setWidth((newWidth * 24) / 100);
			opfManifestGrid.getColumn(3).setWidth((newWidth * 14) / 100);
			int actualWidth = IntStream.rangeClosed(0, 3).map(index -> opfManifestGrid.getColumn(index).getWidth()).sum();
			opfManifestGrid.getColumn(4).setWidth(newWidth - actualWidth);
			
			if (opfManifestGrid.getItemCount() > 0) {
				opfManifestGrid.resetMeasures();
				opfManifestGrid.showSelection();
			}
		});
		
		opfManifestGrid.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent mouseEvent) {
				GridItem gridItem = opfManifestGrid.getItem(new Point(mouseEvent.x, mouseEvent.y));
				if (gridItem != null) {
					modifyManifestItem(gridItem);
				}
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

		SelectionListener opfManifestGridColumnSelectionListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				
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
			}
		};
		
		ControlListener opfManifestGridColumnControlListener = new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent controlEvent) {
				
				if (opfManifestGrid.getItemCount() > 0) {
					opfManifestGrid.resetMeasures();
					opfManifestGrid.showSelection();
				}
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
		gridColumn.setText("Relative file path");
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
		gridManifestItems.addAll(project_ePub.manifestItems);
		
		opfManifestGridSortColumnIndex = 0;
		opfManifestGridSortColumnAsc = true;
		sortManifestGrid();
		loadManifestGrid();

		
//		/* Spine */
//		Section opfSpineSection = formsToolkit.createSection(middleComposite, Section.DESCRIPTION | Section.TITLE_BAR);
//		opfSpineSection.setLayoutData(ui.createFillBothGridData());
//		opfSpineSection.setLayout(ui.createGridLayout());
//		opfSpineSection.setText("Spine"); //$NON-NLS-1$
//		opfSpineSection.setDescription("OPF mainly"); //$NON-NLS-1$
//		Composite opfSpineSectionComposite = formsToolkit.createComposite(opfSpineSection, SWT.NULL);
//		opfSpineSectionComposite.setLayoutData(ui.createFillBothGridData());
//		opfSpineSectionComposite.setLayout(ui.createGridLayout());
//		opfSpineSection.setClient(opfSpineSectionComposite);

//		/* Toc */
//		Section opfTocSection = formsToolkit.createSection(middleComposite, Section.DESCRIPTION | Section.TITLE_BAR);
//		opfTocSection.setLayoutData(ui.createFillBothGridData());
//		opfTocSection.setLayout(ui.createGridLayout());
//		opfTocSection.setText("Toc"); //$NON-NLS-1$
//		opfTocSection.setDescription("OPF mainly"); //$NON-NLS-1$
//		Composite opfTocSectionComposite = formsToolkit.createComposite(opfTocSection, SWT.NULL);
//		opfTocSectionComposite.setLayoutData(ui.createFillBothGridData());
//		opfTocSectionComposite.setLayout(ui.createGridLayout());
//		opfTocSection.setClient(opfTocSectionComposite);

		
		
//		Section displayPreferencesSection = formsToolkit.createSection(opfFormComposite, Section.DESCRIPTION | Section.TITLE_BAR);
//		GridData sectionGridData = new GridData();
//		sectionGridData.minimumHeight = 300;
//		//sectionGridData.minimumWidth = 300;
//		sectionGridData.widthHint = 500;
//		displayPreferencesSection.setLayoutData(sectionGridData);
//
////		Composite rightFillerComposite = new Composite(formComposite, SWT.NONE);
////		GridData rightFillerGridData = new GridData();
////		rightFillerGridData.horizontalAlignment = GridData.FILL;
////		rightFillerGridData.minimumWidth = 6;
////		rightFillerGridData.grabExcessHorizontalSpace = true;
////		rightFillerComposite.setLayoutData(rightFillerGridData);
//	
//		displayPreferencesSection.setText("Display Preferences"); //$NON-NLS-1$
//		displayPreferencesSection.setDescription("These values are used to override the default ones"); //$NON-NLS-1$
//		displayPreferencesSection.marginWidth = 0;
//		displayPreferencesSection.marginHeight = 0;
//		Composite sectionComposite = formsToolkit.createComposite(displayPreferencesSection, SWT.WRAP);
//
//		displayPreferencesSection.setClient(sectionComposite);

		
//		Composite viewerPageComposite = new Composite(getContainer(), SWT.NONE);
//		viewerPageComposite.setLayout(ui.createMarginsGridLayout(1));
	
		ePubProjectFormsPageIndex = addPage(opfForm);
		setPageText(ePubProjectFormsPageIndex, "ePub Project");
	}
	
	/** Creates EPubProject text editor page of the forms editor */
	private void createEPubProjectTextEditorPage() {

		ePubProjectTextEditor = new EPubProjectTextEditor();
		ePubProjectTextEditorPageIndex = r(() -> addPage(ePubProjectTextEditor, getEditorInput()));
		
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
	public String getSourceEPubProjectFilePathAndName() {

		IEditorInput editorInput = getEditorInput();
		
		if (editorInput instanceof FileEditorInput) {
			
			IFile iFile = (IFile) editorInput.getAdapter(IFile.class);
			return iFile.getLocation().toOSString();
		}
		else if (editorInput instanceof FileStoreEditorInput) {
		
			URI urlPath = ((FileStoreEditorInput) editorInput).getURI();
			return r(() -> new File(urlPath.toURL().getPath()).getAbsolutePath());
		}
		
		return editorInput.getName();
	}
	
	public void serialize() {

		String json = r(() -> objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(project_ePub));
		document.set(json);
//		L.p(json);
	}
	
//	/** Get the EPubProject as Base64 text */
//	public String getBase64EPubProjectText() {
//		
//		String ePubProjectText = ePubProjectTextEditor.getDocumentProvider().getDocument(ePubProjectTextEditor.getEditorInput()).get();
//		
//		return new String(Base64.getEncoder().encode(ePubProjectText.getBytes()));
//	}
//
//	/** Get the options CSSs */
//	public String getFormatOptionsCss() {
//		
//		String formatOptionsCss = "";
//		String e = F.enter();
//		
//		
//		return formatOptionsCss;
//	}
//
//	/** Get the options as Base64 text */
//	public String getBase64FormatOptionsCss() {
//		
//		String formatOptionsCss = getFormatOptionsCss();
//
//		return new String(Base64.getEncoder().encode(formatOptionsCss.getBytes()));
//	}

//	/** Put in browser what is in editor */
//	public void refresh() {
//		
//	}

	/** Refreshes contents of viewer page when it is activated */
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);
		
		if (newPageIndex == ePubProjectFormsPageIndex) {
			
			project_ePub = r(() -> objectMapper.readValue(document.get(), EPUB_project.class));

			rootFolderFileControl.reload();
			targetFolderFileControl.reload();
			opfFileFileControl.reload();
			
			gridManifestItems.clear();
			gridManifestItems.addAll(project_ePub.manifestItems);
			
			opfManifestGridSortColumnIndex = 0;
			opfManifestGridSortColumnAsc = true;
			sortManifestGrid();
			loadManifestGrid();
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
	private FileControl addFileControl(UI ui, Composite parentComposite, String labelText, String fileType, boolean isFolder, String fieldName) {
		
		final Composite fileComposite = new Composite(parentComposite, SWT.NONE);
		//ui.addDebug(fileComposite);
	    fileComposite.setLayoutData(ui.createFillHorizontalGridData());
		fileComposite.setLayout(ui.createColumnsSpacingGridLayout(3, UI.sep));
		
		final Label fileLabel = new Label(fileComposite, SWT.NONE);
		fileLabel.setText(labelText);
		fileLabel.setLayoutData(ui.createGridData());

		final Field field = r(() -> project_ePub.getClass().getDeclaredField(fieldName));
		field.setAccessible(true);
		
		final Text fileText = new Text(fileComposite, SWT.SINGLE | SWT.BORDER);
		fileText.setText((String) r(() -> field.get(project_ePub)));
		fileText.setLayoutData(ui.createFillHorizontalGridData());

		final Button fileButton = new Button(fileComposite, SWT.NONE);
		fileButton.setText("Browse");
		fileButton.setLayoutData(ui.createWidthGridData(80));
		
		/* File name */
		fileText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent focusEvent) {
				String focusLostFileName = fileText.getText().trim();
				if (!focusLostFileName.equalsIgnoreCase((String) r(() -> field.get(project_ePub)))) {
					r(() -> { field.set(project_ePub, focusLostFileName); return null; });
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
			public void reload() {
				fileText.setText((String) r(() -> field.get(project_ePub)));
			}
			@Override
			public String getCompleteFileName() {

				return fileText.getText();
			}
		};
	}

	/** Refresh manifest */
	public void refreshManifest() {
		
		if (F.isEmpty(project_ePub.rootFolderNameWithFullPath)) {
			
			MessageDialog.openError(Display.getCurrent().getActiveShell(),
				"Refresh manifest cannot continue", "Root folder name is empty");
				return;
			//L.e("Refresh manifest", new Throwable("Root folder name is empty"));
		}
		
		Path rootFolderPath = Paths.get(project_ePub.rootFolderNameWithFullPath);
		
		linearFileNamesWithPath.clear();
		r(() -> { Files.walkFileTree(rootFolderPath, linearizeFileVisitor); return null; });
		
		project_ePub.manifestItems.clear();
		
		LinkedHashMap<String, EPUB_project_manifest_item> oldManifestItems = new LinkedHashMap<>();
		gridManifestItems.stream().forEach(gridManifestItem ->
			oldManifestItems.put(gridManifestItem.itemFileRelativePath + s + gridManifestItem.itemFileName, gridManifestItem));
		
		for (Path file: linearFileNamesWithPath) {

			Path relativePath = rootFolderPath.relativize(file);
			Path relativeParentFolder = relativePath.getParent();
			
			EPUB_project_manifest_item manifest_item = new EPUB_project_manifest_item();
			manifest_item.itemFileName = relativePath.getFileName().toString();
			manifest_item.itemFileRelativePath = relativeParentFolder == null ? "" : relativeParentFolder.toString();

			manifest_item.itemFileId = "";

			String mediaType = r(() -> Files.probeContentType(file));
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
			
			project_ePub.manifestItems.add(manifest_item);
	    }
		gridManifestItems.clear();
		gridManifestItems.addAll(project_ePub.manifestItems);	

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
		
		String id = project_ePub.manifestIDPrefix;
		
		if (project_ePub.manifestIDGuid) {
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
				break;
			}
			case itemFileRelativePathIndex: {
				manifestItemsComparator.setSortOrder(itemFileRelativePathIndex, itemFileNameIndex, itemFileIdIndex, itemFileMediaTypeIndex, itemFilePropertiesIndex);	
				break;
			}
			case itemFileIdIndex: {
				manifestItemsComparator.setSortOrder(itemFileIdIndex, itemFileNameIndex, itemFileRelativePathIndex, itemFileMediaTypeIndex, itemFilePropertiesIndex);	
				break;
			}
			case itemFileMediaTypeIndex: {
				manifestItemsComparator.setSortOrder(itemFileMediaTypeIndex, itemFileNameIndex, itemFileRelativePathIndex, itemFileIdIndex, itemFilePropertiesIndex);	
				break;
			}
			case itemFilePropertiesIndex: {
				manifestItemsComparator.setSortOrder(itemFilePropertiesIndex, itemFileNameIndex, itemFileRelativePathIndex, itemFileIdIndex, itemFileMediaTypeIndex);	
				break;
			}
		}
		
		Collections.sort(gridManifestItems, manifestItemsComparator);
		if (!opfManifestGridSortColumnAsc) {
			Collections.reverse(gridManifestItems);	
		}
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

	public EPUB_project getProject_ePub() {
		return project_ePub;
	}

	public Font getGridFont() {
		return gridFont;
	}

	public Font getGridFontItalic() {
		return gridFontItalic;
	}
}
