package org.magic.api.beans.technical;

import java.net.URI;

import org.magic.api.beans.MTGDeck;

public class RetrievableDeck {

	private String name;
	private String description;
	private URI url;
	private String author;
	private String color;

	public MTGDeck toBaseDeck() {
		var deck = new MTGDeck();
		deck.setName(getName());
		deck.setDescription("Import from : " + getUrl());

		return deck;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public URI getUrl() {
		return url;
	}

	public void setUrl(URI url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return getName();
	}

}
