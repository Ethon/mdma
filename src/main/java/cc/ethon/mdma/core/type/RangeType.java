package cc.ethon.mdma.core.type;

import java.util.IdentityHashMap;

public class RangeType extends Type {

	private static final IdentityHashMap<Type, RangeType> map = new IdentityHashMap<Type, RangeType>();

	private final Type subType;

	private RangeType(Type subType) {
		super();
		this.subType = subType;
	}

	public static final RangeType getInstance(Type subType) {
		synchronized (map) {
			if (map.containsKey(subType)) {
				return map.get(subType);
			} else {
				final RangeType rangeType = new RangeType(subType);
				map.put(subType, new RangeType(subType));
				return rangeType;
			}
		}
	}

	public Type getSubType() {
		return subType;
	}

	@Override
	public String toString() {
		return subType.toString() + "..";
	}

	// Conversion

	@Override
	public Type commonType(Type other) {
		return other.isRange() && ((RangeType) other).getSubType() == subType ? this : null;
	}

	// Type check

	@Override
	public boolean isBuiltIn() {
		return subType.isBuiltIn();
	}

	@Override
	public boolean isRange() {
		return true;
	}

	// Unary Expressions

	@Override
	public boolean supportsNegate() {
		return false;
	}

	// Binary Expressions

	@Override
	public boolean supportsEqual(Type other) {
		return other.isRange() && subType.supportsEqual(((RangeType) other).getSubType());
	}

	@Override
	public Type equalType(Type other) {
		return supportsEqual(other) ? BoolType.BOOL : null;
	}

	@Override
	public boolean supportsAssign(Type other) {
		return other.isRange() && subType.supportsAssign(((RangeType) other).getSubType());
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
		return subType;
	}

}
