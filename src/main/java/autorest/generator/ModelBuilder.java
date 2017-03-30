package autorest.generator;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class ModelBuilder {
	private String modelName;
	private String modelFile;
	private Boolean simpleKey;
	private List<String> primaryKeys;
	private String primaryKey;
	private List<String> required;

	public ModelBuilder(String model_name, JSchRestriction resource, Map<String, String> snippets){
		this.modelName = model_name;
		this.primaryKeys = new ArrayList<>();
		this.required = resource.getRequired();
		this.setPrimaryKeys(resource.getDependencies());
		String fields = "";
		Map<String, JSchRestriction> props = resource.getProperties();
		for (String name : props.keySet()) {
			String field = "";
			if(simpleKey && name.equals(this.primaryKey))
				field = "_id:{";
			else
				field = name + ":{";
			JSchRestriction prop = props.get(name);
			switch (prop.getFirstType()) {
				case STRING:
					field += "type: String";
					break;
				case INTEGER:
					field += "integer: true, ";
				case NUMBER:
					field += "type: Number";
					break;
				case BOOLEAN:
					field += "type: Boolean";
					break;
				case NULL:
					break;
				case ARRAY:
					// field += "type: [...]";
					break;
				case OBJECT:
					field += "type: Object";
					break;
				default:
					break;
			}
			field +="},\n";
			fields+=field;
		}
		String id_virtual = "";
		if(this.simpleKey){
			id_virtual = snippets.get("id_virtual").replace("{{prop_name}}", this.primaryKey);
		}
		this.modelFile = snippets.get("model");;
		this.modelFile = this.modelFile.replace("{{fields}}", fields.substring(0, fields.length()-2));
		this.modelFile = this.modelFile.replace("{{id_virtual}}", id_virtual);
		this.modelFile = this.modelFile.replace("{{model_name}}", this.modelName);
	}

	public void setPrimaryKeys(Map<String, JSchDependence> deps){
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

	public String toString(){
		return this.modelFile;
	}
}
