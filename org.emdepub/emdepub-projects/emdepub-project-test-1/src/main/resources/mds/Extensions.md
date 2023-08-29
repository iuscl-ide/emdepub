## ++Markdown Semantic Eclipse Plugin++ Extensions

| Extension                        | Used | Obs.                                       |
|----------------------------------|-----:|--------------------------------------------|
| flexmark-ext-abbreviation        |  Yes |                                            |
| flexmark-ext-anchorlink          |    - | Makes all heading a link                   |
| flexmark-ext-aside               |  Yes |                                            |
| flexmark-ext-autolink            |  Yes |                                            |
| flexmark-ext-definition          |  Yes |                                            |
| flexmark-ext-emoji               |  Yes | Only for codes, like `:something:`         |
| flexmark-ext-escaped-character   |    - | No effect                                  |
| flexmark-ext-footnotes           |  Yes |                                            |
| flexmark-ext-gfm-strikethrough   |  Yes | Contains also Subscript                    |
| ~~flexmark-ext-gfm-tables~~      |    - | Deprecated                                 |
| flexmark-ext-gfm-tasklist        |    - | Can't be used in tables, not really needed |
| flexmark-ext-ins                 |  Yes |                                            |
| flexmark-ext-jekyll-front-matter |  Yes |                                            |
| flexmark-ext-jekyll-tag          |    - | {% include %} Not working                  |
| flexmark-ext-spec-example        |    - | What is it?                                |
| flexmark-ext-superscript         |  Yes |                                            |
| flexmark-ext-tables              |  Yes |                                            |
| flexmark-ext-toc                 |  Yes |                                            |
| flexmark-ext-typographic         |  Yes | No (c)                                     |
| flexmark-ext-wikilink            |    - | Not used                                   |
| flexmark-ext-xwiki-macros        |    - | { macro } Not used                         |
| flexmark-ext-yaml-front-matter   |    - | Same as jekyll-front-matter ?              |







### flexmark-ext-aside

aside

### flexmark-ext-definition

Start definition
:   definition

### flexmark-ext-emoji

:warning:
:smile:
:-)

### flexmark-ext-escaped-character

Sample  \t\e\x\t and \\ \- \~

### flexmark-ext-gfm-tasklist
<!--
- non-task item

  - [ ] task item
- [ ] task item
      - [X] task item
- [ ] task item
      - non task item
-->
Bu
      
- [X] -
  
### flexmark-ext-jekyll-tag
     
{% include Include.html %}

Here