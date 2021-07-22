package org.magic.api.beans;

import java.io.Serializable;

public class ConverterItem implements Serializable
{ 
	private static final long serialVersionUID = 1L;
	private String name;
	private int inputId;
	private int outputId;
	private String lang;
	private String source;
	private String destination;
	
	public ConverterItem() {
		
	}
	
	public ConverterItem(String source, String dest, String name, int inputId, int outputId, String lang) {
		this.source=source;
		this.destination=dest;
		this.name = name;
		this.inputId = inputId;
		this.outputId = outputId;
		this.lang = lang;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getIdMkmProduct() {
		return inputId;
	}
	public int getInputId() {
		return inputId;
	}

	public void setInputId(int inputId) {
		this.inputId = inputId;
	}

	public int getOutputId() {
		return outputId;
	}

	public void setOutputId(int outputId) {
		this.outputId = outputId;
	}

	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
}
