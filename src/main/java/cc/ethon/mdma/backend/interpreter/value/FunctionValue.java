package cc.ethon.mdma.backend.interpreter.value;

import java.util.List;

import cc.ethon.mdma.backend.interpreter.FunctionReturn;
import cc.ethon.mdma.backend.interpreter.InterpretingVisitor;
import cc.ethon.mdma.backend.interpreter.VariableMap;
import cc.ethon.mdma.frontend.ast.FunctionNode;
import cc.ethon.mdma.frontend.ast.StatementNode;
import cc.ethon.mdma.frontend.ast.VariableDeclarationNode;

public class FunctionValue extends Value {

	public static interface Callable {
		public Value call(List<Value> arguments);
	}

	private static class FunctionNodeCallable implements Callable {
		private final FunctionNode node;
		private final InterpretingVisitor interpretingVisitor;

		public FunctionNodeCallable(FunctionNode node, InterpretingVisitor interpretingVisitor) {
			super();
			this.node = node;
			this.interpretingVisitor = interpretingVisitor;
		}

		@Override
		public Value call(List<Value> arguments) {
			final VariableMap variables = interpretingVisitor.startVariableMap();
			try {
				for (int i = 0; i < node.getArguments().size(); i++) {
					final VariableDeclarationNode variableDeclarationNode = node.getArguments().get(i);
					variables.setVariable(variableDeclarationNode.getName(), arguments.get(i));
				}
				for (final StatementNode statementNode : node.getChildren()) {
					statementNode.accept(interpretingVisitor);
				}
			} catch (final FunctionReturn ret) {
				return ret.getReturnValue();
			} finally {
				interpretingVisitor.endVariableMap();
			}
			return VoidValue.VOID;
		}
	}

	private final Callable callable;

	public FunctionValue(Callable callable) {
		super();
		this.callable = callable;
	}

	public FunctionValue(FunctionNode node, InterpretingVisitor interpretingVisitor) {
		super();
		this.callable = new FunctionNodeCallable(node, interpretingVisitor);
	}

	public Callable getCallable() {
		return callable;
	}

	@Override
	public Value call(List<Value> arguments) {
		return callable.call(arguments);
	}

}
