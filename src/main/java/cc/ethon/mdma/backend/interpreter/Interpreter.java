package cc.ethon.mdma.backend.interpreter;

import cc.ethon.mdma.backend.interpreter.value.ModuleValue;
import cc.ethon.mdma.frontend.ast.ModuleNode;

public class Interpreter {

	private final VariableMap globalVariables;

	public Interpreter() {
		this.globalVariables = null;
	}

	public VariableMap getGlobalVariables() {
		return globalVariables;
	}

	public ModuleValue interpreteModule(ModuleNode node) {
		return new InterpretingVisitor().interpretModule(node, globalVariables);
	}

}
