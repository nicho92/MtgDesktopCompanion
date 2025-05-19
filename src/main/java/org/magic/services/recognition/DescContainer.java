package org.magic.services.recognition;

public class DescContainer implements Comparable<DescContainer>{


	private ImageDesc descData;
	private String stringData;
	private double match = 0;

	public DescContainer(ImageDesc descData, String stringData) {
		this.descData = descData;
		this.stringData = stringData;
	}


	@Override
	public String toString() {
		return getStringData();
	}

	@Override
	public int compareTo(DescContainer dc) {
		return Double.compare(dc.getMatch(),match);
	}


	public String getNumber()
	{
		return stringData.split("\\|")[2];
	}


	public String getId()
	{
		return stringData.split("\\|")[2];
	}


	public String getName()
	{
		return stringData.split("\\|")[0];
	}

	public String getScryId()
	{
		return stringData.split("\\|")[3];
	}
	
	
	public String getSetCode()
	{
		return stringData.split("\\|")[1];
	}

	public ImageDesc getDescData() {
		return descData;
	}

	public String getStringData() {
		return stringData;
	}

	public void setMatch(double d) {
		match=d;
	}

	public double getMatch() {
		return match;
	}
}
