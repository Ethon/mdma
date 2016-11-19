package cc.ethon.mdma.backend.interpreter.value;

import java.util.Iterator;

public class StringValue extends IterableValue {

	private final String value;

	public StringValue(String value) {
		super();
		this.value = value;
	}

	public static StringValue valueOf(String value) {
		return new StringValue(value);
	}

	public String getValue() {
		return value;
	}

	@Override
	public Iterator<Value> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	// Type check.

	@Override
	public boolean isString() {
		return true;
	}

	// Conversion to Java types.

	@Override
	public String getString() {
		return value;
	}

}
