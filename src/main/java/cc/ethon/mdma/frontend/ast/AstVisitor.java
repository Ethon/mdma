package cc.ethon.mdma.frontend.ast;

public interface AstVisitor {

	void visit(FunctionNode functionNode);

	void visit(IntegerExpressionNode integerNode);

	void visit(ListTypeNode listTypeNode);

	void visit(NamedTypeNode namedTypeNode);

	void visit(ModuleNode moduleNode);

	void visit(StatementBlockNode statementBlockNode);

	void visit(VariableDeclarationNode variableDeclarationNode);

	void visit(BoolExpressionNode boolExpressionNode);

	void visit(ListExpressionNode listExpressionNode);

	void visit(MultiplyExpressionNode multiplyExpressionNode);

	void visit(ForRangeLoopStatementNode forRangeLoopStatementNode);

	void visit(RangeExpressionNode rangeExpressionNode);

	void visit(IdentifierExpressionNode identifierExpressionNode);

	void visit(ModuloExpressionNode moduloExpressionNode);

	void visit(EqualExpressionNode equalExpressionNode);

	void visit(IfStatementNode ifStatementNode);

	void visit(NegateExpressionNode negateExpressionNode);

	void visit(AssignmentExpressionNode assignmentExpressionNode);

	void visit(ExpressionStatementNode expressionStatementNode);

	void visit(IndexExpressionNode indexExpressionNode);

}
