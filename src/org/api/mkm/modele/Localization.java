package org.api.mkm.modele;

public class Localization {

	private int idLanguage;
	private String name;
	private String languageName;
	
	@Override
	public String toString() {
		return getLanguageName();
	}
	
	public int getIdLanguage() {
		return idLanguage;
	}
	public void setIdLanguage(int idLanguage) {
		this.idLanguage = idLanguage;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLanguageName() {
		return languageName;
	}
	public void setLanguageName(String languageName) {
		this.languageName = languageName;
	}
	
	
	
}
