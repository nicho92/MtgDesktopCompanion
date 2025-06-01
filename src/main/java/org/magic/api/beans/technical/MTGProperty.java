package org.magic.api.beans.technical;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;

public class MTGProperty implements Serializable {

	
	private static final long serialVersionUID = 1L;
	private String defaultValue;
	private String comment;
	
	private String[] allowedProperties;
	
	public MTGProperty(String defaultValue, String comment, String... allowedProperties) {
		this.defaultValue = defaultValue;
		this.comment = comment;
		this.allowedProperties = allowedProperties;
	}
	
	public MTGProperty(String defaultValue, String comment) {
		this.defaultValue = defaultValue;
		this.comment = comment;
	}

	@Override
	public String toString() {
		return defaultValue;
	}
	
	
	public MTGProperty() {
		
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String[] getAllowedProperties() {
		return allowedProperties;
	}

	public void setAllowedProperties(String... allowedProperties) {
		this.allowedProperties = allowedProperties;
	}
	
	public static MTGProperty newBooleanProperty(String defaultValue,String comment)
	{
		return new MTGProperty(defaultValue, comment, "true","false");
	}
	
	public static MTGProperty newIntegerProperty(String defaultValue,String comment, int min, int max)
	{
		
		if(max<0)
			return new MTGProperty(defaultValue, comment, "Any value > "+min  );
		
		return new MTGProperty(defaultValue, comment, "Any value between "+min+ " and " + max );
	}
	
	public static MTGProperty newFileProperty(File f)
	{
		return newFileProperty(f, "File where is stored data");
	}
	

	
	public static MTGProperty newDirectoryProperty(File f)
	{
		return newFileProperty(f, "Directory where are stored files");
	}
	
	public static MTGProperty newFileProperty(File f, String comment) {
		return new MTGProperty(f.getAbsolutePath(), comment);
	}
	
	public static MTGProperty newDirectoryProperty(Path f)
	{
		return newDirectoryProperty(f.toFile());
	}
		
	public static MTGProperty newFileProperty(Path f)
	{
		return newFileProperty(f.toFile());
	}

	public static MTGProperty newStringProperty(String string, String...vars) {
		return new MTGProperty(string,"",vars);
	}
	public static MTGProperty newStringProperty(String defaults)
	{
		return new MTGProperty(defaults,"");
	}

	
}


