package cc.ethon.mdma.frontend.ast;

public class ModuloExpressionNode extends BinaryExpressionNode {

	public ModuloExpressionNode(int line, int column, Node parent) {
		super(line, column, parent);
	}

	@Override
	public String toString() {
		return "ModuloExpressionNode [left=" + left + ", right=" + right + "]";
	}

	@Override
	public void accept(AstVisitor visitor) {
		visitor.visit(this);
	}

}
