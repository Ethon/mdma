package cc.ethon.mdma.frontend.ast;

public class EqualExpressionNode extends BinaryExpressionNode {

	public EqualExpressionNode(int line, int column, Node parent) {
		super(line, column, parent);
	}

	@Override
	public String toString() {
		return "EqualExpressionNode [left=" + left + ", right=" + right + "]";
	}

	@Override
	public void accept(AstVisitor visitor) {
		visitor.visit(this);
	}

}
