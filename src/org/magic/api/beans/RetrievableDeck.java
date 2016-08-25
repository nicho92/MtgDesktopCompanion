package org.magic.api.beans;

import java.net.URI;

public class RetrievableDeck {

	
	String name;
	String description;
	URI url;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public URI getUrl() {
		return url;
	}
	public void setUrl(URI url) {
		this.url = url;
	}
	
	
	@Override
	public String toString() {
		return getName();
	}
	
	
}
