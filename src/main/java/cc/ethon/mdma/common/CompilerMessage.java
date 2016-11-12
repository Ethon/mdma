package cc.ethon.mdma.common;

public class CompilerMessage {

	private final String moduleName;
	private final int line;
	private final int column;
	private final String message;

	public CompilerMessage(String moduleName, int line, int column, String message) {
		this.moduleName = moduleName;
		this.line = line;
		this.column = column;
		this.message = message;
	}

	public String getModuleName() {
		return moduleName;
	}

	public int getLine() {
		return line;
	}

	public int getColumn() {
		return column;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return "ParseError [moduleName=" + moduleName + ", line=" + line + ", column=" + column + ", message=" + message + "]";
	}

}