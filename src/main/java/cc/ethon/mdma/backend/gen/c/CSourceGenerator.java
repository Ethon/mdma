package cc.ethon.mdma.backend.gen.c;

import java.io.File;
import java.io.PrintStream;

import cc.ethon.mdma.backend.gen.AstGenerator;
import cc.ethon.mdma.core.analysis.Module;
import cc.ethon.mdma.frontend.ast.Node;

public class CSourceGenerator implements AstGenerator {

	private final Module module;

	public CSourceGenerator(Module module) {
		super();
		this.module = module;
	}

	@Override
	public void generate(Node node, PrintStream out) throws Exception {
		node.accept(new CSourceGeneratingVisitor(module, out));
	}

	@Override
	public void generate(Node node, File file) throws Exception {
		node.accept(new CSourceGeneratingVisitor(module, new PrintStream(file)));
	}

}
