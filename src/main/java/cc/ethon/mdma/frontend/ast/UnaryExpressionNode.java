package cc.ethon.mdma.frontend.ast;

public abstract class UnaryExpressionNode extends ExpressionNode {

	protected ExpressionNode child;

	public UnaryExpressionNode(int line, int column, Node parent) {
		super(line, column, parent);
	}

	public ExpressionNode getChild() {
		return child;
	}

	public void setChild(ExpressionNode child) {
		this.child = child;
	}

}
