package cc.ethon.mdma.frontend.ast;

import java.math.BigInteger;

public class IntegerExpressionNode extends ExpressionNode {

	private BigInteger value;

	public IntegerExpressionNode(int line, int column, Node parent) {
		super(line, column, parent);
	}

	public BigInteger getValue() {
		return value;
	}

	public void setValue(BigInteger value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "IntegerNode [value=" + value + "]";
	}

	@Override
	public void accept(AstVisitor visitor) {
		visitor.visit(this);
	}

}
