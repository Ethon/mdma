package cc.ethon.mdma.backend.interpreter;

import cc.ethon.mdma.backend.interpreter.value.Value;

@SuppressWarnings("serial")
public class FunctionReturn extends RuntimeException {

	private final Value returnValue;

	public FunctionReturn(Value returnValue) {
		super();
		this.returnValue = returnValue;
	}

	public Value getReturnValue() {
		return returnValue;
	}

}
