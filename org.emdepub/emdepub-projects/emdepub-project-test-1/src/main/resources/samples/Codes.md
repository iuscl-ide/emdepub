## Codes

1. First line
2. Second
3. Default flexmark-java pegdown emulation uses less strict HTML block parsing
   which interrupts an HTML block on a blank line. Pegdown only interrupts an
   HTML block on a blank line if all tags in the HTML block are closed. To get
   closer to original pegdown HTML block parsing behavior use the method which
   takes a boolean strictHtml argument:

- First line
- Second
- Default flexmark-java pegdown emulation uses less strict HTML block parsing
  which interrupts an HTML block on a blank line. Pegdown only interrupts an
  HTML block on a blank line if all tags in the HTML block are closed. To get
  closer to original pegdown HTML block parsing behavior use the method which
  takes a boolean strictHtml argument:

### Java

``` java
/** Compact parsed regions */
private void compactRegions(ArrayList<TypedRegion> regions) {
	
	if (regions.size() == 0) {
		return;
	}
	
	ArrayList<TypedRegion> zeroLengthRegions = new ArrayList<>();
	for (TypedRegion region : regions) {
		if (region.getLength() == 0) {
			zeroLengthRegions.add(region);
		}
	}
	regions.removeAll(zeroLengthRegions);
	
	String prevType = regions.get(0).getType();
	int indexCompact = 1;
	while (indexCompact < regions.size()) {
		TypedRegion region = regions.get(indexCompact);
		String newType = region.getType();
		if (newType.equals(prevType)) {
			TypedRegion prevRegion = regions.get(indexCompact - 1);
			regions.set(indexCompact - 1, new TypedRegion(prevRegion.getOffset(),
					prevRegion.getLength() + region.getLength(), prevType));
			regions.remove(indexCompact);
		}
		else {
			prevType = newType;
			indexCompact++;
		}
	}
}
```

### JavaScript

``` js
var foo = function (bar) {
  return bar++;
};

console.log(foo(5));
```

### Markdown

*Highlight.js* fails a little for Markdown

``` markdown
---
Header
---
<!-- Start md -->
# Heading
***BoldItalic***
**Bold1 *Italic1 **Bold2** Italic1* Bold1**
        *Italic1           Italic1*
Something
www.md.com

**Inline <!-- Here is the comment --> comment**

**Start comment**
<!--
To be commented
-->
*End comment*

*<p align="center">**HTML** part</p>*

one *one* normalu normal *two* two

++HTML block:++

<div>
<!-- Another inside comment -->
HTML Text
</div>
```

###### Indented

	/** Headings outline */
	public ArrayList<Position> runFolding(String markdownString, int lineDelimiterLength) {
		
		ArrayList<Position> headerPositions = new ArrayList<>();
		
		/* Parse document in a node */
		Node documentNode = MarkdownFormatterEngine.getParser().parse(markdownString);
		linearHeadings.clear();
		linearFencedCodeBlocks.clear();
		nodeVisitor.visitChildren(documentNode);

		int headingsSize = linearHeadings.size();
    	for (int i = 0; i < headingsSize - 1; i++) {
    		
    		Heading headingStart = linearHeadings.get(i);
    		int positionStart = headingStart.getStartOffset();
    		int levelStart = headingStart.getLevel();
    		boolean found = false;
        	for (int j = i + 1; j < headingsSize; j++) {
        		Heading headingEnd = linearHeadings.get(j);
        		int positionEnd = headingEnd.getStartOffset();
        		if (headingEnd.getLevel() <= levelStart) {
        			headerPositions.add(new Position(positionStart, positionEnd - positionStart));
        			found = true;
        			break;
        		}
        	}
        	if (!found) {
        		headerPositions.add(new Position(positionStart, markdownString.length() - (positionStart)));	
        	}
    	}
    	int lastHeadingStart = linearHeadings.get(headingsSize - 1).getStartOffset();
    	headerPositions.add(new Position(lastHeadingStart, markdownString.length() - lastHeadingStart));

    	for (FencedCodeBlock fencedCodeBlock : linearFencedCodeBlocks) {
    		
    		int fencedCodeBlockStart = fencedCodeBlock.getStartOffset();
    		headerPositions.add(new Position(fencedCodeBlockStart, fencedCodeBlock.getEndOffset() - (fencedCodeBlockStart - lineDelimiterLength)));
    	}
    	
		return headerPositions;
	}

###### Indented JS

	(function foo() {
	  var x = 7;
	  console.log("val " + eval("x + 2"));
	})(); // shows val 9.

