/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.markdown.editor.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.emdepub.activator.R;
import org.emdepub.markdown.editor.engine.MarkdownCompletionProposal.MarkdownCompletionProposalKey;

import com.vladsch.flexmark.ast.Code;
import com.vladsch.flexmark.ast.CodeBlock;
import com.vladsch.flexmark.ast.Emphasis;
import com.vladsch.flexmark.ast.FencedCodeBlock;
import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.ast.HtmlBlock;
import com.vladsch.flexmark.ast.IndentedCodeBlock;
import com.vladsch.flexmark.ast.StrongEmphasis;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import com.vladsch.flexmark.util.ast.VisitHandler;
import com.vladsch.flexmark.util.ast.Visitor;

/** Markdown visitors engine */
public class MarkdownContentAssistEngine {

	/** For now */
	private static final char[] NO_CHARS = new char[0];

	/** Default proposals */
	private static final LinkedHashMap<MarkdownCompletionProposalKey, MarkdownCompletionProposal> defaultProposals;
	
	/** Warning proposals */
	private static final LinkedHashMap<String, MarkdownCompletionProposal> warningProposals = new LinkedHashMap<>();

	/** Fill default and warning proposals */
	static {
		defaultProposals = R.getMarkdownCompletionProposals();
		
		warningProposals.put("Html", new MarkdownCompletionProposal("", 0, 0, 0, 0,
				R.getImage("message_warning"),
				"No Markdown elements available inside an HTML block",
				null, null));
		warningProposals.put("Code", new MarkdownCompletionProposal("", 0, 0, 0, 0,
				R.getImage("message_warning"),
				"No Markdown elements available inside a code inline or code block",
				null, null));
	}
	
	/** Linearize some nodes */
	private static final ArrayList<String> linearNodes = new ArrayList<>();
	private static final int[] cursor = new int[1];
		
	/** If around offset */
	public void visitNode(Node node) {
		
		if ((node.getStartOffset() <= cursor[0]) && (cursor[0] <= node.getEndOffset())) {
			linearNodes.add(node.getClass().getSimpleName());
		}
	}
	
	/** Linearize the nodes */
	private NodeVisitor nodeVisitor = new NodeVisitor(
		new VisitHandler<HtmlBlock>(HtmlBlock.class, new Visitor<HtmlBlock>() {
			@Override
			public void visit(HtmlBlock htmlBlock) {
				visitNode(htmlBlock);
				nodeVisitor.visitChildren(htmlBlock);
			}
		}),

		new VisitHandler<Heading>(Heading.class, new Visitor<Heading>() {
			@Override
			public void visit(Heading heading) {
				visitNode(heading);
				nodeVisitor.visitChildren(heading);
			}
		}),

		new VisitHandler<Emphasis>(Emphasis.class, new Visitor<Emphasis>() {
		@Override
		public void visit(Emphasis emphasis) {
			visitNode(emphasis);
			nodeVisitor.visitChildren(emphasis);
		}
		}),
		new VisitHandler<StrongEmphasis>(StrongEmphasis.class, new Visitor<StrongEmphasis>() {
			@Override
			public void visit(StrongEmphasis strongEmphasis) {
				visitNode(strongEmphasis);
				nodeVisitor.visitChildren(strongEmphasis);
			}
		}),

		new VisitHandler<Code>(Code.class, new Visitor<Code>() {
			@Override
			public void visit(Code code) {
				visitNode(code);
			}
		}),
		new VisitHandler<FencedCodeBlock>(FencedCodeBlock.class, new Visitor<FencedCodeBlock>() {
			@Override
			public void visit(FencedCodeBlock fencedCodeBlock) {
				visitNode(fencedCodeBlock);
			}
		}),
		new VisitHandler<IndentedCodeBlock>(IndentedCodeBlock.class, new Visitor<IndentedCodeBlock>() {
			@Override
			public void visit(IndentedCodeBlock indentedCodeBlock) {
				visitNode(indentedCodeBlock);
			}
		}),
		new VisitHandler<CodeBlock>(CodeBlock.class, new Visitor<CodeBlock>() {
			@Override
			public void visit(CodeBlock codeBlock) {
				visitNode(codeBlock);
			}
		})
	);

	/** Find offset proposals */
	public ICompletionProposal[] runContentAssist(String markdownString, int offset, String lineDelimiter) {
		
		//ArrayList<ICompletionProposal> completionProposals = new ArrayList<>();
		
		/* Parse document in a node */
		Node documentNode = MarkdownFormatterEngine.getParser().parse(markdownString);
		linearNodes.clear();
		cursor[0] = offset;
		nodeVisitor.visitChildren(documentNode);
		//L.p(linearNodes.toString());

		MarkdownCompletionProposal[] completionProposals;

		LinkedHashSet<MarkdownCompletionProposalKey> validProposals = new LinkedHashSet<MarkdownCompletionProposalKey>(Arrays.asList(MarkdownCompletionProposalKey.values()));
		MarkdownCompletionProposal warningProposal = null;
		
		if (linearNodes.size() == 0) {
			/* OK */
		}
		else if (linearNodes.contains("Code") || linearNodes.contains("FencedCodeBlock") ||
				linearNodes.contains("IndentedCodeBlock") || linearNodes.contains("CodeBlock")) {
			warningProposal = warningProposals.get("Code");
		}
		else if (linearNodes.contains("HtmlBlock")) {
			warningProposal = warningProposals.get("Html");
		}
		else {
			if (linearNodes.contains("Heading")) {
				validProposals.remove(MarkdownCompletionProposalKey.HEADING_LEVEL_1);
				validProposals.remove(MarkdownCompletionProposalKey.HEADING_LEVEL_2);
				validProposals.remove(MarkdownCompletionProposalKey.HEADING_LEVEL_3);
				validProposals.remove(MarkdownCompletionProposalKey.HEADING_LEVEL_4);
				validProposals.remove(MarkdownCompletionProposalKey.HEADING_LEVEL_5);
				validProposals.remove(MarkdownCompletionProposalKey.HEADING_LEVEL_6);
			}
			if (linearNodes.get(linearNodes.size() - 1).equals("Emphasis")) {
				validProposals.remove(MarkdownCompletionProposalKey.ITALIC_TEXT);
				validProposals.remove(MarkdownCompletionProposalKey.BOLD_ITALIC_TEXT);
			}
			if (linearNodes.get(linearNodes.size() - 1).equals("StrongEmphasis")) {
				validProposals.remove(MarkdownCompletionProposalKey.BOLD_TEXT);
				validProposals.remove(MarkdownCompletionProposalKey.BOLD_ITALIC_TEXT);
			}
		}

		if (warningProposal != null) {
			completionProposals = new MarkdownCompletionProposal[1];
			warningProposal.setReplacementOffset(offset);
			completionProposals[0] = warningProposal;
			return completionProposals;
		}
		
		completionProposals = new MarkdownCompletionProposal[validProposals.size()];
		int index = 0;
		int lineDelimiterChars = lineDelimiter.length();
		for (MarkdownCompletionProposalKey validProposal : validProposals) {
			MarkdownCompletionProposal completionProposal = defaultProposals.get(validProposal);
			completionProposal.setReplacementString(completionProposal.getReplacementString().replaceAll("\\R", lineDelimiter));
			completionProposal.setReplacementOffset(offset);
			completionProposal.setCursorPosition(completionProposal.getCursorPositionChars() +
					completionProposal.getCursorPositionLineDelimiters() * lineDelimiterChars);
			completionProposals[index] = completionProposal;
			index++;
		}
		
		return completionProposals;
		// defaultProposals.values().toArray(new ICompletionProposal[defaultProposals.values().size()]);
	}
	
	/** For now, then #, | */
	public char[] getAutoActivationCharacters() {
	
		return NO_CHARS;
	}
	
	/** ? */
	public String getErrorMessage() {
		
		return "Content assist error";
	}
}
