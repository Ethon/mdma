package cc.ethon.mdma.core.symbol;

import java.util.List;

import cc.ethon.mdma.core.type.Type;
import cc.ethon.mdma.frontend.ast.Node;

public class FunctionSymbol extends Symbol {

	private final Type returnType;
	private final String name;
	private final List<Type> argumentTypes;
	private final List<String> argumentNames;

	public FunctionSymbol(Node definingNode, SymbolVisibility visibility, Type returnType, String name, List<Type> argumentTypes, List<String> argumentNames) {
		super(definingNode, visibility);
		this.returnType = returnType;
		this.name = name;
		this.argumentTypes = argumentTypes;
		this.argumentNames = argumentNames;
	}

	public Type getReturnType() {
		return returnType;
	}

	public String getName() {
		return name;
	}

	public List<Type> getArgumentTypes() {
		return argumentTypes;
	}

	public List<String> getArgumentNames() {
		return argumentNames;
	}

	@Override
	public String toString() {
		return "FunctionSymbol [returnType=" + returnType + ", name=" + name + ", argumentTypes=" + argumentTypes + ", argumentNames=" + argumentNames + "]";
	}

}
