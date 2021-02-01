/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.ui.editor.md;

import java.io.File;
import java.util.Base64;
import java.util.LinkedHashMap;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.IDE;
import org.emdepub.activator.F;
import org.emdepub.activator.L;
import org.emdepub.activator.R;
import org.emdepub.activator.UI;
import org.emdepub.md.ui.wizards.MarkdownExportAsHtmlWizard.MarkdownExportType;
import org.emdepub.ui.editor.md.prefs.MarkdownHtmlGeneratorPrefs;
import org.emdepub.ui.editor.md.prefs.MarkdownHtmlGeneratorPrefs.FormatCodeStyle;
import org.emdepub.ui.editor.md.prefs.MarkdownHtmlGeneratorPrefs.FormatStyle;
import org.emdepub.ui.editor.md.prefs.MarkdownHtmlGeneratorPrefs.Pref;


public class MarkdownEditor extends FormEditor {

	private static final String s = F.sep();
	private static final String e = F.enter();
	
	public static final LinkedHashMap<FormatStyle, String> formatStylesCss = new LinkedHashMap<>();
	static {
		formatStylesCss.put(FormatStyle.None, null);
		formatStylesCss.put(FormatStyle.GitHub, "github-markdown");
		formatStylesCss.put(FormatStyle.GoogleLike, "google-like-markdown");
		formatStylesCss.put(FormatStyle.SemanticUILike, "semantic-ui-like-markdown");
		formatStylesCss.put(FormatStyle.Custom, "stylesheet");
	}

	public static final LinkedHashMap<Pref, Boolean> formatOptions = new LinkedHashMap<>();
	static {
		formatOptions.put(Pref.FixedContentWidth, true);
		formatOptions.put(Pref.JustifiedParagraphs, true);
		formatOptions.put(Pref.CenterHeaders, false);
	}

	public static final LinkedHashMap<FormatCodeStyle, String> formatCodeStylesCss = new LinkedHashMap<>();
	static {
		formatCodeStylesCss.put(FormatCodeStyle.None, null);
		formatCodeStylesCss.put(FormatCodeStyle.Agate, "agate");
		formatCodeStylesCss.put(FormatCodeStyle.Androidstudio, "androidstudio");
		formatCodeStylesCss.put(FormatCodeStyle.ArduinoLight, "arduino-light");
//		formatCodeStylesCss.put(FormatCodeStyle.Arta, "arta");
//		formatCodeStylesCss.put(FormatCodeStyle.Ascetic, "ascetic");
//		formatCodeStylesCss.put(FormatCodeStyle.AtelierCaveDark, "atelier-cave-dark");
//		formatCodeStylesCss.put(FormatCodeStyle.AtelierCaveLight, "atelier-cave-light");
//		formatCodeStylesCss.put(FormatCodeStyle.AtelierDuneDark, "atelier-dune-dark");
//		formatCodeStylesCss.put(FormatCodeStyle.AtelierDuneLight, "atelier-dune-light");
//		formatCodeStylesCss.put(FormatCodeStyle.AtelierEstuaryDark, "atelier-estuary-dark");
//		formatCodeStylesCss.put(FormatCodeStyle.AtelierEstuaryLight, "atelier-estuary-light");
//		formatCodeStylesCss.put(FormatCodeStyle.AtelierForestDark, "atelier-forest-dark");
//		formatCodeStylesCss.put(FormatCodeStyle.AtelierForestLight, "atelier-forest-light");
//		formatCodeStylesCss.put(FormatCodeStyle.AtelierHeathDark, "atelier-heath-dark");
//		formatCodeStylesCss.put(FormatCodeStyle.AtelierHeathLight, "atelier-heath-light");
//		formatCodeStylesCss.put(FormatCodeStyle.AtelierLakesideDark, "atelier-lakeside-dark");
//		formatCodeStylesCss.put(FormatCodeStyle.AtelierLakesideLight, "atelier-lakeside-light");
//		formatCodeStylesCss.put(FormatCodeStyle.AtelierPlateauDark, "atelier-plateau-dark");
//		formatCodeStylesCss.put(FormatCodeStyle.AtelierPlateauLight, "atelier-plateau-light");
//		formatCodeStylesCss.put(FormatCodeStyle.AtelierSavannaDark, "atelier-savanna-dark");
//		formatCodeStylesCss.put(FormatCodeStyle.AtelierSavannaLight, "atelier-savanna-light");
//		formatCodeStylesCss.put(FormatCodeStyle.AtelierSeasideDark, "atelier-seaside-dark");
//		formatCodeStylesCss.put(FormatCodeStyle.AtelierSeasideLight, "atelier-seaside-light");
//		formatCodeStylesCss.put(FormatCodeStyle.AtelierSulphurpoolDark, "atelier-sulphurpool-dark");
//		formatCodeStylesCss.put(FormatCodeStyle.AtelierSulphurpoolLight, "atelier-sulphurpool-light");
//		formatCodeStylesCss.put(FormatCodeStyle.AtomOneDark, "atom-one-dark");
//		formatCodeStylesCss.put(FormatCodeStyle.AtomOneLight, "atom-one-light");
//		formatCodeStylesCss.put(FormatCodeStyle.BrownPaper, "brown-paper");
//		formatCodeStylesCss.put(FormatCodeStyle.CodepenEmbed, "codepen-embed");
//		formatCodeStylesCss.put(FormatCodeStyle.ColorBrewer, "color-brewer");
//		formatCodeStylesCss.put(FormatCodeStyle.Darcula, "darcula");
		formatCodeStylesCss.put(FormatCodeStyle.Dark, "dark");
//		formatCodeStylesCss.put(FormatCodeStyle.Darkula, "darkula");
		formatCodeStylesCss.put(FormatCodeStyle.Default, "default");
//		formatCodeStylesCss.put(FormatCodeStyle.Docco, "docco");
//		formatCodeStylesCss.put(FormatCodeStyle.Dracula, "dracula");
		formatCodeStylesCss.put(FormatCodeStyle.Far, "far");
		formatCodeStylesCss.put(FormatCodeStyle.Foundation, "foundation");
		formatCodeStylesCss.put(FormatCodeStyle.GithubGist, "github-gist");
		formatCodeStylesCss.put(FormatCodeStyle.Github, "github");
		formatCodeStylesCss.put(FormatCodeStyle.Googlecode, "googlecode");
		formatCodeStylesCss.put(FormatCodeStyle.Grayscale, "grayscale");
		formatCodeStylesCss.put(FormatCodeStyle.GruvboxDark, "gruvbox-dark");
		formatCodeStylesCss.put(FormatCodeStyle.GruvboxLight, "gruvbox-light");
//		formatCodeStylesCss.put(FormatCodeStyle.Hopscotch, "hopscotch");
		formatCodeStylesCss.put(FormatCodeStyle.Hybrid, "hybrid");
		formatCodeStylesCss.put(FormatCodeStyle.Idea, "idea");
		formatCodeStylesCss.put(FormatCodeStyle.IrBlack, "ir-black");
//		formatCodeStylesCss.put(FormatCodeStyle.KimbieDark, "kimbie.dark");
//		formatCodeStylesCss.put(FormatCodeStyle.KimbieLight, "kimbie.light");
		formatCodeStylesCss.put(FormatCodeStyle.Magula, "magula");
//		formatCodeStylesCss.put(FormatCodeStyle.MonoBlue, "mono-blue");
//		formatCodeStylesCss.put(FormatCodeStyle.MonokaiSublime, "monokai-sublime");
//		formatCodeStylesCss.put(FormatCodeStyle.Monokai, "monokai");
//		formatCodeStylesCss.put(FormatCodeStyle.Obsidian, "obsidian");
//		formatCodeStylesCss.put(FormatCodeStyle.Ocean, "ocean");
//		formatCodeStylesCss.put(FormatCodeStyle.ParaisoDark, "paraiso-dark");
//		formatCodeStylesCss.put(FormatCodeStyle.ParaisoLight, "paraiso-light");
//		formatCodeStylesCss.put(FormatCodeStyle.Pojoaque, "pojoaque");
		formatCodeStylesCss.put(FormatCodeStyle.Purebasic, "purebasic");
//		formatCodeStylesCss.put(FormatCodeStyle.Qtcreator_dark, "qtcreator_dark");
//		formatCodeStylesCss.put(FormatCodeStyle.Qtcreator_light, "qtcreator_light");
		formatCodeStylesCss.put(FormatCodeStyle.Railscasts, "railscasts");
//		formatCodeStylesCss.put(FormatCodeStyle.Rainbow, "rainbow");
//		formatCodeStylesCss.put(FormatCodeStyle.Routeros, "routeros");
//		formatCodeStylesCss.put(FormatCodeStyle.SchoolBook, "school-book");
//		formatCodeStylesCss.put(FormatCodeStyle.SolarizedDark, "solarized-dark");
//		formatCodeStylesCss.put(FormatCodeStyle.SolarizedLight, "solarized-light");
		formatCodeStylesCss.put(FormatCodeStyle.Sunburst, "sunburst");
//		formatCodeStylesCss.put(FormatCodeStyle.TomorrowNightBlue, "tomorrow-night-blue");
//		formatCodeStylesCss.put(FormatCodeStyle.TomorrowNightBright, "tomorrow-night-bright");
//		formatCodeStylesCss.put(FormatCodeStyle.TomorrowNightEighties, "tomorrow-night-eighties");
//		formatCodeStylesCss.put(FormatCodeStyle.TomorrowNight, "tomorrow-night");
//		formatCodeStylesCss.put(FormatCodeStyle.Tomorrow, "tomorrow");
		formatCodeStylesCss.put(FormatCodeStyle.Vs, "vs");
		formatCodeStylesCss.put(FormatCodeStyle.Vs2015, "vs2015");
		formatCodeStylesCss.put(FormatCodeStyle.Xcode, "xcode");
//		formatCodeStylesCss.put(FormatCodeStyle.Xt256, "xt256");
//		formatCodeStylesCss.put(FormatCodeStyle.Zenburn, "zenburn");
		formatCodeStylesCss.put(FormatCodeStyle.Custom, "custom");
	}
	
	public static final LinkedHashMap<FormatCodeStyle, String> formatCodeStylesPre = new LinkedHashMap<>();
	static {
		formatCodeStylesPre.put(FormatCodeStyle.None, null);
		formatCodeStylesPre.put(FormatCodeStyle.Agate, "background: rgb(51, 51, 51);color: white;");
		formatCodeStylesPre.put(FormatCodeStyle.Androidstudio, "background: rgb(40, 43, 46);color: rgb(169, 183, 198);");
		formatCodeStylesPre.put(FormatCodeStyle.ArduinoLight, "background: rgb(255, 255, 255);color: rgb(67, 79, 84);");
//		formatCodeStylesPre.put(FormatCodeStyle.Arta, "background: rgb(34, 34, 34);color: rgb(170, 170, 170);");
//		formatCodeStylesPre.put(FormatCodeStyle.Ascetic, "background: white;color: black;");
//		formatCodeStylesPre.put(FormatCodeStyle.AtelierCaveDark, "background: rgb(25, 23, 28);color: rgb(139, 135, 146);");
//		formatCodeStylesPre.put(FormatCodeStyle.AtelierCaveLight, "background: rgb(239, 236, 244);color: rgb(88, 82, 96);");
//		formatCodeStylesPre.put(FormatCodeStyle.AtelierDuneDark, "background: rgb(32, 32, 29);color: rgb(166, 162, 140);");
//		formatCodeStylesPre.put(FormatCodeStyle.AtelierDuneLight, "background: rgb(254, 251, 236);color: rgb(110, 107, 94);");
//		formatCodeStylesPre.put(FormatCodeStyle.AtelierEstuaryDark, "background: rgb(34, 34, 27);color: rgb(146, 145, 129);");
//		formatCodeStylesPre.put(FormatCodeStyle.AtelierEstuaryLight, "background: rgb(244, 243, 236);color: rgb(95, 94, 78);");
//		formatCodeStylesPre.put(FormatCodeStyle.AtelierForestDark, "background: rgb(27, 25, 24);color: rgb(168, 161, 159);");
//		formatCodeStylesPre.put(FormatCodeStyle.AtelierForestLight, "background: rgb(241, 239, 238);color: rgb(104, 97, 94);");
//		formatCodeStylesPre.put(FormatCodeStyle.AtelierHeathDark, "background: rgb(27, 24, 27);color: rgb(171, 155, 171);");
//		formatCodeStylesPre.put(FormatCodeStyle.AtelierHeathLight, "background: rgb(247, 243, 247);color: rgb(105, 93, 105);");
//		formatCodeStylesPre.put(FormatCodeStyle.AtelierLakesideDark, "background: rgb(22, 27, 29);color: rgb(126, 162, 180);");
//		formatCodeStylesPre.put(FormatCodeStyle.AtelierLakesideLight, "background: rgb(235, 248, 255);color: rgb(81, 109, 123);");
//		formatCodeStylesPre.put(FormatCodeStyle.AtelierPlateauDark, "background: rgb(27, 24, 24);color: rgb(138, 133, 133);");
//		formatCodeStylesPre.put(FormatCodeStyle.AtelierPlateauLight, "background: rgb(244, 236, 236);color: rgb(88, 80, 80);");
//		formatCodeStylesPre.put(FormatCodeStyle.AtelierSavannaDark, "background: rgb(23, 28, 25);color: rgb(135, 146, 138);");
//		formatCodeStylesPre.put(FormatCodeStyle.AtelierSavannaLight, "background: rgb(236, 244, 238);color: rgb(82, 96, 87);");
//		formatCodeStylesPre.put(FormatCodeStyle.AtelierSeasideDark, "background: rgb(19, 21, 19);color: rgb(140, 166, 140);");
//		formatCodeStylesPre.put(FormatCodeStyle.AtelierSeasideLight, "background: rgb(244, 251, 244);color: rgb(94, 110, 94);");
//		formatCodeStylesPre.put(FormatCodeStyle.AtelierSulphurpoolDark, "background: rgb(32, 39, 70);color: rgb(151, 157, 180);");
//		formatCodeStylesPre.put(FormatCodeStyle.AtelierSulphurpoolLight, "background: rgb(245, 247, 255);color: rgb(94, 102, 135);");
//		formatCodeStylesPre.put(FormatCodeStyle.AtomOneDark, "background: rgb(40, 44, 52);color: rgb(171, 178, 191);");
//		formatCodeStylesPre.put(FormatCodeStyle.AtomOneLight, "background: rgb(250, 250, 250);color: rgb(56, 58, 66);");
//		formatCodeStylesPre.put(FormatCodeStyle.BrownPaper, "background: rgb(183, 166, 142) url(./brown-papersq.png);color: rgb(54, 60, 105);");
//		formatCodeStylesPre.put(FormatCodeStyle.CodepenEmbed, "background: rgb(34, 34, 34);color: rgb(255, 255, 255);");
//		formatCodeStylesPre.put(FormatCodeStyle.ColorBrewer, "background: rgb(255, 255, 255);color: rgb(0, 0, 0);");
//		formatCodeStylesPre.put(FormatCodeStyle.Darcula, "background: rgb(43, 43, 43);color: rgb(186, 186, 186);");
		formatCodeStylesPre.put(FormatCodeStyle.Dark, "background: rgb(68, 68, 68);color: rgb(221, 221, 221);");
		formatCodeStylesPre.put(FormatCodeStyle.Default, "background: rgb(240, 240, 240);color: rgb(68, 68, 68);");
//		formatCodeStylesPre.put(FormatCodeStyle.Docco, "background: rgb(248, 248, 255);color: rgb(0, 0, 0);");
//		formatCodeStylesPre.put(FormatCodeStyle.Dracula, "background: rgb(40, 42, 54);color: rgb(248, 248, 242);");
		formatCodeStylesPre.put(FormatCodeStyle.Far, "background: rgb(0, 0, 128);color: rgb(0, 255, 255);");
		formatCodeStylesPre.put(FormatCodeStyle.Foundation, "background: rgb(238, 238, 238);color: black;");
		formatCodeStylesPre.put(FormatCodeStyle.GithubGist, "background: white;color: rgb(51, 51, 51);");
		formatCodeStylesPre.put(FormatCodeStyle.Github, "background: rgb(248, 248, 248);color: rgb(51, 51, 51);");
		formatCodeStylesPre.put(FormatCodeStyle.Googlecode, "background: white;color: black;");
		formatCodeStylesPre.put(FormatCodeStyle.Grayscale, "background: rgb(255, 255, 255);color: rgb(51, 51, 51);");
		formatCodeStylesPre.put(FormatCodeStyle.GruvboxDark, "background: rgb(40, 40, 40);color: rgb(235, 219, 178);");
		formatCodeStylesPre.put(FormatCodeStyle.GruvboxLight, "background: rgb(251, 241, 199);color: rgb(60, 56, 54);");
//		formatCodeStylesPre.put(FormatCodeStyle.Hopscotch, "background: rgb(50, 41, 49);color: rgb(185, 181, 184);");
		formatCodeStylesPre.put(FormatCodeStyle.Hybrid, "background: rgb(29, 31, 33);color: rgb(197, 200, 198);");
		formatCodeStylesPre.put(FormatCodeStyle.Idea, "background: rgb(255, 255, 255);color: rgb(0, 0, 0);");
		formatCodeStylesPre.put(FormatCodeStyle.IrBlack, "background: rgb(0, 0, 0);color: rgb(248, 248, 248);");
//		formatCodeStylesPre.put(FormatCodeStyle.KimbieDark, "background: rgb(34, 26, 15);color: rgb(211, 175, 134);");
//		formatCodeStylesPre.put(FormatCodeStyle.KimbieLight, "background: rgb(251, 235, 212);color: rgb(132, 97, 61);");
		formatCodeStylesPre.put(FormatCodeStyle.Magula, "color: black;");
//		formatCodeStylesPre.put(FormatCodeStyle.MonoBlue, "background: rgb(234, 238, 243);color: rgb(0, 25, 58);");
//		formatCodeStylesPre.put(FormatCodeStyle.MonokaiSublime, "background: rgb(35, 36, 31);color: rgb(248, 248, 242);");
//		formatCodeStylesPre.put(FormatCodeStyle.Monokai, "background: rgb(39, 40, 34);color: rgb(221, 221, 221);");
//		formatCodeStylesPre.put(FormatCodeStyle.Obsidian, "background: rgb(40, 43, 46);color: rgb(224, 226, 228);");
//		formatCodeStylesPre.put(FormatCodeStyle.Ocean, "background: rgb(43, 48, 59);color: rgb(192, 197, 206);");
//		formatCodeStylesPre.put(FormatCodeStyle.ParaisoDark, "background: rgb(47, 30, 46);color: rgb(163, 158, 155);");
//		formatCodeStylesPre.put(FormatCodeStyle.ParaisoLight, "background: rgb(231, 233, 219);color: rgb(79, 66, 76);");
//		formatCodeStylesPre.put(FormatCodeStyle.Pojoaque, "background: url(./pojoaque.jpg) repeat scroll left top rgb(24, 25, 20);color: rgb(220, 207, 143);");
		formatCodeStylesPre.put(FormatCodeStyle.Purebasic, "background: rgb(255, 255, 223);color: rgb(0, 0, 0);");
//		formatCodeStylesPre.put(FormatCodeStyle.Qtcreator_dark, "background: rgb(0, 0, 0);color: rgb(170, 170, 170);");
//		formatCodeStylesPre.put(FormatCodeStyle.Qtcreator_light, "background: rgb(255, 255, 255);color: rgb(0, 0, 0);");
		formatCodeStylesPre.put(FormatCodeStyle.Railscasts, "background: rgb(35, 35, 35);color: rgb(230, 225, 220);");
//		formatCodeStylesPre.put(FormatCodeStyle.Rainbow, "background: rgb(71, 73, 73);color: rgb(209, 217, 225);");
//		formatCodeStylesPre.put(FormatCodeStyle.Routeros, "background: rgb(240, 240, 240);color: rgb(68, 68, 68);");
//		formatCodeStylesPre.put(FormatCodeStyle.SchoolBook, "color: rgb(62, 89, 21);");
//		formatCodeStylesPre.put(FormatCodeStyle.SolarizedDark, "background: rgb(0, 43, 54);color: rgb(131, 148, 150);");
//		formatCodeStylesPre.put(FormatCodeStyle.SolarizedLight, "background: rgb(253, 246, 227);color: rgb(101, 123, 131);");
		formatCodeStylesPre.put(FormatCodeStyle.Sunburst, "background: rgb(0, 0, 0);color: rgb(248, 248, 248);");
//		formatCodeStylesPre.put(FormatCodeStyle.TomorrowNightBlue, "background: rgb(0, 36, 81);color: white;");
//		formatCodeStylesPre.put(FormatCodeStyle.TomorrowNightBright, "background: black;color: rgb(234, 234, 234);");
//		formatCodeStylesPre.put(FormatCodeStyle.TomorrowNightEighties, "background: rgb(45, 45, 45);color: rgb(204, 204, 204);");
//		formatCodeStylesPre.put(FormatCodeStyle.TomorrowNight, "background: rgb(29, 31, 33);color: rgb(197, 200, 198);");
//		formatCodeStylesPre.put(FormatCodeStyle.Tomorrow, "background: white;color: rgb(77, 77, 76);");
		formatCodeStylesPre.put(FormatCodeStyle.Vs, "background: white;color: black;");
		formatCodeStylesPre.put(FormatCodeStyle.Vs2015, "background: rgb(30, 30, 30);color: rgb(220, 220, 220);");
		formatCodeStylesPre.put(FormatCodeStyle.Xcode, "background: rgb(255, 255, 255);color: black;");
//		formatCodeStylesPre.put(FormatCodeStyle.Xt256, "background: rgb(0, 0, 0);color: rgb(234, 234, 234);");
//		formatCodeStylesPre.put(FormatCodeStyle.Zenburn, "background: rgb(63, 63, 63);color: rgb(220, 220, 220);");
		formatCodeStylesPre.put(FormatCodeStyle.Custom, "background: white;color: black;");
	}
	
	private static final String mdExportTemplate = R.getTextResourceAsString("texts/md-export-template.html");
	private static final String mdFixedContentWidthCss = R.getTextResourceAsString("texts/md-fixed-content-width.css"); 
	private static final String mdJustifiedParagraphsCss = R.getTextResourceAsString("texts/md-justified-paragraphs.css"); 
	private static final String mdCenterHeadersCss = R.getTextResourceAsString("texts/md-center-headers.css"); 
	
	
	private static final String mdViewerFolderNameWithPath = "C:/Iustin/Programming/_emdepub/repositories/emdepub/org.emdepub/code-language/viewers/markdown";

	
	/** The browser will display the Markdown HTML */
	private Browser viewerBrowser;
	private int browserViewerPageIndex;

	//private ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

	
	/** The Markdown editor */
	private MarkdownTextEditor markdownTextEditor;
	private int markdownTextEditorPageIndex;
	private MarkdownHtmlGeneratorPrefs markdownHtmlGeneratorPrefs;
	private String prefsPropertiesFileNameWithPath;

//	private IEditorPart markdownTextEditorPart;
//	private IDocument sourceDocument;

	/** Creates the viewer page of the forms editor */
	private void createBrowserViewerPage() {
		
		UI ui = new UI(false, Display.getCurrent());
		
		Composite viewerPageComposite = new Composite(getContainer(), SWT.NONE);
		viewerPageComposite.setLayout(ui.createMarginsGridLayout(1));
	
		viewerBrowser = new Browser(viewerPageComposite, SWT.NONE);
		viewerBrowser.setJavascriptEnabled(true);
		viewerBrowser.setText("");
		viewerBrowser.setLayoutData(ui.createFillBothGridData());
		
		/** Status bar */
		viewerBrowser.addStatusTextListener(new StatusTextListener() {
			
			@Override
			public void changed(StatusTextEvent statusTextEvent) {
				//MarkdownSemanticEPEditorContributor.getLinkField().setText(statusTextEvent.text);
			}
		});

		/** Progress */
		viewerBrowser.addProgressListener(new ProgressListener() {
            public void changed(ProgressEvent progressEvent) {
            	/* ILB */
            }
            public void completed(ProgressEvent progressEvent) {
            
            	String formatStyleCss = formatStylesCss.get(markdownHtmlGeneratorPrefs.get(Pref.FormatStyle));
            	String formatCodeStyleCss = formatCodeStylesCss.get(markdownHtmlGeneratorPrefs.get(Pref.FormatCodeStyle));
            	
            	viewerBrowser.execute("window.markdownSettings = {}; " +
        			"window.markdownSettings.markdownText = '" + getBase64MarkdownText() + "'; " +
        			"window.markdownSettings.formatOptionsCss = '" + getBase64FormatOptionsCss() + "'; " +
        			(formatStyleCss == null ? "" : "window.markdownSettings.cssName = '" + formatStyleCss + "'; ") +
        			(formatCodeStyleCss == null ? "" : "window.markdownSettings.cssHighlightName = '" + "highlight/" +  formatCodeStyleCss + "'; ") +
        			"window.markdownSettings.localBaseHref = '" + findLocalBaseHref() + "'; " +
        			"reload();");
            }
        });
		
		browserViewerPageIndex = addPage(viewerPageComposite);
		setPageText(browserViewerPageIndex, "Display");
	}

	/** Creates Markdown text editor page of the forms editor */
	private void createMarkdownTextEditorPage() {

		markdownHtmlGeneratorPrefs = new MarkdownHtmlGeneratorPrefs();
		
		markdownTextEditor = new MarkdownTextEditor();
		try {
			markdownTextEditorPageIndex = addPage(markdownTextEditor, getEditorInput());
		} catch (PartInitException partInitException) {
			L.e("createMarkdownTextEditorPage", partInitException);
		}
		
		this.setPartName(markdownTextEditor.getTitle());
		
		setPageText(markdownTextEditorPageIndex, "Markdown Editor");
		//setSourcePage(markdownTextEditor);
		//sourceDocument = markdownTextEditor.getDocumentProvider().getDocument(markdownTextEditor.getEditorInput());
		
		prefsPropertiesFileNameWithPath = getSourceMarkdownFilePathAndName() + ".prefs";
	}

	/** Create pages */
	@Override
	protected void addPages() {

		createBrowserViewerPage();
		createMarkdownTextEditorPage();
	}
	
	/** LocalBaseHref */
	private String findLocalBaseHref() {
		
		IFile iFile = (IFile) markdownTextEditor.getEditorInput().getAdapter(IFile.class);
		File file = iFile.getLocation().toFile();
		String rootPath = file.getPath().substring(0, file.getPath().length() - file.getName().length()).replace("\\", "/");
		
		return "file:///" + rootPath;
	}

	/** Source Markdown file path and name */
	public String getSourceMarkdownFilePathAndName() {
		
		IFile iFile = (IFile) markdownTextEditor.getEditorInput().getAdapter(IFile.class);
		return iFile.getLocation().toOSString();
	}

	/** Save from here */
	private void savePrefsProperties() {
		
		markdownHtmlGeneratorPrefs.saveProperties(prefsPropertiesFileNameWithPath);
	}
	
	/** Export */
	public void exportAsHtml(MarkdownExportType markdownExportType, String exportName, String exportLocation) {

		String formatStyleCss = formatStylesCss.get(markdownHtmlGeneratorPrefs.get(Pref.FormatStyle));
		String formatCodeStyleCss = formatCodeStylesCss.get(markdownHtmlGeneratorPrefs.get(Pref.FormatCodeStyle));
		
    	String htmlText = (String) viewerBrowser.evaluate("window.markdownSettings = {}; " +
			"window.markdownSettings.markdownText = '" + getBase64MarkdownText() + "'; " +
			(formatCodeStyleCss == null ? "" : "window.markdownSettings.cssHighlightName = '" + "highlight/" +  formatCodeStyleCss + "'; ") +
			"window.markdownSettings.localBaseHref = null; " +
			"return createHTML();");

    	if (markdownExportType == MarkdownExportType.ExportFileOnly) {
    		String exportFileNameWithPath = exportLocation + s + exportName + ".html";
    		
    		closeExternalFile(exportFileNameWithPath);
    		
    		F.saveStringToFile(htmlText, exportFileNameWithPath);
    		openExternalFile(exportFileNameWithPath);
    	}
    	else {
    		String exportFolderNameWithPath = exportLocation + s + exportName;
    		String exportFileNameWithPath = exportFolderNameWithPath + s + exportName + ".html";

    		closeExternalFile(exportFileNameWithPath);
    		
    		F.deleteFolder(exportFolderNameWithPath);
    		F.createFoldersIfNotExists(exportFolderNameWithPath);
    		
    		String markdownExport = mdExportTemplate;
    		markdownExport = markdownExport.replace("{{export-content}}", htmlText);
    		markdownExport = markdownExport.replace("{{export-title}}", exportName);

    		String exportCss = "";
    		
    		/* CSS */
    		if (formatStyleCss != null) {
    			String exportCssFolderNameWithPath = exportFolderNameWithPath + s + "css";
    			F.createFoldersIfNotExists(exportCssFolderNameWithPath);
        		String exportFormatStylesCss = formatStyleCss + ".css";
        		F.copyFile(mdViewerFolderNameWithPath + s + "styles" + s + exportFormatStylesCss,
        			exportCssFolderNameWithPath + s + exportFormatStylesCss);
        		exportCss = exportCss + e + "<link rel=\"stylesheet\" href=\"css/" + exportFormatStylesCss + "\">" + e;
    		}
    		
    		/* CSS Highlight */
    		if (formatCodeStyleCss != null) {
        		String exportCssHighlightFolderNameWithPath = exportFolderNameWithPath + s + "css" + s + "highlight";
    			F.createFoldersIfNotExists(exportCssHighlightFolderNameWithPath);
        		String exportFormatCodeStylesCss = formatCodeStyleCss + ".css";
        		F.copyFile(mdViewerFolderNameWithPath + s + "styles/highlight" + s + exportFormatCodeStylesCss,
       				exportCssHighlightFolderNameWithPath + s + exportFormatCodeStylesCss);
        		exportCss = exportCss + e + "<link rel=\"stylesheet\" href=\"css/highlight/" + exportFormatCodeStylesCss + "\">" + e;
    		}

    		/* CSS options */
    		String formatOptionsCss = getFormatOptionsCss();
    		if (formatOptionsCss.trim().length() > 0) {
    			formatOptionsCss = e + "<style>" + formatOptionsCss + e + "</style>" + e;
    			exportCss = exportCss + e + formatOptionsCss + e;
    		}
    		
    		markdownExport = markdownExport.replace("{{export-css}}", exportCss);
			

    		F.saveStringToFile(markdownExport, exportFileNameWithPath);
    		
    		openExternalFile(exportFileNameWithPath);
    	}
	}
	
	/** Get the Markdown as Base64 text */
	public String getBase64MarkdownText() {
		
		String mdText = markdownTextEditor.getDocumentProvider().getDocument(markdownTextEditor.getEditorInput()).get();
		
		return new String(Base64.getEncoder().encode(mdText.getBytes()));
	}

	/** Get the options CSSs */
	public String getFormatOptionsCss() {
		
		String formatOptionsCss = "";
		String e = F.enter();
		
		if (markdownHtmlGeneratorPrefs.<Boolean>get(Pref.FixedContentWidth)) {
			formatOptionsCss = formatOptionsCss + e + mdFixedContentWidthCss;
		}

		if (markdownHtmlGeneratorPrefs.<Boolean>get(Pref.JustifiedParagraphs)) {
			formatOptionsCss = formatOptionsCss + e + mdJustifiedParagraphsCss;
		}

		if (markdownHtmlGeneratorPrefs.<Boolean>get(Pref.CenterHeaders)) {
			formatOptionsCss = formatOptionsCss + e + mdCenterHeadersCss;
		}
		
		String formatCodeStylePre = formatCodeStylesPre.get(markdownHtmlGeneratorPrefs.get(Pref.FormatCodeStyle));
		if (formatCodeStylePre != null) {
			String formatCodeStylesCssBackground = "article.markdown-body pre {" + e + formatCodeStylePre + e + "}";
			formatOptionsCss = formatOptionsCss + e + formatCodeStylesCssBackground;
		}
		
		/* Kept here to fill the lists */
//		for (FormatCodeStyle formatCodeStyle : FormatCodeStyle.values()) {
//
//			String formatCodeStylesCssFileName = formatCodeStylesCss.get(formatCodeStyle) + ".css";
//			String formatCodeStylesCssFileNameWithPath = mdViewerFolderNameWithPath + s + "styles/highlight" + s + formatCodeStylesCssFileName;
//
//			CSSOMParser cssomParser = new CSSOMParser(new SACParserCSS3());
//			InputSource inputSource;
//			try {
//				inputSource = new InputSource(new FileReader(formatCodeStylesCssFileNameWithPath));
//				CSSStyleSheet cssStyleSheet = cssomParser.parseStyleSheet(inputSource, null, null);
//				
//				
//				CSSRuleList rules = cssStyleSheet.getCssRules();
//				for (int i = 0; i < rules.getLength(); i++) {
//				    final CSSRule rule = rules.item(i);
//				    
//				    if (rule instanceof CSSStyleRule) {
//					    CSSStyleRule cssStyleRule = (CSSStyleRule) rule;
//					    //System.out.println(cssStyleRule.getSelectorText());
//					    if (cssStyleRule.getSelectorText().equals(".hljs") || cssStyleRule.getSelectorText().contains(".hljs,")) {
//					    
//					    	String pre = "";
//					    	String background = cssStyleRule.getStyle().getPropertyValue("background");
//					    	if (!background.equals("")) {
//					    		pre = pre + "background: " + background + ";";
//					    	}
//					    	String color = cssStyleRule.getStyle().getPropertyValue("color");
//					    	if (!color.equals("")) {
//					    		pre = pre + "color: " + color + ";";
//					    	}
//					    	
//					    	System.out.println("formatCodeStylesPre.put(FormatCodeStyle." + formatCodeStyle + ", \"" + pre + "\");");
//					    }
//				    }
//				}
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//		}
		
		return formatOptionsCss;
	}

	/** Get the options as Base64 text */
	public String getBase64FormatOptionsCss() {
		
		String formatOptionsCss = getFormatOptionsCss();

		return new String(Base64.getEncoder().encode(formatOptionsCss.getBytes()));
	}

	/** Open external file in IDE */
	private void openExternalFile(String fileNameWithPath) {

		IFileStore fileStore = EFS.getLocalFileSystem().getStore(new Path(fileNameWithPath));
		// fileStore = fileStore.getChild(names[i]);
		if (!fileStore.fetchInfo().isDirectory() && fileStore.fetchInfo().exists()) {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			try {
				IDE.openEditor(page, new FileStoreEditorInput(fileStore), "org.eclipse.ui.genericeditor.GenericEditor");
			} catch (PartInitException partInitException) {
				L.e("openExternalFile", partInitException);
			}
		}
	}

	/** Close external file opened in IDE */
	private void closeExternalFile(String fileNameWithPath) {

		String fileName = fileNameWithPath.substring(F.getFileFolderName(fileNameWithPath).length() + 1);
		
		IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
		
		for (IEditorReference editorReference : workbenchPage.getEditorReferences()) {
			if (editorReference.getName().equals(fileName)) {
				try {
					String fullPath = java.nio.file.Path.of(editorReference.getEditorInput().getAdapter(FileStoreEditorInput.class).getURI()).toString();
					if (fullPath.equals(fileNameWithPath)) {
						workbenchPage.closeEditor(editorReference.getEditor(true), true);
					}
				} catch (PartInitException partInitException) {
					/* ILB */
				}
			}
//			System.out.println(java.nio.file.Path.of(editorReference.getEditorInput().getAdapter(FileStoreEditorInput.class).getURI()));
//			System.out.println(editorReference);
		};
	}

	/** Put in browser what is in editor */
	public void refresh() {
		//System.out.println("refresh " + getMarkdownText());
		// TODO where is the index
		viewerBrowser.setUrl("file://C:/Iustin/Programming/_emdepub/repositories/emdepub/org.emdepub/code-language/viewers/markdown/index-md.html");
	}

	/** Refreshes contents of viewer page when it is activated */
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);
		
		if (newPageIndex == browserViewerPageIndex) {
			refresh();
		}
//		if (newPageIndex == preferencesPageIndex) {
//			preferencesUI.setInitialFocus();
//		}
	}

	/** Saves the forms editor's document */
	@Override
	public void doSave(IProgressMonitor progressMonitor) {
		
		markdownTextEditor.doSave(progressMonitor);
	}

	/** Saves the forms editor's document as another file */
	@Override
	public void doSaveAs() {

		markdownTextEditor.doSaveAs();
		this.setPartName(markdownTextEditor.getTitle());
		setInput(markdownTextEditor.getEditorInput());
	}
	
//	/** Saves the multi-page editor's document as another file */
//	public void doSaveAs() {
//		textEditorPart.doSaveAs();
//		setPageText(textEditorPageIndex, textEditorPart.getTitle());
//		setInput(textEditorPart.getEditorInput());
//	}
	
	/** Saves the forms editor's document */
	@Override
	public boolean isSaveAsAllowed() {

		return true;
	}

	public MarkdownHtmlGeneratorPrefs getMarkdownHtmlGeneratorPrefs() {
		return markdownHtmlGeneratorPrefs;
	}

	public MarkdownTextEditor getMarkdownTextEditor() {
		return markdownTextEditor;
	}

	public int getBrowserViewerPageIndex() {
		return browserViewerPageIndex;
	}

	public int getMarkdownTextEditorPageIndex() {
		return markdownTextEditorPageIndex;
	}

//	/**
//	 * @param sourcePage The sourcePage to set.
//	 */
//	public void setSourcePage(ITextEditor sourcePage) {
//		this.markdownTextEditor = sourcePage;
//		this.sourcePageIndex = pages.indexOf(sourcePage);
//	}

	
	
}
