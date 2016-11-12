package cc.ethon.mdma.frontend.ast;

import java.util.Collections;
import java.util.List;

public abstract class StatementContainerNode extends StatementNode {

	protected List<StatementNode> children;

	public StatementContainerNode(int line, int column, Node parent) {
		super(line, column, parent);
		children = Collections.emptyList();
	}

	public List<StatementNode> getChildren() {
		return children;
	}

	public void setChildren(List<StatementNode> children) {
		this.children = children;
	}

	@Override
	public String toString() {
		return "ContainerNode [children=" + children + "]";
	}

}
