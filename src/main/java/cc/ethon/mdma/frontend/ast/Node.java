package cc.ethon.mdma.frontend.ast;

import cc.ethon.mdma.core.symbol.SymbolTable;

public abstract class Node {

	protected final int line, column;
	protected final Node parent;
	protected SymbolTable symbolTable;

	public Node(int line, int column, Node parent) {
		super();
		this.line = line;
		this.column = column;
		this.parent = parent;
	}

	public int getLine() {
		return line;
	}

	public int getColumn() {
		return column;
	}

	public Node getParent() {
		return parent;
	}

	public SymbolTable getSymbolTable() {
		return symbolTable;
	}

	public void setSymbolTable(SymbolTable symbolTable) {
		this.symbolTable = symbolTable;
	}

	public abstract void accept(AstVisitor visitor);
}
