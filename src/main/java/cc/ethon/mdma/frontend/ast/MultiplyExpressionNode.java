package cc.ethon.mdma.frontend.ast;

public class MultiplyExpressionNode extends BinaryExpressionNode {

	public MultiplyExpressionNode(int line, int column, Node parent) {
		super(line, column, parent);
	}

	@Override
	public void accept(AstVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public String toString() {
		return "MultiplyExpressionNode [left=" + left + ", right=" + right + "]";
	}

}
