package cc.ethon.mdma.backend.gen.c;

import java.io.File;
import java.io.PrintStream;

import cc.ethon.mdma.backend.gen.AstGenerator;
import cc.ethon.mdma.frontend.ast.Node;

public class CHeaderGenerator implements AstGenerator {

	@Override
	public void generate(Node node, PrintStream out) throws Exception {
		node.accept(new CHeaderGeneratingVisitor(out));
	}

	@Override
	public void generate(Node node, File file) throws Exception {
		node.accept(new CHeaderGeneratingVisitor(new PrintStream(file)));
	}

}
