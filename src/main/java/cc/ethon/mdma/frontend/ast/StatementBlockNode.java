package cc.ethon.mdma.frontend.ast;

public class StatementBlockNode extends StatementContainerNode {

	public StatementBlockNode(int line, int column, Node parent) {
		super(line, column, parent);
	}

	@Override
	public void accept(AstVisitor visitor) {
		visitor.visit(this);
	}

}
