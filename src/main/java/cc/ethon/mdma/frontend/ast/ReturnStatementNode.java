package cc.ethon.mdma.frontend.ast;

public class ReturnStatementNode extends StatementNode {

	private ExpressionNode returnedExpression;

	public ReturnStatementNode(int line, int column, Node parent) {
		super(line, column, parent);
	}

	public ExpressionNode getReturnedExpression() {
		return returnedExpression;
	}

	public void setReturnedExpression(ExpressionNode returnedExpression) {
		this.returnedExpression = returnedExpression;
	}

	@Override
	public void accept(AstVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public String toString() {
		return "ReturnStatementNode [returnedExpression=" + returnedExpression + "]";
	}

}
