package org.magic.api.beans;

public class MTGDominance {

	private int position;
	private String cardName;
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
