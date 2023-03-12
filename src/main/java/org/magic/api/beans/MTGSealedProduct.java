package org.magic.api.beans;

import org.magic.api.beans.enums.EnumExtra;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.interfaces.abstracts.extra.AbstractProduct;

public class MTGSealedProduct extends AbstractProduct{

	private static final long serialVersionUID = 1L;

	private String lang;
	private int num;
	private EnumExtra extra;

	public MTGSealedProduct() {
		setTypeProduct(EnumItems.SEALED);
	}

	public EnumExtra getExtra() {
		return extra;
	}
	public void setExtra(EnumExtra extra) {
		this.extra = extra;
	}

	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}

	@Override
	public String toString() {
		return getTypeProduct() + (getExtra()!=null?" "+getExtra():"") + " (" +getLang()+") ";
	}

	@Override
	public String getStoreId() {
		return getTypeProduct().name() + (getExtra()!=null?"-"+getExtra().name():"")+ "-"+getLang() + "-"+getNum();
	}
	

}
