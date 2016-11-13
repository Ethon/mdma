package cc.ethon.mdma.core.type;

import java.math.BigInteger;

public class IntegerType extends Type {

	public static final int INFINITE_SIZE = Integer.MAX_VALUE;

	public static final IntegerType BYTE = new IntegerType("byte", 8, Byte.MIN_VALUE, Byte.MAX_VALUE);
	public static final IntegerType SHORT = new IntegerType("short", 16, Short.MIN_VALUE, Short.MAX_VALUE);
	public static final IntegerType INT = new IntegerType("int", 32, Integer.MIN_VALUE, Integer.MAX_VALUE);
	public static final IntegerType LONG = new IntegerType("long", 64, Long.MIN_VALUE, Long.MAX_VALUE);
	public static final IntegerType BIGINT = new IntegerType("bigint", INFINITE_SIZE, 0, 0);
	public static final IntegerType CHAR = new IntegerType("char", 8, Character.MIN_VALUE, Character.MAX_VALUE);

	private final String name;
	private final int size;
	private final BigInteger minValue, maxValue;

	private IntegerType(String name, int size, long minValue, long maxValue) {
		this.name = name;
		this.size = size;
		this.minValue = BigInteger.valueOf(minValue);
		this.maxValue = BigInteger.valueOf(maxValue);
	}

	public int getSize() {
		return size;
	}

	public IntegerType biggerType(IntegerType other) {
		return size > other.size ? this : other;
	}

	public boolean isValueInBounds(BigInteger value) {
		if (size == INFINITE_SIZE) {
			return true;
		}
		return minValue.compareTo(value) >= 0 && maxValue.compareTo(value) <= 0;
	}

	@Override
	public String toString() {
		return name;
	}

	// Conversion

	@Override
	public Type commonType(Type other) {
		return other.isInteger() ? biggerType((IntegerType) other) : null;
	}

	// Type check

	@Override
	public boolean isBuiltIn() {
		return true;
	}

	@Override
	public boolean isInteger() {
		return true;
	}

	// Unary Expressions

	@Override
	public boolean supportsNegate() {
		return false;
	}

	// Binary Expressions

	@Override
	public boolean supportsMultiply(Type other) {
		return other.isInteger();
	}

	@Override
	public Type multiplyType(Type other) {
		return other.isInteger() ? biggerType((IntegerType) other) : null;
	}

	@Override
	public boolean supportsModulo(Type other) {
		return other.isInteger();
	}

	@Override
	public Type moduloType(Type other) {
		return other.isInteger() ? this : null;
	}

	@Override
	public boolean supportsEqual(Type other) {
		return true;
	}

	@Override
	public Type equalType(Type other) {
		return supportsEqual(other) ? BoolType.BOOL : null;
	}

	@Override
	public boolean supportsAssign(Type other) {
		return other.isInteger() && size >= ((IntegerType) other).size;
	}

	@Override
	public Type assignType(Type other) {
		return supportsAssign(other) ? this : null;
	}

	@Override
	public boolean supportsRange(Type other) {
		return other.isInteger();
	}

	@Override
	public Type rangeType(Type other) {
		return supportsRange(other) ? RangeType.getInstance(biggerType((IntegerType) other)) : null;
	}

}
