package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.api.cardtrader.tools.URLCallInfo;
import org.api.manapool.model.EnumCondition;
import org.api.manapool.model.EnumFinish;
import org.api.manapool.model.EnumLangages;
import org.api.manapool.model.ProductQueryEntry;
import org.api.manapool.services.InventoryService;
import org.api.manapool.tools.ManaPoolConstants;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.beans.technical.audit.NetworkInfo;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.api.interfaces.abstracts.AbstractTechnicalServiceManager;

public class ManaPoolExport extends AbstractCardExport {

	@Override
	public EnumExportCategory getCategory() {
		return EnumExportCategory.ONLINE;
	}
	
	
	@Override
	public String getStockFileExtension() {
		return null;
	}

	@Override
	public String getName() {
		return "ManaPool";
	}
	
	@Override
	public List<String> listAuthenticationAttributes() {
		return List.of("EMAIL","TOKEN");
	}
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	
	@Override
	public boolean needFile() {
		return false;
	}
	
	
	@Override
	public String getVersion() {
		return ManaPoolConstants.API_VERSION;
	}
	
	
	@Override
	public void exportStock(List<MTGCardStock> stock, File f) throws IOException {
		
		var inventoryManager = new InventoryService(getAuthenticator().get("EMAIL"), getAuthenticator().get("TOKEN"));
//		inventoryManager.setListener((URLCallInfo callInfo)->{
//			var netinfo = new NetworkInfo();
//			netinfo.setEnd(callInfo.getEnd());
//			netinfo.setStart(callInfo.getStart());
//			netinfo.setRequest(callInfo.getRequest());
//			netinfo.setReponse(callInfo.getResponse());
//
//			AbstractTechnicalServiceManager.inst().store(netinfo);
//
//	});
		
		var items = stock.stream().map(mcs->{
			
			var item = new ProductQueryEntry();
				 item.setScryfallId(mcs.getProduct().getScryfallId());
				 item.setQuantity(mcs.getQte());
				 item.setPrice(mcs.getValue().doubleValue());
				 item.setCondition( EnumCondition.valueOf(aliases.getConditionFor(this, mcs.getCondition())));
				 item.setFinishId(mcs.isFoil()?EnumFinish.FO:EnumFinish.NF);
				 item.setLanguage(EnumLangages.FR);
				 
				return item;
		}).toList();
		
		
		
		inventoryManager.addInventoryItems(items).forEach(e->{
			logger.info(e.getId());
		});;
		
		
		
		
	}
	
	
}
