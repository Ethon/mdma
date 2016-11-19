package cc.ethon.mdma.backend.interpreter.value;

import cc.ethon.mdma.backend.interpreter.VariableMap;

public class ModuleValue extends Value {

	private final VariableMap variables;

	public ModuleValue(VariableMap variables) {
		super();
		this.variables = variables;
	}

	public VariableMap getVariables() {
		return variables;
	}

	// Binary operators.

	@Override
	public Value access(Value name) {
		return name.isString() ? variables.getVariable(name.getString()) : super.access(name);
	}

	@Override
	public Value access(String name) {
		return variables.getVariable(name);
	}

}
