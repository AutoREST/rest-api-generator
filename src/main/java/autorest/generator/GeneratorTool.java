package autorest.generator;

import java.io.*;
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

	public GeneratorTool(String fileName, Boolean verbose) throws IOException{
		JSchParser yyparser = new JSchParser(fileName);
		yyparser.setVerbose(verbose);
		Boolean parsed = yyparser.parse();

		if (parsed) {
			PFSHandler pfsh = yyparser.getPFSHandler();
			if(pfsh != null){
				this.loadSnippets();
				StringBuilder routes = new StringBuilder();
				StringBuilder routers_requires = new StringBuilder();
				Map<String, String> models = new HashMap<>();
				Map<String, String> routers = new HashMap<>();
				Map<String, JSchRestriction> definitions = pfsh.getDefinitions();
				JSchRestriction mainJSchema = pfsh.getMainJSchema();

				for (String resourceName : definitions.keySet()) {
					JSchRestriction resource = definitions.get(resourceName);

					routers.put(resourceName, generateRouter(resourceName, resource));

					routes.append(this.snippets.get("routes").replace("#resource_name#", resourceName));
					routers_requires.append(this.snippets.get("routers_requires").replace("#resource_name#", resourceName));
				}

				String apiJs = this.snippets.get("api.js");
				apiJs = apiJs.replace("/*#routers_requires#*/", routers_requires.toString());
				apiJs = apiJs.replace("/*#routes#*/", routes.toString());

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

	private String generateRouter(String resourceName, JSchRestriction resource){
		String model_name = capitalize(resourceName);
		StringBuilder buildedQuery = new StringBuilder();
		Map<String, JSchRestriction> props = resource.getProperties();
		String resourceId = "id";
		for (String propName : props.keySet()) {
			if(!propName.equals(resourceId))
				buildedQuery.append(this.snippets.get("query_from_body").replace("#prop_name#", propName));
		}

		String get_route = this.snippets.get("get_route");
		get_route = get_route.replace("/*#resource_name#*/", resourceName);
		get_route = get_route.replace("/*#query_building#*/", buildedQuery);
		get_route = get_route.replace("/*#model_name#*/", model_name);
		// System.out.println("get_route:\n"+get_route);
		return get_route;
	}

	private static String capitalize(final String line) {
	   return Character.toUpperCase(line.charAt(0)) + line.substring(1);
	}

	private void loadSnippets()  throws IOException{
		this.snippets = new HashMap<>();
		this.snippets.put("api.js", getResourseAsString("/snippets/nodejs/api.js"));
		this.snippets.put("routers_requires", getResourseAsString("/snippets/nodejs/lines/routers_requires"));
		this.snippets.put("routes", getResourseAsString("/snippets/nodejs/lines/routes"));
		this.snippets.put("query_from_body", getResourseAsString("/snippets/nodejs/lines/query_from_body"));
		this.snippets.put("get_route", getResourseAsString("/snippets/nodejs/routers/routes/get_route"));
		this.snippets.put("router", getResourseAsString("/snippets/nodejs/routers/router.js"));
	}

	private void saveApi(String savePathName, String apiJs, Map<String, String> models, Map<String, String> routers) throws IOException{
		String zipFile = savePathName + ".zip";

		FileOutputStream fos = new FileOutputStream(zipFile);
		ZipOutputStream zos = new ZipOutputStream(fos);

		ZipEntry entry = new ZipEntry("api.js");
		zos.putNextEntry(entry);
		zos.write(apiJs.getBytes());
		zos.closeEntry();

		entry = new ZipEntry("models/");
		zos.putNextEntry(entry);
		for (String modelName : models.keySet()) {
			ZipEntry modelEntry = new ZipEntry("models/"+modelName.toLowerCase()+".js");
			zos.putNextEntry(modelEntry);
			zos.write(models.get(modelName).getBytes());
			zos.closeEntry();
		}

		entry = new ZipEntry("routers/");
		zos.putNextEntry(entry);
		for (String routerName : routers.keySet()) {
			ZipEntry routerEntry = new ZipEntry("routers/"+routerName.toLowerCase()+".js");
			zos.putNextEntry(routerEntry);
			zos.write(routers.get(routerName).getBytes());
			zos.closeEntry();
		}
		zos.close();
	}
}
