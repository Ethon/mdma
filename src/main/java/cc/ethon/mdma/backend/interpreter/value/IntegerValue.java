package cc.ethon.mdma.backend.interpreter.value;

public class IntegerValue extends Value {

	private final long value;

	public IntegerValue(long value) {
		super();
		this.value = value;
	}

	public static IntegerValue valueOf(long value) {
		return new IntegerValue(value);
	}

	public long getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "IntegerValue [value=" + value + "]";
	}

	// Type check.

	@Override
	public boolean isInteger() {
		return true;
	}

	// Conversion to Java type.

	@Override
	public long getIntegerValue() {
		return value;
	}

	// Binary operators.

	@Override
	public Value add(Value other) {
		return other.isInteger() ? valueOf(value + other.getIntegerValue()) : super.add(other);
	}

	@Override
	public Value multiply(Value other) {
		return other.isInteger() ? valueOf(value * other.getIntegerValue()) : super.add(other);
	}

	@Override
	public Value modulo(Value other) {
		return other.isInteger() ? valueOf(value % other.getIntegerValue()) : super.modulo(other);
	}

	@Override
	public Value lessThan(Value other) {
		return other.isInteger() ? BoolValue.valueOf(value < other.getIntegerValue()) : super.lessThan(other);
	}

	@Override
	public Value equalTo(Value other) {
		return other.isInteger() ? BoolValue.valueOf(value == other.getIntegerValue()) : super.equalTo(other);
	}
}
