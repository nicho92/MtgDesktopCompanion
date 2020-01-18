package org.magic.api.beans;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

import org.magic.tools.FileTools;
import org.magic.tools.ImageTools;

import com.google.common.io.Files;

public class GedEntry<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	private String id;
	private T entity;
	private String name;
	private String ext;
	private byte[] content;
	private boolean isImage;
	private Icon icon;
	
	@Override
	public String toString() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
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
	
	public void setExt(String ext) {
		this.ext = ext;
	}
	
	public void setContent(byte[] content) {
		this.content = content;
	}
	
	public File toFile() throws IOException
	{
		File f = new File(name+"."+ext);
		FileTools.saveFile(f, content);
		return f;
	}
	
	public void setIsImage(boolean isImage) {
		this.isImage = isImage;
	}
	
	public void setIcon(Icon icon) {
		this.icon = icon;
	}
	
	public GedEntry(File f) throws IOException {
		setName(Files.getNameWithoutExtension(f.getName()));
		setExt(Files.getFileExtension(f.getName()));
		setContent(Files.toByteArray(f));
		setIsImage(ImageTools.isImage(f));
		setIcon(FileSystemView.getFileSystemView().getSystemIcon(f));
		setId(FileTools.checksum(f));
	}
	
	public boolean isImage()
	{
		return isImage;
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

	public String getFullName() {
		return getName()+"."+getExt();
	}
	
}
