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

	}



	public Category(int idCategory, String categoryName) {
		super();
		this.idCategory = idCategory;
		this.categoryName = categoryName;
	}


	@Override
	public int hashCode() {
		return idCategory;
	}

	@Override
	public boolean equals(Object obj) {

		if(obj==null)
			return false;

		return obj.hashCode()==hashCode();

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
