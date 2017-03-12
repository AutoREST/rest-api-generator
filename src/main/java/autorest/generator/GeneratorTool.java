package autorest.generator;

import java.io.*;
import org.apache.commons.io.IOUtils;

public class GeneratorTool {
	private static String encoding = "UTF-8";

	public static void main(String args[]) throws IOException {
		if ( args.length > 0 ) {
			JSchParser yyparser;
			yyparser = new JSchParser(args[0]);
			if(args.length > 1){
				for(int i=1; i < args.length; i++){
					if(args[i].equals("-v")){
						yyparser.setVerbose(true);
					}
				}
			}
			yyparser.parse();
		}
		else {
			System.out.println("\n\tFormat: java JSchParser inputFile\n");
		}
	}

	public static String getResourseAsString(String resource) throws IOException{
		InputStream input = GeneratorTool.class.getResourceAsStream(resource);
		return IOUtils.toString(input, encoding);
	}
}
