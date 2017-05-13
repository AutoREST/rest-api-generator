package autorest.generator;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.io.Serializable;
import autorest.util.DeepCopy;

public class JSchRestriction implements Serializable{

	private static String identStr = "  ";
	private static String identStr2 = identStr+identStr;
	private static String identStr3 = identStr+identStr+identStr;

	private List<JSONType> types = null;
	private String title = null;
	private String description = null;

	private Integer minLength = null, maxLength = null;
	private String pattern = null;

	private Double minimum = null, maximum = null;
	private Boolean exMinimum = null, exMaximum = null;
	private Double multipleOf = null;

	private JSchRestriction sameItems = null;
	private List<JSchRestriction> variItems = null;
	private Boolean addItemsBool = null;
	private JSchRestriction addItemsJSch = null;
	private Integer minItems = null, maxItems = null;
	private Boolean uniqueItems = null;

	private Map<String, JSchRestriction> properties = null;
	private Boolean addPropertiesBool = null;
	private JSchRestriction addPropertiesJSch = null;
	private List<String> required = null;
	private Integer minProperties = null, maxProperties = null;
	private Map<String, JSchDependence> dependencies = null;
	private Map<String, JSchRestriction> patterProperties = null;

	private List<JSchRestriction> anyOf = null;
	private List<JSchRestriction> allOf = null;
	private List<JSchRestriction> oneOf = null;
	private JSchRestriction not = null;
	private List<Object> enumValues = null;

	private String ref = null;

	public JSchRestriction(){
		this.types = new ArrayList<>();
	}

	public void addType(JSONType type){
		this.types.add(type);
	}

	public List<JSONType> getTypes(){
		return this.types;
	}
	public JSONType getFirstType(){
		if(this.types.size()>0)
			return this.types.get(0);
		return JSONType.OBJECT;
	}
	public void setTitle(String newTitle){ this.title = newTitle; }
	public String getTitle(){ return this.title; }
	public void setDescription(String newDescription){ this.description = newDescription; }
	public String getDescription(){ return this.description; }
	public void setMinLength(Integer value) { this.minLength = value; }
	public Integer getMinLength() { return this.minLength; }
	public void setMaxLength(Integer value) { this.maxLength = value; }
	public Integer getMaxLength() { return this.maxLength; }
	public void setPattern(String value) { this.pattern = value; }
	public String getPattern() { return this.pattern; }
	public void setMinimum(Double value) { this.minimum = value; }
	public Double getMinimum() { return this.minimum; }
	public void setMaximum(Double value) { this.maximum = value; }
	public Double getMaximum() { return this.maximum; }
	public void setExMinimum(Boolean value) { this.exMinimum = value; }
	public Boolean getExMinimum() { return this.exMinimum; }
	public void setExMaximum(Boolean value) { this.exMaximum = value; }
	public Boolean getExMaximum() { return this.exMaximum; }
	public void setMultipleOf(Double value) { this.multipleOf = value; }
	public Double getMultipleOf() { return this.multipleOf; }
	public void setSameItems(JSchRestriction value) { this.sameItems = value; }
	public JSchRestriction getSameItems() { return this.sameItems; }
	public void setVariItems(List<JSchRestriction> value) { this.variItems = value; }
	public List<JSchRestriction> getVariItems() { return this.variItems; }
	public void setAdditionalItems(Object value){
		if(value instanceof Boolean)
			this.setAdditionalItemsBool((Boolean)value);
		else if(value instanceof JSchRestriction)
			this.setAdditionalItemsJSch((JSchRestriction)value);
	}
	public void setAdditionalItemsBool(Boolean value) { this.addItemsBool = value; }
	public Boolean getAdditionalItemsBool() { return this.addItemsBool; }
	public void setAdditionalItemsJSch(JSchRestriction value) { this.addItemsJSch = value; }
	public JSchRestriction getAdditionalItemsJSch() { return this.addItemsJSch; }
	public void setMinItems(Integer value) { this.minItems = value; }
	public Integer getMinItems() { return this.minItems; }
	public void setMaxItems(Integer value) { this.maxItems = value; }
	public Integer getMaxItems() { return this.maxItems; }
	public void setUniqueItems(Boolean value) { this.uniqueItems = value; }
	public Boolean getUniqueItems() { return this.uniqueItems; }
	public void setProperties(Map<String, JSchRestriction> value) { this.properties = value; }
	public Map<String, JSchRestriction> getProperties() { return this.properties; }
	public void setAdditionalProperties(Object value){
		if(value instanceof Boolean)
			this.setAdditionalPropertiesBool((Boolean)value);
		else if(value instanceof JSchRestriction)
			this.setAdditionalPropertiesJSch((JSchRestriction)value);
	}
	public void setAdditionalPropertiesBool(Boolean value) { this.addPropertiesBool = value; }
	public Boolean getAdditionalPropertiesBool() { return this.addPropertiesBool; }
	public void setAdditionalPropertiesJSch(JSchRestriction value) { this.addPropertiesJSch = value; }
	public JSchRestriction getAdditionalPropertiesJSch() { return this.addPropertiesJSch; }
	public void setRequired(List<String> value) { this.required = value; }
	public List<String> getRequired() { return this.required; }
	public void setMinProperties(Integer value) { this.minProperties = value; }
	public Integer getMinProperties() { return this.minProperties; }
	public void setMaxProperties(Integer value) { this.maxProperties = value; }
	public Integer getMaxProperties() { return this.maxProperties; }
	public void setDependencies(Map<String, JSchDependence> value) { this.dependencies = value; }
	public Map<String, JSchDependence> getDependencies() { return this.dependencies; }
	public void setPatternProperties(Map<String, JSchRestriction> value) { this.patterProperties = value; }
	public Map<String, JSchRestriction> getPatternProperties() { return this.patterProperties; }
	public void setAnyOf(List<JSchRestriction> value) { this.anyOf = value; }
	public List<JSchRestriction> getAnyOf() { return this.anyOf; }
	public void setAllOf(List<JSchRestriction> value) { this.allOf = value; }
	public List<JSchRestriction> getAllOf() { return this.allOf; }
	public void setOneOf(List<JSchRestriction> value) { this.oneOf = value; }
	public List<JSchRestriction> getOneOf() { return this.oneOf; }
	public void setNot(JSchRestriction value) { this.not = value; }
	public JSchRestriction getNot() { return this.not; }
	public void setEnumValues(List<Object> value) { this.enumValues = value; }
	public List<Object> getEnumValues() { return this.enumValues; }
	public void setRef(String value) { this.ref = value; }
	public String getRef() { return this.ref; }

	public Boolean isSimplekey(){
		List<String> primaryKeys = new ArrayList<>();
		if(this.dependencies != null){
			for (String p : this.dependencies.keySet()) {
				List<String> kwords = this.dependencies.get(p).getKwords();
				if(kwords != null){
					for (String r : kwords) {
						if(!primaryKeys.contains(r))
							primaryKeys.add(r);
					}
				}
			}
		}
		return (primaryKeys.size() == 1);
	}

	public Boolean hasRefs(){
		if(this.ref != null)
			return true;
		if(this.sameItems != null && this.sameItems.hasRefs())
			return true;
		if(this.variItems != null)
			for (JSchRestriction jsch : this.variItems)
				if(jsch.hasRefs())
					return true;
		if(this.addItemsJSch != null && this.addItemsJSch.hasRefs())
			return true;
		if(this.properties != null)
			for (String key : this.properties.keySet())
				if(this.properties.get(key).hasRefs())
					return true;
		if(this.addPropertiesJSch != null && this.addPropertiesJSch.hasRefs())
			return true;
		if(this.dependencies != null)
			for (String key : this.dependencies.keySet())
				if(this.dependencies.get(key).hasRefs())
					return true;
		if(this.patterProperties != null)
			for (String key : this.patterProperties.keySet())
				if(this.patterProperties.get(key).hasRefs())
					return true;
		if(this.anyOf != null)
			for (JSchRestriction jsch : this.anyOf)
				if(jsch.hasRefs())
					return true;
		if(this.allOf != null)
			for (JSchRestriction jsch : this.allOf)
				if(jsch.hasRefs())
					return true;
		if(this.oneOf != null)
			for (JSchRestriction jsch : this.oneOf)
				if(jsch.hasRefs())
					return true;
		if(this.not != null && this.not.hasRefs())
			return true;
		if(this.enumValues != null)
			for (Object obj : this.enumValues)
				if(obj instanceof JSchRestriction)
					if(((JSchRestriction)obj).hasRefs())
						return true;
		return false;
	}

	public String toString(){
		return this.toString("");
	}

	public String toString(String baseIdent){
		StringBuilder str = new StringBuilder();
		String ident1 = baseIdent;
		String ident2 = baseIdent+identStr;
		String ident3 = baseIdent+identStr2;
		String ident4 = baseIdent+identStr3;

		if(this.title != null)
			str.append(ident2+"Title: "+this.title+"\n");
		if(this.description != null)
			str.append(ident2+"Description: "+this.description+"\n");
		if(this.types.size() > 0){
			str.append(ident2+"Type(s) and restriction(s):\n");
			for (JSONType t : this.types) {
				str.append(ident2+t+"\n");
				switch (t) {
					case STRING:
						if(this.minLength != null)
							str.append(ident2+"Minimum length: "+this.minLength+"\n");
						if(this.maxLength != null)
							str.append(ident2+"Maximum length: "+this.maxLength+"\n");
						if(this.pattern != null)
							str.append(ident2+"Pattern: "+this.pattern+"\n");
						break;
					case INTEGER:
					case NUMBER:
						if(this.minimum != null)
							str.append(ident2+"Minumum: "+this.minimum+"\n");
						if(this.exMinimum != null)
							str.append(ident2+"Exclusive minimum: "+this.exMinimum+"\n");
						if(this.maximum != null)
							str.append(ident2+"Maximum: "+this.maximum+"\n");
						if(this.exMaximum != null)
							str.append(ident2+"Exclusive maximum: "+this.exMaximum+"\n");
						if(this.multipleOf != null)
							str.append(ident2+"Multiple of: "+this.multipleOf+"\n");
						break;
					case ARRAY:
						if(this.sameItems != null)
							str.append(ident2+"Same items: \n"+this.sameItems.toString(ident3)+"\n");
						if(this.variItems != null){
							str.append(ident2+"Various items:\n");
							for (JSchRestriction jsch : this.variItems) {
								str.append(jsch.toString(ident3)+"\n");
							}
						}
						if(this.addItemsBool != null)
							str.append(ident2+"Additional items: "+this.addItemsBool+"\n");
						if(this.addItemsJSch != null)
							str.append(ident2+"Additional items: \n"+this.addItemsJSch.toString(ident3)+"\n");
						if(this.minItems != null)
							str.append(ident2+"Minimum items: "+this.minItems+"\n");
						if(this.maxItems != null)
							str.append(ident2+"Maximum items: "+this.maxItems+"\n");
						if(this.uniqueItems != null)
							str.append(ident2+"Unique items: "+this.uniqueItems+"\n");
						break;
					case OBJECT:
						if(this.properties != null){
							str.append(ident2+"Properties: \n");
							for (String pName : this.properties.keySet()) {
								str.append(ident3+pName+"\n");
								str.append(this.properties.get(pName).toString(ident4));
							}
						}
						if(this.addPropertiesBool != null)
							str.append(ident2+"Additional properties: "+this.addPropertiesBool+"\n");
						if(this.addPropertiesJSch != null)
							str.append(ident2+"Additional properties: \n"+this.addPropertiesJSch.toString(ident3)+"\n");
						if(this.required != null){
							String reqStr = "[";
							for (String req : this.required) {
								reqStr += req + " ";
							}
							reqStr += "]";
							str.append(ident2+"Required: "+reqStr+"\n");
						}
						if(this.minProperties != null)
							str.append(ident2+"Minimum properties: "+this.minProperties+"\n");
						if(this.maxProperties != null)
							str.append(ident2+"Maximum properties: "+this.maxProperties+"\n");
						if(this.dependencies != null){
							str.append(ident2+"Dependencies:\n");
							for (String k : this.dependencies.keySet()) {
								str.append(this.dependencies.get(k).toString(ident3));
							}
						}
						if(this.patterProperties != null){
							str.append(ident2+"Pattern properties: \n");
							for (String k : this.patterProperties.keySet()) {
								str.append(this.patterProperties.get(ident3));
							}
						}
						break;
					case BOOLEAN:
					case NULL:
						//BOOLEAN and NULL are cool, no restrictions
						break;
					default:
						str.append(ident2+"UNREGONIZED TYPE\n");
				}
			}
		}
		if(this.anyOf != null){
			str.append(ident1+"Any of:\n");
			for (JSchRestriction k : this.anyOf) {
				str.append(k.toString(ident2));
			}
		}
		if(this.allOf != null){
			str.append(ident1+"All of:\n");
			for (JSchRestriction k : this.allOf) {
				str.append(k.toString(ident2));
			}
		}
		if(this.oneOf != null){
			str.append(ident1+"Any of:\n");
			for (JSchRestriction k : this.oneOf) {
				str.append(k.toString(ident2));
			}
		}
		if(this.not != null){
			str.append(ident1+"Not:\n");
			str.append(this.not.toString(ident2));
		}
		if(this.enumValues != null){
			str.append(ident1+"Enum:\n");
			str.append(ident1+this.enumValues+"\n");
		}
		if(this.ref != null){
			str.append(ident1+"$ref: " + this.ref+"\n");
		}
		return str.toString();
	}

	public JSchRestriction clone() {
		return (JSchRestriction)DeepCopy.copy(this);
	}
}
