package cc.ethon.mdma.frontend.ast;

public class ModuleNode extends StatementContainerNode {

	public ModuleNode() {
		super(0, 0, null);
	}

	@Override
	public String toString() {
		return "ModuleNode [children=" + children + "]";
	}

	@Override
	public void accept(AstVisitor visitor) {
		visitor.visit(this);
	}

}
