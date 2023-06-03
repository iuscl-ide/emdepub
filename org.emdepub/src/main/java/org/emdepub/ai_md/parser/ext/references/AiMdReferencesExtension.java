package org.emdepub.ai_md.parser.ext.references;

import org.emdepub.ai_md.parser.ext.references.internal.AiMdReferenceBlockFormatter;
import org.emdepub.ai_md.parser.ext.references.internal.AiMdReferenceBlockParser;

import com.vladsch.flexmark.formatter.Formatter;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataHolder;

public class AiMdReferencesExtension implements Parser.ParserExtension, Formatter.FormatterExtension {

	private AiMdReferencesExtension() { }
	
    public static AiMdReferencesExtension create() {
        return new AiMdReferencesExtension();
    }

    @Override
    public void parserOptions(MutableDataHolder options) { /* ILB */ }

    @Override
    public void extend(Parser.Builder parserBuilder) {
        parserBuilder.customBlockParserFactory(new AiMdReferenceBlockParser.Factory());
    }

	@Override
	public void rendererOptions(MutableDataHolder options) { /* ILB */ }

	@Override
	public void extend(Formatter.Builder formatterBuilder) {
		formatterBuilder.nodeFormatterFactory(new AiMdReferenceBlockFormatter.Factory());
	}
}