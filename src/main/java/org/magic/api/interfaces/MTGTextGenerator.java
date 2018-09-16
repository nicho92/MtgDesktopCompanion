package org.magic.api.interfaces;

public interface MTGTextGenerator extends MTGPlugin{

	public String generateText();

	public String[] suggestWords(String[] start);

	public String[] suggestWords(String start);

	public void init();
}