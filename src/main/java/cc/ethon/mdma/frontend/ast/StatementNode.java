package cc.ethon.mdma.frontend.ast;

public abstract class StatementNode extends Node {

	public StatementNode(int line, int column, Node parent) {
		super(line, column, parent);
	}

}
