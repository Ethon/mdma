package cc.ethon.mdma.core.type;

public class BoolType extends Type {

	public static final BoolType BOOL = new BoolType();

	private BoolType() {
	}

	@Override
	public String toString() {
		return "bool";
	}

	// Type check

	@Override
	public boolean isBuiltIn() {
		return true;
	}

	@Override
	public boolean isBool() {
		return true;
	}

	// Conversion

	@Override
	public Type commonType(Type other) {
		return other.isBool() ? this : null;
	}

	// Unary Expressions

	@Override
	public boolean supportsNegate() {
		return true;
	}

	@Override
	public Type negateType() {
		return this;
	}

	// Binary Expressions

	@Override
	public boolean supportsEqual(Type other) {
		return other.isBool();
	}

	@Override
	public Type equalType(Type other) {
		return supportsEqual(other) ? this : null;
	}

	@Override
	public boolean supportsAssign(Type other) {
		return other.isBool();
	}

	@Override
	public Type assignType(Type other) {
		return supportsAssign(other) ? this : null;
	}

	@Override
	public boolean supportsRange(Type other) {
		return other.isBool();
	}

	@Override
	public Type rangeType(Type other) {
		return supportsRange(other) ? RangeType.getInstance(this) : null;
	}

}
