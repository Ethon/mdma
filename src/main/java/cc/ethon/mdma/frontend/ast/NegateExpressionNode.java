package cc.ethon.mdma.frontend.ast;

public class NegateExpressionNode extends UnaryExpressionNode {

	public NegateExpressionNode(int line, int column, Node parent) {
		super(line, column, parent);
	}

	@Override
	public String toString() {
		return "NegateExpressionNode [child=" + child + "]";
	}

	@Override
	public void accept(AstVisitor visitor) {
		visitor.visit(this);
	}

}
