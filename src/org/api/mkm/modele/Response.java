package org.api.mkm.modele;

import java.util.List;

public class Response<T> {
	
	List<T> product;
	List<T> article;
	List<Link> links;
	
	
	
	
	public List<T> getProduct() {
		return product;
	}
	public void setProduct(List<T> product) {
		this.product = product;
	}
	public List<Link> getLinks() {
		return links;
	}
	public void setLinks(List<Link> links) {
		this.links = links;
	}
	public List<T> getArticle() {
		return article;
	}
	public void setArticle(List<T> article) {
		this.article = article;
	}
	
	
	
	

}
