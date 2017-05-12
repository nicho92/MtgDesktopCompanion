package org.magic.api.beans;

public class KeyWord {

	private String keyword;
	private String description;
	
	public KeyWord() {
		// TODO Auto-generated constructor stub
	}
	
	public KeyWord(String k)
	{
		this.keyword=k;
	}
	
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public boolean equals(Object obj) {
		return ((KeyWord)obj).getKeyword().equals(this.getKeyword());
	}
	
	@Override
	public String toString() {
		return getKeyword();
	}
	
		
}
