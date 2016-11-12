package cc.ethon.mdma.frontend.ast;

public class ListTypeNode extends TypeNode {

	private TypeNode subType;

	public ListTypeNode(int line, int column, Node parent) {
		super(line, column, parent);
	}

	public TypeNode getSubType() {
		return subType;
	}

	public void setSubType(TypeNode subType) {
		this.subType = subType;
	}

	@Override
	public String toString() {
		return "ListTypeNode [subType=" + subType + "]";
	}

	@Override
	public void accept(AstVisitor visitor) {
		visitor.visit(this);
	}

}
