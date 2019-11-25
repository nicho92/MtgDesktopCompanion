package org.magic.api.beans;

public class SeleadStock {

	private int id=-1;
	private Packaging product;
	private int qte=1;
	private String comment;
	private EnumStock condition;
	
	public SeleadStock()
	{
		
	}
	
	public SeleadStock(Packaging p)
	{
		setProduct(p);
	}
	
	public SeleadStock(Packaging p, int qte)
	{
		setProduct(p);
		setQte(qte);
	}
	
	public SeleadStock(MagicEdition e, Packaging.TYPE type,String lang)
	{
		product = new Packaging();
		product.setEdition(e);
		product.setType(type);
		product.setLang(lang);
		
		setProduct(product);
		
	}
	
	@Override
	public String toString() {
		return getId()+"-"+getProduct();
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Packaging getProduct() {
		return product;
	}
	public void setProduct(Packaging product) {
		this.product = product;
	}
	public int getQte() {
		return qte;
	}
	public void setQte(int qte) {
		this.qte = qte;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public EnumStock getCondition() {
		return condition;
	}
	public void setCondition(EnumStock condition) {
		this.condition = condition;
	}
	
	
	
}
