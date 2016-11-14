package cc.ethon.mdma.backend.gen.c;

import java.io.PrintStream;
import java.util.UUID;

import cc.ethon.mdma.core.symbol.FunctionSymbol;
import cc.ethon.mdma.core.symbol.SymbolVisibility;

public class CCodeEmitter {

	private final PrintStream out;
	private final CTypeMapper typeMapper;
	private final String indent;
	private int indentLevel;

	private void increaseIndentation() {
		++indentLevel;
	}

	private void decreaseIndentation() {
		--indentLevel;
	}

	private void indent() {
		for (int i = 0; i < indentLevel; ++i) {
			out.println(indent);
		}
	}

	private void emitFunctionHeaderPart(FunctionSymbol symbol) {
		final String returnType = typeMapper.getTypeName(symbol.getReturnType());

		final StringBuilder builder = new StringBuilder();
		for (int i = 0; i < symbol.getArgumentTypes().size(); ++i) {
			if (i != 0) {
				builder.append(", ");
			}
			builder.append(typeMapper.getTypeName(symbol.getArgumentTypes().get(i)));
			builder.append(' ');
			builder.append(symbol.getArgumentNames().get(i));
		}
		final String arguments = builder.toString();

		out.printf("%s %s(%s)", returnType, symbol.getName(), arguments);
	}

	public CCodeEmitter(PrintStream out, CTypeMapper typeMapper) {
		this(out, typeMapper, "  ");
	}

	public CCodeEmitter(PrintStream out, CTypeMapper typeMapper, String indent) {
		super();
		this.out = out;
		this.typeMapper = typeMapper;
		this.indent = indent;
		this.indentLevel = 0;
	}

	public void emitEmptyLine() {
		out.println();
	}

	public String startIncludeGuarded() {
		final String guard = "HEADER_UUID_" + UUID.randomUUID().toString().replace('-', '_').toUpperCase();
		out.println("#ifndef " + guard);
		out.println("#define " + guard);
		out.println();
		return guard;
	}

	public void endIncludeGuarded(String guard) {
		out.println("#endif /* " + guard + " */");
		out.println();
	}

	public void startStatementBlock() {
		out.println(" {");
		increaseIndentation();
	}

	public void endStatementBlock() {
		decreaseIndentation();
		indent();
		out.println("}");
	}

	public void emitSystemInclude(String include) {
		out.println("#include <" + include + ">");
	}

	public void emitUserInclude(String include) {
		out.println("#include \"" + include + "\"");
	}

	public void emitFunctionDeclaration(FunctionSymbol symbol) {
		indent();
		if (symbol.getVisibility() == SymbolVisibility.MODULE_PRIVATE) {
			out.print("static ");
		}
		emitFunctionHeaderPart(symbol);
		out.println(";");
		out.println();
	}
}
