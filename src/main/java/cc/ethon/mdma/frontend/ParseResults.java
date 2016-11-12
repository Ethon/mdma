package cc.ethon.mdma.frontend;

import java.util.Collections;
import java.util.List;

import cc.ethon.mdma.common.CompilerMessage;
import cc.ethon.mdma.frontend.ast.Node;

public class ParseResults {

	private final String moduleName;
	private final Node node;
	private final List<CompilerMessage> lexerErrors;
	private final List<CompilerMessage> parserErrors;

	public ParseResults(String moduleName, Node node, List<CompilerMessage> lexerErrors, List<CompilerMessage> parserErrors) {
		super();
		this.moduleName = moduleName;
		this.node = node;
		this.lexerErrors = Collections.unmodifiableList(lexerErrors);
		this.parserErrors = Collections.unmodifiableList(parserErrors);
	}

	public String getModuleName() {
		return moduleName;
	}

	public Node getNode() {
		return node;
	}

	public List<CompilerMessage> getLexerErrors() {
		return lexerErrors;
	}

	public List<CompilerMessage> getParserErrors() {
		return parserErrors;
	}

	public int getLexerErrorCount() {
		return lexerErrors.size();
	}

	public int getParserErrorCount() {
		return parserErrors.size();
	}

	public int getTotalErrorCount() {
		return getLexerErrorCount() + getParserErrorCount();
	}

	public boolean success() {
		return getTotalErrorCount() == 0;
	}

}
