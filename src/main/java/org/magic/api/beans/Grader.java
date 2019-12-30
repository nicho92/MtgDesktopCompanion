package org.magic.api.beans;

public class Grader {

	private String name;
	private String url;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public Grader() {
		//do nothing
	}
	
	public Grader(String name, String url) {
		this.name = name;
		this.url = url;
	}
	
	@Override
	public String toString() {
		return getName();
	}

}
