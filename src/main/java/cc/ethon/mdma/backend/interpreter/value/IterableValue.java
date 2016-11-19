package cc.ethon.mdma.backend.interpreter.value;

import java.util.Iterator;

public abstract class IterableValue extends Value implements Iterable<Value> {

	@Override
	public Iterator<Value> getIterator() {
		return iterator();
	}

}
