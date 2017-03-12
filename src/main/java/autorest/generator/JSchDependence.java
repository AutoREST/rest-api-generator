package autorest.generator;
import java.util.List;

import java.util.ArrayList;
import java.io.Serializable;

public class JSchDependence implements Serializable{
	public List<String> kwords = null;
	public JSchRestriction jsch = null;

	public void setKwords(List<String> value) { this.kwords = value; }
	public List<String> getKwords() { return this.kwords; }
	public void setJSch(JSchRestriction value) { this.jsch = value; }
	public JSchRestriction getJSch() { return this.jsch; }

	public JSchDependence(){

	}

	public Boolean hasRefs(){
		if(this.jsch != null && this.jsch.hasRefs())
			return true;
		return false;
	}

	public String toString(){
		return this.toString("");
	}

	public String toString(String baseIdent){
		StringBuilder str = new StringBuilder();
		if(this.kwords != null){
			String reqStr = "[";
			for (String req : this.kwords) {
				reqStr += req + " ";
			}
			reqStr += "]";
			str.append(baseIdent+reqStr+"\n");
		}
		if(this.jsch != null)
			str.append(baseIdent+this.jsch.toString(baseIdent));
		return str.toString();
	}
}
