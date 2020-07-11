package org.magic.api.interfaces;

import java.util.List;

import org.magic.api.beans.MTGImportExportException;

public interface MTGCardsImporter {
	
	public List<MTGImportExportException> rejects();
		
	public void clear();
	
	public void reject(String msg);

}
