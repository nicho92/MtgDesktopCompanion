package org.magic.api.beans.technical.audit;

import java.io.File;

public class FileAccessInfo extends AbstractAuditableItem{
	private static final long serialVersionUID = 1L;
	public enum ACCESSTYPE {READ,WRITE,DELETE, CREATE}
	
	
	private File file;
	private ACCESSTYPE accesstype;
	
	
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public ACCESSTYPE getAccesstype() {
		return accesstype;
	}
	public void setAccesstype(ACCESSTYPE accesstype) {
		this.accesstype = accesstype;
	}
	
	
}
