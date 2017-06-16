package org.api.mkm.modele;

import java.util.List;

public class ProductListFile {

	private String productsfile;
	private String mime;
	private List<Link> links;
	
	public String getProductsfile() {
		return productsfile;
	}
	public void setProductsfile(String productsfile) {
		this.productsfile = productsfile;
	}
	public String getMime() {
		return mime;
	}
	public void setMime(String mime) {
		this.mime = mime;
	}
	public List<Link> getLinks() {
		return links;
	}
	public void setLinks(List<Link> links) {
		this.links = links;
	}
}
