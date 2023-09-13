package org.magic.api.beans.technical.audit;

import java.io.File;

import org.magic.api.beans.abstracts.AbstractAuditableItem;

public class FileAccessInfo extends AbstractAuditableItem{
	private static final long serialVersionUID = 1L;
	public enum ACCESSTYPE {READ,WRITE,DELETE, CREATE}
	
	
	private File file;
	private ACCESSTYPE accesstype;
	
	
	public FileAccessInfo(File f) {
		this.file=f;
	}
	
	public File getFile() {
		return file;
	}

	public ACCESSTYPE getAccesstype() {
		return accesstype;
	}
	public void setAccesstype(ACCESSTYPE accesstype) {
		this.accesstype = accesstype;
	}
	
	
}
