package autorest.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.stream.Stream;
import java.util.stream.Collectors;

public class Resource {

	private String name;
	private String collectionName;
	private JSchRestriction restrictions;
	private Map<String, JSchRestriction> properties;
	private List<String> required;
	private Map<String, JSchDependence> dependencies;
	private List<String> primaryKeys;
	private String primaryKey;
	private String parentResource;
	private String inheritanceProp;
	private List<String> navegableProps;
	private List<String> arrayProps;
	private Map<String, Reference> references;
	private List<Reference> referencedBy;
	private List<Resource> specializations;

	public Resource(String name, JSchRestriction restrictions, PFSHandler pfsh) throws Exception{
		this.parentResource = null;
		this.name = name.toLowerCase();
		this.collectionName = this.name;
		this.restrictions = restrictions;
		this.properties = this.restrictions.getProperties();
		this.required = this.restrictions.getRequired();
		this.dependencies = this.restrictions.getDependencies();
		this.primaryKeys = new ArrayList<>();
		this.navegableProps = new ArrayList<>();
		this.arrayProps = new ArrayList<>();
		this.references = new HashMap<>();
		this.referencedBy = new ArrayList<>();
		this.specializations = new ArrayList<>();
		//to avoid NPEs
		if(this.properties == null)
			this.properties = new HashMap<>();
		if(this.required == null)
			this.required = new ArrayList<>();
		if(this.dependencies == null)
			this.dependencies = new HashMap<>();

		this.defineReferences(pfsh);
		if(this.parentResource == null)
			this.setPrimaryKeys();
	}

	public Boolean isSimpleKey(){
		return this.primaryKeys.size() == 1;
	}

	public String getName() {
		return this.name;
	}

	public String getModelName() {
		return Character.toUpperCase(this.name.charAt(0)) + this.name.substring(1);
	}

	private void setPrimaryKeys() throws Exception{
		if(dependencies != null && !dependencies.isEmpty()){
			for (String p : dependencies.keySet()) {
				List<String> kwords = dependencies.get(p).getKwords();
				if(kwords != null){
					for (String r : kwords) {
						if(!this.primaryKeys.contains(r))
							this.primaryKeys.add(r);
					}
				}
			}
		}
		else
			for (String p : properties.keySet())
				if(!this.primaryKeys.contains(p))
					this.primaryKeys.add(p);

		if(this.primaryKeys.isEmpty())
			throw new Exception("A resource must have a primary key!");
		this.primaryKey = "";
		if(this.primaryKeys.size() == 1){
			this.primaryKey = this.primaryKeys.get(0);
		}
	}

	public List<String> getPrimaryKeys(){
		return this.primaryKeys;
	}

	public String getPrimaryKey(){
		return this.primaryKeys.get(0);
	}

	private void defineReferences(PFSHandler pfsh) throws Exception{
		Pattern regexReference = Pattern.compile("#/definitions/(?<DEF>\\w+)(?<PROPS>/properties/(?<PROP>\\w+))?");

		for (String name : this.properties.keySet()) {
			JSchRestriction prop = this.properties.get(name);
			if(prop.hasRefs() && prop.getRef() != null){
				Reference refObj = getReference(prop.getRef());
				Boolean inheritance = refObj.getResourceName() != null && refObj.getPropertyName() == null;
				if(!inheritance){
					this.references.put(name, refObj);
					prop = pfsh.getDefinition(refObj.getResourceName());
					if(prop == null)
						throw new Exception("["+this.name+"] Referenced resource ("+refObj.getResourceName()+") not found.");
				}
				else{
					if(this.parentResource != null)
						throw new Exception("There can be only one parent resource.");
					this.parentResource = refObj.getResourceName();
					this.inheritanceProp = name;
				}
			}
			if(prop.getFirstType() == JSONType.ARRAY){
				JSchRestriction sameItems = prop.getSameItems();
				if(sameItems != null){
					if(sameItems.hasRefs() && sameItems.getRef() != null){
						Reference refObj = getReference(sameItems.getRef());
						Boolean inheritance = refObj.getResourceName() != null && refObj.getPropertyName() == null;
						if(!inheritance){
							this.references.put(name, refObj);
						}
						else{
							throw new Exception("Inheritance is invalid inside an array ("+this.name+"/"+name+").");
						}
					}
					this.arrayProps.add(name);
				}
			}
			if(prop.getFirstType() == JSONType.OBJECT
				&& !this.references.keySet().contains(name)
				&& !this.inheritanceProp.equals(name)){
				this.navegableProps.add(name);
			}
		}
	}

	private Reference getReference(String ref) throws Exception{
		Pattern regexReference = Pattern.compile("#/definitions/(?<DEF>\\w+)(?<PROPS>/properties/(?<PROP>\\w+))?");
		String refResource = null;
		String refProp = null;
		Matcher m = regexReference.matcher(ref);
		Boolean matches = m.matches();
		if(matches){
			if(m.group("DEF").length() > 0)
				refResource = m.group("DEF");
			if(m.group("PROPS") != null && m.group("PROPS").length() > 0)
				refProp = m.group("PROP");
		}
		else {
			throw new Exception("Not an acceptable JPointer ("+ref+")");
		}
		return new Reference(ref, refResource, refProp);
	}

	public void addParentProperties(Resource parent) throws Exception{
		this.collectionName = parent.getCollectionName();
		Map<String, JSchRestriction> parentProps = parent.getProperties();

		this.properties.remove(this.inheritanceProp);
		for (String name : parentProps.keySet()) {
			this.properties.put(name, parentProps.get(name));
		}
		this.required = Stream.concat(this.required.stream(),
									  parent.getRequired().stream())
									  .collect(Collectors.toList());

		Map<String, JSchDependence> parentDeps = parent.getDependencies();
		for (String key : parentDeps.keySet()) {
			JSchDependence parentDep = parentDeps.get(key);
			if(this.dependencies.keySet().contains(key)){
				this.dependencies.get(key).mergeKwords(parentDep);
			}
			else {
				this.dependencies.put(key, parentDep);
			}
		}
		this.setPrimaryKeys();

		parent.addSpecialization(this);
	}

	public String getCollectionName(){
		return this.collectionName;
	}

	public Map<String, JSchRestriction> getProperties(){
		return this.properties;
	}

	public void replaceProperty(String name, JSchRestriction newValue){
		if(this.arrayProps.contains(name))
			this.properties.get(name).setSameItems(newValue);
		else
			this.properties.put(name, newValue);
	}

	public List<String> getRequired(){
		return this.required;
	}

	public Map<String, JSchDependence> getDependencies(){
		return this.dependencies;
	}

	public List<String> getNavegableProperties(){
		return this.navegableProps;
	}

	public List<String> getArrayProps(){
		return this.arrayProps;
	}

	public List<String> getPropertiesEndpoints() throws Exception{
		return Stream.concat(this.arrayProps.stream(),
							 this.navegableProps.stream())
							 .collect(Collectors.toList());
	}

	public Map<String, Reference> getReferences(){
		return this.references;
	}

	public void addRefferer(Reference refferer){
		this.referencedBy.add(refferer);
	}

	public List<Reference> getReferencedBy(){
		return this.referencedBy;
	}

	public void addSpecialization(Resource specialization){
		this.specializations.add(specialization);
	}

	public List<Resource> getSpecializations(){
		return this.specializations;
	}

	public Boolean hasSpecializations(){
		return this.specializations.size() > 0;
	}

	public Boolean hasParent(){
		return this.parentResource != null;
	}

	public String getParentName(){
		return this.parentResource;
	}

	public Boolean hasPendingRefs(){
		for (String name : this.properties.keySet()) {
			JSchRestriction prop = this.properties.get(name);
			if(prop.hasRefs())
				return true;
		}
		return false;
	}
}
