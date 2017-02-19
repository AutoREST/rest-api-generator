package autorest.generator;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class JSchRestriction {
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

    public String toString(){
        return this.toString("");
    }

    public String toString(String baseIdent){
        StringBuilder str = new StringBuilder();

        if(this.title != null)
            str.append(baseIdent+"Title: "+this.title+"\n");
        if(this.description != null)
            str.append(baseIdent+"Description: "+this.description+"\n");
        if(this.types.size() > 0){
            str.append(baseIdent+"Type(s) and restriction(s):\n");
            for (JSONType t : this.types) {
                str.append(baseIdent+"\t"+t+"\n");
                switch (t) {
                    case STRING:
                        if(this.minLength != null)
                            str.append(baseIdent+"\tMinimum length: "+this.minLength+"\n");
                        if(this.maxLength != null)
                            str.append(baseIdent+"\tMaximum length: "+this.maxLength+"\n");
                        if(this.pattern != null)
                            str.append(baseIdent+"\tPattern: "+this.pattern+"\n");
                        break;
                    case INTEGER:
                    case NUMBER:
                        if(this.minimum != null)
                            str.append(baseIdent+"\tMinumum: "+this.minimum+"\n");
                        if(this.exMinimum != null)
                            str.append(baseIdent+"\tExclusive minimum: "+this.exMinimum+"\n");
                        if(this.maximum != null)
                            str.append(baseIdent+"\tMaximum: "+this.maximum+"\n");
                        if(this.exMaximum != null)
                            str.append(baseIdent+"\tExclusive maximum: "+this.exMaximum+"\n");
                        if(this.multipleOf != null)
                            str.append(baseIdent+"\tMultiple of: "+this.multipleOf+"\n");
                        break;
                    case ARRAY:
                        if(this.sameItems != null)
                            str.append(baseIdent+"\tSame items: \n"+this.sameItems.toString(baseIdent+"\t\t")+"\n");
                        if(this.variItems != null){
                            str.append(baseIdent+"\tVarious items:\n");
                            for (JSchRestriction jsch : this.variItems) {
                                str.append(jsch.toString(baseIdent+"\t\t")+"\n");
                            }
                        }
                        if(this.addItemsBool != null)
                            str.append(baseIdent+"\tAdditional items: "+this.addItemsBool+"\n");
                        if(this.addItemsJSch != null)
                            str.append(baseIdent+"\tAdditional items: \n"+this.addItemsJSch.toString(baseIdent+"\t\t")+"\n");
                        if(this.minItems != null)
                            str.append(baseIdent+"\tMinimum items: "+this.minItems+"\n");
                        if(this.maxItems != null)
                            str.append(baseIdent+"\tMaximum items: "+this.maxItems+"\n");
                        if(this.uniqueItems != null)
                            str.append(baseIdent+"\tUnique items: "+this.uniqueItems+"\n");
                        break;
                    case OBJECT:
                        if(this.properties != null){
                            str.append(baseIdent+"\tProperties: \n");
                            for (String pName : this.properties.keySet()) {
                                str.append(baseIdent+"\t\t"+pName+"\n");
                                str.append(this.properties.get(pName).toString(baseIdent+"\t\t\t"));
                            }
                        }
                        if(this.addPropertiesBool != null)
                            str.append(baseIdent+"\tAdditional properties: "+this.addPropertiesBool+"\n");
                        if(this.addPropertiesJSch != null)
                            str.append(baseIdent+"\tAdditional properties: \n"+this.addPropertiesJSch.toString(baseIdent+"\t\t")+"\n");
                        if(this.required != null){
                            String reqStr = "[";
                            for (String req : this.required) {
                                reqStr += req + " ";
                            }
                            reqStr += "]";
                            str.append(baseIdent+"\tRequired: "+reqStr+"\n");
                        }
                        if(this.minProperties != null)
                            str.append(baseIdent+"\tMinimum properties: "+this.minProperties+"\n");
                        if(this.maxProperties != null)
                            str.append(baseIdent+"\tMaximum properties: "+this.maxProperties+"\n");
                        if(this.dependencies != null){
                            str.append(baseIdent+"\tDependencies:\n");
                            for (String k : this.dependencies.keySet()) {
                                str.append(this.dependencies.get(k).toString(baseIdent+"\t\t"));
                            }
                        }
                        if(this.patterProperties != null){
                            str.append(baseIdent+"\tPattern properties: \n");
                            for (String k : this.patterProperties.keySet()) {
                                str.append(this.patterProperties.get(baseIdent+"\t\t"));
                            }
                        }
                        break;
                    case BOOLEAN:
                    case NULL:
                        //BOOLEAN and NULL are cool, no restrictions
                        break;
                    default:
                        str.append(baseIdent+"\tUNREGONIZED TYPE\n");
                }
            }
        }
        if(this.anyOf != null){
            str.append(baseIdent+"Any of:\n");
            for (JSchRestriction k : this.anyOf) {
                str.append(k.toString(baseIdent+"\t"));
            }
        }
        if(this.allOf != null){
            str.append(baseIdent+"All of:\n");
            for (JSchRestriction k : this.allOf) {
                str.append(k.toString(baseIdent+"\t"));
            }
        }
        if(this.oneOf != null){
            str.append(baseIdent+"Any of:\n");
            for (JSchRestriction k : this.oneOf) {
                str.append(k.toString(baseIdent+"\t"));
            }
        }
        if(this.not != null){
            str.append(baseIdent+"Not:\n");
            str.append(this.not.toString(baseIdent+"\t"));
        }
        if(this.enumValues != null){
            str.append(baseIdent+"Enum:\n");
			str.append(baseIdent+this.enumValues+"\n");
        }
        if(this.ref != null){
            str.append(baseIdent+"$ref: " + this.ref+"\n");
        }
        return str.toString();
    }
}
