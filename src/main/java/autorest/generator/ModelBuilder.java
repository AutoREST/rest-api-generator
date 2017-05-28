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
		this.primaryKey = "";
		if(this.resource.isSimpleKey()){
			id_virtual = snippets.get("id_virtual");
			this.primaryKey = this.resource.getPrimaryKey();
		}
		String fields = buildFields(this.resource.getProperties());

		String propsH = "";
		List<String> navProps = this.resource.getNavegableProperties();
		if(!navProps.isEmpty() && this.resource.isSimpleKey()){
			for (String propName : navProps)
				propsH += snippets.get("prop_hyperlink").replace("{{prop_name}}", propName);
		}
		String deleteType = "";
		String typeField = "";
		Boolean generic = this.resource.hasSpecializations();
		Boolean specialization = this.resource.hasParent();
		if(generic || specialization)
			deleteType = "delete doc.type";
		if(specialization)
			typeField = (fields.length()>0 ? "," : "")+"type: {type: String, default: '"+this.resource.getName()+"'}";
		String refRes = "";
		String arraysItems = "";
		String arraysLinks = "";
		List<String> arrayProps = this.resource.getArrayProps();
		Map<String, Reference> references = this.resource.getReferences();
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
		String fields = "";
		for (String name : props.keySet()) {
			String fieldName;
			Boolean isRequired = this.resource.getRequired().contains(name);
			if(this.resource.isSimpleKey() && name.equals(this.primaryKey))
				fieldName = "_id";
			else
				fieldName = name;
			JSchRestriction prop = props.get(name);
			fields += buildField(prop, fieldName, isRequired) + ",\n";
		}
		if(fields.length()>0)
			fields = fields.substring(0, fields.length()-2);
		return fields;
	}

	public String buildField(JSchRestriction prop, String fieldName, Boolean isRequired) throws Exception{
		String field = "";
		field = fieldName + ":{";
		switch (prop.getFirstType()) {
			case ARRAY:
				JSchRestriction sameItems = prop.getSameItems();
				if(sameItems != null){
					field += "type: ["+sameItems.getFirstType().toMongooseType()+"]";
				}
				break;
			case INTEGER:
				field += "integer: true, ";
			default:
				field += "type: " + prop.getFirstType().toMongooseType();
				break;
		}
		if(isRequired)
			field += ", required: true";

		field += "}";
		return field;
	}

	public String toString(){
		return this.modelFile;
	}
}
