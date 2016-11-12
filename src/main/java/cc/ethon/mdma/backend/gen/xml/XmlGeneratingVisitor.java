package cc.ethon.mdma.backend.gen.xml;

import java.util.List;
import java.util.Optional;
import java.util.Stack;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cc.ethon.mdma.frontend.ast.AssignmentExpressionNode;
import cc.ethon.mdma.frontend.ast.AstVisitor;
import cc.ethon.mdma.frontend.ast.BinaryExpressionNode;
import cc.ethon.mdma.frontend.ast.BoolExpressionNode;
import cc.ethon.mdma.frontend.ast.EqualExpressionNode;
import cc.ethon.mdma.frontend.ast.ExpressionNode;
import cc.ethon.mdma.frontend.ast.ExpressionStatementNode;
import cc.ethon.mdma.frontend.ast.ForRangeLoopStatementNode;
import cc.ethon.mdma.frontend.ast.FunctionNode;
import cc.ethon.mdma.frontend.ast.IdentifierExpressionNode;
import cc.ethon.mdma.frontend.ast.IfStatementNode;
import cc.ethon.mdma.frontend.ast.IfStatementNode.ConditionBodyPair;
import cc.ethon.mdma.frontend.ast.IndexExpressionNode;
import cc.ethon.mdma.frontend.ast.IntegerExpressionNode;
import cc.ethon.mdma.frontend.ast.ListExpressionNode;
import cc.ethon.mdma.frontend.ast.ListTypeNode;
import cc.ethon.mdma.frontend.ast.ModuleNode;
import cc.ethon.mdma.frontend.ast.ModuloExpressionNode;
import cc.ethon.mdma.frontend.ast.MultiplyExpressionNode;
import cc.ethon.mdma.frontend.ast.NamedTypeNode;
import cc.ethon.mdma.frontend.ast.NegateExpressionNode;
import cc.ethon.mdma.frontend.ast.Node;
import cc.ethon.mdma.frontend.ast.RangeExpressionNode;
import cc.ethon.mdma.frontend.ast.StatementBlockNode;
import cc.ethon.mdma.frontend.ast.UnaryExpressionNode;
import cc.ethon.mdma.frontend.ast.VariableDeclarationNode;

class XmlGeneratingVisitor implements AstVisitor {

	private final Document doc;
	private final Stack<Element> elementStack;
	private final boolean generateTypeAttributes;

	private void addToCurrentParent(Element element) {
		elementStack.peek().appendChild(element);
	}

	private Element createElement(Node node, Optional<String> value) {
		final Element element = doc.createElement(node.getClass().getSimpleName());
		element.setAttribute("column", String.valueOf(node.getColumn()));
		element.setAttribute("line", String.valueOf(node.getLine()));
		if (value.isPresent()) {
			element.appendChild(doc.createTextNode(value.get()));
		}
		return element;
	}

	private Element createElement(Node node) {
		return createElement(node, Optional.empty());
	}

	private Element createElement(Node node, Object value) {
		return createElement(node, Optional.of(value.toString()));
	}

	private void setAsParentElement(Element element) {
		if (!elementStack.empty()) {
			elementStack.peek().appendChild(element);
		} else {
			doc.appendChild(element);
		}
		elementStack.push(element);
	}

	private Element createParentElement(Node node) {
		final Element element = createElement(node);
		setAsParentElement(element);
		return element;
	}

	private void doVisitWithAddingNewParent(Node node, Element parent) {
		elementStack.peek().appendChild(parent);
		elementStack.push(parent);
		try {
			node.accept(this);
		} finally {
			elementStack.pop();
		}
	}

	private void doVisitWithAddingNewParent(List<? extends Node> nodes, Element parent) {
		elementStack.peek().appendChild(parent);
		elementStack.push(parent);
		try {
			doVisitWithoutAddingNewParent(nodes);
		} finally {
			elementStack.pop();
		}
	}

	private void doVisitWithoutAddingNewParent(List<? extends Node> nodes) {
		for (final Node node : nodes) {
			node.accept(this);
		}
	}

	private void doVisitWithoutAddingNewParent(Node node) {
		node.accept(this);
	}

	private void doVisitPrimaryExpressionNode(ExpressionNode node, String stringValue) {
		final Element element = createElement(node, stringValue);
		generateType(node, element);
		addToCurrentParent(element);
	}

	private void doVisitUnaryExpressionNode(UnaryExpressionNode node) {
		createParentElement(node);
		generateType(node);
		try {
			doVisitWithoutAddingNewParent(node.getChild());
		} finally {
			elementStack.pop();
		}
	}

	private void doVisitBinaryExpressionNode(BinaryExpressionNode node) {
		createParentElement(node);
		generateType(node);
		try {
			doVisitWithoutAddingNewParent(node.getLeft());
			doVisitWithoutAddingNewParent(node.getRight());
		} finally {
			elementStack.pop();
		}
	}

	private void generateType(ExpressionNode node) {
		generateType(node, elementStack.peek());
	}

	private void generateType(ExpressionNode node, Element appendTo) {
		if (generateTypeAttributes) {
			appendTo.setAttribute("type", node.getType().toString());
		}
	}

	public XmlGeneratingVisitor(Document doc) {
		this(doc, false);
	}

	public XmlGeneratingVisitor(Document doc, boolean generateTypeAttributes) {
		this.doc = doc;
		elementStack = new Stack<Element>();
		this.generateTypeAttributes = generateTypeAttributes;
	}

	@Override
	public void visit(FunctionNode functionNode) {
		final Element function = createParentElement(functionNode);

		try {
			function.setAttribute("name", functionNode.getName());
			doVisitWithAddingNewParent(functionNode.getReturnType(), doc.createElement("ReturnType"));
			doVisitWithAddingNewParent(functionNode.getArguments(), doc.createElement("Arguments"));
			doVisitWithAddingNewParent(functionNode.getChildren(), doc.createElement("Body"));
		} finally {
			elementStack.pop();
		}
	}

	@Override
	public void visit(IntegerExpressionNode integerNode) {
		doVisitPrimaryExpressionNode(integerNode, integerNode.getValue().toString());
	}

	@Override
	public void visit(ListTypeNode listTypeNode) {
		createParentElement(listTypeNode);
		try {
			doVisitWithAddingNewParent(listTypeNode.getSubType(), doc.createElement("SubType"));
		} finally {
			elementStack.pop();
		}
	}

	@Override
	public void visit(NamedTypeNode namedTypeNode) {
		addToCurrentParent(createElement(namedTypeNode, namedTypeNode.getName()));
	}

	@Override
	public void visit(ModuleNode moduleNode) {
		createParentElement(moduleNode);
		doVisitWithAddingNewParent(moduleNode.getChildren(), doc.createElement("TopLevelStatements"));
	}

	@Override
	public void visit(StatementBlockNode statementBlockNode) {
		createParentElement(statementBlockNode);
		try {
			doVisitWithoutAddingNewParent(statementBlockNode.getChildren());
		} finally {
			elementStack.pop();
		}
	}

	@Override
	public void visit(VariableDeclarationNode variableDeclarationNode) {
		final Element variableDeclaration = createParentElement(variableDeclarationNode);
		try {
			variableDeclaration.setAttribute("name", variableDeclarationNode.getName());
			doVisitWithAddingNewParent(variableDeclarationNode.getType(), doc.createElement("Type"));
			if (variableDeclarationNode.getAssignedValue() != null) {
				doVisitWithAddingNewParent(variableDeclarationNode.getAssignedValue(), doc.createElement("AssignedValue"));
			}
		} finally {
			elementStack.pop();
		}

	}

	@Override
	public void visit(BoolExpressionNode boolExpressionNode) {
		doVisitPrimaryExpressionNode(boolExpressionNode, Boolean.toString(boolExpressionNode.getValue()));
	}

	@Override
	public void visit(ListExpressionNode listExpressionNode) {
		createParentElement(listExpressionNode);
		generateType(listExpressionNode);
		try {
			doVisitWithoutAddingNewParent(listExpressionNode.getValues());
		} finally {
			elementStack.pop();
		}
	}

	@Override
	public void visit(MultiplyExpressionNode multiplyExpressionNode) {
		doVisitBinaryExpressionNode(multiplyExpressionNode);
	}

	@Override
	public void visit(ForRangeLoopStatementNode forRangeLoop) {
		createParentElement(forRangeLoop);
		try {
			doVisitWithAddingNewParent(forRangeLoop.getVariable(), doc.createElement("Variable"));
			doVisitWithAddingNewParent(forRangeLoop.getRange(), doc.createElement("Range"));
			doVisitWithAddingNewParent(forRangeLoop.getChildren(), doc.createElement("Body"));
		} finally {
			elementStack.pop();
		}
	}

	@Override
	public void visit(RangeExpressionNode rangeExpressionNode) {
		doVisitBinaryExpressionNode(rangeExpressionNode);
	}

	@Override
	public void visit(IdentifierExpressionNode identifierExpressionNode) {
		doVisitPrimaryExpressionNode(identifierExpressionNode, identifierExpressionNode.getName());
	}

	@Override
	public void visit(ModuloExpressionNode moduloExpressionNode) {
		doVisitBinaryExpressionNode(moduloExpressionNode);
	}

	@Override
	public void visit(EqualExpressionNode equalExpressionNode) {
		doVisitBinaryExpressionNode(equalExpressionNode);
	}

	@Override
	public void visit(IfStatementNode ifStatementNode) {
		createParentElement(ifStatementNode);
		try {
			doVisitWithAddingNewParent(ifStatementNode.getCondition(), doc.createElement("Condition"));
			doVisitWithAddingNewParent(ifStatementNode.getBody(), doc.createElement("Body"));

			if (ifStatementNode.getElifs() != null) {
				setAsParentElement(doc.createElement("Elifs"));
				try {
					for (final ConditionBodyPair conditionBodyPair : ifStatementNode.getElifs()) {
						setAsParentElement(doc.createElement("Elif"));
						try {
							doVisitWithAddingNewParent(conditionBodyPair.getCondition(), doc.createElement("Condition"));
							doVisitWithAddingNewParent(conditionBodyPair.getBody(), doc.createElement("Body"));
						} finally {
							elementStack.pop();
						}
					}
				} finally {
					elementStack.pop();
				}
			}

			if (ifStatementNode.getElseBody() != null) {
				doVisitWithAddingNewParent(ifStatementNode.getElseBody(), doc.createElement("Else"));
			}
		} finally {
			elementStack.pop();
		}
	}

	@Override
	public void visit(NegateExpressionNode negateExpressionNode) {
		doVisitUnaryExpressionNode(negateExpressionNode);
	}

	@Override
	public void visit(AssignmentExpressionNode assignmentExpressionNode) {
		doVisitBinaryExpressionNode(assignmentExpressionNode);
	}

	@Override
	public void visit(ExpressionStatementNode expressionStatementNode) {
		createParentElement(expressionStatementNode);
		try {
			doVisitWithoutAddingNewParent(expressionStatementNode.getExpression());
		} finally {
			elementStack.pop();
		}
	}

	@Override
	public void visit(IndexExpressionNode indexExpressionNode) {
		doVisitBinaryExpressionNode(indexExpressionNode);
	}
}
