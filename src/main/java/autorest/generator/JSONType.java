package autorest.generator;

public enum JSONType {

    STRING("string"),
    INTEGER("integer"),
	NUMBER("number"),
	BOOLEAN("boolean"),
	NULL("null"),
	ARRAY("array"),
	OBJECT("object");

    private String value;

    private JSONType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

	public static JSONType getJsonType(Object o){
        if(o == null)
            return JSONType.NULL;
        if(o instanceof Short || o instanceof Integer || o instanceof Long)
            return JSONType.INTEGER;
        if(o instanceof Double)
            return JSONType.NUMBER;
        if(o instanceof String)
            return JSONType.STRING;
        if(o instanceof Boolean)
            return JSONType.BOOLEAN;
        // if(o instanceof Array)
        //     return JSONType.ARRAY;
		return JSONType.OBJECT;
	}
}
