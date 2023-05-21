package org.emdepub.ai_md.engine;

import java.util.Arrays;

import org.emdepub.ai_md.call.AiMdCall;
import org.emdepub.ai_md.parser.ext.references.AiMdReferenceBlock;
import org.emdepub.ai_md.parser.ext.references.AiMdReferencesExtension;
import org.emdepub.common.utils.CU;
import org.jetbrains.annotations.NotNull;

import com.vladsch.flexmark.ast.Text;
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.formatter.Formatter;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.block.NodePostProcessor;
import com.vladsch.flexmark.parser.block.NodePostProcessorFactory;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeTracker;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AiMdEngine {

	static MutableDataSet parserOptions = new MutableDataSet();
	static {
		parserOptions.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(), TaskListExtension.create(),
				AiMdReferencesExtension.create(), AiMdReferenceBlockReplacingExtension.create()));	
	}
	static Parser parser = Parser.builder(parserOptions).build();
	static Formatter formatter = Formatter.builder(parserOptions).build();

    static class AiMdReferenceBlockReplacingPostProcessor extends NodePostProcessor {

        static class Factory extends NodePostProcessorFactory {

            public Factory(DataHolder options) {
                super(false);

                addNodes(AiMdReferenceBlock.class);
            }

            @NotNull
            @Override
            public NodePostProcessor apply(@NotNull Document document) {
                return new AiMdReferenceBlockReplacingPostProcessor();
            }
        }

        @Override
        public void process(@NotNull NodeTracker state, @NotNull Node node) {
        	if (node instanceof AiMdReferenceBlock) {
        		AiMdReferenceBlock aiMdReferenceBlock = (AiMdReferenceBlock) node;
        		
        		String aiSourceText = aiMdReferenceBlock.getTextBlockContent().getContents().toStringOrNull();
        		
        		
        		
        		String aiResultText = AiMdCall.callRephrase(aiSourceText);
        		
                Text text = new Text(aiResultText);
                aiMdReferenceBlock.insertAfter(text);
                state.nodeAdded(text);

                aiMdReferenceBlock.unlink();
                state.nodeRemoved(aiMdReferenceBlock);
        	}
        }
    }

    static class AiMdReferenceBlockReplacingExtension implements Parser.ParserExtension {

        private AiMdReferenceBlockReplacingExtension() { }

        @Override
        public void parserOptions(MutableDataHolder options) { }

        @Override
        public void extend(Parser.Builder parserBuilder) {
            parserBuilder.postProcessorFactory(new AiMdReferenceBlockReplacingPostProcessor.Factory(parserBuilder));
        }

        public static AiMdReferenceBlockReplacingExtension create() {
            return new AiMdReferenceBlockReplacingExtension();
        }
    }
	
	public static void generateMdFromAiMd(String aiMdSource, String fileDestinationFilePath) {

		Node documentNodeReplacing = parser.parse(aiMdSource);
		
		CU.saveStringToFile(formatter.render(documentNodeReplacing), fileDestinationFilePath);
	}
}
