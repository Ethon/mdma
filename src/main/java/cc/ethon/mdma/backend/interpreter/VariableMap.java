package cc.ethon.mdma.backend.interpreter;

import java.util.HashMap;
import java.util.Map;

import cc.ethon.mdma.backend.interpreter.value.Value;

public class VariableMap {

	private final VariableMap parent;
	private final Map<String, Value> variables;

	public VariableMap(VariableMap parent) {
		this.parent = parent;
		variables = new HashMap<String, Value>();
	}

	public VariableMap getParent() {
		return parent;
	}

	public Value getVariable(String name) {
		final Value result = variables.get(name);
		if (result != null) {
			return result;
		}

		if (parent != null) {
			return parent.getVariable(name);
		}
		throw new IllegalStateException("Variable " + name + " not available");
	}

	public void setVariable(String name, Value value) {
		variables.put(name, value);
	}

}
