*(text rephrased with ChatGPT)*

# Eclipse Markdown ePub TOML Plugin

**Emdepub** is an Eclipse plugin that facilitates the display and modification of files written in [Markdown](http://daringfireball.net/projects/markdown/syntax) syntax (with `.md` extension), in addition to enabling the creation of books in [ePub](https://en.wikipedia.org/wiki/EPUB) format (`.epub` extension) and editing of [TOML](https://toml.io/en/v1.0.0) files (`.toml` extension).

### Versions

**NEW VERSION 1.0.3 - CORRECTIONS**

**VERSION 1.0.2 - ADDED SUPPORT FOR TOML**

The plugin now offers support for [TOML](https://toml.io/en/v1.0.0), including syntax highlighting, verification, and suggestions.

### About

This plugin, which is aimed at providing faster and higher-quality syntax highlighting and resulting HTML output, is a newer version of the now-defunct [Markdown Semantic EP](http://markdownsemanticep.org) plugin ([GitHub](https://github.com/iuscl-ide/markdownsemanticep)), which is no longer maintained.

The primary difference between the two plugins is that this one uses `tm4e`, `JavaScript rendering`, and `MS VSC` to create a more modern and visually-appealing interface. Code manipulation still uses [Flexmark](https://github.com/vsch/flexmark-java).

For more detailed information about the plugin's various functionalities, please visit [Markdown progress](http://emdepub.org/progress.html).

To use the **ePub** creation feature, please refer to the example within the [GitHub](https://github.com/iuscl-ide/emdepub/tree/main/org.emdepub/emdepub-projects/emdepub-project-test-1/src/main/resources/ePub-samples/THREE_MEN_IN_A_BOAT "THREE_MEN_IN_A_BOAT ePub example") source.

### Install

Install as a normal Eclipse plugin:

``` Eclipse
Help -> Install new software...
```    
    
And then add the site `https://emdepub.org/update`
