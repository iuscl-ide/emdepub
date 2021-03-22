<!--
---
Eclipse Markdown ePub Plugin - 2021 emdepub.org
---
-->


# Progress

### | [Home](index.html) || [Progress](progress.html) || [Code](https://github.com/iuscl-ide/emdepub) |

|                      Feature                      | Status |                                                            Implementation                                                             |
|---------------------------------------------------|:------:|---------------------------------------------------------------------------------------------------------------------------------------|
| **Display (HTML Rendering)**                      |        |                                                                                                                                       |
| [Render](#render)                                 |  YES   | [marked.js](https://marked.js.org/ "Marked Documentation")                                                                            |
| [Render style](#render-style)                     |  YES   | [github-markdown.css](https://github.com/sindresorhus/github-markdown-css) and some variations                                        |
| [Code highlight](#code-highlight)                 |  YES   | [highlight.js](https://highlightjs.org/)                                                                                              |
| [Code highlight style](#code-highlight-style)     |  YES   | Some *highlight.js* themes                                                                                                            |
| **Source (Markdown Editor)**                      |        |                                                                                                                                       |
| [Formatting](#formatting) (including tables!)     |  YES   | [Flexmark](https://github.com/vsch/flexmark-java)                                                                                     |
| [Syntax](#syntax)                                 |  YES   | [markdown.tmLanguage](https://github.com/microsoft/vscode-markdown-tm-grammar/blob/main/syntaxes/markdown.tmLanguage)                 |
| [Theme](#theme)                                   |  YES   | Markdown Light                                                                                                                        |
| [Language configuration](#language-configuration) |  YES   | [language-configuration.json](https://github.com/microsoft/vscode/blob/master/extensions/markdown-basics/language-configuration.json) |
| [Folding](#folding)                               |  YES   | *Flexmark*                                                                                                                            |
| [Outline](#Outline)                               |  YES   | *Flexmark*                                                                                                                            |
| [Spell check](#spell-check)                       |   NO   | Not enough information, it appears sometimes                                                                                          |
| [Auto edit](#auto-edit)                           |  YES   | done with the new [language configuration](#language-configuration)                                                                   |
| [Content assist](#content-assist)                 |  YES   | CTRL + SPACE, *Flexmark*                                                                                                              |
| [Hover](#hover)                                   |   NO   | Hover to show what?                                                                                                                   |
| [Presentation](#presentation)                     |   NO   | Eclipse's way to color the code, now done the modern way                                                                              |
| [Validator](#validator)                           |   NO   | Put errors and warnings, maybe in the future, for MD elements in HTML blocks or in code inline/blocks                                 |
| [Additional Formatting](#additional-formatting)   |  YES   | Bold and italic toolbar commands                                                                                                      |

----------



### Bugs



|                        Bug                        | Status |                   Implementation                    |
|---------------------------------------------------|--------|-----------------------------------------------------|
| Undo / Redo not working                           |        | Sometimes it works?                                 |
| ~~Justified paragraphs is not working for lists~~ |        |                                                     |
| *Spell check* not working                         |        | Sometimes it works, if opened as text and re-opened |



----------

### <a name="render"></a>Render

|            Feature            |                         Implementation                          |
|-------------------------------|-----------------------------------------------------------------|
| Detect Markdown text encoding | [jschardet.js](https://github.com/aadsm/jschardet)              |
| Transform text encoding       | [iconv.js](https://www.npmjs.com/package/iconv-lite)            |
| Emoji                         | [emoji.js](https://www.npmjs.com/package/emoji-js)              |
| Arrange generated HTML        | [beautify-html.js](https://github.com/beautify-web/js-beautify) |
| JQuery                        | [jquery](https://jquery.com/)                                   |

----------

### <a name="render-style"></a>Render style

|      Style      |                               Implementation                               |
|-----------------|----------------------------------------------------------------------------|
| Github          | [github-markdown.css](https://github.com/sindresorhus/github-markdown-css) |
| Google-like     | google-like-markdown.css                                                   |
| SemanticUI-like | semantic-ui-like-markdown.css                                              |
| Custom          | stylesheet.css                                                             |

----------

### <a name="code-highlight"></a>Code highlight

All done by [highlight.js](https://highlightjs.org/)

----------

### <a name="code-highlight-style"></a>Code highlight style

Some *highlight.js* themes

----------

### <a name="formatting"></a>Formatting

Done in *Flexmark* with parse and visit headings

*Flexmark* extensions used for source formatting

| Extension | Implemented |
|-----------|-------------|
| tables    | YES         |
| tasklist  | YES         |

Unable to find a way to activate Format command for Generic Editor Source menu

----------

### <a name="syntax"></a>Syntax

TextMate grammar from VSCode

----------

### <a name="theme"></a>Theme

Extended from Eclipse Generic Editor Theme "Light"

----------

### <a name="language-configuration"></a>Language configuration

From VSCode: brackets, auto closing pairs, surrounding pairs, the folding is not implemented in Eclipse

----------

### <a name="folding"></a>Folding

Done in *Flexmark* with parse and visit headings, fenced code blocks and indented code blocks

Unable to find a way to activate the Folding menu for Generic Editor left margin

----------

### <a name="outline"></a>Outline

Done in *Flexmark* with parse and visit headings

----------

### <a name="spell-check"></a>Spell check

Unable to find a way to activate spell check for Generic Editor

----------

### <a name="auto-edit"></a>Auto edit

Auto close some pairs, done with the new [language configuration](#language-configuration)

----------

### <a name="content-assist"></a>Content assist

CTRL + SPACE, insert (by context)

|       Insert        |                                      Text                                      |
|---------------------|--------------------------------------------------------------------------------|
| Bold text           | `**bold text**`                                                                |
| Italic text         | `*italic text*`                                                                |
| Bold italic text    | `***bold italic text***`                                                       |
| Strikethrough text  | `~~strikethrough text~~`                                                       |
| Quoted text         | `> quoted text`                                                                |
| Inline code         | ``` `inline code` ```                                                          |
| Fenced code block   | ````` ``` language ````` **`ENTER`** `fenced code` **`ENTER`** ````` ``` ````` |
| Indented code block | **`TAB`** indented code                                                        |
| Heading level 1     | `# Heading level 1`                                                            |
| Heading level 2     | `## Heading level 2`                                                           |
| Heading level 3     | `### Heading level 3`                                                          |
| Heading level 4     | `#### Heading level 4`                                                         |
| Heading level 5     | `##### Heading level 5`                                                        |
| Heading level 6     | `###### Heading level 6`                                                       |
| Unordered list      | `- first` **`ENTER`** `- second` **`ENTER`** `- third`                         |
| Ordered list        | `1. first` **`ENTER`** `2. second` **`ENTER`** `3. third`                      |
| Definition list     | `term` **`ENTER`** `: definition`                                              |
| Link                | `[link text](https://link_address "Link info (tooltip)")`                      |
| Image               | `![image alt (tooltip)](https://image_address)`                                |
| Horizontal rule     | `----------`                                                                   |

----------

### <a name="hover"></a>Hover

Hover to show what?

----------

### <a name="presentation"></a>Presentation

Eclipse's way to color the code, now done the new way

----------

### <a name="validator"></a>Validator

Put errors and warnings, maybe in the future, for MD elements in HTML blocks or in code inline/blocks

----------

### <a name="additional-formatting"></a>Additional formatting

Bold and italic commands, on/off for text selection

----------

### Website hosted by [GitHub](https://github.com/iuscl-ide/emdepub) || 2021 [emdepub.org](https://emdepub.org)