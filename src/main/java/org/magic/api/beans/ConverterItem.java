package org.magic.api.beans;

import java.io.Serializable;

public class ConverterItem implements Serializable, Comparable<ConverterItem>
{ 
	private static final long serialVersionUID = 1L;
	private int id=-1;
	private boolean updated=false;
	private String name;
	private int inputId;
	private int outputId;
	private String lang="";
	private String source;
	private String destination;
	
	public ConverterItem() {
		
	}
	
	@Override
	public String toString() {
		return String.valueOf(getId());
	}
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isUpdated() {
		return updated;
	}

	public void setUpdated(boolean updated) {
		this.updated = updated;
	}
	
	public ConverterItem(String source, String dest, String name, String lang, int inputId, int outputId ) {
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

	@Override
	public int compareTo(ConverterItem o) {
		return getId() - o.getId();
	}
}
