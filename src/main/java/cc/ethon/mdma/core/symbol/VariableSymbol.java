package cc.ethon.mdma.core.symbol;

import cc.ethon.mdma.core.type.Type;
import cc.ethon.mdma.frontend.ast.Node;

public class VariableSymbol extends Symbol {

	private final Type type;
	private final String name;

	public VariableSymbol(Node definingNode, Type type, String name) {
		super(definingNode);
		this.type = type;
		this.name = name;
	}

	public Type getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "VariableSymbol [type=" + type + ", name=" + name + "]";
	}

}
