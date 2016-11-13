package cc.ethon.mdma.core.analysis;

import cc.ethon.mdma.core.symbol.SymbolTable;
import cc.ethon.mdma.frontend.ast.ModuleNode;

public class Analyzer {

	public AnalysisResults analyzeModule(String moduleName, ModuleNode node, SymbolTable parentSymbolTable) {
		final Module module = new Module(moduleName, node);
		final AnalysisResults results = new AnalysisResults(module);
		final AnalyzingVisitor analyzingVisitor = new AnalyzingVisitor(results, parentSymbolTable);
		node.accept(analyzingVisitor);
		results.finishAnalysis();
		return results;
	}

}
