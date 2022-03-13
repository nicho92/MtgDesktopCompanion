package org.magic.api.beans;

import java.io.Serializable;

public class ConverterItem implements Serializable, Comparable<ConverterItem>
{ 
	private static final long serialVersionUID = 1L;
	private Long id=-1L;
	private boolean updated=false;
	private String name;
	private Long inputId;
	private Long outputId;
	private String source;
	private String destination;
	
	
	@Override
	public String toString() {
		return String.valueOf(getId());
	}
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id=id;

	}
	
	public void setId(Integer id) {
		this.id = id.longValue();
	}

	public boolean isUpdated() {
		return updated;
	}

	public void setUpdated(boolean updated) {
		this.updated = updated;
	}
	
	public Long getIdFor(String name)
	{
		if(destination.equalsIgnoreCase(name))
			return outputId;
		
		if(source.equalsIgnoreCase(name))
			return inputId;
		
		return -1L;
	}
	
	
	public ConverterItem(String source, String dest, String name, Integer inputId, Integer outputId ) {
		this.source=source;
		this.destination=dest;
		this.name = name;
		this.inputId = inputId.longValue();
		this.outputId = outputId.longValue();
	}
	
	public ConverterItem(String source, String dest, String name,Long inputId, Long outputId ) {
		this.source=source;
		this.destination=dest;
		this.name = name;
		this.inputId = inputId;
		this.outputId = outputId;
	}
	
	public ConverterItem()
	{
		
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
	
	public Long getInputId() {
		return inputId;
	}

	public void setInputId(Integer inputId) {
		this.inputId = inputId.longValue();
	}
	
	public void setInputId(Long inputId) {
		this.inputId = inputId;
	}

	public Long getOutputId() {
		return outputId;
	}

	public void setOutputId(Integer outputId) {
		this.outputId = outputId.longValue();
	}
	
	public void setOutputId(Long outputId) {
		this.outputId = outputId;
	}

	@Override
	public int compareTo(ConverterItem o) {
		return (int) (getId() - o.getId());
	}
}
