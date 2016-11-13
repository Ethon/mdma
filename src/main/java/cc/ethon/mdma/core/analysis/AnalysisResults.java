package cc.ethon.mdma.core.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cc.ethon.mdma.common.CompilerMessage;

public class AnalysisResults {

	private final Module module;
	private List<CompilerMessage> infos;
	private List<CompilerMessage> warnings;
	private List<CompilerMessage> errors;

	void finishAnalysis() {
		infos = Collections.unmodifiableList(infos);
		warnings = Collections.unmodifiableList(warnings);
		errors = Collections.unmodifiableList(errors);
	}

	public AnalysisResults(Module module) {
		this.module = module;
		this.infos = new ArrayList<CompilerMessage>();
		this.warnings = new ArrayList<CompilerMessage>();
		this.errors = new ArrayList<CompilerMessage>();
	}

	public Module getModule() {
		return module;
	}

	public List<CompilerMessage> getInfos() {
		return infos;
	}

	public List<CompilerMessage> getWarnings() {
		return warnings;
	}

	public List<CompilerMessage> getErrors() {
		return errors;
	}

	public boolean success() {
		return getErrors().size() == 0;
	}

}
