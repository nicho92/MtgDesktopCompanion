package org.magic.api.beans;

public class CardDominance {

	private int position;
	private String cardName;
	private double dominance;
	private double decksPercent;
	private double players;
	
	
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public String getCardName() {
		return cardName;
	}
	public void setCardName(String cardName) {
		this.cardName = cardName;
	}
	public double getDominance() {
		return dominance;
	}
	public void setDominance(double dominance) {
		this.dominance = dominance;
	}
	public double getDecksPercent() {
		return decksPercent;
	}
	public void setDecksPercent(double decksPercent) {
		this.decksPercent = decksPercent;
	}
	public double getPlayers() {
		return players;
	}
	public void setPlayers(double players) {
		this.players = players;
	}
	
	@Override
	public String toString() {
		return getCardName();
	}
	
	
}
