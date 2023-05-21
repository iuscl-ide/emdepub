package com.staticwebsite;

import com.staticwebsite.commons.CU;
import com.staticwebsite.md.MarkdownHtmlGeneratorEngine;

import lombok.extern.flogger.Flogger;

@Flogger
public class StaticWebSiteGeneratorMain {

	/*
	 * For Lombok: in plugin run --> VM arguments:
	 * 
	 * -javaagent:<path-to-lombok-jar>/lombok.jar
	 * 
	 * And, on Plug-ins --> org.projectlombok.feature --> Plug-in Location:
	 * 
	 * Workspace 
	 */
	
	public static final String rootPath = "C:\\Iustin\\Programming\\_emdepub\\repositories\\emdepub\\org.emdepub\\emdepub-projects\\a";
	
	public static void main(String[] args) {

		StaticWebSiteGeneratorMain siteMain = new StaticWebSiteGeneratorMain();
		siteMain.generate();
	}

	private void generate() {
		
		final String s = CU.S;
		
		String sourcePath = rootPath + s + "site-source";
		
		String sourceMarkdownPath = sourcePath + s + "markdown";
		
		String markdownPagesPath = sourceMarkdownPath + s + "pages";
		
		/* MD */
		
		String htmlIndexContent = MarkdownHtmlGeneratorEngine.generate(CU.loadFileInString(markdownPagesPath + s + "index.md"));
		String htmlFaqContent = MarkdownHtmlGeneratorEngine.generate(CU.loadFileInString(markdownPagesPath + s + "faq.md"));
		String htmlScreenshotsContent = MarkdownHtmlGeneratorEngine.generate(CU.loadFileInString(markdownPagesPath + s + "screenshots.md"));
		//String out = MarkdownHtmlGeneratorEngine.generate(CU.loadFileInString(sourceMarkdownPath + s + "docs" + s + "conclusion.md"));
		
		
		String sitePath = rootPath + s + "site-target" + s + "site";
		
		log.atInfo().log("Delete folder: %s", sitePath);
		CU.deleteFolder(sitePath);
		
		log.atInfo().log("Create folder: %s", sitePath);
		CU.createFoldersIfNotExists(sitePath);
		
		CU.copyFolderContentIntoFolder(rootPath + s + "site-static-resources", sitePath);
		//CU.copyFolderWithNameIntoFolder(rootPath + s + "site-static-resources", sitePath);
		//CU.copyFileIntoFolder(sourcePath + s + "html" + s + "hf-t.html", sitePath);
		//CU.copyFileIntoRenamedFile(sourcePath + s + "html" + s + "hf-t.html", sitePath + s + "index.html");
		
		String htmlHeaderFoooter = CU.loadFileInString(sourcePath + s + "html" + s + "hf-t.html");
		
		String htmlHeader = htmlHeaderFoooter.substring(0, htmlHeaderFoooter.indexOf("<!-- 8< 1 -->"));
		String htmlFooter = htmlHeaderFoooter.substring(htmlHeaderFoooter.indexOf("<!-- 8< 2 -->"));
		
		String htmlLogos = CU.loadFileInString(sourcePath + s + "html" + s + "page-logos.html");
		
		/* index.html */
		String htmlIndexLogo = htmlLogos.substring(htmlLogos.indexOf("<!-- 8< 1 -->") + "<!-- 8< 1 -->".length(), htmlLogos.indexOf("<!-- 8< 2 -->"));
		String htmlIndex = htmlHeader + htmlIndexLogo + htmlIndexContent + htmlFooter;
		htmlIndex = htmlIndex.replace("""
			<a class="item" href="index.html">""", """
			<a class="active item" href="index.html">""");
		CU.saveStringToFile(htmlIndex, sitePath + s + "index.html");

		/* screenshot.html */
		String htmlScreenshotLogo = htmlLogos.substring(htmlLogos.indexOf("<!-- 8< 2 -->") + "<!-- 8< 2 -->".length(), htmlLogos.indexOf("<!-- 8< 3 -->"));
		String htmlScreenshots = htmlHeader + htmlScreenshotLogo + htmlScreenshotsContent + htmlFooter;
		htmlScreenshots = htmlScreenshots.replace("""
				<a class="item" href="screenshots.html">""", """
				<a class="active item" href="screenshots.html">""");
		CU.saveStringToFile(htmlScreenshots, sitePath + s + "screenshots.html");

		/* faq.html */
		String htmlFaqLogo = htmlLogos.substring(htmlLogos.indexOf("<!-- 8< 3 -->") + "<!-- 8< 3 -->".length(), htmlLogos.indexOf("<!-- 8< 4 -->"));
		String htmlFaq = htmlHeader + htmlFaqLogo + htmlFaqContent + htmlFooter;
		htmlFaq = htmlFaq.replace("""
				<a class="item" href="faq.html">""", """
				<a class="active item" href="faq.html">""");
		CU.saveStringToFile(htmlFaq, sitePath + s + "faq.html");
		
	}
}
