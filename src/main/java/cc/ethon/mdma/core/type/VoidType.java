package cc.ethon.mdma.core.type;

public class VoidType extends Type {

	public static final VoidType VOID = new VoidType();

	private VoidType() {
	}

	@Override
	public String toString() {
		return "void";
	}

	// Type check

	@Override
	public boolean isBuiltIn() {
		return true;
	}

	@Override
	public boolean isVoid() {
		return true;
	}

}
