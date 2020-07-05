package org.magic.api.interfaces;

import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MTGImportExportException;

public interface MTGCardsImporter {
	
	List<MTGImportExportException> rejects = new ArrayList<>();

	
	public default List<MTGImportExportException> rejects(){
		return rejects;
	}
	
	public default void clear() {
		rejects.clear();
	}
	
	
	
	public void reject(String msg);

}
