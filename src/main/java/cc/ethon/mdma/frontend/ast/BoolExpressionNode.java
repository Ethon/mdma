package cc.ethon.mdma.frontend.ast;

public class BoolExpressionNode extends ExpressionNode {

	private boolean value;

	public BoolExpressionNode(int line, int column, Node parent) {
		super(line, column, parent);
	}

	public boolean getValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "BoolExpressionNode [value=" + value + "]";
	}

	@Override
	public void accept(AstVisitor visitor) {
		visitor.visit(this);
	}

}
