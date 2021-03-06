package autorest.generator;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class ModelBuilder {
	private Resource resource;
	private String modelFile;
	private String primaryKey;

	public ModelBuilder(Resource res, Map<String, String> snippets) throws Exception{
		this.resource = res;

		String id_virtual = "";
		String multikeyIndex = "";
		this.primaryKey = "";
		if(this.resource.isSimpleKey()){
			id_virtual = snippets.get("id_virtual");
			this.primaryKey = this.resource.getPrimaryKey();
		}
		else {
			List<String> arrIndexKeys = new ArrayList<>();
			for (String key : this.resource.getPrimaryKeys())
				arrIndexKeys.add(key + ": 1");
			String indexKeys = String.join(",", arrIndexKeys);
			multikeyIndex = "{{model_name}}Schema.index({" + indexKeys + "}, { unique: true });";
		}
		String fields = buildFields(this.resource.getProperties());

		String deleteType = "";
		String typeField = "";
		Boolean generic = this.resource.hasSpecializations();
		Boolean specialization = this.resource.hasParent();
		if(generic || specialization)
			deleteType = "delete doc.__type";
		if(specialization)
			typeField = (fields.length()>0 ? "," : "")+"__type: {type: String, default: '"+this.resource.getName()+"'}";
		String propsH = "";
		String refRes = "";
		String arraysItems = "";
		String arraysLinks = "";
		List<String> arrayProps = this.resource.getArrayProps();
		Map<String, Reference> references = this.resource.getReferences();
		List<String> navProps = this.resource.getNavegableProperties();
		if((!navProps.isEmpty() || !arrayProps.isEmpty()) && this.resource.isSimpleKey()){
			for (String propName : navProps)
				propsH += snippets.get("prop_hyperlink").replace("{{prop_name}}", propName);

			for (String propName : arrayProps)//for simple arrays that will be skipped by the references generation
				if(!references.keySet().contains(propName))
					propsH += snippets.get("prop_hyperlink").replace("{{prop_name}}", propName);
		}
		if(!references.isEmpty()){
			for (String propName : references.keySet()) {
				if(!arrayProps.contains(propName)){
					String hyperlink = snippets.get("ref_resource_prop");
					hyperlink = hyperlink.replace("{{prop_name}}", propName);
					hyperlink = hyperlink.replace("{{ref_resource_name}}", references.get(propName).getResourceName());
					refRes += hyperlink;
				}
				else {
					String hyperlinkItems = snippets.get("ref_resource_array_item");
					hyperlinkItems = hyperlinkItems.replace("{{prop_name}}", propName);
					hyperlinkItems = hyperlinkItems.replace("{{ref_resource_name}}", references.get(propName).getResourceName());
					arraysItems += hyperlinkItems;
					if (this.resource.isSimpleKey()) {
						String hyperlink = snippets.get("prop_hyperlink");
						hyperlink = hyperlink.replace("{{prop_name}}", propName);
						arraysLinks += hyperlink;
					}
				}
			}
		}
		this.modelFile = snippets.get("model");
		this.modelFile = this.modelFile.replace("{{fields}}", fields);
		this.modelFile = this.modelFile.replace("{{multikey_unique_index}}",multikeyIndex);
		this.modelFile = this.modelFile.replace("{{id_virtual}}", id_virtual);
		this.modelFile = this.modelFile.replace("{{delete_type}}", deleteType);
		this.modelFile = this.modelFile.replace("{{type_field}}", typeField);
		this.modelFile = this.modelFile.replace("{{props_hyperlinks}}", propsH);
		this.modelFile = this.modelFile.replace("{{ref_resources_props}}", refRes);
		this.modelFile = this.modelFile.replace("{{ref_resources_array_items}}", arraysItems);
		this.modelFile = this.modelFile.replace("{{ref_resources_array_hyperlinks}}", arraysLinks);
		this.modelFile = this.modelFile.replace("{{id_field_name}}", this.primaryKey);
		this.modelFile = this.modelFile.replace("{{resource_name}}", this.resource.getName());
		this.modelFile = this.modelFile.replace("{{collection_name}}", this.resource.getCollectionName());
		this.modelFile = this.modelFile.replace("{{model_name}}", this.resource.getModelName());
	}

	public String buildFields(Map<String, JSchRestriction> props) throws Exception{
		List<String> fields = new ArrayList<>();
		for (String name : props.keySet()) {
			String fieldName;
			Boolean isRequired = this.resource.getRequired().contains(name);
			if(this.resource.isSimpleKey() && name.equals(this.primaryKey))
				fieldName = "_id";
			else
				fieldName = name;
			JSchRestriction prop = props.get(name);
			fields.add(buildField(prop, fieldName, isRequired));
		}
		return String.join(",\n", fields);
	}

	public String buildField(JSchRestriction prop, String fieldName, Boolean isRequired) throws Exception{
		String field = "";
		field = fieldName + ":{";
		field += buildMongooseSchema(prop);
		if(isRequired)
			field += ", required: true";

		field += "}";
		return field;
	}

	private String buildMongooseSchema(JSchRestriction prop){
		List<String> schemaProps = new ArrayList<>();
		JSONType type =  prop.getFirstType();
		if(type == JSONType.ARRAY){
			JSchRestriction sameItems = prop.getSameItems();
			if(sameItems != null)
				schemaProps.add("type: [{" + buildMongooseSchema(sameItems) + "}]");
			List<String> validations = new ArrayList<>();
			/*TODO: The following comments are possible messages to the validations, they would be availavle in variable `err`, as in: err.errors[{{prop_name}}].properties.message
			§§§§§ , msg:'{PATH} should be at least {{value}}.'
			§§§§§ , msg:'{PATH} limit is {{value}}.'
			*/
			Integer value = prop.getMinItems();
			if(value != null)
				validations.add("{validator: function(v){return v.length >= {{value}};}}".replace("{{value}}", value.toString()));
			value = prop.getMaxItems();
			if(value != null)
				validations.add("{validator: function(v){return v.length <= {{value}};}}".replace("{{value}}", value.toString()));
			if(validations.size() > 0)
				schemaProps.add("validate: ["+String.join(",\n", validations)+"]");
		}
		else{
			schemaProps.add("type: " + type.toMongooseType());
			if(type == JSONType.STRING){
				Integer value = prop.getMinLength();
				if(value != null)
					schemaProps.add("minlength: " + value);
				value = prop.getMaxLength();
				if(value != null)
					schemaProps.add("maxlength: " + value);
				//TODO: add patterns, but they are a fight with BYACC too...
			}
			else if(type == JSONType.INTEGER || type == JSONType.NUMBER){
				if(type == JSONType.INTEGER)
					schemaProps.add("integer: true");
				Double value = prop.getMaximum();
				if (value != null) {
					Boolean ex = prop.getExMaximum();
					if(ex != null && ex)
						value--;
					schemaProps.add("max: " + value);
				}
				value = prop.getMinimum();
				if (value != null) {
					Boolean ex = prop.getExMinimum();
					if(ex != null && ex)
						value++;
					schemaProps.add("min: " + value);
				}
			}
			//TODO: add object restrictions, to inner classes
		}
		String schema = String.join(",", schemaProps);
		return schema;
	}

	public String toString(){
		return this.modelFile;
	}
}
