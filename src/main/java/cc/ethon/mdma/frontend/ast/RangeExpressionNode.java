package cc.ethon.mdma.frontend.ast;

public class RangeExpressionNode extends BinaryExpressionNode {

	public RangeExpressionNode(int line, int column, Node parent) {
		super(line, column, parent);
	}

	@Override
	public String toString() {
		return "RangeExpressionNode [left=" + left + ", right=" + right + "]";
	}

	@Override
	public void accept(AstVisitor visitor) {
		visitor.visit(this);
	}

}
