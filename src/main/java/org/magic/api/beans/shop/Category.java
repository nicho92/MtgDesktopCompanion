package org.magic.api.beans.shop;

import java.io.Serializable;

public class Category implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int idCategory;
	private String categoryName;
	
	public Category() {
		// TODO Auto-generated constructor stub
	}
	
	
	
	public Category(int idCategory, String categoryName) {
		super();
		this.idCategory = idCategory;
		this.categoryName = categoryName;
	}



	public int getIdCategory() {
		return idCategory;
	}
	public void setIdCategory(int idCategory) {
		this.idCategory = idCategory;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	
	@Override
	public String toString() {
		return getCategoryName();
	}
}
