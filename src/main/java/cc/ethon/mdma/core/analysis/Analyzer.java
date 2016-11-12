package cc.ethon.mdma.core.analysis;

import cc.ethon.mdma.core.symbol.SymbolTable;
import cc.ethon.mdma.frontend.ast.Node;

public class Analyzer {

	public AnalysisResults analyze(String moduleName, Node node, SymbolTable parentSymbolTable) {
		final AnalysisResults results = new AnalysisResults();
		final AnalyzingVisitor analyzingVisitor = new AnalyzingVisitor(moduleName, results, parentSymbolTable);
		node.accept(analyzingVisitor);
		return results;
	}

}
