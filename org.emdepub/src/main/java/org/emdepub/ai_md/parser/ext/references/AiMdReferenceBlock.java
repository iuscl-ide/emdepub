package org.emdepub.ai_md.parser.ext.references;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.vladsch.flexmark.util.ast.Block;
import com.vladsch.flexmark.util.ast.BlockContent;
import com.vladsch.flexmark.util.ast.DoNotDecorate;
import com.vladsch.flexmark.util.sequence.BasedSequence;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level=AccessLevel.PRIVATE)
@Getter
@Setter
public class AiMdReferenceBlock extends Block implements DoNotDecorate {
	
	int headerStartLineNumber = -1;
	int headerEndLineNumber = -1;
	int textEndLineNumber = -1;
	
	BasedSequence beginBasedSequence;
	BasedSequence headerEndBasedSequence;
	BasedSequence endBasedSequence;

	final List<Integer> headerStartInvalidLineNumbers = new ArrayList<>();
	final List<Integer> headerEndInvalidLineNumbers = new ArrayList<>();
	final List<Integer> referenceEndInvalidLineNumbers = new ArrayList<>();
	
	final BlockContent headerBlockContent = new BlockContent();
	final BlockContent textBlockContent = new BlockContent();
	
    @NotNull
    @Override
    public BasedSequence[] getSegments() {
    	
        return new BasedSequence[] { beginBasedSequence, headerBlockContent.getContents(), headerEndBasedSequence, textBlockContent.getContents(), endBasedSequence };
    }
}