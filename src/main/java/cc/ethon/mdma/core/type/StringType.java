package cc.ethon.mdma.core.type;

public class StringType extends Type {

	public static final StringType STRING = new StringType();

	private StringType() {
	}

	@Override
	public String toString() {
		return "string";
	}

	// Conversion

	@Override
	public Type commonType(Type other) {
		return this == other ? this : null;
	}

	// Type check

	@Override
	public boolean isBuiltIn() {
		return true;
	}

	@Override
	public boolean isString() {
		return true;
	}

	// Binary Expressions

	@Override
	public boolean supportsMultiply(Type other) {
		return other.isInteger();
	}

	@Override
	public Type multiplyType(Type other) {
		return supportsMultiply(other) ? this : null;
	}

	@Override
	public boolean supportsEqual(Type other) {
		return other.isString();
	}

	@Override
	public Type equalType(Type other) {
		return supportsEqual(other) ? BoolType.BOOL : null;
	}

	@Override
	public boolean supportsAssign(Type other) {
		return other.isString();
	}

	@Override
	public Type assignType(Type other) {
		return supportsAssign(other) ? this : null;
	}

	@Override
	public boolean supportsIndex(Type other) {
		return other.isInteger();
	}

	@Override
	public Type indexType(Type other) {
		return IntegerType.CHAR;
	}

}
