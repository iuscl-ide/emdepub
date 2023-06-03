package org.emdepub.ai_md.parser.ext.references.internal;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.emdepub.ai_md.parser.ext.references.AiMdReferenceBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.vladsch.flexmark.formatter.MarkdownWriter;
import com.vladsch.flexmark.formatter.NodeFormatter;
import com.vladsch.flexmark.formatter.NodeFormatterContext;
import com.vladsch.flexmark.formatter.NodeFormatterFactory;
import com.vladsch.flexmark.formatter.NodeFormattingHandler;
import com.vladsch.flexmark.formatter.NodeFormattingHandler.CustomNodeFormatter;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataHolder;

public class AiMdReferenceBlockFormatter implements NodeFormatter {

	public static class Factory implements NodeFormatterFactory {
		@NotNull
		public NodeFormatter create(@NotNull DataHolder options) {
			return new AiMdReferenceBlockFormatter(options);
		}
	}
	
	public AiMdReferenceBlockFormatter(DataHolder options) { /* ILB */ }

	@Override
	public @Nullable Set<Class<?>> getNodeClasses() {
		return null;
	}

	@Override
	public @Nullable Set<NodeFormattingHandler<?>> getNodeFormattingHandlers() {
		return new HashSet<NodeFormattingHandler<? extends Node>>(Arrays.asList(
            new NodeFormattingHandler<AiMdReferenceBlock>(AiMdReferenceBlock.class, new CustomNodeFormatter<AiMdReferenceBlock>() {
                @Override
                public void render(AiMdReferenceBlock aiMdReferenceBlock, NodeFormatterContext context, MarkdownWriter markdown) {
                	markdown.append(aiMdReferenceBlock.getChars().appendEOL().appendEOL());
                }
            })
		));
	}
}
