package cc.ethon.mdma.backend.interpreter.value;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public abstract class Value {

	// Type check.

	public boolean isBool() {
		return false;
	}

	public boolean isInteger() {
		return false;
	}

	public boolean isList() {
		return false;
	}

	public boolean isRange() {
		return false;
	}

	public boolean isString() {
		return false;
	}

	// Conversion to Java types.

	public boolean getBoolValue() {
		throw new UnsupportedOperationException();
	}

	public long getIntegerValue() {
		throw new UnsupportedOperationException();
	}

	public List<Value> getListValue() {
		throw new UnsupportedOperationException();
	}

	public Iterator<Value> getIterator() {
		throw new UnsupportedOperationException();
	}

	public String getString() {
		throw new UnsupportedOperationException();
	}

	// Misc operators.

	public Value call(Value... arguments) {
		return call(Arrays.asList(arguments));
	}

	public Value call(List<Value> arguments) {
		throw new UnsupportedOperationException();
	}

	// Unary operator.

	public Value negate() {
		throw new UnsupportedOperationException();
	}

	// Binary operators.

	public Value access(Value name) {
		throw new UnsupportedOperationException();
	}

	public Value access(String name) {
		return access(StringValue.valueOf(name));
	}

	public Value indexGet(Value index) {
		throw new UnsupportedOperationException();
	}

	public Value indexSet(Value index, Value value) {
		throw new UnsupportedOperationException();
	}

	public Value add(Value other) {
		throw new UnsupportedOperationException();
	}

	public Value multiply(Value other) {
		throw new UnsupportedOperationException();
	}

	public Value modulo(Value other) {
		throw new UnsupportedOperationException();
	}

	public Value lessThan(Value other) {
		throw new UnsupportedOperationException();
	}

	public Value equalTo(Value other) {
		throw new UnsupportedOperationException();
	}
}
