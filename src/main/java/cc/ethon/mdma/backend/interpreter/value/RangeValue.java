package cc.ethon.mdma.backend.interpreter.value;

import java.util.Iterator;

public class RangeValue extends IterableValue {

	private class RangeIterator implements Iterator<Value> {

		private Value current;

		public RangeIterator() {
			current = begin;
		}

		@Override
		public boolean hasNext() {
			return current.lessThan(end).getBoolValue();
		}

		@Override
		public Value next() {
			final Value old = current;
			current = current.add(increment);
			return old;
		}

	}

	private final Value begin, end, increment;

	public RangeValue(Value begin, Value end, Value increment) {
		super();
		this.begin = begin;
		this.end = end;
		this.increment = increment;
	}

	public Value getBegin() {
		return begin;
	}

	public Value getEnd() {
		return end;
	}

	public Value getIncrement() {
		return increment;
	}

	@Override
	public String toString() {
		return "RangeValue [begin=" + begin + ", end=" + end + ", increment=" + increment + "]";
	}

	@Override
	public Iterator<Value> iterator() {
		return new RangeIterator();
	}

}
