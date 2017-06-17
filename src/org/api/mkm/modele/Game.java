package org.api.mkm.modele;

public class Game {
	 private int idGame;
	 private String name; 
	 private String abbreviation;
	 private Link links;
	public int getIdGame() {
		return idGame;
	}
	public void setIdGame(int idGame) {
		this.idGame = idGame;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAbbreviation() {
		return abbreviation;
	}
	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}
	public Link getLinks() {
		return links;
	}
	public void setLinks(Link links) {
		this.links = links;
	}
	 
	 
	 
}
