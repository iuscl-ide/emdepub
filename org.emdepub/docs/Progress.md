
## Progress

|            Feature             | Status |                                            Implementation                                            |
|--------------------------------|--------|------------------------------------------------------------------------------------------------------|
| **Display (HTML Rerendering)** |        |                                                                                                      |
| Render                         |        | [marked.js](https://marked.js.org/ "Marked Documentation") and [helper libraries](#render-libraries) |
| Render style                   |        | 3 css                                                                                                |
| Code highlight                 |        | highlight.js                                                                                         |
| Code highlight style           |        | n css                                                                                                |
| **Source (MD Editor)**         |        |                                                                                                      |
| Formatting                     |        | flexmark [with extensions](#flexmark-extensions)                                                     |
| Syntax                         |        | markdown.tmLanguage (TextMate grammar from VSCode)                                                   |
| Theme                          |        | Markdown Light (extended from Eclipse Generic Editor Theme "Light")                                  |
| Language configuration         |        | language-configuration.json (from VSCode, the folding is not implemented in Eclipse)                 |
| Folding                        |        | Based on the Eclipse folding for HTML tags, plus folding for Markdown headers                        |
| Outline                        | MAYBE  | Not yet                                                                                              |
| Spell check                    | NO     | Unable to find a way to activate spell check for Generic Editor                                      |
| Auto edit                      | MAYBE  | Auto close some                                                                                      |
| Content assist                 | MAYBE  | CTRL + SPACE create link, table, header                                                              |
| Hover                          | MAYBE  |                                                                                                      |
| Presentation                   |        | Eclipse's way to color the code                                                                      |
| Validator                      |        | Put errors and warnings                                                                              |
| Formatter                      |        | Bold, italic etc.                                                                                    |

(brackets, auto closing pairs, surrounding pairs)

### Bugs

|           Bug           | Status | Implementation |
|-------------------------|--------|----------------|
| Undo / Redo not working |        |                |
|                         |        |                |
|                         |        |                |

### <a name="rdender-libraries"></a>Render

|        Sub-Feature         | Status |        Implementation         |
|----------------------------|--------|-------------------------------|
| Libraries                  |        |                               |
|                            |        | jschardet.js                  |
|                            |        | iconv.js                      |
|                            |        | emoji.js                      |
|                            |        | beautify-html.js              |
|                            |        | jquery                        |
| Styles                     |        |                               |
|                            |        | github-markdown.css           |
|                            |        | google-like-markdown.css      |
|                            |        | semantic-ui-like-markdown.css |
|                            |        | stylesheet.css                |
| Source (MD Editor)         |        |                               |
| Display (HTML Rerendering) |        |                               |

### Flexmark extensions for source formatting <a name="flexmark-extensions"></a>

| Extension | Status | Implementation |
|-----------|--------|----------------|
| tables    | YES    |                |
| tasklist  | YES    |                |

