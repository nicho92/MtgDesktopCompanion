package org.api.mkm.modele;

import java.util.List;

public class WantItem extends Product{

	 		String idWant;         
		    int count;   
		    double wishPrice;      
		    boolean mailAlert;
		    String type="product";
		    private Product product;
		    private List<Integer> idLanguage;
		    private String minCondition;
		    private boolean isFoil;
		    private boolean isSigned;
		    private boolean isPlayset;
		    private boolean isAltered;
		    
			public String getIdWant() {
				return idWant;
			}
			public void setIdWant(String idWant) {
				this.idWant = idWant;
			}
			public int getCount() {
				return count;
			}
			public void setCount(int count) {
				this.count = count;
			}
			public double getWishPrice() {
				return wishPrice;
			}
			public void setWishPrice(double wishPrice) {
				this.wishPrice = wishPrice;
			}
			public boolean isMailAlert() {
				return mailAlert;
			}
			public void setMailAlert(boolean mailAlert) {
				this.mailAlert = mailAlert;
			}
			public String getType() {
				return type;
			}
			public void setType(String type) {
				this.type = type;
			}
			public Product getProduct() {
				return product;
			}
			public void setProduct(Product product) {
				this.product = product;
			}
			public List<Integer> getIdLanguage() {
				return idLanguage;
			}
			public void setIdLanguage(List<Integer> idLanguage) {
				this.idLanguage = idLanguage;
			}
			public String getMinCondition() {
				return minCondition;
			}
			public void setMinCondition(String minCondition) {
				this.minCondition = minCondition;
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
			public boolean isAltered() {
				return isAltered;
			}
			public void setAltered(boolean isAltered) {
				this.isAltered = isAltered;
			}

	
}
