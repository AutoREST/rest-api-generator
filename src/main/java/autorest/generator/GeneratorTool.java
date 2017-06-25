package autorest.generator;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.IOUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GeneratorTool {
	private static String encoding = "UTF-8";
	private Map<String, String> snippets;

	public static void main(String args[]){
		try{
			if ( args.length > 0 ) {
				String fileName = args[0];
				Boolean verbose = false;
				String optionsJSON = "{}";
				if(args.length > 1)
					optionsJSON = args[1];

				GeneratorTool generator = new GeneratorTool(fileName, optionsJSON);
			}
			else {
				System.out.println("\n\tRequired input file path\n");
			}
		}
		catch (Exception e) {
			System.out.println("ERROR: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public GeneratorTool(String fileName, String optionsJSON) throws Exception{
		this(new FileReader(fileName), optionsJSON);
	}

	public GeneratorTool(FileReader file, String optionsJSON) throws Exception{
		ObjectMapper mapper = new ObjectMapper(); // create once, reuse
		Options options = mapper.readValue(optionsJSON, Options.class);
		PFISCompiler yyparser = new PFISCompiler(file);
		yyparser.setVerbose(options.Verbose);
		Boolean parsed = yyparser.parse();

		if (parsed) {
			PFSHandler pfsh = yyparser.getPFSHandler();
			if(pfsh != null){
				//TODO: HTTP Method Stub Library... work on that
				this.loadSnippets();
				pfsh.initializeResources();
				StringBuilder routes = new StringBuilder();
				StringBuilder routers_requires = new StringBuilder();
				Map<String, ModelBuilder> models = new HashMap<>();
				Map<String, RouterBuilder> routers = new HashMap<>();
				Map<String, JSchRestriction> definitions = pfsh.getDefinitions();
				JSchRestriction mainJSchema = pfsh.getMainJSchema();

				Map<String, Resource> resources = pfsh.getResources();
				for (String resourceName : resources.keySet()) {
					Resource res = resources.get(resourceName);
					ModelBuilder model = new ModelBuilder(res, this.snippets);
					resourceName = res.getName();
					models.put(resourceName, model);
					routers.put(resourceName, new RouterBuilder(res, this.snippets));

					routes.append(this.snippets.get("routes").replace("{{resource_name}}", resourceName));
					routers_requires.append(this.snippets.get("routers_requires").replace("{{resource_name}}", resourceName));
				}

				String apiJs = this.snippets.get("api.js");
				apiJs = apiJs.replace("{{routers_requires}}", routers_requires.toString());
				apiJs = apiJs.replace("{{routes}}", routes.toString());
				apiJs = apiJs.replace("{{api_database}}", options.DataBaseName);
				//TODO: generate console.log of the GET endpoints of each resource
				String packageJson = this.snippets.get("package.json");
				packageJson = packageJson.replace("{{api_repo_url}}", options.APIRepoURL);

				String savedAPI = this.saveApi(options.APIName, packageJson, apiJs, models, routers);
				System.out.println("API saved in: " + savedAPI);
			}
		}
	}

	private String getResourseAsString(String resource) throws IOException{
		InputStream input = GeneratorTool.class.getResourceAsStream(resource);
		return IOUtils.toString(input, encoding);
	}

	private static String capitalize(final String line) {
	   return Character.toUpperCase(line.charAt(0)) + line.substring(1);
	}

	private void loadSnippets()  throws IOException{
		this.snippets = new HashMap<>();
		this.snippets.put("package.json", getResourseAsString("/snippets/nodejs/package.json"));
		this.snippets.put("api.js", getResourseAsString("/snippets/nodejs/api.js"));
		this.snippets.put("routers_requires", getResourseAsString("/snippets/nodejs/lines/routers_requires"));
		this.snippets.put("routes", getResourseAsString("/snippets/nodejs/lines/routes"));
		this.snippets.put("router", getResourseAsString("/snippets/nodejs/routers/router.js"));
		this.snippets.put("model", getResourseAsString("/snippets/nodejs/models/model.js"));
		this.snippets.put("get_route_simple", getResourseAsString("/snippets/nodejs/routers/routes/get_route_simple"));
		this.snippets.put("get_route_multi", getResourseAsString("/snippets/nodejs/routers/routes/get_route_multi"));
		this.snippets.put("head_route_simple", getResourseAsString("/snippets/nodejs/routers/routes/head_route_simple"));
		this.snippets.put("head_route_multi", getResourseAsString("/snippets/nodejs/routers/routes/head_route_multi"));
		this.snippets.put("post_route_simple", getResourseAsString("/snippets/nodejs/routers/routes/post_route_simple"));
		this.snippets.put("post_route_multi", getResourseAsString("/snippets/nodejs/routers/routes/post_route_multi"));
		this.snippets.put("put_route_simple", getResourseAsString("/snippets/nodejs/routers/routes/put_route_simple"));
		this.snippets.put("put_route_multi", getResourseAsString("/snippets/nodejs/routers/routes/put_route_multi"));
		this.snippets.put("patch_route_simple", getResourseAsString("/snippets/nodejs/routers/routes/patch_route_simple"));
		this.snippets.put("patch_route_multi", getResourseAsString("/snippets/nodejs/routers/routes/patch_route_multi"));
		this.snippets.put("delete_route_simple", getResourseAsString("/snippets/nodejs/routers/routes/delete_route_simple"));
		this.snippets.put("delete_route_multi", getResourseAsString("/snippets/nodejs/routers/routes/delete_route_multi"));
		this.snippets.put("head_route_simple_prop", getResourseAsString("/snippets/nodejs/routers/routes/head_route_simple_prop"));
		this.snippets.put("get_route_simple_prop", getResourseAsString("/snippets/nodejs/routers/routes/get_route_simple_prop"));
		this.snippets.put("put_route_simple_prop", getResourseAsString("/snippets/nodejs/routers/routes/put_route_simple_prop"));
		this.snippets.put("patch_route_simple_prop", getResourseAsString("/snippets/nodejs/routers/routes/patch_route_simple_prop"));
		this.snippets.put("delete_route_simple_prop", getResourseAsString("/snippets/nodejs/routers/routes/delete_route_simple_prop"));
		this.snippets.put("add_prop_query", getResourseAsString("/snippets/nodejs/lines/add_prop_query"));
		this.snippets.put("add_prop_data", getResourseAsString("/snippets/nodejs/lines/add_prop_data"));
		this.snippets.put("req_val_body", getResourseAsString("/snippets/nodejs/lines/request_validation_prop_body"));
		this.snippets.put("req_val_query", getResourseAsString("/snippets/nodejs/lines/request_validation_prop_query"));
		this.snippets.put("id_virtual", getResourseAsString("/snippets/nodejs/models/id_virtual"));
		this.snippets.put("prop_hyperlink", getResourseAsString("/snippets/nodejs/models/prop_hyperlink"));
		this.snippets.put("ref_resource_prop", getResourseAsString("/snippets/nodejs/models/ref_resource_prop"));
		this.snippets.put("ref_resource_array_item", getResourseAsString("/snippets/nodejs/models/ref_resource_array_item"));
	}

	private String saveApi(String apiName, String packageJson,String apiJs, Map<String, ModelBuilder> models, Map<String, RouterBuilder> routers) throws IOException{
		Path currentRelativePath = Paths.get("");
		String absotultePath = currentRelativePath.toAbsolutePath().toString();
		String zipFile = Paths.get(absotultePath, apiName + ".zip").toAbsolutePath().toString();

		FileOutputStream fos = new FileOutputStream(zipFile);
		ZipOutputStream zos = new ZipOutputStream(fos);

		ZipEntry entry = new ZipEntry("package.json");
		zos.putNextEntry(entry);
		zos.write(packageJson.getBytes());
		zos.closeEntry();

		entry = new ZipEntry("api.js");
		zos.putNextEntry(entry);
		zos.write(apiJs.getBytes());
		zos.closeEntry();

		entry = new ZipEntry("models/");
		zos.putNextEntry(entry);
		for (String modelName : models.keySet()) {
			ZipEntry modelEntry = new ZipEntry("models/"+modelName.toLowerCase()+".js");
			zos.putNextEntry(modelEntry);
			zos.write(models.get(modelName).toString().getBytes());
			zos.closeEntry();
		}

		entry = new ZipEntry("routers/");
		zos.putNextEntry(entry);
		for (String routerName : routers.keySet()) {
			ZipEntry routerEntry = new ZipEntry("routers/"+routerName.toLowerCase()+".js");
			zos.putNextEntry(routerEntry);
			zos.write(routers.get(routerName).toString().getBytes());
			zos.closeEntry();
		}
		zos.close();

		return zipFile;
	}
}
