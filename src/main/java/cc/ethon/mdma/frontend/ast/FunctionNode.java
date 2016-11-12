package cc.ethon.mdma.frontend.ast;

import java.util.List;

public class FunctionNode extends StatementContainerNode {

	private String name;
	private List<VariableDeclarationNode> arguments;
	private TypeNode returnType;

	public FunctionNode(int line, int column, Node parent) {
		super(line, column, parent);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<VariableDeclarationNode> getArguments() {
		return arguments;
	}

	public void setArguments(List<VariableDeclarationNode> arguments) {
		this.arguments = arguments;
	}

	public TypeNode getReturnType() {
		return returnType;
	}

	public void setReturnType(TypeNode returnType) {
		this.returnType = returnType;
	}

	@Override
	public String toString() {
		return "FunctionNode [arguments=" + arguments + ", returnType=" + returnType + ", children=" + children + "]";
	}

	@Override
	public void accept(AstVisitor visitor) {
		visitor.visit(this);
	}

}
