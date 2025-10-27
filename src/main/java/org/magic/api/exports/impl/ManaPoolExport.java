package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.Currency;
import java.util.List;

import org.api.manapool.listener.URLCallInfo;
import org.api.manapool.model.Product;
import org.api.manapool.model.ProductQueryEntry;
import org.api.manapool.model.enums.EnumCondition;
import org.api.manapool.model.enums.EnumFinish;
import org.api.manapool.model.enums.EnumLangages;
import org.api.manapool.services.ManaPoolAPIService;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.beans.technical.audit.NetworkInfo;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.api.interfaces.abstracts.AbstractTechnicalServiceManager;
import org.magic.services.MTGControler;
import org.magic.services.tools.POMReader;

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
		return POMReader.readVersionFromPom(ManaPoolAPIService.class, "/META-INF/maven/com.github.nicho92/manapool-api-java/pom.properties");
	}
	
	
	@Override
	public void exportStock(List<MTGCardStock> stock, File f) throws IOException {
		
		var inventoryManager = new ManaPoolAPIService(getAuthenticator().get("EMAIL"), getAuthenticator().get("TOKEN"));
		inventoryManager.getClient().setCallListener((URLCallInfo callInfo)->{
			var netinfo = new NetworkInfo();
				netinfo.setStart(callInfo.getStart());
				netinfo.setEnd(callInfo.getEnd());
				netinfo.setRequest(callInfo.getRequest());
				netinfo.setReponse(callInfo.getResponse());

			AbstractTechnicalServiceManager.inst().store(netinfo);

	});
		
		var items = stock.stream().map(mcs->{
			
			var item = new ProductQueryEntry();
				 item.setScryfallId(mcs.getProduct().getScryfallId());
				 item.setQuantity(mcs.getQte());
				 
				 if(MTGControler.getInstance().getCurrencyService().isEnable()) 
					 item.setPrice(MTGControler.getInstance().getCurrencyService().convertTo(Currency.getInstance("USD"), mcs.getValue().doubleValue()));
				 else
					 item.setPrice(mcs.getValue().doubleValue());	 
				 
				try {
					item.setCondition(EnumCondition.valueOf(aliases.getConditionFor(this, mcs.getCondition())));
				}
				catch(IllegalArgumentException _)
				{
					logger.warn("EnumCondition {} is not found",mcs.getCondition().name() );
					item.setCondition(EnumCondition.NM);
				}
				 item.setFinishId(mcs.isFoil()?EnumFinish.FO:EnumFinish.NF);
				 
				 if(mcs.isEtched())
					 item.setFinishId(EnumFinish.EF);
							 
				 var lcode = mcs.getLanguage().toUpperCase().substring(0,2);
				 try {
					 item.setLanguage(EnumLangages.valueOf(lcode));
				 }
				 catch(Exception e)
				 {
					 logger.warn("EnumLangage not found for {}", lcode);
					 item.setLanguage(EnumLangages.EN);
				 }
					 
				notify(mcs.getProduct());
				return item;
		}).toList();
		
		
		inventoryManager.addInventoryItems(items).forEach(it->{
			
			var mcs = filter(stock, it.getProduct().getSingle());
			
			if(mcs!=null)
				mcs.getTiersAppIds().put(getName(), it.getId());
		});
		
		
	}
	
	private MTGCardStock filter(List<MTGCardStock> stock, Product p)
	{
		var opt = stock.stream().filter(mcs->{
			
			return mcs.getProduct().getScryfallId().equals(p.getScryfallId())
					&&
					(p.getFinishId()==EnumFinish.FO?mcs.isFoil():true)
					&&
					(p.getCondition()==EnumCondition.valueOf(aliases.getConditionFor(this, mcs.getCondition(),p.getCondition().name())))
					;
			
		}).findFirst();
		
		if(opt.isPresent())
			return opt.get();
		
		return null;
		
		
	}
	
	
	
}
