package cc.ethon.mdma.frontend.ast;

public class AssignmentExpressionNode extends BinaryExpressionNode {

	public AssignmentExpressionNode(int line, int column, Node parent) {
		super(line, column, parent);
	}

	@Override
	public String toString() {
		return "AssignmentExpressionNode [left=" + left + ", right=" + right + "]";
	}

	@Override
	public void accept(AstVisitor visitor) {
		visitor.visit(this);
	}

}
