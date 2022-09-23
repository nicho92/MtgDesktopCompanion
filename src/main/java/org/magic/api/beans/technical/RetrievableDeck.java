package org.magic.api.beans.technical;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MagicDeck;

public class RetrievableDeck {

	private String name;
	private String description;
	private URI url;
	private String author;
	private String color;
	private List<String> keycards;


	public MagicDeck toBaseDeck()
	{
		var deck = new MagicDeck();
		deck.setName(getName());
		deck.setDescription("Import from : " + getUrl());

		return deck;
	}


	public RetrievableDeck() {
		keycards = new ArrayList<>();
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

	public List<String> getKeycards() {
		return keycards;
	}

	public void setKeycards(List<String> keycards) {
		this.keycards = keycards;
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
