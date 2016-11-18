package cc.ethon.mdma.test.sampletest.util;

import java.util.List;
import java.util.Stack;

import org.junit.Assert;

import cc.ethon.mdma.frontend.ast.AssignmentExpressionNode;
import cc.ethon.mdma.frontend.ast.AstVisitor;
import cc.ethon.mdma.frontend.ast.BinaryExpressionNode;
import cc.ethon.mdma.frontend.ast.BoolExpressionNode;
import cc.ethon.mdma.frontend.ast.EqualExpressionNode;
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
import cc.ethon.mdma.frontend.ast.ReturnStatementNode;
import cc.ethon.mdma.frontend.ast.StatementBlockNode;
import cc.ethon.mdma.frontend.ast.UnaryExpressionNode;
import cc.ethon.mdma.frontend.ast.VariableDeclarationNode;

public class ParentCheckingVisitor implements AstVisitor {

	private final Stack<Node> parents;

	private void assertParent(Node node) {
		Assert.assertEquals(node.getParent(), parents.peek());
	}

	private <T extends Node> void visitChild(Node parent, T child) {
		if (child == null) {
			return;
		}

		parents.push(parent);
		try {
			child.accept(this);
		} finally {
			parents.pop();
		}
	}

	private <T extends Node> void visitChildren(Node parent, List<T> children) {
		parents.push(parent);
		try {
			for (final Node node : children) {
				node.accept(this);
			}
		} finally {
			parents.pop();
		}
	}

	private void assertUnaryExpression(UnaryExpressionNode node) {
		assertParent(node);
		visitChild(node, node.getChild());
	}

	private void assertBinaryExpression(BinaryExpressionNode node) {
		assertParent(node);
		visitChild(node, node.getLeft());
		visitChild(node, node.getRight());
	}

	public ParentCheckingVisitor() {
		parents = new Stack<Node>();
		parents.push(null);
	}

	@Override
	public void visit(FunctionNode functionNode) {
		assertParent(functionNode);
		visitChild(functionNode, functionNode.getReturnType());
		visitChildren(functionNode, functionNode.getArguments());
		visitChildren(functionNode, functionNode.getChildren());
	}

	@Override
	public void visit(IntegerExpressionNode integerNode) {
		assertParent(integerNode);
	}

	@Override
	public void visit(ListTypeNode listTypeNode) {
		assertParent(listTypeNode);
		visitChild(listTypeNode, listTypeNode.getSubType());
	}

	@Override
	public void visit(NamedTypeNode namedTypeNode) {
		assertParent(namedTypeNode);
	}

	@Override
	public void visit(ModuleNode moduleNode) {
		assertParent(moduleNode);
		visitChildren(moduleNode, moduleNode.getChildren());
	}

	@Override
	public void visit(StatementBlockNode statementBlockNode) {
		assertParent(statementBlockNode);
		visitChildren(statementBlockNode, statementBlockNode.getChildren());
	}

	@Override
	public void visit(VariableDeclarationNode variableDeclarationNode) {
		assertParent(variableDeclarationNode);
		visitChild(variableDeclarationNode, variableDeclarationNode.getType());
		visitChild(variableDeclarationNode, variableDeclarationNode.getAssignedValue());
	}

	@Override
	public void visit(BoolExpressionNode boolExpressionNode) {
		assertParent(boolExpressionNode);
	}

	@Override
	public void visit(ListExpressionNode listExpressionNode) {
		assertParent(listExpressionNode);
		visitChildren(listExpressionNode, listExpressionNode.getValues());
	}

	@Override
	public void visit(MultiplyExpressionNode multiplyExpressionNode) {
		assertBinaryExpression(multiplyExpressionNode);
	}

	@Override
	public void visit(ForRangeLoopStatementNode forRangeLoop) {
		assertParent(forRangeLoop);
		visitChild(forRangeLoop, forRangeLoop.getVariable());
		visitChild(forRangeLoop, forRangeLoop.getRange());
		visitChildren(forRangeLoop, forRangeLoop.getChildren());
	}

	@Override
	public void visit(RangeExpressionNode rangeExpressionNode) {
		assertBinaryExpression(rangeExpressionNode);
	}

	@Override
	public void visit(IdentifierExpressionNode identifierExpressionNode) {
		assertParent(identifierExpressionNode);
	}

	@Override
	public void visit(ModuloExpressionNode moduloExpressionNode) {
		assertBinaryExpression(moduloExpressionNode);
	}

	@Override
	public void visit(EqualExpressionNode equalExpressionNode) {
		assertBinaryExpression(equalExpressionNode);
	}

	@Override
	public void visit(IfStatementNode ifStatementNode) {
		assertParent(ifStatementNode);
		visitChild(ifStatementNode, ifStatementNode.getCondition());
		visitChildren(ifStatementNode, ifStatementNode.getBody());
		if (ifStatementNode.getElifs() != null) {
			for (final ConditionBodyPair conditionBodyPair : ifStatementNode.getElifs()) {
				visitChild(ifStatementNode, conditionBodyPair.getCondition());
				visitChildren(ifStatementNode, ifStatementNode.getBody());
			}
		}
		if (ifStatementNode.getElseBody() != null) {
			visitChildren(ifStatementNode, ifStatementNode.getElseBody());
		}
	}

	@Override
	public void visit(NegateExpressionNode negateExpressionNode) {
		assertUnaryExpression(negateExpressionNode);
	}

	@Override
	public void visit(AssignmentExpressionNode assignmentExpressionNode) {
		assertBinaryExpression(assignmentExpressionNode);
	}

	@Override
	public void visit(ExpressionStatementNode expressionStatementNode) {
		assertParent(expressionStatementNode);
		visitChild(expressionStatementNode, expressionStatementNode.getExpression());
	}

	@Override
	public void visit(IndexExpressionNode indexExpressionNode) {
		assertBinaryExpression(indexExpressionNode);
	}

	@Override
	public void visit(ReturnStatementNode returnStatementNode) {
		assertParent(returnStatementNode);
		visitChild(returnStatementNode, returnStatementNode.getReturnedExpression());
	}

}
