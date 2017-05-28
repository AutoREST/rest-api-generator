package autorest.generator;

public class Options{
	public Boolean Verbose;
	public String APIName;
	public String DataBaseName;
	public String APIRepoURL;

	public Options(){
		this.Verbose = false;
		this.APIName = "generatedAPI";
		this.DataBaseName = "apiDB";
		this.APIRepoURL = "http://www.github.com";
	}

	public String toString(){
		String s = "";
		s += "Verbose: " + this.Verbose + "\n";
		s += "APIName: " + this.APIName + "\n";
		s += "DataBaseName: " + this.DataBaseName + "\n";
		s += "APIRepoURL: " + this.APIRepoURL + "\n";
		return s;
	}
}
