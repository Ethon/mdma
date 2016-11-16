package cc.ethon.mdma.backend.gen.c;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import cc.ethon.mdma.core.type.BoolType;
import cc.ethon.mdma.core.type.IntegerType;
import cc.ethon.mdma.core.type.ListType;
import cc.ethon.mdma.core.type.Type;

public class CExpressionMapper {

	private final CTypeMapper typeMapper;
	private final CCodeEmitter emitter;
	private final VariableAllocator variableAllocator;

	public CExpressionMapper(CTypeMapper typeMapper, CCodeEmitter emitter, VariableAllocator variableAllocator) {
		this.typeMapper = typeMapper;
		this.emitter = emitter;
		this.variableAllocator = variableAllocator;
	}

	// Primary Expressions.

	public String mapBoolExpression(boolean value) {
		final String variable = variableAllocator.createTemporaryVariable();
		emitter.emitVariableDeclaration(BoolType.BOOL, variable, Boolean.toString(value));
		return variable;
	}

	public String mapIntegerExpression(Type type, BigInteger value) {
		if (type == IntegerType.BIGINT) {
			throw new UnsupportedOperationException();
		}
		final String variable = variableAllocator.createTemporaryVariable();
		emitter.emitVariableDeclaration(type, variable, value.toString());
		return variable;
	}

	public String mapListExpression(ListType type, List<String> values) {
		final Type subType = type.getSubType();
		final String subTypeName = typeMapper.getTypeName(subType);

		final String variable = variableAllocator.createTemporaryVariable();
		final String initializer = String.format("mdma_list_init(%d, sizeof(%s))", values.size(), subTypeName);
		emitter.emitVariableDeclaration(type, variable, initializer);

		final List<String> valueList = values.stream().map(val -> String.format("(%s)%s", subTypeName, val)).collect(Collectors.toList());
		final List<String> arguments = new ArrayList<String>(2 + values.size());
		arguments.add(String.valueOf(values.size()));
		arguments.add("sizeof(" + subTypeName + ")");
		arguments.addAll(valueList);
		emitter.emitFunctionCall("mdma_list_fill_va", arguments);
		return variable;
	}

	// Binary Expressions.

	public String mapMultiplyExpression(Type leftType, String left, Type rightType, String right) {
		if (leftType.isList()) {
			if (rightType.isInteger()) {
				emitter.emitFunctionCall("mdma_list_mul_long", Arrays.asList(left, right));
				return left;
			}
		}
		throw new UnsupportedOperationException();
	}

}
