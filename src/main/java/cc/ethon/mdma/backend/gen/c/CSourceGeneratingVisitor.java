package cc.ethon.mdma.backend.gen.c;

import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

import cc.ethon.mdma.core.analysis.Module;
import cc.ethon.mdma.core.symbol.FunctionSymbol;
import cc.ethon.mdma.core.symbol.SymbolTable;
import cc.ethon.mdma.core.symbol.VariableSymbol;
import cc.ethon.mdma.core.type.ListType;
import cc.ethon.mdma.core.type.Type;
import cc.ethon.mdma.frontend.ast.AssignmentExpressionNode;
import cc.ethon.mdma.frontend.ast.AstVisitor;
import cc.ethon.mdma.frontend.ast.BoolExpressionNode;
import cc.ethon.mdma.frontend.ast.EqualExpressionNode;
import cc.ethon.mdma.frontend.ast.ExpressionNode;
import cc.ethon.mdma.frontend.ast.ExpressionStatementNode;
import cc.ethon.mdma.frontend.ast.ForRangeLoopStatementNode;
import cc.ethon.mdma.frontend.ast.FunctionNode;
import cc.ethon.mdma.frontend.ast.IdentifierExpressionNode;
import cc.ethon.mdma.frontend.ast.IfStatementNode;
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
import cc.ethon.mdma.frontend.ast.ReturnStatementNode;
import cc.ethon.mdma.frontend.ast.StatementBlockNode;
import cc.ethon.mdma.frontend.ast.VariableDeclarationNode;

public class CSourceGeneratingVisitor implements AstVisitor {

	private final Module module;
	private final CTypeMapper typeMapper;
	private final CCodeEmitter emitter;
	private final VariableAllocator variableAllocator;
	private final CExpressionMapper expressionMapper;

	private final Deque<String> expressionStack;

	private void doVisit(List<? extends Node> nodes) {
		for (final Node node : nodes) {
			node.accept(this);
		}
	}

	private String visitExpressionNode(ExpressionNode node) {
		node.accept(this);
		return expressionStack.removeLast();
	}

	private List<String> visitExpressionNodes(List<? extends ExpressionNode> nodes) {
		return nodes.stream().map(node -> visitExpressionNode(node)).collect(Collectors.toList());
	}

	public CSourceGeneratingVisitor(Module module, PrintStream out) {
		this.module = module;
		typeMapper = new CTypeMapper();
		emitter = new CCodeEmitter(out, typeMapper);
		variableAllocator = new VariableAllocator();
		expressionMapper = new CExpressionMapper(typeMapper, emitter, variableAllocator);
		expressionStack = new ArrayDeque<String>();
	}

	@Override
	public void visit(FunctionNode functionNode) {
		final SymbolTable symbolTable = functionNode.getSymbolTable();
		final FunctionSymbol symbol = (FunctionSymbol) symbolTable.lookupSymbol(functionNode.getName(), false);
		emitter.startFunctionDefinition(symbol);
		doVisit(functionNode.getChildren());
		emitter.endFunctionDefinition();
	}

	@Override
	public void visit(IntegerExpressionNode integerNode) {
		emitter.emitLineDirective(integerNode.getLine(), null);
		expressionStack.addLast(expressionMapper.mapIntegerExpression(integerNode.getType(), integerNode.getValue()));
		emitter.emitEmptyLine();
	}

	@Override
	public void visit(ListTypeNode listTypeNode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(NamedTypeNode namedTypeNode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ModuleNode moduleNode) {
		emitter.emitUserInclude(module.getName().replace('.', '/') + ".h");
		emitter.emitEmptyLine();
		doVisit(moduleNode.getChildren());
	}

	@Override
	public void visit(StatementBlockNode statementBlockNode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(VariableDeclarationNode variableDeclarationNode) {
		String value = null;
		if (variableDeclarationNode.getAssignedValue() != null) {
			value = visitExpressionNode(variableDeclarationNode.getAssignedValue());
		}
		final VariableSymbol symbol = (VariableSymbol) variableDeclarationNode.getSymbolTable().lookupSymbol(variableDeclarationNode.getName());

		emitter.emitLineDirective(variableDeclarationNode.getLine(), null);
		emitter.emitVariableDeclaration(symbol.getType(), symbol.getName(), value);
		emitter.emitEmptyLine();
	}

	@Override
	public void visit(BoolExpressionNode boolExpressionNode) {
		emitter.emitLineDirective(boolExpressionNode.getLine(), null);
		expressionStack.addLast(expressionMapper.mapBoolExpression(boolExpressionNode.getValue()));
		emitter.emitEmptyLine();
	}

	@Override
	public void visit(ListExpressionNode listExpressionNode) {
		final List<String> values = visitExpressionNodes(listExpressionNode.getValues());

		emitter.emitLineDirective(listExpressionNode.getLine(), null);
		expressionStack.addLast(expressionMapper.mapListExpression((ListType) listExpressionNode.getType(), values));
		emitter.emitEmptyLine();
	}

	@Override
	public void visit(MultiplyExpressionNode multiplyExpressionNode) {
		final String left = visitExpressionNode(multiplyExpressionNode.getLeft());
		final Type leftType = multiplyExpressionNode.getLeft().getType();
		final String right = visitExpressionNode(multiplyExpressionNode.getRight());
		final Type rightType = multiplyExpressionNode.getRight().getType();

		emitter.emitLineDirective(multiplyExpressionNode.getLine(), null);
		expressionStack.addLast(expressionMapper.mapMultiplyExpression(leftType, left, rightType, right));
		emitter.emitEmptyLine();
	}

	@Override
	public void visit(ForRangeLoopStatementNode forRangeLoopStatementNode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(RangeExpressionNode rangeExpressionNode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IdentifierExpressionNode identifierExpressionNode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ModuloExpressionNode moduloExpressionNode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(EqualExpressionNode equalExpressionNode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IfStatementNode ifStatementNode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(NegateExpressionNode negateExpressionNode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(AssignmentExpressionNode assignmentExpressionNode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ExpressionStatementNode expressionStatementNode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IndexExpressionNode indexExpressionNode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ReturnStatementNode returnStatementNode) {
		// TODO Auto-generated method stub

	}

}
