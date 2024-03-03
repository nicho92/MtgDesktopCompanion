package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.api.cardtrader.enums.ConditionEnum;
import org.api.cardtrader.enums.Identifier;
import org.api.cardtrader.services.CardTraderConstants;
import org.api.cardtrader.services.CardTraderService;
import org.api.cardtrader.tools.URLCallInfo;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.beans.technical.audit.NetworkInfo;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.TechnicalServiceManager;
import org.magic.services.tools.MTG;

public class CardTraderStockExport extends AbstractCardExport {

	private CardTraderService serv;


	@Override
	public String getFileExtension() {
		return "";
	}


	private void init()
	{
		serv = new CardTraderService(getAuthenticator().get("TOKEN"));

		serv.setListener((URLCallInfo callInfo)->{
				var netinfo = new NetworkInfo();
				netinfo.setEnd(callInfo.getEnd());
				netinfo.setStart(callInfo.getStart());
				netinfo.setRequest(callInfo.getRequest());
				netinfo.setReponse(callInfo.getResponse());

				TechnicalServiceManager.inst().store(netinfo);

		});

	}

	@Override
	public EnumExportCategory getCategory() {
		return EnumExportCategory.ONLINE;
	}


	@Override
	public boolean needFile() {
		return false;
	}


	@Override
	public void exportStock(List<MTGCardStock> stock, File f) throws IOException {
		if(serv==null)
			init();
		
		
		stock.forEach(mcs->{
					try {
					serv.addProduct(mcs.getProduct().getScryfallId(),Identifier.scryfall_id,mcs.getPrice(),mcs.getQte(),mcs.getComment(),ConditionEnum.valueOf(aliases.getConditionFor(this, EnumCondition.NEAR_MINT)),String.valueOf(mcs.getId()));
					notify(mcs.getProduct());
				} catch (IOException e) {
					logger.error(e);
				}
						
		});
		
		
		
	}

	@Override
	public List<MTGCardStock> importStock(String content) throws IOException {
		if(serv==null)
			init();


		var ret = new ArrayList<MTGCardStock>();
		serv.listStock().forEach(mp->{
			var mcs = new MTGCardStock();
				var exp = mp.getExpansion();
				var bluePrint = serv.listBluePrintsByExpansion(exp).stream().filter(bp->bp.getId().equals(mp.getIdBlueprint())).findFirst().orElse(null);
				if(bluePrint!=null)
				{
					MTGCard mc=null;
					try {
						mc = MTG.getEnabledPlugin(MTGCardsProvider.class).getCardByScryfallId(bluePrint.getScryfallId());
						mcs.setProduct(mc);
						mcs.setAltered(mp.isAltered());
						mcs.setFoil(mp.isFoil());
						mcs.setSigned(mp.isSigned());
						mcs.setComment(mp.getDescription());
						mcs.setLanguage(mp.getLanguage());
						mcs.setPrice(mp.getPrice().getValue());
						mcs.setQte(mp.getQty());
						mcs.setUpdated(true);
						notify(mcs.getProduct());
					} catch (IOException e) {
						logger.error("Error for getting card {}",bluePrint.getProductUrl());
					}


					if(mc!=null)
						ret.add(mcs);
				}
				else
				{
					logger.error("BluePrints {} not found",mp.getIdBlueprint());
				}


		});

		return ret;

	}

	
	
	

	@Override
	public String getVersion() {
		return CardTraderConstants.CARDTRADER_JAVA_API_VERSION;
	}

	@Override
	public List<String> listAuthenticationAttributes() {
		return List.of("TOKEN");
	}


	@Override
	public MTGDeck importDeck(String f, String name) throws IOException {
		throw new IOException("Not implemented");
	}

	@Override
	public String getName() {
		return CardTraderConstants.CARDTRADER_NAME;
	}


	@Override
	public Icon getIcon() {
		return new ImageIcon(CardTraderStockExport.class.getResource("/icons/plugins/"+CardTraderConstants.CARDTRADER_NAME.toLowerCase()+".png"));
	}

}
