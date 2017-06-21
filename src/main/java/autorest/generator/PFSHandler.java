package autorest.generator;
import java.util.List;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Map;
import java.util.HashMap;
import autorest.util.DeepCopy;

public class PFSHandler {
	private Map<String, JSchRestriction> defs;
	private JSchRestriction mJSch;
	private Map<String, Resource> resources;

	private Boolean resolvableRefs;
	private String resolveMessage;

	public PFSHandler(Map<String, JSchRestriction> definitions, JSchRestriction mainJSchema) throws Exception{
		this.resources = new HashMap<>();
		if(definitions != null)
			this.defs = definitions;
		else
			this.defs = new HashMap<>();
		if(mainJSchema != null)
			this.mJSch = mainJSchema;
		else
			this.mJSch = new JSchRestriction();
		this.resolvableRefs = false;
		this.resolveMessage = "Didn't tried to resolve.";
	}

	public void initializeResources() throws Exception{
		this.loadResources();
		this.resolveResourcesReferences();
	}

	public Map<String, JSchRestriction> getDefinitions(){
		return this.defs;
	}

	public JSchRestriction getMainJSchema(){
		return this.mJSch;
	}

	public String getResolveMessage(){
		return this.resolveMessage;
	}

	public Map<String, Resource> getResources(){
		return this.resources;
	}

	private void loadResources() throws Exception{
		for (String name : this.defs.keySet()) {
			try {
				Resource res = new Resource(name, this.defs.get(name).clone(), this);
				this.resources.put(name, res);
			}
			catch (Exception e) {
				throw new Exception("Exception while loading [" + name + "] in the PFSHandler processor. ", e);
			}
		}
	}

	private void resolveResourcesReferences() throws Exception{
		for (String name : this.resources.keySet()) {
			Resource res = this.resources.get(name);
			if(res.hasParent()){
				// get all parent properties and add them to the child
				Resource parent = this.resources.get(res.getParentName());
				res.addParentProperties(parent);
			}
			// replace all properties, that are references, by the corresponding JSch
			Map<String, Reference> references = res.getReferences();
			for (String propName : references.keySet()) {
				Reference ref = references.get(propName);
				if(ref.getPropertyName() != null && ref.getResourceName() != null){
					Resource refResource = this.resources.get(ref.getResourceName());
					JSchRestriction newPropJSch = refResource.getProperties().get(ref.getPropertyName());
					res.replaceProperty(propName, newPropJSch);
					refResource.addRefferer(new Reference(res, propName));
				}
			}
			// ensure all references have been addressed
			if(res.hasPendingRefs())
				throw new Exception("The resource "+name+" has unresolved/invalid references.");
		}
	}

	public Boolean canResolveRefs(){
		this.resolvableRefs = false;
		try {
			this.resolvableRefs = PFSHandler.canResolveRefs(this.defs, this.mJSch);
			this.resolveMessage = "JSON Schema references are valid.";
		}
		catch (Exception ex) {
			this.resolvableRefs = false;
			this.resolveMessage = "Error validating JSON Schema references. ["+ex.getMessage()+"]";
		}
		return this.resolvableRefs;
	}

	public static Boolean canResolveRefs(Map<String, JSchRestriction> definitions, JSchRestriction mainJSchema) throws Exception{
		Map<String, JSchRestriction> cpDefinitions = (Map<String, JSchRestriction>)DeepCopy.copy(definitions);
		JSchRestriction cpMainJSchema = mainJSchema.clone();
		PFSHandler pfsHandler = new PFSHandler(cpDefinitions, cpMainJSchema);
		return pfsHandler.resolveReferences();
	}

	private Boolean resolveReferences() throws Exception{
		this.mJSch = this.parseRefs(this.mJSch);
		for (String key : this.defs.keySet()) {
			JSchRestriction jsch = this.defs.get(key);
			if(jsch.hasRefs()){
				jsch = this.parseRefs(jsch);
				this.defs.put(key, jsch);
				if(jsch.hasRefs()){
					return false;
				}
			}
		}
		if(this.mJSch.hasRefs()){
			return false;
		}
		return true;
	}

	private JSchRestriction parseRefs(JSchRestriction jsch) throws Exception{
		if(jsch.getRef() != null){
			JSchRestriction reffered = this.dereference(jsch.getRef());
			return reffered;
		}
		if(jsch.getSameItems() != null){
			JSchRestriction sameItems = jsch.getSameItems();
			if(sameItems.hasRefs()){
				jsch.setSameItems(parseRefs(sameItems));
			}
		}
		// if(this.variItems != null)
		//	 for (JSchRestriction jsch : this.variItems)
		//		 if(jsch.hasRefs())
		//			 return true;
		// if(this.addItemsJSch != null && this.addItemsJSch.hasRefs())
		//	 return true;
		if(jsch.getProperties() != null){
			Map<String, JSchRestriction> properties = jsch.getProperties();
			for (String key : properties.keySet()){
				JSchRestriction pSch = properties.get(key);
				if(pSch.hasRefs()){
					pSch = parseRefs(pSch);
					properties.put(key, pSch);
				}
			}
		}
		// if(this.addPropertiesJSch != null && this.addPropertiesJSch.hasRefs())
		//	 return true;
		// if(this.dependencies != null)
		//	 for (String key : this.dependencies.keySet())
		//		 if(this.dependencies.get(key).hasRefs())
		//			 return true;
		// if(this.patterProperties != null)
		//	 for (String key : this.patterProperties.keySet())
		//		 if(this.patterProperties.get(key).hasRefs())
		//			 return true;
		// if(this.anyOf != null)
		//	 for (JSchRestriction jsch : this.anyOf)
		//		 if(jsch.hasRefs())
		//			 return true;
		// if(this.allOf != null)
		//	 for (JSchRestriction jsch : this.allOf)
		//		 if(jsch.hasRefs())
		//			 return true;
		// if(this.oneOf != null)
		//	 for (JSchRestriction jsch : this.oneOf)
		//		 if(jsch.hasRefs())
		//			 return true;
		// if(this.not != null && this.not.hasRefs())
		//	 return true;
		// if(this.enumValues != null)
		//	 for (Object obj : this.enumValues)
		//		 if(obj instanceof JSchRestriction)
		//			 if(((JSchRestriction)obj).hasRefs())
		//				 return true;
		// return false;
		if(jsch.hasRefs()){
			//System.out.println("jsch: " + jsch);
			//throw new Exception("Unable to parse reference to JSON Schema.");
		}
		return jsch;
	}

	public String referencedResource(String jpointer){
		Stack<String> keys = splitInStack(jpointer, "/");
		String resource = null;
		while (!keys.empty() && resource == null) {
			String key = keys.pop();
			if(key.equals("definitions"))
				resource = keys.pop();
		}
		return resource;
	}

	public JSchRestriction getDefinition(String resource){
		if(resource != null && this.defs.keySet().contains(resource))
			return this.defs.get(resource);
		return null;
	}

	public JSchRestriction dereference(String jpointer) throws Exception{
		return this.dereference(this.defs, this.mJSch, jpointer);
	}

	public static JSchRestriction dereference(Map<String, JSchRestriction> usedDefs, JSchRestriction usedMainJSch, String jpointer) throws Exception{
		int indexSep = jpointer.indexOf("#");
		String schemaFile = null;
		if(indexSep > 0)
			schemaFile = jpointer.substring(0,indexSep);
		else if(indexSep < 0)
			throw new Exception("Invalid JSON Pointer");
		if(schemaFile != null){
			//TODO: search locally or download the file
		}
		String reference = jpointer.substring(indexSep+1);
		if(reference != null) {
			Stack<String> keys = splitInStack(reference, "/");
			if(keys.empty())
				return usedMainJSch;
			String key = keys.pop();
			if(key.equals("definitions")){
				key = keys.pop();
				if(usedDefs.keySet().contains(key)){
					return getSchemaFromPath(usedDefs.get(key), keys);
				}
			}
			else{
				return getSchemaFromPath(usedMainJSch, keys);
			}
		}

		throw new Exception("JSON Schema for JSON Pointer " + jpointer + " not found.");
	}

	private static Stack<String> splitInStack(String text, String separator){
		Stack<String> strStck = new Stack<>();
		String[] values = text.split(separator);
		for (int i = values.length-1; i >= 0; i--)
			if(!values[i].trim().isEmpty())
				strStck.push(values[i].trim());
		return strStck;
	}

	private static JSchRestriction getSchemaFromPath(JSchRestriction base, Stack<String> remainingPath) throws Exception{
		if(!remainingPath.empty()){
			String key = remainingPath.pop();
			switch (key) {
				case "properties":
					key = remainingPath.pop();
					Map<String, JSchRestriction> props = base.getProperties();
					if(props != null){
						if(props.keySet().contains(key)){
							return getSchemaFromPath(props.get(key), remainingPath);
						}
					}
					break;
				default:
					throw new Exception("Unexpected key: " + key);
			}
		}
		else{
			return base;
		}
		throw new Exception("Remaining Path of JPointer not found.");
	}
}
