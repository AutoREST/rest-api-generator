package autorest.generator;

public class Reference {
	private String jpointer;
	private String resourceName;
	private String propName;
	private Resource resource;

	public Reference(String jpointer, String resourceName){
		this.jpointer = jpointer;
		this.resourceName = resourceName;
	}

	public Reference(String jpointer, String resourceName, String propName){
		this(jpointer, resourceName);
		this.propName = propName;
	}

	public Reference(Resource res, String propName){
		this.resource = res;
		this.propName = propName;
	}

	public void setJPointer(String value) {
		this.jpointer = value;
	}

	public String getJPointer() {
		return this.jpointer;
	}

	public void setResourceName(String value) {
		this.resourceName = value;
	}

	public String getResourceName() {
		return this.resourceName;
	}

	public void setPropertyName(String value) {
		this.propName = value;
	}

	public String getPropertyName() {
		return this.propName;
	}
}
