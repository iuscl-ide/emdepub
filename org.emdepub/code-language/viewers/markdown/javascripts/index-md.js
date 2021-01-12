/* ePub MD Viewer - 2018-2020 epub-md-viewer.com */

"use strict";

const Electron = require("electron");
const { shell, clipboard } = require('electron')
const MARKED = require("marked");
const FS = require("fs");
const Path = require("path");
const $ = require("jquery");
const HighlightJs = require("highlight.js");
const CHARDET = require("chardet");
const iconv = require('iconv-lite');
const EMOJI = require('node-emoji');

const IPC = Electron.ipcRenderer;
let mdFile;
let mdFileEncoding;

const $content = $("#content");
const $head = $("head");

/* Display dialog */
const $generatedHtmlDialogBody = $("#generated-html-dialog-body");
const $detailsDialogBody = $("#details-dialog-body");
const $displayDialogLabel = $("#display-dialog-label");
const $displayDialog = $("#display-dialog");
const $generatedHtmlPre = $("#generated-html-modal-pre");


/* Options */
const mdOptions = {
    renderEmbeddedHtml: true,
    justifyParagraphs: true,
    centerHeaders: false,
    displayStyle: "_0",
    codeHighlightStyle: "_2"
};

/* Styles */
const mdDisplayStyles = {
    "_0": '<link id="css_0" rel="stylesheet" href="../css/github-markdown.css">"',
    "_1": '<link id="css_1" rel="stylesheet" href="../css/google-like-markdown.css">"',
    "_1_1": '<link id="css_1_1" rel="stylesheet" href="../css/read-fonts.css">"'
};

const mdCodeHighlightStyles = {
    /* Highlight.js GitHub style */
    "_1": '<link id="hl_1" rel="stylesheet" href="../node_modules/highlight.js/styles/github.css">',
    /* Highlight.js Google Code style */
    "_2": '<link id="hl_2" rel="stylesheet" href="../node_modules/highlight.js/styles/googlecode.css">'
};

/* Helper */
const isUrlAbsolute = url => {
    return url.indexOf('//') === 0 ? true : url.indexOf('://') === -1 ? false : url.indexOf('.') === -1 ? false : url.indexOf('/') === -1 ? false : url.indexOf(':') > url.indexOf('/') ? false : url.indexOf('://') < url.indexOf('.') ? true : false;
};

/* MD load */
const loadMDFile = () => {

    document.title = Path.basename(mdFile) + " \u2022 ePub MD Viewer";

    document.getElementById("content").innerHTML = "";

//    $("base").attr("href", Path.dirname(mdFile));

    $("#css_0").remove();
    $("#css_1").remove();
    $("#css_1_1").remove();

    $("#hl_1").remove();
    $("#hl_2").remove();

    if (mdOptions.displayStyle === "_0") {
        $head.append(mdDisplayStyles._0);
    } else {
        $head.append(mdDisplayStyles._1_1);
        $head.append(mdDisplayStyles._1);
    }

    if (mdOptions.codeHighlightStyle === "_0") {
        $head.append(mdCodeHighlightStyles._1);
    }
    if (mdOptions.codeHighlightStyle === "_1") {
        $head.append(mdCodeHighlightStyles._1);
    } else if (mdOptions.codeHighlightStyle === "_2") {
        $head.append(mdCodeHighlightStyles._2);
    }

    MARKED.setOptions({
        sanitize: !mdOptions.renderEmbeddedHtml
    });

    if (mdOptions.justifyParagraphs) {
        $("#content").css("text-align", "justify");
    } else {
        $("#content").css("text-align", "");
    }

    if (mdOptions.centerHeaders) {
        $head.append("<style id='style_1'>h1 {text-align: center;} h2 {text-align: center;} h3 {text-align: center;} " +
            "h4 {text-align: center;} h5 {text-align: center;} h6 {text-align: center;}</style>");
    }

    let mdFileContent;
    try {
        mdFileContent = FS.readFileSync(mdFile);
    } catch (exception) {
        $content.html(MARKED("An error occurred reading the file: " + exception.message));
        return;
    }
    //console.log(chardet.detect(data));
    //console.log(data.toString());

    mdFileEncoding = CHARDET.detect(mdFileContent);
    let mdText = iconv.decode(mdFileContent, mdFileEncoding);
    mdText = EMOJI.emojify(mdText);
    mdText = MARKED(mdText);
    $content.html(mdText);

    if (mdOptions.codeHighlightStyle !== "_0") {
        HighlightJs.initHighlighting.called = false;
        HighlightJs.initHighlighting();
    }

    /* Links list */
    let selection = $content.find("a[href]").add($content.filter("a[href]"));
    const replacementExpWith_TOREPLACE_ = "javascript:window.mdUI.navigateMdLink('_TOREPLACE_')";

    selection.each((_index, linkElement) => {

        let $linkElement = $(linkElement);
        let linkUrl = $linkElement.attr("href");
        if (!linkUrl.startsWith("#")) {
            let link = replacementExpWith_TOREPLACE_.replace("_TOREPLACE_", linkUrl);
            $linkElement.attr("href", link);
        }
    });

    /* Image src */
    selection = $content.find("img[src]").add($content.filter("img[src]"));

    selection.each((_index, imgElement) => {

        let $imgElement = $(imgElement);
        let imgSrc = $imgElement.attr("src");
        if (!isUrlAbsolute(imgSrc)) {
            let link = Path.dirname(mdFile) + "/" + imgSrc;
            $imgElement.attr("src", link);
        }
    });
};

/* Start */
IPC.on("response_index_start", (_event, mdParameter, _appLocale) => {

    mdFile = mdParameter;
    loadMDFile();
});

/* File dialog */
IPC.on("file_dialog_result_md", (_event, files) => {
    console.log(files);
    console.log("receive " + files);
    if ((files !== undefined) && (files !== null)) {
        mdFile = files[0];
        loadMDFile();
    }
});

/* Application main menu */
IPC.on("app_menu_command_md", (_event, appMenuCommand, _args) => {

    if (appMenuCommand === "renderEmbeddedHtml") {
        mdOptions.renderEmbeddedHtml = !mdOptions.renderEmbeddedHtml;
        loadMDFile();
    }
    if (appMenuCommand === "justifyParagraphs") {
        mdOptions.justifyParagraphs = !mdOptions.justifyParagraphs;
        loadMDFile();
    }
    if (appMenuCommand === "centerHeaders") {
        mdOptions.centerHeaders = !mdOptions.centerHeaders;
        loadMDFile();
    }

    if (appMenuCommand === "css_0") {
        mdOptions.displayStyle = "_0";
        loadMDFile();
    }
    if (appMenuCommand === "css_1") {
        mdOptions.displayStyle = "_1";
        loadMDFile();
    }

    if (appMenuCommand === "hl_0") {
        mdOptions.codeHighlightStyle = "_0";
        loadMDFile();
    }
    if (appMenuCommand === "hl_1") {
        mdOptions.codeHighlightStyle = "_1";
        loadMDFile();
    }
    if (appMenuCommand === "hl_2") {
        mdOptions.codeHighlightStyle = "_2";
        loadMDFile();
    }
    if (appMenuCommand === "show_html") {
        $detailsDialogBody.hide();
        $generatedHtmlDialogBody.show();

        $displayDialogLabel.html("Markdown generated HTML");
        $generatedHtmlPre.text($content.html());
        HighlightJs.highlightBlock($generatedHtmlPre[0]);
        $displayDialog.modal();
    }
    if (appMenuCommand === "show_details") {
        $generatedHtmlDialogBody.hide();
        $detailsDialogBody.show();

        $displayDialogLabel.html("MD details");
        let $detailsDialogBodyRow = $("#details-dialog-body-row");
        $detailsDialogBody.empty();

        let $newDetailsDialogBodyRow = $detailsDialogBodyRow.clone();
        $newDetailsDialogBodyRow.appendTo($detailsDialogBody);

        $newDetailsDialogBodyRow.find("#details-label").html("File");
        $newDetailsDialogBodyRow.find("#details-content").html(mdFile);

        $newDetailsDialogBodyRow = $detailsDialogBodyRow.clone();
        $newDetailsDialogBodyRow.appendTo($detailsDialogBody);

        $newDetailsDialogBodyRow.find("#details-label").html("Encoding (detected)");
        $newDetailsDialogBodyRow.find("#details-content").html(mdFileEncoding);


        $displayDialog.modal();
    }
    if (appMenuCommand === "copyAllAsStyleText") {
        clipboard.writeHTML($content.html());
    }
    if (appMenuCommand === "copyAllAsNormalText") {
        clipboard.writeText($content.text());
    }
});

/* To start when main is ready */
IPC.send("request_index_start", "ready");

/* Navigate MD link */
const navigateMdLink = async (linkUrl) => {

    await shell.openExternal(linkUrl);
};

/* Publish UI interface */
const mdUI = {};
mdUI.navigateMdLink = navigateMdLink;
module.exports = mdUI;
