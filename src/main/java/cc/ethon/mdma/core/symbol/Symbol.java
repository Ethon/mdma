package cc.ethon.mdma.core.symbol;

import cc.ethon.mdma.frontend.ast.Node;

public abstract class Symbol {

	private final Node definingNode;

	public Symbol(Node definingNode) {
		this.definingNode = definingNode;
	}

	public Node getDefiningNode() {
		return definingNode;
	}

}
