package autorest.generator;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class RouterBuilder {

	private String resourceId = "";
	private String router, head_route, get_route, get_route_prop, post_route, put_route, patch_route, delete_route;
	private Map<String, String> snippets;
	private String queryBuilder;
	private String allFieldsData;
	private String requiredFieldsData;
	private String notRequiredFieldsData;

	public RouterBuilder(String resourceName, ModelBuilder model, JSchRestriction resource, Map<String, String> snippets){
		List<String> propertiesEndpoints = model.getPropertiesEndpoints();
		this.snippets = snippets;
		this.setSnippets(model);
		this.buildPropsBasedReplacements(model, resource.getProperties());

		post_route = post_route.replace("{{id_field_name}}", resourceId);
		post_route = post_route.replace("{{required_fields}}", requiredFieldsData);
		post_route = post_route.replace("{{not_required_fields}}", notRequiredFieldsData);

		put_route = put_route.replace("{{id_field_name}}", resourceId);
		put_route = put_route.replace("{{required_fields}}", requiredFieldsData);
		put_route = put_route.replace("{{not_required_fields}}", notRequiredFieldsData);

		patch_route = patch_route.replace("{{all_fields}}", allFieldsData);

		router = router.replace("{{head_route}}", head_route);
		router = router.replace("{{get_route}}", get_route);
		router = router.replace("{{get_route_props}}", buildPropertiesEndpoints(propertiesEndpoints));
		router = router.replace("{{post_route}}", post_route);
		router = router.replace("{{put_route}}", put_route);
		router = router.replace("{{patch_route}}", patch_route);
		router = router.replace("{{delete_route}}", delete_route);

		router = router.replace("{{query_building}}", queryBuilder);
		router = router.replace("{{identifier_check}}", "");
		router = router.replace("{{resource_name}}", resourceName);
		router = router.replace("{{model_name}}", model.getModelName());
	}

	public void setSnippets(ModelBuilder model){
		if(model.isSimplekey()){
			if(model.getKey() != null)
				this.resourceId = model.getKey();
			this.head_route = this.snippets.get("head_route_simple");
			this.get_route = this.snippets.get("get_route_simple");
			this.get_route_prop = this.snippets.get("get_route_simple_prop");
			this.post_route = this.snippets.get("post_route_simple");
			this.put_route = this.snippets.get("put_route_simple");
			this.patch_route = this.snippets.get("patch_route_simple");
			this.delete_route = this.snippets.get("delete_route_simple");
		}
		else {
			this.head_route = this.snippets.get("head_route_multi");
			this.get_route = this.snippets.get("get_route_multi");
			this.post_route = this.snippets.get("post_route_multi");
			this.put_route = this.snippets.get("put_route_multi");
			this.patch_route = this.snippets.get("patch_route_multi");
			this.delete_route = this.snippets.get("delete_route_multi");
		}

		router = this.snippets.get("router");
	}

	private void buildPropsBasedReplacements(ModelBuilder model, Map<String, JSchRestriction> props){
		StringBuilder queryBuilderSB = new StringBuilder();
		StringBuilder allFieldsDataSB = new StringBuilder();
		StringBuilder requiredFieldsDataSB = new StringBuilder();
		StringBuilder notRequiredFieldsDataSB = new StringBuilder();

		List<String> required = model.getRequired();

		for (String propName : props.keySet()) {
			if(!propName.equals(resourceId)){
				queryBuilderSB.append(this.snippets.get("add_prop_query").replace("{{prop_name}}", propName));
				allFieldsDataSB.append(this.snippets.get("add_prop_data").replace("{{prop_name}}", propName));
				if(required.contains(propName))
					requiredFieldsDataSB.append((",\n{{prop_name}}: req.body.{{prop_name}}").replace("{{prop_name}}", propName));
				else
					notRequiredFieldsDataSB.append(this.snippets.get("add_prop_data").replace("{{prop_name}}", propName));
			}
		}

		this.queryBuilder = queryBuilderSB.toString();
		this.allFieldsData = allFieldsDataSB.toString();
		this.requiredFieldsData = requiredFieldsDataSB.toString();
		this.notRequiredFieldsData = notRequiredFieldsDataSB.toString();
	}

	public String buildPropertiesEndpoints(List<String> propertiesEndpoints){
		StringBuilder props = new StringBuilder();
		for (String propName : propertiesEndpoints)
			props.append(this.get_route_prop.replace("{{prop_name}}", propName)+"\n");
		return props.toString();
	}

	public String toString(){
		return this.router;
	}
}
