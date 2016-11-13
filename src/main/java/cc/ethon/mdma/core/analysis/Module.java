package cc.ethon.mdma.core.analysis;

import java.util.ArrayList;
import java.util.List;

import cc.ethon.mdma.core.symbol.FunctionSymbol;
import cc.ethon.mdma.core.symbol.VariableSymbol;
import cc.ethon.mdma.frontend.ast.ModuleNode;

public class Module {

	private final String name;
	private final ModuleNode node;
	private final List<VariableSymbol> globalVariables;
	private final List<FunctionSymbol> globalFunctions;
	private final List<String> stringLiterals;

	public Module(String name, ModuleNode node) {
		this.name = name;
		this.node = node;
		this.globalVariables = new ArrayList<VariableSymbol>();
		this.globalFunctions = new ArrayList<FunctionSymbol>();
		this.stringLiterals = new ArrayList<String>();
	}

	public String getName() {
		return name;
	}

	public ModuleNode getNode() {
		return node;
	}

	public List<VariableSymbol> getGlobalVariables() {
		return globalVariables;
	}

	public List<FunctionSymbol> getGlobalFunctions() {
		return globalFunctions;
	}

	public List<String> getStringLiterals() {
		return stringLiterals;
	}

}
