package cc.ethon.mdma.frontend.ast;

public class VariableDeclarationNode extends StatementNode {

	private String name;
	private TypeNode type;
	private ExpressionNode assignedValue;

	public VariableDeclarationNode(int line, int column, Node parent) {
		super(line, column, parent);
	}

	public VariableDeclarationNode(int line, int column, Node parent, String name, TypeNode type) {
		super(line, column, parent);
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TypeNode getType() {
		return type;
	}

	public void setType(TypeNode type) {
		this.type = type;
	}

	public ExpressionNode getAssignedValue() {
		return assignedValue;
	}

	public void setAssignedValue(ExpressionNode assignedValue) {
		this.assignedValue = assignedValue;
	}

	@Override
	public String toString() {
		return "VariableDeclarationNode [name=" + name + ", type=" + type + ", assignedValue=" + assignedValue + "]";
	}

	@Override
	public void accept(AstVisitor visitor) {
		visitor.visit(this);
	}

}
