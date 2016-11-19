package cc.ethon.mdma.backend.interpreter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cc.ethon.mdma.backend.interpreter.value.BoolValue;
import cc.ethon.mdma.backend.interpreter.value.FunctionValue;
import cc.ethon.mdma.backend.interpreter.value.IntegerValue;
import cc.ethon.mdma.backend.interpreter.value.ListValue;
import cc.ethon.mdma.backend.interpreter.value.ModuleValue;
import cc.ethon.mdma.backend.interpreter.value.RangeValue;
import cc.ethon.mdma.backend.interpreter.value.Value;
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
import cc.ethon.mdma.frontend.ast.VariableDeclarationNode;

public class InterpretingVisitor implements AstVisitor {

	private Value currentValue;
	private VariableMap variables;

	private Value visitExpression(ExpressionNode node) {
		node.accept(this);
		return currentValue;
	}

	private void doVisit(List<? extends Node> nodes) {
		for (final Node node : nodes) {
			node.accept(this);
		}
	}

	private void doVisitBlock(List<? extends Node> nodes) {
		startVariableMap();
		doVisit(nodes);
		endVariableMap();
	}

	public ModuleValue interpretModule(ModuleNode module, VariableMap globals) {
		currentValue = null;
		variables = globals;
		visit(module);
		return (ModuleValue) currentValue;
	}

	public VariableMap startVariableMap() {
		return variables = new VariableMap(variables);
	}

	public void endVariableMap() {
		variables = variables.getParent();
	}

	@Override
	public void visit(FunctionNode functionNode) {
		currentValue = new FunctionValue(functionNode, this);
		variables.setVariable(functionNode.getName(), currentValue);
	}

	@Override
	public void visit(IntegerExpressionNode integerNode) {
		currentValue = IntegerValue.valueOf(integerNode.getValue().longValue());
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
		final ModuleValue module = new ModuleValue(startVariableMap());
		doVisit(moduleNode.getChildren());
		currentValue = module;
	}

	@Override
	public void visit(StatementBlockNode statementBlockNode) {
		doVisitBlock(statementBlockNode.getChildren());
	}

	@Override
	public void visit(VariableDeclarationNode variableDeclarationNode) {
		Value value = null;
		if (variableDeclarationNode.getAssignedValue() != null) {
			value = visitExpression(variableDeclarationNode.getAssignedValue());
		}
		variables.setVariable(variableDeclarationNode.getName(), value);
	}

	@Override
	public void visit(BoolExpressionNode boolExpressionNode) {
		currentValue = BoolValue.valueOf(boolExpressionNode.getValue());
	}

	@Override
	public void visit(ListExpressionNode listExpressionNode) {
		final List<Value> values = new ArrayList<Value>(listExpressionNode.getValues().size());
		listExpressionNode.getValues().stream().map(node -> visitExpression(node)).forEach(value -> values.add(value));
		currentValue = ListValue.valueOf(values);
	}

	@Override
	public void visit(MultiplyExpressionNode multiplyExpressionNode) {
		currentValue = visitExpression(multiplyExpressionNode.getLeft()).multiply(visitExpression(multiplyExpressionNode.getRight()));
	}

	@Override
	public void visit(ForRangeLoopStatementNode forRangeLoopStatementNode) {
		final String variableName = forRangeLoopStatementNode.getVariable().getName();
		final Value range = visitExpression(forRangeLoopStatementNode.getRange());
		final Iterator<Value> iterator = range.getIterator();
		while (iterator.hasNext()) {
			startVariableMap();
			variables.setVariable(variableName, iterator.next());
			doVisit(forRangeLoopStatementNode.getChildren());
			endVariableMap();
		}
	}

	@Override
	public void visit(RangeExpressionNode rangeExpressionNode) {
		final Value begin = visitExpression(rangeExpressionNode.getLeft());
		final Value end = visitExpression(rangeExpressionNode.getRight());
		final Value increment = IntegerValue.valueOf(1);
		currentValue = new RangeValue(begin, end, increment);
	}

	@Override
	public void visit(IdentifierExpressionNode identifierExpressionNode) {
		currentValue = variables.getVariable(identifierExpressionNode.getName());
	}

	@Override
	public void visit(ModuloExpressionNode moduloExpressionNode) {
		currentValue = visitExpression(moduloExpressionNode.getLeft()).modulo(visitExpression(moduloExpressionNode.getRight()));
	}

	@Override
	public void visit(EqualExpressionNode equalExpressionNode) {
		currentValue = visitExpression(equalExpressionNode.getLeft()).equalTo(visitExpression(equalExpressionNode.getRight()));
	}

	@Override
	public void visit(IfStatementNode ifStatementNode) {
		if (visitExpression(ifStatementNode.getCondition()).getBoolValue()) {
			doVisitBlock(ifStatementNode.getBody());
			return;
		}
		if (ifStatementNode.getElifs() != null) {
			for (final ConditionBodyPair conditionBodyPair : ifStatementNode.getElifs()) {
				if (visitExpression(conditionBodyPair.getCondition()).getBoolValue()) {
					doVisitBlock(conditionBodyPair.getBody());
				}
			}
		}
		if (ifStatementNode.getElseBody() != null) {
			doVisitBlock(ifStatementNode.getElseBody());
		}
	}

	@Override
	public void visit(NegateExpressionNode negateExpressionNode) {
		currentValue = visitExpression(negateExpressionNode.getChild()).negate();
	}

	@Override
	public void visit(AssignmentExpressionNode assignmentExpressionNode) {
		final Value right = visitExpression(assignmentExpressionNode.getRight());
		if (assignmentExpressionNode.getLeft() instanceof IdentifierExpressionNode) {
			final IdentifierExpressionNode identNode = (IdentifierExpressionNode) assignmentExpressionNode.getLeft();
			variables.setVariable(identNode.getName(), right);
		} else if (assignmentExpressionNode.getLeft() instanceof IndexExpressionNode) {
			final IndexExpressionNode indexNode = (IndexExpressionNode) assignmentExpressionNode.getLeft();
			final Value left = visitExpression(indexNode.getLeft());
			final Value index = visitExpression(indexNode.getRight());
			left.indexSet(index, right);
		} else {
			throw new UnsupportedOperationException();
		}
		currentValue = right;
	}

	@Override
	public void visit(ExpressionStatementNode expressionStatementNode) {
		expressionStatementNode.getExpression().accept(this);
	}

	@Override
	public void visit(IndexExpressionNode indexExpressionNode) {
		currentValue = visitExpression(indexExpressionNode.getLeft()).indexGet(visitExpression(indexExpressionNode.getRight()));
	}

	@Override
	public void visit(ReturnStatementNode returnStatementNode) {
		throw new FunctionReturn(visitExpression(returnStatementNode.getReturnedExpression()));
	}

}
