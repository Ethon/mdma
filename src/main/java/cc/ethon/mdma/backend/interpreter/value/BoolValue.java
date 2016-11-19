package cc.ethon.mdma.backend.interpreter.value;

public class BoolValue extends Value {

	private final boolean value;

	public BoolValue(boolean value) {
		super();
		this.value = value;
	}

	public static BoolValue valueOf(boolean value) {
		return new BoolValue(value);
	}

	public boolean isValue() {
		return value;
	}

	@Override
	public String toString() {
		return "BoolValue [value=" + value + "]";
	}

	@Override
	public boolean isBool() {
		return true;
	}

	@Override
	public boolean getBoolValue() {
		return value;
	}

	// Unary operator.

	@Override
	public Value negate() {
		return valueOf(!value);
	}

}
