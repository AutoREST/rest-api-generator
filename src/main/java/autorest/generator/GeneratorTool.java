package autorest.generator;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.IOUtils;

public class GeneratorTool {
	private static String encoding = "UTF-8";
	private Map<String, String> snippets;

	public static void main(String args[]){
		try{
			if ( args.length > 0 ) {
				String fileName = args[0];
				Boolean verbose = false;
				if(args.length > 1)
					for(int i=1; i < args.length; i++)
						if(args[i].equals("-v"))
							verbose = true;

				GeneratorTool generator = new GeneratorTool(fileName, verbose);
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

	public GeneratorTool(String fileName, Boolean verbose) throws Exception{
		JSchParser yyparser = new JSchParser(fileName);
		yyparser.setVerbose(verbose);
		Boolean parsed = yyparser.parse();

		if (parsed) {
			PFSHandler pfsh = yyparser.getPFSHandler();
			if(pfsh != null){
				this.loadSnippets();
				StringBuilder routes = new StringBuilder();
				StringBuilder routers_requires = new StringBuilder();
				Map<String, ModelBuilder> models = new HashMap<>();
				Map<String, RouterBuilder> routers = new HashMap<>();
				Map<String, JSchRestriction> definitions = pfsh.getDefinitions();
				JSchRestriction mainJSchema = pfsh.getMainJSchema();

				for (String resourceName : definitions.keySet()) {
					JSchRestriction resource = definitions.get(resourceName);
					ModelBuilder model = new ModelBuilder(capitalize(resourceName), resource, pfsh, this.snippets);
					models.put(resourceName, model);
					routers.put(resourceName, new RouterBuilder(resourceName, model, resource, this.snippets));

					routes.append(this.snippets.get("routes").replace("{{resource_name}}", resourceName));
					routers_requires.append(this.snippets.get("routers_requires").replace("{{resource_name}}", resourceName));
				}

				String apiJs = this.snippets.get("api.js");
				apiJs = apiJs.replace("{{routers_requires}}", routers_requires.toString());
				apiJs = apiJs.replace("{{routes}}", routes.toString());

				String apiZipPath = "generatedAPI";
				this.saveApi(apiZipPath, apiJs, models, routers);
				System.out.println("API saved in: " + apiZipPath);
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
		this.snippets.put("get_route_simple_prop", getResourseAsString("/snippets/nodejs/routers/routes/get_route_simple_prop"));
		this.snippets.put("get_route_multi", getResourseAsString("/snippets/nodejs/routers/routes/get_route_multi"));
		this.snippets.put("head_route_simple", getResourseAsString("/snippets/nodejs/routers/routes/head_route_simple"));
		this.snippets.put("head_route_multi", getResourseAsString("/snippets/nodejs/routers/routes/head_route_multi"));
		this.snippets.put("post_route_simple", getResourseAsString("/snippets/nodejs/routers/routes/post_route_simple"));
		this.snippets.put("put_route_simple", getResourseAsString("/snippets/nodejs/routers/routes/put_route_simple"));
		this.snippets.put("patch_route_simple", getResourseAsString("/snippets/nodejs/routers/routes/patch_route_simple"));
		this.snippets.put("delete_route_simple", getResourseAsString("/snippets/nodejs/routers/routes/delete_route_simple"));
		this.snippets.put("add_prop_query", getResourseAsString("/snippets/nodejs/lines/add_prop_query"));
		this.snippets.put("add_prop_data", getResourseAsString("/snippets/nodejs/lines/add_prop_data"));
		this.snippets.put("id_virtual", getResourseAsString("/snippets/nodejs/models/id_virtual"));
		this.snippets.put("prop_hyperlink", getResourseAsString("/snippets/nodejs/models/prop_hyperlink"));
		this.snippets.put("ref_resource_prop", getResourseAsString("/snippets/nodejs/models/ref_resource_prop"));
		this.snippets.put("ref_resource_array_item", getResourseAsString("/snippets/nodejs/models/ref_resource_array_item"));
	}

	private void saveApi(String savePathName, String apiJs, Map<String, ModelBuilder> models, Map<String, RouterBuilder> routers) throws IOException{
		String zipFile = savePathName + ".zip";

		FileOutputStream fos = new FileOutputStream(zipFile);
		ZipOutputStream zos = new ZipOutputStream(fos);

		ZipEntry entry = new ZipEntry("package.json");
		zos.putNextEntry(entry);
		zos.write(this.snippets.get("package.json").getBytes());
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
	}
}
