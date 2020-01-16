package org.magic.api.beans;

import java.io.File;

import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

import org.magic.tools.ImageTools;

import com.google.common.io.Files;

public class GedEntry<T> {

	private String id;
	private T entity;
	private File file;
	
	
	@Override
	public String toString() {
		return super.toString();
	}
	
	public Icon getIcon()
	{
		return FileSystemView.getFileSystemView().getSystemIcon(file);
	}
	
	public String getName()
	{
		if(file!=null)
			return Files.getNameWithoutExtension(file.getAbsolutePath());
		
		return null;
	}
	
	public GedEntry(File f) {
		this.file=f;
	}
	
	public boolean isImage()
	{
		return ImageTools.isImage(file);
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public T getEntity() {
		return entity;
	}
	public void setEntity(T entity) {
		this.entity = entity;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	
}
