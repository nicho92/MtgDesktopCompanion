package org.magic.api.externalshop.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.api.cardnexus.configuration.NexusConfig;
import org.api.cardnexus.model.CardProduct;
import org.api.cardnexus.model.SealedProduct;
import org.api.cardnexus.model.enums.EnumFinishes;
import org.api.cardnexus.model.enums.EnumSealedType;
import org.api.cardnexus.model.requests.InventoryRequest;
import org.api.cardnexus.model.requests.SearchProductRequest;
import org.api.cardnexus.services.InventoryService;
import org.api.cardnexus.services.ProductsService;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.shop.Category;
import org.magic.api.beans.shop.Contact;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGSealedProvider;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.api.interfaces.abstracts.AbstractExternalShop;
import org.magic.api.interfaces.extra.MTGProduct;
import org.magic.services.MTGConstants;
import org.magic.services.tools.MTG;

public class CardNexusExternalShop extends AbstractExternalShop {
    
    
    private ProductsService pService;
    private InventoryService iService;

    public CardNexusExternalShop() {
	    pService = new ProductsService();
	    iService = new InventoryService();
    }
    
    private void init()
    {
	NexusConfig.setToken(getAuthenticator().get("CARDNEXUS_API_KEY"));
	NexusConfig.DIRECTORY_FEED=MTGConstants.DATA_DIR;
	NexusConfig.DEFAULT_GAME_VALUE="mtg";
	try {
        	    pService.listExpansion("mtg");
        	    //pService.cachingProducts("mtg", false);
        	} catch (IOException e) {
        	    logger.error(e);
        	} 
	
    }
    
    @Override
    public STATUT getStatut() {
          return STATUT.DEV;
    }
    
    @Override
    public String getVersion() {
        return NexusConfig.API_VERSION;
    }
    
    
    @Override
    public List<MTGProduct> listProducts(String name) throws IOException {
	init();
	
	var ret = new ArrayList<MTGProduct>();
	var req = SearchProductRequest.create().setName(name).contains();
	     
	var results = pService.searchProduct(req);
		
	results.forEach(p->{
	    if(p instanceof CardProduct card)
	    {
		try {
		    var ed = MTG.getEnabledPlugin(MTGCardsProvider.class).getSetById(p.getExpansion().code());
		    var item = MTG.getEnabledPlugin(MTGCardsProvider.class).getCardByNumber(card.getPrintNumber(),  ed);
		    item.setUrl(card.getImageUrl());
		    ret.add(item);
		} catch (Exception e) {
		    logger.error(e);
		}
	    }
	    else  if(p instanceof SealedProduct sealed)
	    {
		try {
		    
		    var ed = MTG.getEnabledPlugin(MTGCardsProvider.class).getSetById(p.getExpansion().code());
		    var items = MTG.getEnabledPlugin(MTGSealedProvider.class).get(ed, parseTypeProduct(sealed.getProductCategory()));
		    var item = items.getFirst();
		    
		    item.setUrl(p.getImageUrl());
		    ret.add(item);
		    
		} catch (Exception e) {
		  logger.error(e);
		}
	    }
	});
	
	return ret;
	
    }

    private EnumItems parseTypeProduct(EnumSealedType productCategory) {
	switch(productCategory)
		{
	case booster_box: return EnumItems.BOX;
	case booster_case: return EnumItems.CASE;
	case booster_pack: return EnumItems.BOOSTER;
	case bundle: return EnumItems.BUNDLE;
	case bundle_case: return EnumItems.CASE;
	case commander_deck: return EnumItems.COMMANDER_DECK;
	case starter_deck: return EnumItems.STARTER;
	case preconstructed_deck: return EnumItems.CONSTRUCTPACK;
	case preconstructed_deck_box: return EnumItems.CONSTRUCTPACK;
	case prerelease_kit: return EnumItems.PRERELEASEPACK;
	default : return EnumItems.LOTS; 
	
		}
    }   
    
    @Override
    protected List<MTGStockItem> loadStock(String search) throws IOException {
	init();
	var ret = new ArrayList<MTGStockItem>();
	
	var req2 = SearchProductRequest.create().setName(search).contains();
	
	
	var products = pService.searchProduct(req2);

	var req = InventoryRequest.create().setProductIds(products.stream().map(ap->ap.getId()).toList());
	
	var lines = iService.getInventoryLines(req);
	
	lines.forEach(il->{
	    
	    var opt= products.stream().filter(ap->il.productId()==ap.getId()).findAny();
	    var product=opt.get();
	    
	    
	    if(product instanceof CardProduct c)
	    {
		
		
			var item = new MTGCardStock();
				try {
				    item.setProduct(MTG.getEnabledPlugin(MTGCardsProvider.class).getCardByNumber(c.getPrintNumber(), pService.getExpansionById(product.getExpansionId()).code()));
				} catch (IOException e) {
				   logger.error("cant find product for {}",c);
				}
				item.setFoil(il.finish()==EnumFinishes.Foil);
				item.setCondition(aliases.getReversedConditionFor(this, il.condition().getLabel(), EnumCondition.NEAR_MINT));
				item.setDateUpdate(il.updatedAt());
				item.setComment(il.comment());
				item.setQte(il.quantity());
				item.setEtched(il.finish()==EnumFinishes.Etched);
				item.setSigned(il.finish()==EnumFinishes.Signed);
				item.setLanguage(il.language());
				item.getTiersAppIds().put("cardNexus",il.id());
				try {
				if(!c.getPricesByFinish().isEmpty())
				    item.setPrice(c.getPricesByFinish().get(il.finish()).cardmarket().marketValue());
				}
				catch(Exception e)
				{
				    logger.error("error gettings price market for {}",c);
				}
				ret.add(item); 
				
		
	    }
	});
	
	
	return ret;
    }

    @Override
    public MTGStockItem getStockById(EnumItems typeStock, String id) throws IOException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public List<Category> listCategories() throws IOException {
	
	var ret = new ArrayList<Category>();
	int index = 0;
	
	ret.add(new Category(index++, "card"));
	
	for(var s : EnumSealedType.values())
	    ret.add(new Category(index++, s.name()));
	
	return ret;
    }

    @Override
    public Integer saveOrUpdateContact(Contact c) throws IOException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Contact getContactByEmail(String email) throws IOException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public List<Contact> listContacts() throws IOException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void deleteContact(Contact contact) throws IOException {
	// TODO Auto-generated method stub

    }

    @Override
    public Contact getContactByLogin(String login, String passw) throws IOException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public String saveOrUpdateTransaction(Transaction t) throws IOException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void deleteTransaction(Transaction t) throws IOException {
	// TODO Auto-generated method stub

    }

    @Override
    public Transaction getTransactionById(String id) throws IOException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public List<Transaction> listTransactions(Contact c) throws IOException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public boolean enableContact(String token) throws IOException {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public String getName() {
	return "CardNexus";
    }

    @Override
    protected List<Transaction> loadTransaction() throws IOException {
	// TODO Auto-generated method stub
	return null;
    }

  

    @Override
    protected void saveOrUpdateStock(List<MTGStockItem> it) throws IOException {
	// TODO Auto-generated method stub

    }

}
