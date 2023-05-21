package org.emdepub.ai_md.parser.ext.references.internal;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.emdepub.ai_md.parser.ext.references.AiMdReferenceBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.vladsch.flexmark.parser.block.AbstractBlockParser;
import com.vladsch.flexmark.parser.block.AbstractBlockParserFactory;
import com.vladsch.flexmark.parser.block.BlockContinue;
import com.vladsch.flexmark.parser.block.BlockParser;
import com.vladsch.flexmark.parser.block.BlockParserFactory;
import com.vladsch.flexmark.parser.block.BlockStart;
import com.vladsch.flexmark.parser.block.CustomBlockParserFactory;
import com.vladsch.flexmark.parser.block.MatchedBlockParser;
import com.vladsch.flexmark.parser.block.ParserState;
import com.vladsch.flexmark.util.ast.Block;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.sequence.BasedSequence;

public class AiMdReferenceBlockParser extends AbstractBlockParser {

    final private static Pattern REFERENCE_BEGIN_PATTERN = Pattern.compile("^● ai[ \\t]*$");
    final private static int REFERENCE_BEGIN_LENGTH = 4;
    final private static Pattern REFERENCE_HEADER_END_PATTERN = Pattern.compile("^▶[ \\t]*$");
    final private static Pattern REFERENCE_END_PATTERN = Pattern.compile("^◀[ \\t]*$");

    final private AiMdReferenceBlock aiMdReferenceBlock = new AiMdReferenceBlock();

    private boolean tryContinueInHeader = true;
    
    @Override
    public Block getBlock() {
        return aiMdReferenceBlock;
    }

    @Override
    public BlockContinue tryContinue(ParserState state) {

		Matcher matcher;

    	if (state.getIndent() == 0) {
    		BasedSequence tryContinueSequence = state.getLine();
    		
    		if (tryContinueInHeader) {

        		matcher = REFERENCE_HEADER_END_PATTERN.matcher(tryContinueSequence);
        		if (matcher.find()) {
                    aiMdReferenceBlock.setHeaderEndBasedSequence(tryContinueSequence);
        			aiMdReferenceBlock.setHeaderEndLineNumber(state.getLineNumber());
        			
        			tryContinueInHeader = false;
        		}
        		else {
        			matcher = REFERENCE_END_PATTERN.matcher(tryContinueSequence);
            		if (matcher.find()) {
            			aiMdReferenceBlock.getReferenceEndInvalidLineNumbers().add(state.getLineNumber());
            		}
            		else {
            			matcher = REFERENCE_BEGIN_PATTERN.matcher(tryContinueSequence);
                		if (matcher.find()) {
                			aiMdReferenceBlock.getHeaderStartInvalidLineNumbers().add(state.getLineNumber());
                		}
                		else {
                			aiMdReferenceBlock.getHeaderBlockContent().add(tryContinueSequence, state.getIndent());
                		}
            		}
        		}
    		}
    		else {

        		matcher = REFERENCE_END_PATTERN.matcher(tryContinueSequence);
        		if (matcher.find()) {
                    aiMdReferenceBlock.setEndBasedSequence(tryContinueSequence);
                    aiMdReferenceBlock.setTextEndLineNumber(state.getLineNumber());
                    
                    return BlockContinue.finished();
        		}
        		else {
        			matcher = REFERENCE_HEADER_END_PATTERN.matcher(tryContinueSequence);
            		if (matcher.find()) {
            			aiMdReferenceBlock.getHeaderEndInvalidLineNumbers().add(state.getLineNumber());
            		}
            		else {
            			matcher = REFERENCE_BEGIN_PATTERN.matcher(tryContinueSequence);
                		if (matcher.find()) {
                			aiMdReferenceBlock.getHeaderStartInvalidLineNumbers().add(state.getLineNumber());
                		}
                		else {
                			aiMdReferenceBlock.getTextBlockContent().add(tryContinueSequence, state.getIndent());
                		}
            		}
        		}
    		}
    	}
    	
    	return BlockContinue.atIndex(state.getIndex());
    }

    @Override
    public void addLine(ParserState state, BasedSequence line) { /* ILB */ }

    @Override
    public boolean isPropagatingLastBlankLine(BlockParser lastMatchedBlockParser) {
        return false;
    }

    @Override
    public void closeBlock(ParserState state) {
    	
    	aiMdReferenceBlock.setCharsFromContentOnly();
    }

    public static class Factory implements CustomBlockParserFactory {
        @Nullable
        @Override
        public Set<Class<?>> getAfterDependents() {
        	return null;
        }

        @Nullable
        @Override
        public Set<Class<?>> getBeforeDependents() {
        	return null;
        }

        @Override
        public boolean affectsGlobalScope() {
            return false;
        }

        @NotNull
        @Override
        public BlockParserFactory apply(@NotNull DataHolder options) {
            return new BlockFactory(options);
        }
    }

    private static class BlockFactory extends AbstractBlockParserFactory {
        private BlockFactory(DataHolder options) {
            super(options);
        }

        @Override
        public BlockStart tryStart(ParserState state, MatchedBlockParser matchedBlockParser) {
        	
        	if (state.getIndent() == 0) {
        		BasedSequence tryStartSequence = state.getLine();
        		Matcher matcher = REFERENCE_BEGIN_PATTERN.matcher(tryStartSequence);
        		if (matcher.find()) {
                    AiMdReferenceBlockParser aiMdReferenceBlockParser = new AiMdReferenceBlockParser();
                    aiMdReferenceBlockParser.aiMdReferenceBlock.setBeginBasedSequence(tryStartSequence);
                    aiMdReferenceBlockParser.aiMdReferenceBlock.setHeaderStartLineNumber(state.getLineNumber());
                    
                    return BlockStart.of(aiMdReferenceBlockParser).atIndex(REFERENCE_BEGIN_LENGTH);
        		}
        	}
        	
            return BlockStart.none();
        }
    }
}
