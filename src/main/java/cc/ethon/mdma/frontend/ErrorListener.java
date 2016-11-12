package cc.ethon.mdma.frontend;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

import cc.ethon.mdma.common.CompilerMessage;

class ErrorListener implements ANTLRErrorListener {

	private final String moduleName;
	private final List<CompilerMessage> errors;

	public ErrorListener(String moduleName) {
		this.moduleName = moduleName;
		errors = new ArrayList<CompilerMessage>();
	}

	public String getModuleName() {
		return moduleName;
	}

	public List<CompilerMessage> getErrors() {
		return errors;
	}

	@Override
	public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
		errors.add(new CompilerMessage(moduleName, line, charPositionInLine, msg));
	}

	@Override
	public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact, BitSet ambigAlts, ATNConfigSet configs) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex, BitSet conflictingAlts, ATNConfigSet configs) {
	}

	@Override
	public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction, ATNConfigSet configs) {
	}

}
