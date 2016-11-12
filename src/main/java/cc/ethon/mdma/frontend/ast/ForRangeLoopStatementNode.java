package cc.ethon.mdma.frontend.ast;

public class ForRangeLoopStatementNode extends StatementContainerNode {

	private VariableDeclarationNode variable;
	private ExpressionNode range;

	public ForRangeLoopStatementNode(int line, int column, Node parent) {
		super(line, column, parent);
	}

	public VariableDeclarationNode getVariable() {
		return variable;
	}

	public void setVariable(VariableDeclarationNode variable) {
		this.variable = variable;
	}

	public ExpressionNode getRange() {
		return range;
	}

	public void setRange(ExpressionNode range) {
		this.range = range;
	}

	@Override
	public String toString() {
		return "ForRangeLoop [variable=" + variable + ", range=" + range + ", children=" + children + "]";
	}

	@Override
	public void accept(AstVisitor visitor) {
		visitor.visit(this);
	}

}
