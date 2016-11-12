package cc.ethon.mdma.frontend;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.RuleNode;

import cc.ethon.mdma.MdmaBaseVisitor;
import cc.ethon.mdma.MdmaParser.AssignExpressionContext;
import cc.ethon.mdma.MdmaParser.BoolExpressionContext;
import cc.ethon.mdma.MdmaParser.ElifStatementContext;
import cc.ethon.mdma.MdmaParser.EqualExpressionContext;
import cc.ethon.mdma.MdmaParser.ExpressionStatementContext;
import cc.ethon.mdma.MdmaParser.ForRangeLoopContext;
import cc.ethon.mdma.MdmaParser.FunctionDefinitionContext;
import cc.ethon.mdma.MdmaParser.FunctionHeaderContext;
import cc.ethon.mdma.MdmaParser.IdentifierExpressionContext;
import cc.ethon.mdma.MdmaParser.IfStatementContext;
import cc.ethon.mdma.MdmaParser.IndexExpressionContext;
import cc.ethon.mdma.MdmaParser.IntExpressionContext;
import cc.ethon.mdma.MdmaParser.ListExpressionContext;
import cc.ethon.mdma.MdmaParser.ListTypeContext;
import cc.ethon.mdma.MdmaParser.ModuleContext;
import cc.ethon.mdma.MdmaParser.ModuloExpressionContext;
import cc.ethon.mdma.MdmaParser.MultiplyExpressionContext;
import cc.ethon.mdma.MdmaParser.NamedTypeContext;
import cc.ethon.mdma.MdmaParser.NegateExpressionContext;
import cc.ethon.mdma.MdmaParser.RangeExpressionContext;
import cc.ethon.mdma.MdmaParser.VariableDeclarationStatementContext;
import cc.ethon.mdma.MdmaParser.VariableTypeNamePairContext;
import cc.ethon.mdma.frontend.ast.AssignmentExpressionNode;
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
import cc.ethon.mdma.frontend.ast.StatementNode;
import cc.ethon.mdma.frontend.ast.TypeNode;
import cc.ethon.mdma.frontend.ast.UnaryExpressionNode;
import cc.ethon.mdma.frontend.ast.VariableDeclarationNode;

class AstGeneratingVisitor extends MdmaBaseVisitor<Node> {

	private final List<List<Node>> nodeStack;
	private final Stack<Node> parents;

	private void createNodeStack() {
		nodeStack.add(new ArrayList<Node>());
	}

	private List<Node> popNodeStack() {
		return nodeStack.remove(nodeStack.size() - 1);
	}

	private void pushNode(Node node) {
		nodeStack.get(nodeStack.size() - 1).add(node);
	}

	private Node popNode() {
		final List<Node> peek = nodeStack.get(nodeStack.size() - 1);
		return peek.remove(peek.size() - 1);
	}

	private void pushParent(Node parent) {
		parents.push(parent);
	}

	private void popParent() {
		parents.pop();
	}

	private Node getParent() {
		return parents.peek();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <T extends Node> List<T> visitChildrenNodesWithNewStack(RuleNode ctx, Class<T> type) {
		createNodeStack();
		visitChildren(ctx);
		return (List) popNodeStack();
	}

	private <T extends Node> List<T> visitChildrenNodesWithNewStackAndParent(RuleNode ctx, Node parent, Class<T> type) {
		pushParent(parent);
		try {
			return visitChildrenNodesWithNewStack(ctx, type);
		} finally {
			popParent();
		}
	}

	private <T extends Node> T visitChildNodeWithNewStackAndParent(RuleNode ctx, Node parent, Class<T> type) {
		return visitChildrenNodesWithNewStackAndParent(ctx, parent, type).get(0);
	}

	@SuppressWarnings("unchecked")
	private <T extends Node> T visitNode(RuleNode ctx, Node parent, Class<T> type) {
		pushParent(parent);
		try {
			ctx.accept(this);
			return (T) popNode();
		} finally {
			popParent();
		}
	}

	private <T> T createNode(Class<T> clazz, Token start) {
		final int line = start.getLine(), column = start.getCharPositionInLine();
		try {
			return clazz.getConstructor(int.class, int.class, Node.class).newInstance(line, column, getParent());
		} catch (final Exception e) {
			throw new IllegalArgumentException("Bad node type: " + clazz.getName());
		}
	}

	private <T extends UnaryExpressionNode> T createUnaryExpressionWithChildrenAndPush(Class<T> clazz, RuleNode ctx, Token start) {
		final UnaryExpressionNode node = createNode(clazz, start);
		node.setChild(visitChildNodeWithNewStackAndParent(ctx, node, ExpressionNode.class));
		pushNode(node);
		return clazz.cast(node);
	}

	private <T extends BinaryExpressionNode> T createBinaryExpressionNodeWithChildrenAndPush(Class<T> clazz, RuleNode ctx, Token start) {
		final BinaryExpressionNode node = createNode(clazz, start);
		final List<ExpressionNode> children = visitChildrenNodesWithNewStackAndParent(ctx, node, ExpressionNode.class);
		node.setLeft(children.get(0));
		node.setRight(children.get(1));
		pushNode(node);
		return clazz.cast(node);
	}

	public AstGeneratingVisitor() {
		nodeStack = new ArrayList<List<Node>>();
		parents = new Stack<Node>();
	}

	@Override
	public Node visitModule(ModuleContext ctx) {
		final ModuleNode module = new ModuleNode();
		module.setChildren(visitChildrenNodesWithNewStackAndParent(ctx, module, StatementNode.class));
		return module;
	}

	@Override
	public Node visitFunctionDefinition(FunctionDefinitionContext ctx) {
		final int line = ctx.start.getLine(), column = ctx.start.getCharPositionInLine();
		final FunctionNode function = new FunctionNode(line, column, getParent());

		final FunctionHeaderContext header = ctx.functionHeader();
		function.setName(header.Ident().getText());
		function.setArguments(visitChildrenNodesWithNewStackAndParent(header.functionArgumentList(), function, VariableDeclarationNode.class));

		TypeNode returnType;
		if (header.returnSpec() != null) {
			returnType = visitChildNodeWithNewStackAndParent(header.returnSpec().type(), function, TypeNode.class);
		} else {
			returnType = new NamedTypeNode(line, column, function);
			((NamedTypeNode) returnType).setName("void");
		}
		function.setReturnType(returnType);

		function.setChildren(visitChildrenNodesWithNewStackAndParent(ctx.statementBlock(), function, StatementNode.class));
		pushNode(function);
		return function;
	}

	@Override
	public Node visitIfStatement(IfStatementContext ctx) {
		final IfStatementNode node = createNode(IfStatementNode.class, ctx.start);
		node.setCondition(visitNode(ctx.expression(), node, ExpressionNode.class));
		node.setBody(visitChildrenNodesWithNewStackAndParent(ctx.statementBlock(), node, StatementNode.class));

		if (ctx.elifStatement() != null && !ctx.elifStatement().isEmpty()) {
			final List<ConditionBodyPair> elifs = new ArrayList<ConditionBodyPair>();
			for (final ElifStatementContext elifStatementContext : ctx.elifStatement()) {
				final ExpressionNode condition = visitNode(elifStatementContext.expression(), node, ExpressionNode.class);
				final List<StatementNode> body = visitChildrenNodesWithNewStackAndParent(elifStatementContext.statementBlock(), node, StatementNode.class);
				elifs.add(new ConditionBodyPair(condition, body));
			}
			node.setElifs(elifs);
		}

		if (ctx.elseStatement() != null && !ctx.elseStatement().isEmpty()) {
			node.setElseBody(visitChildrenNodesWithNewStackAndParent(ctx.elseStatement().statementBlock(), node, StatementNode.class));
		}

		pushNode(node);
		return node;
	}

	@Override
	public Node visitForRangeLoop(ForRangeLoopContext ctx) {
		final ForRangeLoopStatementNode node = createNode(ForRangeLoopStatementNode.class, ctx.start);
		node.setVariable(visitNode(ctx.variableTypeNamePair(), node, VariableDeclarationNode.class));
		node.setRange(visitChildNodeWithNewStackAndParent(ctx.expression(), node, ExpressionNode.class));
		node.setChildren(visitChildrenNodesWithNewStackAndParent(ctx.statementBlock(), node, StatementNode.class));
		pushNode(node);
		return node;
	}

	@Override
	public Node visitVariableTypeNamePair(VariableTypeNamePairContext ctx) {
		final int line = ctx.start.getLine(), column = ctx.start.getCharPositionInLine();
		final VariableDeclarationNode node = new VariableDeclarationNode(line, column, getParent());
		node.setName(ctx.Ident().getText());
		node.setType(visitChildNodeWithNewStackAndParent(ctx.type(), node, TypeNode.class));
		pushNode(node);
		return node;
	}

	@Override
	public Node visitVariableDeclarationStatement(VariableDeclarationStatementContext ctx) {
		final VariableDeclarationNode node = createNode(VariableDeclarationNode.class, ctx.start);
		node.setName(ctx.variableTypeNamePair().Ident().getText());
		node.setType(visitChildNodeWithNewStackAndParent(ctx.variableTypeNamePair().type(), node, TypeNode.class));
		if (ctx.expression() != null && !ctx.expression().isEmpty()) {
			node.setAssignedValue(visitChildNodeWithNewStackAndParent(ctx.expression(), node, ExpressionNode.class));
		}
		pushNode(node);
		return node;
	}

	@Override
	public Node visitExpressionStatement(ExpressionStatementContext ctx) {
		final ExpressionStatementNode node = createNode(ExpressionStatementNode.class, ctx.start);
		node.setExpression(visitNode(ctx.expression(), node, ExpressionNode.class));
		pushNode(node);
		return node;
	}

	@Override
	public Node visitNamedType(NamedTypeContext ctx) {
		final int line = ctx.start.getLine(), column = ctx.start.getCharPositionInLine();
		final NamedTypeNode node = new NamedTypeNode(line, column, getParent());
		node.setName(ctx.Ident().getText());
		pushNode(node);
		return node;
	}

	@Override
	public Node visitListType(ListTypeContext ctx) {
		final int line = ctx.start.getLine(), column = ctx.start.getCharPositionInLine();
		final ListTypeNode node = new ListTypeNode(line, column, getParent());
		node.setSubType(visitChildNodeWithNewStackAndParent(ctx, node, TypeNode.class));
		pushNode(node);
		return node;
	}

	@Override
	public Node visitMultiplyExpression(MultiplyExpressionContext ctx) {
		return createBinaryExpressionNodeWithChildrenAndPush(MultiplyExpressionNode.class, ctx, ctx.start);
	}

	@Override
	public Node visitModuloExpression(ModuloExpressionContext ctx) {
		return createBinaryExpressionNodeWithChildrenAndPush(ModuloExpressionNode.class, ctx, ctx.start);
	}

	@Override
	public Node visitRangeExpression(RangeExpressionContext ctx) {
		return createBinaryExpressionNodeWithChildrenAndPush(RangeExpressionNode.class, ctx, ctx.start);
	}

	@Override
	public Node visitEqualExpression(EqualExpressionContext ctx) {
		return createBinaryExpressionNodeWithChildrenAndPush(EqualExpressionNode.class, ctx, ctx.start);
	}

	@Override
	public Node visitIndexExpression(IndexExpressionContext ctx) {
		return createBinaryExpressionNodeWithChildrenAndPush(IndexExpressionNode.class, ctx, ctx.start);
	}

	@Override
	public Node visitAssignExpression(AssignExpressionContext ctx) {
		return createBinaryExpressionNodeWithChildrenAndPush(AssignmentExpressionNode.class, ctx, ctx.start);
	}

	@Override
	public Node visitNegateExpression(NegateExpressionContext ctx) {
		return createUnaryExpressionWithChildrenAndPush(NegateExpressionNode.class, ctx, ctx.start);
	}

	@Override
	public Node visitListExpression(ListExpressionContext ctx) {
		final int line = ctx.start.getLine(), column = ctx.start.getCharPositionInLine();
		final ListExpressionNode node = new ListExpressionNode(line, column, getParent());
		node.setValues(visitChildrenNodesWithNewStackAndParent(ctx, node, ExpressionNode.class));
		pushNode(node);
		return node;
	}

	@Override
	public Node visitIntExpression(IntExpressionContext ctx) {
		final int line = ctx.start.getLine(), column = ctx.start.getCharPositionInLine();
		final IntegerExpressionNode node = new IntegerExpressionNode(line, column, getParent());
		if (ctx.decIntExpression() != null) {
			node.setValue(BigInteger.valueOf(Long.valueOf(ctx.decIntExpression().DecInt().getText())));
		} else {
			throw new IllegalStateException("Unhandled int expression " + ctx.getText());
		}
		pushNode(node);
		return node;
	}

	@Override
	public Node visitBoolExpression(BoolExpressionContext ctx) {
		final int line = ctx.start.getLine(), column = ctx.start.getCharPositionInLine();
		final BoolExpressionNode node = new BoolExpressionNode(line, column, getParent());
		node.setValue(ctx.Bool().getText().equals("true"));
		pushNode(node);
		return node;
	}

	@Override
	public Node visitIdentifierExpression(IdentifierExpressionContext ctx) {
		final IdentifierExpressionNode node = createNode(IdentifierExpressionNode.class, ctx.start);
		node.setName(ctx.Ident().getText());
		pushNode(node);
		return node;
	}
}
