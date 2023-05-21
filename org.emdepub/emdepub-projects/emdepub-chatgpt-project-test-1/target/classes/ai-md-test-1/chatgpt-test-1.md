---
title: titlu
step:  &id001                  # defines anchor label &id001
    instrument:      Lasik 2000
    pulseEnergy:     5.4
    pulseDuration:   12
    repetition:      1000
    spotSize:        1mm

item: |
	## Codes
	1. First
	 line
	2. Second
	3. Default flexmark-java pegdown emulation uses less strict HTML block parsing
	   which interrupts an HTML block on a blank line. Pegdown only interrupts an
	   HTML block on a blank line if all tags in the HTML block are closed. To get
	   closer to original pegdown HTML block parsing behavior use the method which
	   takes a boolean strictHtml argument:
  
---

### Levels

● ai: { "method" : "rephrase" }  ▶
The log levels will be identified by rules, and will be displayed in customisable colors
◀

<center>
![Levels with Colors](help-introduction-levels--screenshot.png)
</center>

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