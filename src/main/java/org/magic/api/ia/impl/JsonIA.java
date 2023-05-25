package org.magic.api.ia.impl;

import java.io.IOException;

import javax.swing.JOptionPane;

import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.abstracts.AbstractIA;
import org.magic.services.network.URLTools;

public class JsonIA extends AbstractIA {

	@Override
	public String ask(String prompt) throws IOException {
		return JOptionPane.showInputDialog("Copy IA Json code");
	}

	@Override
	public MagicCard generateRandomCard(String description) throws IOException {
		
		return parseIaCardSuggestion(URLTools.toJson(description).getAsJsonObject());
		
	}

	@Override
	public String getName() {
		return "Json";
	}

}
