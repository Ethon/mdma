package cc.ethon.mdma.core.symbol;

import cc.ethon.mdma.frontend.ast.Node;

public abstract class Symbol {

	private final Node definingNode;
	private final SymbolVisibility visibility;

	public Symbol(Node definingNode, SymbolVisibility visibility) {
		super();
		this.definingNode = definingNode;
		this.visibility = visibility;
	}

	public Node getDefiningNode() {
		return definingNode;
	}

	public SymbolVisibility getVisibility() {
		return visibility;
	}

}
