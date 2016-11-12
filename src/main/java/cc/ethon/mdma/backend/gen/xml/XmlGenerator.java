package cc.ethon.mdma.backend.gen.xml;

import java.io.File;
import java.io.PrintStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import cc.ethon.mdma.backend.gen.AstGenerator;
import cc.ethon.mdma.frontend.ast.Node;

public class XmlGenerator implements AstGenerator {

	private final DocumentBuilderFactory docFactory;
	private final DocumentBuilder docBuilder;
	private Document doc;
	private final int indent;
	private boolean generateTypes;

	private void doGenerate(Node node, StreamResult result) throws TransformerException {
		doc = docBuilder.newDocument();
		node.accept(new XmlGeneratingVisitor(doc, generateTypes));
		final TransformerFactory transformerFactory = TransformerFactory.newInstance();
		final Transformer transformer = transformerFactory.newTransformer();
		if (indent > 0) {
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", String.valueOf(indent));
		}
		final DOMSource source = new DOMSource(doc);
		transformer.transform(source, result);
	}

	public XmlGenerator() throws ParserConfigurationException {
		this(4);
	}

	public XmlGenerator(int indent) throws ParserConfigurationException {
		docFactory = DocumentBuilderFactory.newInstance();
		docBuilder = docFactory.newDocumentBuilder();
		this.indent = indent;
		generateTypes = false;
	}

	public void setGenerateTypes(boolean generateTypes) {
		this.generateTypes = generateTypes;
	}

	@Override
	public void generate(Node node, PrintStream out) throws TransformerException {
		doGenerate(node, new StreamResult(out));
	}

	@Override
	public void generate(Node node, File file) throws TransformerException {
		doGenerate(node, new StreamResult(file));
	}

}
