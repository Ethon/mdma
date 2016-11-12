package cc.ethon.mdma.test.sampletest;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.Assert;
import org.junit.Test;

import cc.ethon.mdma.backend.gen.xml.XmlGenerator;
import cc.ethon.mdma.common.CompilerMessage;
import cc.ethon.mdma.core.analysis.AnalysisResults;
import cc.ethon.mdma.core.analysis.Analyzer;
import cc.ethon.mdma.frontend.ParseResults;
import cc.ethon.mdma.frontend.Parser;
import cc.ethon.mdma.frontend.ast.Node;
import cc.ethon.mdma.test.sampletest.util.ParentCheckingVisitor;

public abstract class AbstractSampleTest {

	private final Map<String, String> resourceCache = new HashMap<String, String>();

	private String getResourceFileContent(String name) throws IOException {
		final String fromCache = resourceCache.get(name);
		if (fromCache != null) {
			return fromCache;
		}

		final BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(name)));
		final StringBuilder result = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			result.append(line);
			result.append(System.getProperty("line.separator"));
		}
		final String resultString = result.toString();
		resourceCache.put(name, resultString);
		return resultString;
	}

	private ParseResults parse() {
		try {
			final String string = getResourceFileContent(getSourceFileName());
			final String moduleName = Parser.makeModuleName(getSourceFileName());
			final ParseResults results = new Parser().parseModuleString(string, moduleName);
			if (!results.success()) {
				System.err.println("*** Lexer Errors ***");
				for (final CompilerMessage parseError : results.getLexerErrors()) {
					System.err.println(parseError);
				}
				System.err.println("*** Parser Errors ***");
				for (final CompilerMessage parseError : results.getParserErrors()) {
					System.err.println(parseError);
				}
				Assert.fail("Parsing failed with errors");
			}
			return results;
		} catch (final Exception e) {
			e.printStackTrace();
			Assert.fail("Parsing failed with exception");
		}
		return null;
	}

	private AnalysisResults analyze(ParseResults parseResults) {
		final AnalysisResults results = new Analyzer().analyze(parseResults.getModuleName(), parseResults.getNode(), null);
		if (!results.success()) {
			System.err.println("*** Analysis Errors ***");
			for (final CompilerMessage error : results.getErrors()) {
				System.err.println(error);
			}
			Assert.fail("Analysis failed with errors");
		}
		if (!results.getWarnings().isEmpty()) {
			System.err.println("*** Warnings ***");
			for (final CompilerMessage warning : results.getWarnings()) {
				System.err.println(warning);
			}
		}
		if (!results.getInfos().isEmpty()) {
			System.err.println("*** Infos ***");
			for (final CompilerMessage info : results.getInfos()) {
				System.err.println(info);
			}
		}
		return results;
	}

	private String createXmlString(XmlGenerator gen, Node node) throws TransformerException, UnsupportedEncodingException {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		gen.generate(node, new PrintStream(out));
		return out.toString("UTF-8");
	}

	protected abstract String getSourceFileName();

	protected String getXmlBeforeAnalysisFileName() {
		return getSourceFileName() + ".beforeAnalysis.xml";
	}

	protected String getXmlAfterAnalysisFileName() {
		return getSourceFileName() + ".afterAnalysis.xml";
	}

	// @Test
	public void createXmlFiles() throws TransformerException, ParserConfigurationException, URISyntaxException {
		final String fileName = new File(getSourceFileName()).getName();

		final ParseResults results = parse();
		final XmlGenerator gen = new XmlGenerator();
		final File beforeAnalysis = new File(fileName + ".beforeAnalysis.xml");
		gen.generate(results.getNode(), beforeAnalysis);

		analyze(results);
		final File afterAnalysis = new File(fileName + ".afterAnalysis.xml");
		gen.setGenerateTypes(true);
		gen.generate(results.getNode(), afterAnalysis);
	}

	@Test
	public void testAstNodeParents() {
		final ParseResults results = parse();
		results.getNode().accept(new ParentCheckingVisitor());
	}

	@Test
	public void testAstBeforeAnalysis() throws Exception {
		final ParseResults results = parse();
		final XmlGenerator gen = new XmlGenerator();
		Assert.assertEquals(getResourceFileContent(getXmlBeforeAnalysisFileName()), createXmlString(gen, results.getNode()));
	}

	@Test
	public void testAstAfterAnalysis() throws Exception {
		final ParseResults results = parse();
		analyze(results);
		final XmlGenerator gen = new XmlGenerator();
		gen.setGenerateTypes(true);
		Assert.assertEquals(getResourceFileContent(getXmlAfterAnalysisFileName()), createXmlString(gen, results.getNode()));
	}
}
