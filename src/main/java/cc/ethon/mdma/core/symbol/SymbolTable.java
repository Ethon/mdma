package cc.ethon.mdma.core.symbol;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class SymbolTable {

	private final SymbolTable parent;
	private final Map<String, Symbol> symbols;

	public SymbolTable(SymbolTable parent) {
		this.parent = parent;
		this.symbols = new TreeMap<String, Symbol>();
	}

	public Map<String, Symbol> getSymbols() {
		return Collections.unmodifiableMap(symbols);
	}

	public Symbol lookupSymbol(String name) {
		return lookupSymbol(name, true);
	}

	public Symbol lookupSymbol(String name, boolean recursive) {
		final Symbol symbol = symbols.get(name);
		if (symbol == null && recursive && parent != null) {
			return parent.lookupSymbol(name, recursive);
		}
		return symbol;
	}

	public boolean defineSymbol(String name, Symbol symbol) {
		if (symbols.containsKey(name)) {
			return false;
		}
		symbols.put(name, symbol);
		return true;
	}

	@Override
	public String toString() {
		return "SymbolTable [symbols=" + symbols + "]";
	}

}
