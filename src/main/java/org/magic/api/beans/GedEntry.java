package org.magic.api.beans;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;

import org.magic.api.exports.impl.JsonExport;
import org.magic.api.interfaces.MTGStorable;
import org.magic.tools.CryptoUtils;
import org.magic.tools.FileTools;
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
	private Icon icon;
	private Class<T> classe;
	private String object;
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
	
	public Icon getIcon()
	{
		return icon;
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
	
	public void setIcon(Icon icon) {
		this.icon = icon;
	}
	
	public void setObject(T object) {
		this.object = new JsonExport().toJson(object);
	}
	
	public T getObject() {
		return new JsonExport().fromJson(object, classe);
	}
	
	public void setClasse(Class<T> classe) {
		this.classe = classe;
	}
	
	public GedEntry()
	{
		
	}
	
	public GedEntry(File f,Class<T> classe, T instance) throws IOException {
		this.classe=classe;
		setName(f.getName());
		setContent(Files.toByteArray(f));
		setIsImage(ImageTools.isImage(f));
		setIcon(FileSystemView.getFileSystemView().getSystemIcon(f));
		setObject(instance);
		setClasse(classe);
		setId(instance.getStoreId());
	}

	public GedEntry(byte[] content,Class<T> classe,String id,String name)  {
		this.classe=classe;
		setName(name);
		setContent(content);
		setIsImage(ImageTools.isImage(content));
		
		if(isImage())
		{
			try {
				setIcon(new ImageIcon(ImageTools.toImage(getContent())));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
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
	    
	    if(getObject()!=null)
	    	obj.addProperty("classe", getObject().getClass().getCanonicalName());
	    
	    obj.addProperty("ext",getExt());
	    obj.addProperty("obj",String.valueOf(getObject()));
	    obj.addProperty("data",CryptoUtils.toBase64(getContent()));
	    
	    return obj;
	}

}
