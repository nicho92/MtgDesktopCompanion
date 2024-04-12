package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.swing.Icon;

import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.MTGConstants;
import org.magic.services.tools.MTG;

public class MTGCompanionStockExport extends AbstractCardExport {

	@Override
	public EnumExportCategory getCategory() {
		return EnumExportCategory.MTGCOMPANION;
	}
	

	@Override
	public String getName() {
		return "MTGCompanion Stock";
	}

	
	@Override
	public boolean needFile() {
		return false;
	}
	
	@Override
	public String getFileExtension() {
		return null;
	}

	@Override
	public void exportStock(List<MTGCardStock> stock, File f) throws IOException {
		
		for(var mcs : stock)
		{
			mcs.setId(-1);
			mcs.setUpdated(true);
			try {
				MTG.getEnabledPlugin(MTGDao.class).saveOrUpdateCardStock(mcs);
			} catch (SQLException e) {
				logger.error(e);
			}
			notify(mcs.getProduct());
		}
	}
	
	
	@Override
	public Icon getIcon() {
		return MTGConstants.ICON_STOCK;
	}

}
