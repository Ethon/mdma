package cc.ethon.mdma.core.type;

import java.util.IdentityHashMap;

public class ListType extends Type {

	private static final IdentityHashMap<Type, ListType> map = new IdentityHashMap<Type, ListType>();

	private final Type subType;

	private ListType(Type subType) {
		super();
		this.subType = subType;
	}

	public static final ListType getInstance(Type subType) {
		synchronized (map) {
			if (map.containsKey(subType)) {
				return map.get(subType);
			} else {
				final ListType listType = new ListType(subType);
				map.put(subType, listType);
				return listType;
			}
		}
	}

	public Type getSubType() {
		return subType;
	}

	@Override
	public String toString() {
		return "[" + subType.toString() + "]";
	}

	// Conversion

	@Override
	public Type commonType(Type other) {
		return other.isList() && ((ListType) other).getSubType() == subType ? this : null;
	}

	// Type check

	@Override
	public boolean isBuiltIn() {
		return subType.isBuiltIn();
	}

	@Override
	public boolean isList() {
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
		return other.isList() && subType.supportsEqual(((ListType) other).subType);
	}

	@Override
	public Type equalType(Type other) {
		return supportsEqual(other) ? BoolType.BOOL : null;
	}

	@Override
	public boolean supportsAssign(Type other) {
		return other.isList() && subType.supportsAssign(((ListType) other).subType);
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
		return supportsIndex(other) ? subType : null;
	}

}
