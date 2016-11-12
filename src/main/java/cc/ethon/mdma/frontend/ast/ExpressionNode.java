package cc.ethon.mdma.frontend.ast;

import cc.ethon.mdma.core.type.Type;

public abstract class ExpressionNode extends Node {

	private Type type;

	public ExpressionNode(int line, int column, Node parent) {
		super(line, column, parent);
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

}
