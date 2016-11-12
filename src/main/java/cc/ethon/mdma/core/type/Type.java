package cc.ethon.mdma.core.type;

public abstract class Type {

	// Type check

	public boolean isBuiltIn() {
		return false;
	}

	public boolean isInteger() {
		return false;
	}

	public boolean isFloat() {
		return false;
	}

	public boolean isBool() {
		return false;
	}

	public boolean isList() {
		return false;
	}

	public boolean isString() {
		return false;
	}

	public boolean isRange() {
		return false;
	}

	public boolean isVoid() {
		return false;
	}

	// Conversion

	public Type commonType(Type other) {
		return null;
	}

	// Unary Expressions

	public boolean supportsNegate() {
		return negateType() != null;
	}

	public Type negateType() {
		return null;
	}

	// Binary Expressions

	public boolean supportsMultiply(Type other) {
		return multiplyType(other) != null;
	}

	public Type multiplyType(Type other) {
		return null;
	}

	public boolean supportsModulo(Type other) {
		return moduloType(other) != null;
	}

	public Type moduloType(Type other) {
		return null;
	}

	public boolean supportsEqual(Type other) {
		return equalType(other) != null;
	}

	public Type equalType(Type other) {
		return null;
	}

	public boolean supportsAssign(Type other) {
		return assignType(other) != null;
	}

	public Type assignType(Type other) {
		return null;
	}

	public boolean supportsRange(Type other) {
		return rangeType(other) != null;
	}

	public Type rangeType(Type other) {
		return null;
	}

	public boolean supportsIndex(Type other) {
		return indexType(other) != null;
	}

	public Type indexType(Type other) {
		return null;
	}

}
