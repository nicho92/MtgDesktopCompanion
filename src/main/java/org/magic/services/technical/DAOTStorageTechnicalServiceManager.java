package org.magic.services.technical;

import java.io.IOException;
import java.util.List;

import org.magic.api.beans.abstracts.AbstractAuditableItem;
import org.magic.api.interfaces.abstracts.AbstractTechnicalServiceManager;

public class DAOTStorageTechnicalServiceManager extends AbstractTechnicalServiceManager {

	@Override
	protected <T extends AbstractAuditableItem> void store(Class<T> c, List<T> list) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected <T extends AbstractAuditableItem> List<T> restore(Class<T> c) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	

}
