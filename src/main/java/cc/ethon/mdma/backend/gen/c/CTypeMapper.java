package cc.ethon.mdma.backend.gen.c;

import java.util.IdentityHashMap;
import java.util.Map;

import cc.ethon.mdma.core.type.BoolType;
import cc.ethon.mdma.core.type.IntegerType;
import cc.ethon.mdma.core.type.StringType;
import cc.ethon.mdma.core.type.Type;
import cc.ethon.mdma.core.type.VoidType;

public class CTypeMapper {

	private final static Map<Type, String> TYPE_MAPPING;

	static {
		TYPE_MAPPING = new IdentityHashMap<Type, String>();
		TYPE_MAPPING.put(IntegerType.BYTE, "int8_t");
		TYPE_MAPPING.put(IntegerType.SHORT, "int16_t");
		TYPE_MAPPING.put(IntegerType.INT, "int32_t");
		TYPE_MAPPING.put(IntegerType.LONG, "int64_t");
		TYPE_MAPPING.put(BoolType.BOOL, "uint8_t");
		TYPE_MAPPING.put(VoidType.VOID, "void");
		TYPE_MAPPING.put(StringType.STRING, "mdma_string_t");
	}

	public String getTypeName(Type type) {
		final String result = TYPE_MAPPING.get(type);
		if (result != null) {
			return result;
		}
		if (type.isList()) {
			return "mdma_list_t";
		}
		throw new UnsupportedOperationException(type.toString());
	}

	public String getPointerTypeName(Type type) {
		return getTypeName(type) + "*";
	}

}
