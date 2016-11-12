package cc.ethon.mdma.frontend;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import cc.ethon.mdma.MdmaLexer;
import cc.ethon.mdma.MdmaParser;
import cc.ethon.mdma.MdmaParser.ModuleContext;
import cc.ethon.mdma.frontend.ast.ModuleNode;

public class Parser {

	private static boolean validateFileName(String fileName) {
		if (fileName.isEmpty()) {
			return false;
		}
		if (!Character.isJavaIdentifierStart(fileName.charAt(0))) {
			return false;
		}
		for (int i = 1; i < fileName.length(); ++i) {
			if (!Character.isJavaIdentifierPart(fileName.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static String makeModuleName(String path) {
		final String fileName = new File(path).getName();
		final String[] parts = fileName.split("\\.");
		if (parts.length != 2 || !validateFileName(parts[0]) || !"mdma".equals(parts[1])) {
			throw new IllegalArgumentException("Illegal file name: " + path);
		}
		return parts[0];
	}

	private String getModuleName(File source) {
		return makeModuleName(source.getAbsolutePath());
	}

	private ParseResults doParseModule(MdmaLexer lexer, String moduleName) {
		lexer.removeErrorListeners();
		final ErrorListener lexerErrorListener = new ErrorListener(moduleName);
		lexer.addErrorListener(lexerErrorListener);

		final CommonTokenStream tokens = new CommonTokenStream(lexer);
		final MdmaParser parser = new MdmaParser(tokens);
		parser.removeErrorListeners();
		final ErrorListener parserErrorListener = new ErrorListener(moduleName);
		parser.addErrorListener(parserErrorListener);

		final ModuleContext tree = parser.module();
		final ModuleNode module = (ModuleNode) new AstGeneratingVisitor().visitModule(tree);
		return new ParseResults(moduleName, module, lexerErrorListener.getErrors(), parserErrorListener.getErrors());
	}

	public Parser() {
	}

	public ParseResults parseModuleString(String input, String moduleName) {
		final MdmaLexer lexer = new MdmaLexer(new ANTLRInputStream(input));
		return doParseModule(lexer, moduleName);
	}

	public ParseResults parseModuleFile(File source) throws IOException {
		return parseModuleFile(source, null);
	}

	public ParseResults parseModuleFile(File source, String moduleName) throws IOException {
		if (!source.isFile() || !source.canRead()) {
			throw new FileNotFoundException("File does not exist or is no readable file: " + source);
		}
		final MdmaLexer lexer = new MdmaLexer(new ANTLRFileStream(source.getAbsolutePath()));
		return doParseModule(lexer, moduleName != null ? moduleName : getModuleName(source));
	}

}
