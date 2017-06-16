package org.api.mkm.modele;

public class Article
{
	private int idArticle;
	private int idProduct;
	private Localization language;
	private String comments;
	private double price;
	private int count;
	private boolean inShoppingCart;
	private User seller;
	private String condition;
	private boolean isFoil;
	private boolean isSigned;
	private boolean isPlayset;
	private Product product;
	private Link links;
	
	public static enum ARTICLES_ATT {start,maxResults,userType ,minUserScore ,idLanguage ,minCondition,isFoil ,isSigned,isAltered,minAvailable};

	
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public Localization getLanguage() {
		return language;
	}
	public void setLanguage(Localization language) {
		this.language = language;
	}
	public int getIdArticle() {
		return idArticle;
	}
	public void setIdArticle(int idArticle) {
		this.idArticle = idArticle;
	}
	public int getIdProduct() {
		return idProduct;
	}
	public void setIdProduct(int idProduct) {
		this.idProduct = idProduct;
	}
	
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public boolean isInShoppingCart() {
		return inShoppingCart;
	}
	public void setInShoppingCart(boolean inShoppingCart) {
		this.inShoppingCart = inShoppingCart;
	}
	public User getSeller() {
		return seller;
	}
	public void setSeller(User seller) {
		this.seller = seller;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public boolean isFoil() {
		return isFoil;
	}
	public void setFoil(boolean isFoil) {
		this.isFoil = isFoil;
	}
	public boolean isSigned() {
		return isSigned;
	}
	public void setSigned(boolean isSigned) {
		this.isSigned = isSigned;
	}
	public boolean isPlayset() {
		return isPlayset;
	}
	public void setPlayset(boolean isPlayset) {
		this.isPlayset = isPlayset;
	}
	public Link getLinks() {
		return links;
	}
	public void setLinks(Link links) {
		this.links = links;
	}
	
	
	
}