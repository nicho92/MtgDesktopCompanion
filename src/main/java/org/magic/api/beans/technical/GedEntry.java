package org.magic.api.beans.technical;

import java.io.Serializable;

import org.magic.api.interfaces.MTGStorable;
import org.magic.tools.CryptoUtils;
import org.magic.tools.ImageTools;

import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class GedEntry <T extends MTGStorable> implements Serializable {

	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private byte[] content;
	private boolean isImage;
	private Class<T> classe;
	private String ext;
	
	
	@Override
	public String toString() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
		ext=Files.getFileExtension(name);
	}
	
	public byte[] getContent() {
		return content;
	}
	
	public String getExt() {
		return ext;
	}
	
	
	public String getName()
	{
		return name;
	}
	
	public void setContent(byte[] content) {
		this.content = content;
	}

	public void setIsImage(boolean isImage) {
		this.isImage = isImage;
	}
	
	
	
	public void setClasse(Class<T> classe) {
		this.classe = classe;
	}
	
	public GedEntry()
	{
		
	}
	
	public GedEntry(byte[] content,Class<T> classe,String id,String name)  {
		this.classe=classe;
		setName(name);
		setContent(content);
		setIsImage(ImageTools.isImage(content));
		setClasse(classe);
		setId(id);
	}
	
	public long getLength()
	{
		return content.length;
	}
	
	
	
	public Class<T> getClasse() {
		return classe;
	}
	
	
	public boolean isImage() {
		return isImage;
	}
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public JsonElement toJson() {
		var obj = new JsonObject();
		obj.addProperty("id", getId());
	    obj.addProperty("name", getName());
	    obj.addProperty("size", getContent().length);
    	obj.addProperty("classe", classe.getCanonicalName());
	    obj.addProperty("ext",getExt());
	    obj.addProperty("data",CryptoUtils.toBase64(getContent()));
	    
	    return obj;
	}

}
