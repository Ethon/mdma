package cc.ethon.mdma.backend.gen.c;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import cc.ethon.mdma.core.symbol.FunctionSymbol;
import cc.ethon.mdma.core.symbol.SymbolTable;
import cc.ethon.mdma.frontend.ast.AssignmentExpressionNode;
import cc.ethon.mdma.frontend.ast.AstVisitor;
import cc.ethon.mdma.frontend.ast.BoolExpressionNode;
import cc.ethon.mdma.frontend.ast.EqualExpressionNode;
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
import cc.ethon.mdma.frontend.ast.StatementBlockNode;
import cc.ethon.mdma.frontend.ast.VariableDeclarationNode;

public class CHeaderGeneratingVisitor implements AstVisitor {


	private final CTypeMapper typeMapper;
	private final CCodeEmitter emitter;

	private void doVisit(List<? extends Node> nodes) {
		for (final Node node : nodes) {
			node.accept(this);
		}
	}

	public CHeaderGeneratingVisitor(PrintStream out) {
		this.typeMapper = new CTypeMapper();
		this.emitter = new CCodeEmitter(out, typeMapper);
	}

	@Override
	public void visit(FunctionNode functionNode) {
		final SymbolTable symbolTable = functionNode.getSymbolTable();
		final FunctionSymbol symbol = (FunctionSymbol) symbolTable.lookupSymbol(functionNode.getName(), false);
		emitter.emitFunctionDeclaration(symbol);
	}

	@Override
	public void visit(IntegerExpressionNode integerNode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visit(ListTypeNode listTypeNode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visit(NamedTypeNode namedTypeNode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visit(ModuleNode moduleNode) {
		final String guard = emitter.startIncludeGuarded();
		for (final String include : SYSTEM_INCLUDES) {
			emitter.emitSystemInclude(include);
		}
		emitter.emitEmptyLine();
		doVisit(moduleNode.getChildren());
		emitter.endIncludeGuarded(guard);
	}

	@Override
	public void visit(StatementBlockNode statementBlockNode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visit(VariableDeclarationNode variableDeclarationNode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visit(BoolExpressionNode boolExpressionNode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visit(ListExpressionNode listExpressionNode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visit(MultiplyExpressionNode multiplyExpressionNode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visit(ForRangeLoopStatementNode forRangeLoop) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visit(RangeExpressionNode rangeExpressionNode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visit(IdentifierExpressionNode identifierExpressionNode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visit(ModuloExpressionNode moduloExpressionNode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visit(EqualExpressionNode equalExpressionNode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visit(IfStatementNode ifStatementNode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visit(NegateExpressionNode negateExpressionNode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visit(AssignmentExpressionNode assignmentExpressionNode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visit(ExpressionStatementNode expressionStatementNode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visit(IndexExpressionNode indexExpressionNode) {
		throw new UnsupportedOperationException();
	}

}
