package autorest.generator;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class ModelBuilder {
	private PFSHandler pfsh;
	private String modelName;
	private String modelFile;
	private Boolean simpleKey;
	private List<String> primaryKeys;
	private String primaryKey;
	private List<String> required;
	private List<String> propertiesEndpoints;
	private Map<String, String> refResourcesSingle;
	private Map<String, String> refResourcesInArray;

	public ModelBuilder(String model_name, JSchRestriction resource, PFSHandler pfsh, Map<String, String> snippets) throws Exception{
		this.pfsh = pfsh;
		this.modelName = model_name;
		this.primaryKeys = new ArrayList<>();
		this.propertiesEndpoints = new ArrayList<>();
		this.refResourcesSingle = new HashMap<>();
		this.refResourcesInArray = new HashMap<>();
		this.required = resource.getRequired();
		if(this.required == null)
			this.required = new ArrayList<>();
		this.setPrimaryKeys(resource.getDependencies());
		String fields = buildFields(resource.getProperties());

		String id_virtual = "";
		if(this.simpleKey){
			id_virtual = snippets.get("id_virtual");
		}
		String propsH = "";
		if(!this.propertiesEndpoints.isEmpty() && this.simpleKey){
			for (String propName : propertiesEndpoints)
				propsH += snippets.get("prop_hyperlink").replace("{{prop_name}}", propName);
		}
		String refRes = "";
		String arraysItems = "";
		String arraysLinks = "";
		if(!this.refResourcesInArray.isEmpty()){
			for (String propName : this.refResourcesInArray.keySet()) {
				String hyperlinkItems = snippets.get("ref_resource_array_item");
				hyperlinkItems = hyperlinkItems.replace("{{prop_name}}", propName);
				hyperlinkItems = hyperlinkItems.replace("{{ref_resource_name}}", this.refResourcesInArray.get(propName));
				arraysItems += hyperlinkItems;
				if (this.simpleKey) {
					String hyperlink = snippets.get("prop_hyperlink");
					hyperlink = hyperlink.replace("{{prop_name}}", propName);
					propertiesEndpoints.add(propName);//will make the RouterBuilder create the endpoint method
					arraysLinks += hyperlink;
				}
			}
		}
		if(!this.refResourcesSingle.isEmpty()){
			for (String propName : this.refResourcesSingle.keySet()) {
				String hyperlink = snippets.get("ref_resource_prop");
				hyperlink = hyperlink.replace("{{prop_name}}", propName);
				hyperlink = hyperlink.replace("{{ref_resource_name}}", this.refResourcesSingle.get(propName));
				refRes += hyperlink;
			}
		}
		this.modelFile = snippets.get("model");
		this.modelFile = this.modelFile.replace("{{fields}}", fields);
		this.modelFile = this.modelFile.replace("{{id_virtual}}", id_virtual);
		this.modelFile = this.modelFile.replace("{{props_hyperlinks}}", propsH);
		this.modelFile = this.modelFile.replace("{{ref_resources_props}}", refRes);
		this.modelFile = this.modelFile.replace("{{ref_resources_array_items}}", arraysItems);
		this.modelFile = this.modelFile.replace("{{ref_resources_array_hyperlinks}}", arraysLinks);
		this.modelFile = this.modelFile.replace("{{id_field_name}}", this.primaryKey);
		this.modelFile = this.modelFile.replace("{{resource_name}}", this.modelName.toLowerCase());
		this.modelFile = this.modelFile.replace("{{model_name}}", this.modelName);
	}

	public String buildFields(Map<String, JSchRestriction> props) throws Exception{
		String fields = "";
		for (String name : props.keySet()) {
			String fieldName;
			Boolean isRequired = this.required.contains(name);
			if(this.simpleKey && name.equals(this.primaryKey))
				fieldName = "_id";
			else
				fieldName = name;
			JSchRestriction prop = props.get(name);
			if(prop.hasRefs() && prop.getRef() != null){
				String ref = prop.getRef();
				String refResource = this.pfsh.referencedResource(ref);
				JSchRestriction resource = this.pfsh.getResource(refResource);
				if(resource != null && resource.isSimplekey())
					this.refResourcesSingle.put(fieldName, refResource);
				prop = this.pfsh.dereference(ref).clone();
			}
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
					Boolean endpoint = true;
					if(sameItems.hasRefs() && sameItems.getRef() != null){
						endpoint = false;
						String ref = sameItems.getRef();
						String refResource = this.pfsh.referencedResource(ref);
						this.refResourcesInArray.put(fieldName, refResource);
						sameItems = this.pfsh.dereference(ref).clone();
					}
					if(!sameItems.hasRefs()){
						field += "type: ["+sameItems.getFirstType().toMongooseType()+"]";
						if(endpoint)
							this.propertiesEndpoints.add(fieldName);
					}
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

	public void setPrimaryKeys(Map<String, JSchDependence> deps) throws Exception{
		if(deps != null){
			for (String p : deps.keySet()) {
				List<String> kwords = deps.get(p).getKwords();
				if(kwords != null){
					for (String r : kwords) {
						if(!this.primaryKeys.contains(r))
							this.primaryKeys.add(r);
					}
				}
			}
		}
		if(this.primaryKeys.isEmpty())
			throw new Exception("A resource must have a primary key!");
		this.primaryKey = "";
		if(this.primaryKeys.size() == 1){
			this.primaryKey = this.primaryKeys.get(0);
			this.simpleKey = true;
		}
		else {
			this.simpleKey = false;
		}
	}

	public String getModelName(){
		return this.modelName;
	}

	public Boolean isSimplekey(){
		return this.simpleKey;
	}

	public List<String> getKeys(){
		return this.primaryKeys;
	}

	public String getKey(){
		return this.primaryKey;
	}

	public List<String> getRequired(){
		return this.required;
	}

	public List<String> getPropertiesEndpoints(){
		return this.propertiesEndpoints;
	}

	public String toString(){
		return this.modelFile;
	}
}
