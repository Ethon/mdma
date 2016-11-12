package cc.ethon.mdma.frontend.ast;

import java.util.List;

public class ListExpressionNode extends ExpressionNode {

	private List<ExpressionNode> values;

	public ListExpressionNode(int line, int column, Node parent) {
		super(line, column, parent);
	}

	public List<ExpressionNode> getValues() {
		return values;
	}

	public void setValues(List<ExpressionNode> values) {
		this.values = values;
	}

	@Override
	public String toString() {
		return "ListExpressionNode [values=" + values + "]";
	}

	@Override
	public void accept(AstVisitor visitor) {
		visitor.visit(this);
	}

}
