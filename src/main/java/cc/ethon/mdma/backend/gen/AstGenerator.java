package cc.ethon.mdma.backend.gen;

import java.io.File;
import java.io.PrintStream;

import cc.ethon.mdma.frontend.ast.Node;

public interface AstGenerator {

	public void generate(Node node, PrintStream out) throws Exception;

	public void generate(Node node, File file) throws Exception;

}
