package cc.ethon.mdma.frontend.ast;

public class IndexExpressionNode extends BinaryExpressionNode {

	public IndexExpressionNode(int line, int column, Node parent) {
		super(line, column, parent);
	}

	@Override
	public String toString() {
		return "IndexExpressionNode [left=" + left + ", right=" + right + "]";
	}

	@Override
	public void accept(AstVisitor visitor) {
		visitor.visit(this);
	}

}
