package cc.ethon.mdma.frontend.ast;

public abstract class BinaryExpressionNode extends ExpressionNode {

	protected ExpressionNode left, right;

	public BinaryExpressionNode(int line, int column, Node parent) {
		super(line, column, parent);
	}

	public ExpressionNode getLeft() {
		return left;
	}

	public void setLeft(ExpressionNode left) {
		this.left = left;
	}

	public ExpressionNode getRight() {
		return right;
	}

	public void setRight(ExpressionNode right) {
		this.right = right;
	}

}
