
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

public class JSchSemantics {

    private List<String> keywords = null;
    private Map<String, List<String>> compatibilityTable = null;

	public JSchSemantics(){
		LoadSemanticTables();
	}

    private void LoadSemanticTables() {
        if (keywords == null) {
            keywords = new ArrayList<>();
            String[] k = {"type", "minLength", "maxLength", "pattern", "minimum", "maximum", "exclusiveMinimum", "exclusiveMaximum", "multipleOf", "minItems",
                "maxItems", "items", "uniqueItems", "properties", "additionalProperties", "required", "minProperties", "maxProperties", "dependencies",
                "patternProperties"};
            for (int i = 0; i < k.length; i++) {
                keywords.add(k[i]);
            }
        }

		if (compatibilityTable == null) {
			ArrayList<String> stringKeywords = new ArrayList<>();
			stringKeywords.add("type");
			stringKeywords.add("minLength");
			stringKeywords.add("maxLength");
			stringKeywords.add("pattern");
			ArrayList<String> numberKeywords = new ArrayList<>();
			numberKeywords.add("type");
			numberKeywords.add("minimum");
			numberKeywords.add("maximum");
			numberKeywords.add("exclusiveMinimum");
			numberKeywords.add("exclusiveMaximum");
			numberKeywords.add("multipleOf");
			ArrayList<String> integerKeywords = new ArrayList<>();
			integerKeywords.add("type");
			integerKeywords.add("minimum");
			integerKeywords.add("maximum");
			integerKeywords.add("exclusiveMinimum");
			integerKeywords.add("exclusiveMaximum");
			integerKeywords.add("multipleOf");
			ArrayList<String> booleanKeyrods = new ArrayList<>();
			booleanKeyrods.add("type");
			ArrayList<String> nullKeywords = new ArrayList<>();
			nullKeywords.add("type");
			ArrayList<String> arrayKeywords = new ArrayList<>();
			arrayKeywords.add("type");
			arrayKeywords.add("minItems");
			arrayKeywords.add("maxItems");
			arrayKeywords.add("items");
			arrayKeywords.add("uniqueItems");
			ArrayList<String> objectKeywords = new ArrayList<>();
			objectKeywords.add("type");
			objectKeywords.add("properties");
			objectKeywords.add("additionalProperties");
			objectKeywords.add("required");
			objectKeywords.add("minProperties");
			objectKeywords.add("maxProperties");
			objectKeywords.add("dependencies");
			objectKeywords.add("patternProperties");

            compatibilityTable = new HashMap<>();
			compatibilityTable.put("string",stringKeywords);
			compatibilityTable.put("number",numberKeywords);
			compatibilityTable.put("integer",integerKeywords);
			compatibilityTable.put("boolean",booleanKeyrods);
			compatibilityTable.put("null",nullKeywords);
			compatibilityTable.put("array",arrayKeywords);
			compatibilityTable.put("object",objectKeywords);
        }
    }

    public boolean Compatible(JSONType type, String keyword) {
		if(keywords.contains(keyword)){
			List<String> typeKeywords = compatibilityTable.get(type.toString());
			if(typeKeywords != null && typeKeywords.contains(keyword))
				return true;
		}
		return false;
	}

    public boolean Compatible(List<JSONType> types, String keyword) {
		for (JSONType type : types)
			if(!Compatible(type,keyword))
				return false;
		return true;
	}

	public boolean Compatible(Object J, JSONKeyValue k) {
		return Compatible(JSONType.getJsonType(J),k.key);
	}
}
