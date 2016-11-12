package cc.ethon.mdma.frontend.ast;

public class ExpressionStatementNode extends StatementNode {

	private ExpressionNode expression;

	public ExpressionStatementNode(int line, int column, Node parent) {
		super(line, column, parent);
	}

	public ExpressionNode getExpression() {
		return expression;
	}

	public void setExpression(ExpressionNode expression) {
		this.expression = expression;
	}

	@Override
	public String toString() {
		return "ExpressionStatementNode [expression=" + expression + "]";
	}

	@Override
	public void accept(AstVisitor visitor) {
		visitor.visit(this);
	}

}
