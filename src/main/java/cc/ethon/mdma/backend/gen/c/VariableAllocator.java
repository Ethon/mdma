package cc.ethon.mdma.backend.gen.c;

public class VariableAllocator {

	private int count;

	public VariableAllocator() {
		count = 0;
	}

	public String createTemporaryVariable() {
		return "tmp" + count++;
	}

}
