package cc.ethon.mdma.core.analysis;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import cc.ethon.mdma.common.CompilerMessage;
import cc.ethon.mdma.core.symbol.FunctionSymbol;
import cc.ethon.mdma.core.symbol.Symbol;
import cc.ethon.mdma.core.symbol.SymbolTable;
import cc.ethon.mdma.core.symbol.SymbolVisibility;
import cc.ethon.mdma.core.symbol.VariableSymbol;
import cc.ethon.mdma.core.type.BoolType;
import cc.ethon.mdma.core.type.IntegerType;
import cc.ethon.mdma.core.type.ListType;
import cc.ethon.mdma.core.type.RangeType;
import cc.ethon.mdma.core.type.StringType;
import cc.ethon.mdma.core.type.Type;
import cc.ethon.mdma.core.type.VoidType;
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
import cc.ethon.mdma.frontend.ast.TypeNode;
import cc.ethon.mdma.frontend.ast.VariableDeclarationNode;

class AnalyzingVisitor implements AstVisitor {

	public enum State {
		NoError, Warning, SoftError, HardError
	}

	private static final Map<String, Type> BUILTIN_PRIMITIVE_TYPES;

	static {
		BUILTIN_PRIMITIVE_TYPES = new HashMap<String, Type>();

		BUILTIN_PRIMITIVE_TYPES.put("byte", IntegerType.BYTE);
		BUILTIN_PRIMITIVE_TYPES.put("short", IntegerType.SHORT);
		BUILTIN_PRIMITIVE_TYPES.put("int", IntegerType.INT);
		BUILTIN_PRIMITIVE_TYPES.put("long", IntegerType.LONG);
		BUILTIN_PRIMITIVE_TYPES.put("bigint", IntegerType.BIGINT);
		BUILTIN_PRIMITIVE_TYPES.put("char", IntegerType.CHAR);

		BUILTIN_PRIMITIVE_TYPES.put("bool", BoolType.BOOL);
		BUILTIN_PRIMITIVE_TYPES.put("string", StringType.STRING);
		BUILTIN_PRIMITIVE_TYPES.put("void", VoidType.VOID);
	}

	private State state;
	private final Deque<SymbolTable> symbolTables;
	private Type currentType;
	private final AnalysisResults results;
	private boolean isTopLevel;

	private void softenError() {
		if (state == State.HardError) {
			state = State.SoftError;
		}
	}

	private void startSymbolTable() {
		symbolTables.push(new SymbolTable(symbolTables.peekLast()));
	}

	private SymbolTable endSymbolTable() {
		return symbolTables.removeLast();
	}

	private SymbolTable getSymbolTable() {
		return symbolTables.peekLast();
	}

	private Type evaluateTypeNode(TypeNode typeNode) {
		typeNode.accept(this);
		return currentType;
	}

	private void doVisit(List<? extends Node> nodes) {
		for (final Node node : nodes) {
			node.accept(this);
			if (state == State.HardError) {
				break;
			}
		}
	}

	private CompilerMessage createMessageForNode(Node node, String description) {
		return new CompilerMessage(results.getModule().getName(), node.getLine(), node.getColumn(), description);
	}

	private Symbol lookupSymbol(Node node, String name, boolean recursive) {
		final Symbol symbol = getSymbolTable().lookupSymbol(name, recursive);
		if (symbol == null) {
			final String description = String.format("Symbol %s was not defined", name);
			final CompilerMessage message = createMessageForNode(node, description);
			results.getErrors().add(message);
			state = State.HardError;
		}
		return symbol;
	}

	private void defineSymbol(String name, Symbol symbol) {
		if (!getSymbolTable().defineSymbol(name, symbol)) {
			final Symbol existing = getSymbolTable().lookupSymbol(name);
			final Node node = symbol.getDefiningNode();
			final Node existingNode = existing.getDefiningNode();
			final String description = String.format("Symbol %s was already defined at %d:%d", name, existingNode.getLine(), existingNode.getColumn());
			final CompilerMessage message = createMessageForNode(node, description);
			results.getErrors().add(message);
			state = State.HardError;
		}
	}

	private Type getTypeFromName(Node node, String name) {
		final Type type = BUILTIN_PRIMITIVE_TYPES.get(name);
		if (type == null) {
			final String description = "Unknown type name " + name;
			final CompilerMessage message = createMessageForNode(node, description);
			results.getErrors().add(message);
			state = State.HardError;
		}
		return type;
	}

	private static class OperationTestResult {
		public boolean result;
		public Type type;
		public String operator;

		public OperationTestResult(boolean result, Type type, String operator) {
			this.result = result;
			this.type = type;
			this.operator = operator;
		}
	}

	private void visitBinaryExpression(BinaryExpressionNode binaryExpressionNode, BiFunction<Type, Type, OperationTestResult> test) {
		if (state == State.HardError) {
			return;
		}

		// Visit left side.
		binaryExpressionNode.getLeft().accept(this);
		if (state == State.HardError) {
			return;
		}
		final Type leftType = binaryExpressionNode.getLeft().getType();

		// Visit right side.
		binaryExpressionNode.getRight().accept(this);
		if (state == State.HardError) {
			return;
		}
		final Type rightType = binaryExpressionNode.getRight().getType();

		// Test if the operation can be done.
		final OperationTestResult testResult = test.apply(leftType, rightType);
		if (!testResult.result) {
			final String description = String.format("No operator defined for expression %s %s %s", leftType, testResult.operator, rightType);
			final CompilerMessage message = createMessageForNode(binaryExpressionNode, description);
			results.getErrors().add(message);
			state = State.HardError;
			return;
		}
		binaryExpressionNode.setType(testResult.type);
	}

	public AnalyzingVisitor(AnalysisResults results, SymbolTable parentSymbolTable) {
		state = State.NoError;
		symbolTables = new ArrayDeque<SymbolTable>();
		currentType = null;
		this.results = results;
		this.isTopLevel = true;

		if (parentSymbolTable != null) {
			symbolTables.addLast(parentSymbolTable);
		}
	}

	@Override
	public void visit(FunctionNode functionNode) {
		if (state == State.HardError) {
			return;
		}

		isTopLevel = false;
		startSymbolTable();
		final Type type = functionNode.getReturnType() == null ? VoidType.VOID : evaluateTypeNode(functionNode.getReturnType());
		if (state == State.HardError) {
			softenError();
			isTopLevel = true;
			return;
		}

		final List<Type> argumentTypes = new ArrayList<Type>();
		final List<String> argumentNames = new ArrayList<String>();
		for (final VariableDeclarationNode variableDeclarationNode : functionNode.getArguments()) {
			variableDeclarationNode.accept(this);
			final Symbol symbol = getSymbolTable().lookupSymbol(variableDeclarationNode.getName(), false);
			argumentTypes.add(((VariableSymbol) symbol).getType());
			argumentNames.add(variableDeclarationNode.getName());
		}
		doVisit(functionNode.getChildren());
		if (state == State.HardError) {
			softenError();
			isTopLevel = true;
			return;
		}

		endSymbolTable();
		final FunctionSymbol symbol = new FunctionSymbol(functionNode, SymbolVisibility.PUBLIC, type, functionNode.getName(), argumentTypes, argumentNames);
		defineSymbol(functionNode.getName(), symbol);
		results.getModule().getGlobalFunctions().add(symbol);
		isTopLevel = true;
	}

	@Override
	public void visit(IntegerExpressionNode integerNode) {
		if (state == State.HardError) {
			return;
		}
		integerNode.setType(IntegerType.INT);
	}

	@Override
	public void visit(ListTypeNode listTypeNode) {
		if (state == State.HardError) {
			return;
		}

		listTypeNode.getSubType().accept(this);
		if (state == State.HardError) {
			return;
		}
		currentType = ListType.getInstance(currentType);
	}

	@Override
	public void visit(NamedTypeNode namedTypeNode) {
		if (state == State.HardError) {
			return;
		}
		currentType = getTypeFromName(namedTypeNode, namedTypeNode.getName());
	}

	@Override
	public void visit(ModuleNode moduleNode) {
		if (state == State.HardError) {
			return;
		}

		isTopLevel = true;
		startSymbolTable();
		doVisit(moduleNode.getChildren());
		endSymbolTable();
		softenError();
	}

	@Override
	public void visit(StatementBlockNode statementBlockNode) {
		if (state == State.HardError) {
			return;
		}

		startSymbolTable();
		doVisit(statementBlockNode.getChildren());
		endSymbolTable();
		softenError();
	}

	@Override
	public void visit(VariableDeclarationNode variableDeclarationNode) {
		if (state == State.HardError) {
			return;
		}

		final Type type = evaluateTypeNode(variableDeclarationNode.getType());
		if (state == State.HardError) {
			return;
		}

		Type valueType = null;
		if (variableDeclarationNode.getAssignedValue() != null) {
			variableDeclarationNode.getAssignedValue().accept(this);
			valueType = variableDeclarationNode.getAssignedValue().getType();
			softenError();
		}

		if (valueType != null && !type.supportsAssign(valueType)) {
			final String description = String.format("No operator defined for expression %s = %s", type, valueType);
			final CompilerMessage message = createMessageForNode(variableDeclarationNode, description);
			results.getErrors().add(message);
			state = State.SoftError;
		}

		final String name = variableDeclarationNode.getName();
		defineSymbol(name, new VariableSymbol(variableDeclarationNode, isTopLevel ? SymbolVisibility.PUBLIC : SymbolVisibility.LOCAL, type, name));
	}

	@Override
	public void visit(BoolExpressionNode boolExpressionNode) {
		if (state == State.HardError) {
			return;
		}
		boolExpressionNode.setType(BoolType.BOOL);
	}

	@Override
	public void visit(ListExpressionNode listExpressionNode) {
		if (state == State.HardError) {
			return;
		}

		Type subType = null;
		for (final ExpressionNode expressionNode : listExpressionNode.getValues()) {
			expressionNode.accept(this);
			if (state == State.HardError) {
				return;
			}
			final Type newSubType = subType == null ? expressionNode.getType() : expressionNode.getType().commonType(subType);
			if (newSubType == null) {
				final String description = String.format("Incompatibles types %s and %s in list expression", subType, expressionNode.getType());
				final CompilerMessage message = createMessageForNode(listExpressionNode, description);
				results.getErrors().add(message);
				state = State.HardError;
				return;
			}
			subType = newSubType;
		}

		listExpressionNode.setType(ListType.getInstance(subType));
	}

	@Override
	public void visit(MultiplyExpressionNode multiplyExpressionNode) {
		visitBinaryExpression(multiplyExpressionNode, new BiFunction<Type, Type, AnalyzingVisitor.OperationTestResult>() {
			@Override
			public OperationTestResult apply(Type t, Type u) {
				return new OperationTestResult(t.supportsMultiply(u), t.multiplyType(u), "*");
			}
		});
	}

	@Override
	public void visit(ForRangeLoopStatementNode forRangeLoop) {
		if (state == State.HardError) {
			return;
		}

		forRangeLoop.getVariable().accept(this);
		if (state == State.HardError) {
			softenError();
			return;
		}

		forRangeLoop.getRange().accept(this);
		softenError();

		if (forRangeLoop.getRange().getType() != null) {
			final Type rangeType = forRangeLoop.getRange().getType();
			if (!rangeType.isRange() && rangeType.isList()) {
				final String description = "range or list required for iteration";
				final CompilerMessage message = createMessageForNode(forRangeLoop, description);
				results.getErrors().add(message);
				state = State.SoftError;
			}

			final Type subType = rangeType.isRange() ? ((RangeType) rangeType).getSubType() : ((ListType) rangeType).getSubType();
			final Type variableType = evaluateTypeNode(forRangeLoop.getVariable().getType());
			if (!variableType.supportsAssign(subType)) {
				final String description = String.format("Can't iterate with a variable of type %s over %s", variableType, rangeType);
				final CompilerMessage message = createMessageForNode(forRangeLoop, description);
				results.getErrors().add(message);
				state = State.SoftError;
			}
		}

		startSymbolTable();
		doVisit(forRangeLoop.getChildren());
		endSymbolTable();
		softenError();
	}

	@Override
	public void visit(RangeExpressionNode rangeExpressionNode) {
		visitBinaryExpression(rangeExpressionNode, new BiFunction<Type, Type, AnalyzingVisitor.OperationTestResult>() {
			@Override
			public OperationTestResult apply(Type t, Type u) {
				return new OperationTestResult(t.supportsRange(u), t.rangeType(u), "..");
			}
		});
	}

	@Override
	public void visit(IdentifierExpressionNode identifierExpressionNode) {
		if (state == State.HardError) {
			return;
		}

		final VariableSymbol symbol = (VariableSymbol) lookupSymbol(identifierExpressionNode, identifierExpressionNode.getName(), true);
		if (state == State.HardError || symbol == null) {
			return;
		}
		identifierExpressionNode.setType(symbol.getType());
	}

	@Override
	public void visit(ModuloExpressionNode moduloExpressionNode) {
		visitBinaryExpression(moduloExpressionNode, new BiFunction<Type, Type, AnalyzingVisitor.OperationTestResult>() {
			@Override
			public OperationTestResult apply(Type t, Type u) {
				return new OperationTestResult(t.supportsModulo(u), t.moduloType(u), "%");
			}
		});
	}

	@Override
	public void visit(EqualExpressionNode equalExpressionNode) {
		visitBinaryExpression(equalExpressionNode, new BiFunction<Type, Type, AnalyzingVisitor.OperationTestResult>() {
			@Override
			public OperationTestResult apply(Type t, Type u) {
				return new OperationTestResult(t.supportsEqual(u), t.equalType(u), "==");
			}
		});
	}

	@Override
	public void visit(IfStatementNode ifStatementNode) {
		if (state == State.HardError) {
			return;
		}

		ExpressionNode conditionNode = ifStatementNode.getCondition();
		conditionNode.accept(this);
		softenError();
		Type conditionType = conditionNode.getType();
		if (conditionType != null && !conditionType.isBool()) {
			final String description = String.format("Type %s is not allowed in an if condition, bool required", conditionType);
			final CompilerMessage message = createMessageForNode(conditionNode, description);
			results.getErrors().add(message);
			state = State.SoftError;
		}

		startSymbolTable();
		doVisit(ifStatementNode.getBody());
		endSymbolTable();
		softenError();

		if (ifStatementNode.getElifs() != null && !ifStatementNode.getElifs().isEmpty()) {
			for (final ConditionBodyPair conditionBodyPair : ifStatementNode.getElifs()) {
				conditionNode = conditionBodyPair.getCondition();
				conditionNode.accept(this);
				softenError();
				conditionType = conditionNode.getType();
				if (conditionType != null && !conditionType.isBool()) {
					final String description = String.format("Type %s is not allowed in an if condition, bool required", conditionType);
					final CompilerMessage message = createMessageForNode(conditionNode, description);
					results.getErrors().add(message);
					state = State.SoftError;
				}

				startSymbolTable();
				doVisit(conditionBodyPair.getBody());
				endSymbolTable();
				softenError();
			}
		}

		if (ifStatementNode.getElseBody() != null && !ifStatementNode.getElseBody().isEmpty()) {
			startSymbolTable();
			doVisit(ifStatementNode.getElseBody());
			endSymbolTable();
			softenError();
		}
	}

	@Override
	public void visit(NegateExpressionNode negateExpressionNode) {
		if (state == State.HardError) {
			return;
		}

		final ExpressionNode child = negateExpressionNode.getChild();
		negateExpressionNode.getChild().accept(this);
		if (state == State.HardError) {
			return;
		}

		final Type childType = child.getType();
		if (!childType.supportsNegate()) {
			final String description = String.format("No operator defined for expression !%s", childType);
			final CompilerMessage message = createMessageForNode(negateExpressionNode, description);
			results.getErrors().add(message);
			state = State.HardError;
			return;
		}
		negateExpressionNode.setType(childType.negateType());
	}

	@Override
	public void visit(AssignmentExpressionNode assignmentExpressionNode) {
		visitBinaryExpression(assignmentExpressionNode, new BiFunction<Type, Type, AnalyzingVisitor.OperationTestResult>() {
			@Override
			public OperationTestResult apply(Type t, Type u) {
				return new OperationTestResult(t.supportsAssign(u), t.assignType(u), "[");
			}
		});
	}

	@Override
	public void visit(ExpressionStatementNode expressionStatementNode) {
		if (state == State.HardError) {
			return;
		}
		expressionStatementNode.getExpression().accept(this);
	}

	@Override
	public void visit(IndexExpressionNode indexExpressionNode) {
		visitBinaryExpression(indexExpressionNode, new BiFunction<Type, Type, AnalyzingVisitor.OperationTestResult>() {
			@Override
			public OperationTestResult apply(Type t, Type u) {
				return new OperationTestResult(t.supportsIndex(u), t.indexType(u), "[");
			}
		});
	}

}
