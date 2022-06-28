<!--
---
Eclipse Markdown ePub TOML Plugin - 2022 emdepub.org
---
-->

# Eclipse Markdown ePub TOML Plugin

### | [Home](index.html) || [Markdown Progress](progress.html) || [Code](https://github.com/iuscl-ide/emdepub) |

Emdepub is an Eclipse plugin for displaying and editing [Markdown (.md)](http://daringfireball.net/projects/markdown/syntax) files, and creating [ePub (.epub)](https://en.wikipedia.org/wiki/EPUB) books.

**ADDED SUPPORT FOR TOML**

The plugin now has support for [TOML](https://toml.io/en/v1.0.0), with syntax, verification and proposals

This plugin is a new version of the now deprecated (no longer maintained) plugin [Markdown Semantic EP](http://markdownsemanticep.org) ([GitHub](https://github.com/iuscl-ide/markdownsemanticep)). The difference between the two comes from the fact that the visual part is now done in the Eclipse "modern" way, with tm4e, JavaScript render, and MS VSC. The code manipulation part has remain in [Flexmark](https://github.com/vsch/flexmark-java) and for a detailed description of each functionality, please go to [Markdown progress](progress.html)

The main goals are speed and quality for both the syntax highlight and the resulted HTML. These qualities still doesn't seem to be entirely fulfilled by the existing Eclipse plugins. 

For using the ePub creator, plase see the example inside the GitHub sources.

### Install

Install as a normal Eclipse plugin:

``` Eclipse
Help -> Install new software...
```    
    
And then add the site `https://emdepub.org/update`

### Website hosted by [GitHub](https://github.com/iuscl-ide/emdepub) || 2022 [emdepub.org](https://emdepub.org)