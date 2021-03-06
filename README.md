# Eclipse Markdown ePub Plugin

Emdepub is an Eclipse plugin for displaying and editing [Markdown (.md)](http://daringfireball.net/projects/markdown/syntax) files, and creating [ePub (.epub)](https://en.wikipedia.org/wiki/EPUB) books.

This plugin is a new version of the now deprecated (no longer maintained) plugin [Markdown Semantic EP](http://markdownsemanticep.org) ([GitHub](https://github.com/iuscl-ide/markdownsemanticep)). The difference between the two comes from the fact that the visual part is now done in the Eclipse "modern" way, with tm4e, JavaScript render, and MS VSC. The code manipulation part has remain in [Flexmark](https://github.com/vsch/flexmark-java) and for a detailed description of each functionality, please go to [progress](https://emdepub.org/progress.html)

The main goals are speed and quality for both the syntax highlight and the resulted HTML. These qualities still doesn�t seem to be entirely fulfilled by the existing Eclipse plugins. 

For using the ePub creator, plase see the example inside the GitHub sources.

### Install

Install as a normal Eclipse plugin:

``` Eclipse
Help -> Install new software...
```    
    
And then add the site `https://emdepub.org/update`