package org.magic.api.interfaces;

import java.util.List;

import org.magic.api.beans.MTGImportExportException;

public interface CardsImporter {

	
	
	public List<MTGImportExportException> rejects();
}
