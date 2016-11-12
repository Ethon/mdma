package cc.ethon.mdma.frontend.ast;

import java.util.List;

public class IfStatementNode extends StatementNode {

	public static class ConditionBodyPair {

		private final ExpressionNode condition;
		private final List<StatementNode> body;

		public ConditionBodyPair(ExpressionNode condition, List<StatementNode> body) {
			this.condition = condition;
			this.body = body;
		}

		public ExpressionNode getCondition() {
			return condition;
		}

		public List<StatementNode> getBody() {
			return body;
		}

		@Override
		public String toString() {
			return "ConditionBodyPair [condition=" + condition + ", body=" + body + "]";
		}

	}

	private ExpressionNode condition;
	private List<StatementNode> body;
	private List<ConditionBodyPair> elifs;
	private List<StatementNode> elseBody;

	public IfStatementNode(int line, int column, Node parent) {
		super(line, column, parent);
	}

	public ExpressionNode getCondition() {
		return condition;
	}

	public void setCondition(ExpressionNode condition) {
		this.condition = condition;
	}

	public List<StatementNode> getBody() {
		return body;
	}

	public void setBody(List<StatementNode> body) {
		this.body = body;
	}

	public List<ConditionBodyPair> getElifs() {
		return elifs;
	}

	public void setElifs(List<ConditionBodyPair> elifs) {
		this.elifs = elifs;
	}

	public List<StatementNode> getElseBody() {
		return elseBody;
	}

	public void setElseBody(List<StatementNode> elseBody) {
		this.elseBody = elseBody;
	}

	@Override
	public String toString() {
		return "IfStatementNode [condition=" + condition + ", body=" + body + ", elifs=" + elifs + ", elseBody=" + elseBody + "]";
	}

	@Override
	public void accept(AstVisitor visitor) {
		visitor.visit(this);
	}

}
