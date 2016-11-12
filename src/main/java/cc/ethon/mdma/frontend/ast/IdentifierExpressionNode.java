package cc.ethon.mdma.frontend.ast;

public class IdentifierExpressionNode extends ExpressionNode {

	private String name;

	public IdentifierExpressionNode(int line, int column, Node parent) {
		super(line, column, parent);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "IndentifierExpressionNode [name=" + name + "]";
	}

	@Override
	public void accept(AstVisitor visitor) {
		visitor.visit(this);
	}

}
