package cc.ethon.mdma.backend.interpreter.value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ListValue extends IterableValue {

	private final List<Value> value;

	public ListValue() {
		value = new ArrayList<Value>();
	}

	public ListValue(List<Value> value) {
		this.value = value;
	}

	public static ListValue valueOf(List<Value> value) {
		return new ListValue(value);
	}

	public static ListValue valueOf(Value... values) {
		final List<Value> value = new ArrayList<Value>(values.length);
		value.addAll(Arrays.asList(values));
		return valueOf(value);
	}

	public List<Value> getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "ListValue [value=" + value + "]";
	}

	@Override
	public Iterator<Value> iterator() {
		return value.iterator();
	}

	// Type check.

	@Override
	public boolean isList() {
		return true;
	}

	// Conversion to Java type.

	@Override
	public List<Value> getListValue() {
		return value;
	}

	// Binary operators.

	@Override
	public Value indexGet(Value index) {
		return index.isInteger() ? value.get((int) index.getIntegerValue()) : super.indexGet(index);
	}

	@Override
	public Value indexSet(Value index, Value value) {
		if (!index.isInteger()) {
			return super.indexSet(index, value);
		}
		this.value.set((int) index.getIntegerValue(), value);
		return value;
	}

	@Override
	public Value multiply(Value other) {
		if (!other.isInteger()) {
			return super.multiply(other);
		}

		final List<Value> result = new ArrayList<Value>((int) (value.size() * Math.max(other.getIntegerValue(), 0)));
		for (int i = 0; i < Math.max(other.getIntegerValue(), 0); ++i) {
			result.addAll(value);
		}
		return valueOf(result);
	};

}
