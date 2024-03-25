package org.magic.services.technical;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

import org.magic.api.beans.abstracts.AbstractAuditableItem;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.abstracts.AbstractTechnicalServiceManager;
import org.magic.services.tools.MTG;

public class DAOStorageTechnicalServiceManager extends AbstractTechnicalServiceManager {

	
	@Override
	protected <T extends AbstractAuditableItem> void store(Class<T> c, List<T> list) throws IOException {
		try {
			MTG.getEnabledPlugin(MTGDao.class).storeTechnicalItem(c, list);
		} catch (SQLException e) {
			throw new IOException(e);
		}
		
	}

	@Override
	protected <T extends AbstractAuditableItem> List<T> restore(Class<T> c, Instant start ,Instant end) throws IOException {
		try {
			return MTG.getEnabledPlugin(MTGDao.class).restoreTechnicalItem(c,start,end);
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}

	

}
