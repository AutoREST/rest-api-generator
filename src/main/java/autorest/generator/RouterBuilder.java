package autorest.generator;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class RouterBuilder {

	private Resource resource;
	private String resourceId = "";
	private String router, head_route, get_route, get_route_prop, post_route, put_route, patch_route, delete_route;
	private Map<String, String> snippets;
	private String queryBuilder;
	private String reqValBody;
	private String reqValQuery;
	private String allFieldsData;
	private String requiredFieldsData;
	private String notRequiredFieldsData;

	public RouterBuilder(Resource res, Map<String, String> snippets) throws Exception{
		this.resource = res;
		this.snippets = snippets;
		this.setSnippets();
		this.buildPropsBasedReplacements();

		post_route = post_route.replace("{{request_validation}}", reqValBody);
		post_route = post_route.replace("{{id_field_name}}", resourceId);
		post_route = post_route.replace("{{required_fields}}", requiredFieldsData);
		post_route = post_route.replace("{{not_required_fields}}", notRequiredFieldsData);

		put_route = put_route.replace("{{request_validation}}", reqValQuery);
		put_route = put_route.replace("{{id_field_name}}", resourceId);
		put_route = put_route.replace("{{required_fields}}", requiredFieldsData);
		put_route = put_route.replace("{{not_required_fields}}", notRequiredFieldsData);

		patch_route = patch_route.replace("{{request_validation}}", reqValQuery);
		patch_route = patch_route.replace("{{all_fields}}", allFieldsData);

		delete_route = delete_route.replace("{{request_validation}}", reqValQuery);

		head_route = head_route.replace("{{request_validation}}", reqValQuery);

		router = router.replace("{{head_route}}", head_route);
		router = router.replace("{{get_route}}", get_route);
		router = router.replace("{{get_route_props}}", buildPropertiesEndpoints());
		router = router.replace("{{post_route}}", post_route);
		router = router.replace("{{put_route}}", put_route);
		router = router.replace("{{patch_route}}", patch_route);
		router = router.replace("{{delete_route}}", delete_route);

		router = router.replace("{{query_building}}", queryBuilder);
		router = router.replace("{{identifier_check}}", "");
		if(this.resource.hasParent())
			router = router.replace("{{query_by_type}}", "query.type='"+this.resource.getName()+"';");
		else
			router = router.replace("{{query_by_type}}", "");
		router = router.replace("{{resource_name}}", this.resource.getName());
		router = router.replace("{{model_name}}", this.resource.getModelName());
	}

	public void setSnippets(){
		if(this.resource.isSimpleKey()){
			if(this.resource.getPrimaryKey() != null)
				this.resourceId = this.resource.getPrimaryKey();
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

	private void buildPropsBasedReplacements(){
		Map<String, JSchRestriction> props = this.resource.getProperties();
		StringBuilder queryBuilderSB = new StringBuilder();
		StringBuilder reqValBodySB = new StringBuilder();
		StringBuilder reqValQuerySB = new StringBuilder();
		StringBuilder allFieldsDataSB = new StringBuilder();
		StringBuilder requiredFieldsDataSB = new StringBuilder();
		StringBuilder notRequiredFieldsDataSB = new StringBuilder();

		List<String> required = this.resource.getRequired();

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

		for (String key : this.resource.getPrimaryKeys()) {
			reqValBodySB.append(this.snippets.get("req_val_body").replace("{{prop_name}}", key));
			reqValQuerySB.append(this.snippets.get("req_val_query").replace("{{prop_name}}", key));
		}

		this.queryBuilder = queryBuilderSB.toString();
		this.reqValBody = reqValBodySB.toString();
		this.reqValQuery = reqValQuerySB.toString();
		this.allFieldsData = allFieldsDataSB.toString();
		this.requiredFieldsData = requiredFieldsDataSB.toString();
		if(!this.resource.isSimpleKey())
			this.requiredFieldsData = this.requiredFieldsData.substring(2);//no _id, then no need of the leading ",\n"
		this.notRequiredFieldsData = notRequiredFieldsDataSB.toString();
	}

	public String buildPropertiesEndpoints() throws Exception{
		StringBuilder props = new StringBuilder();
		for (String propName : this.resource.getPropertiesEndpoints())
			props.append(this.get_route_prop.replace("{{prop_name}}", propName)+"\n");
		return props.toString();
	}

	public String toString(){
		return this.router;
	}
}
