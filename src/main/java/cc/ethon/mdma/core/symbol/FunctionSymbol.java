package cc.ethon.mdma.core.symbol;

import java.util.List;

import cc.ethon.mdma.core.type.Type;
import cc.ethon.mdma.frontend.ast.Node;

public class FunctionSymbol extends Symbol {

	private final Type returnType;
	private final String name;
	private final List<Type> argumentTypes;

	public FunctionSymbol(Node definingNode, Type returnType, String name, List<Type> argumentTypes) {
		super(definingNode);
		this.returnType = returnType;
		this.name = name;
		this.argumentTypes = argumentTypes;
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

	@Override
	public String toString() {
		return "FunctionSymbol [returnType=" + returnType + ", name=" + name + ", argumentTypes=" + argumentTypes + "]";
	}

}
