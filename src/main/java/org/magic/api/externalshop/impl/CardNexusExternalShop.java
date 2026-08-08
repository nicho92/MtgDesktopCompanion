package org.magic.api.externalshop.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.api.cardnexus.configuration.NexusConfig;
import org.api.cardnexus.model.AbstractProduct;
import org.api.cardnexus.model.CardProduct;
import org.api.cardnexus.model.InventoryLine;
import org.api.cardnexus.model.Order;
import org.api.cardnexus.model.SealedProduct;
import org.api.cardnexus.model.User;
import org.api.cardnexus.model.enums.EnumFinishes;
import org.api.cardnexus.model.enums.EnumSealedType;
import org.api.cardnexus.model.requests.InventoryLinesRequest;
import org.api.cardnexus.model.requests.SalesRequest;
import org.api.cardnexus.model.requests.SearchInventoryRequest;
import org.api.cardnexus.model.requests.SearchProductRequest;
import org.api.cardnexus.model.requests.UpdateInventoryRequest;
import org.api.cardnexus.services.InventoryService;
import org.api.cardnexus.services.OrdersService;
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
import org.magic.services.tools.CardNexusTools;
import org.magic.services.tools.MTG;

public class CardNexusExternalShop extends AbstractExternalShop {
    
    
    private ProductsService pService;
    private InventoryService iService;
    private OrdersService oService;

    public CardNexusExternalShop() {
	    pService = new ProductsService();
	    iService = new InventoryService();
	    oService = new OrdersService();
    }
    
    private void init()
    {
	CardNexusTools.initConfig();
	try {
        	    pService.listExpansion(NexusConfig.getDefaultGameValue()); //caching
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
    public MTGStockItem getStockById(EnumItems typeStock, String id) throws IOException {
	var line = iService.getInventoryLine(id);
	var p = pService.getProductById(line.productId());
	return parseStockItem(p, line);
	
	
    }

    @Override
    protected void saveOrUpdateStock(List<MTGStockItem> items) throws IOException {
	
	
	
	
	items.forEach(item->{
	    var listId = item.getTiersAppIds(getName());
	    var req = UpdateInventoryRequest.create()
		    						.setQuantity(item.getQte())
		    						.setComment(item.getComment())
		    						.setLanguage(item.getLanguage());
	    try {
		var ret = iService.updateInventoryLine(listId, req);
		
		logger.info("line updated {}", ret);
		
		
	    } catch (IOException e) {
		logger.error(e);
	    }
	});
	

    }
    
    
    @Override
    protected List<MTGStockItem> loadStock(String search) throws IOException {
	init();
	var ret = new ArrayList<MTGStockItem>();
	
	List<InventoryLine> lines;  
		
	if(!StringUtils.isEmpty(search))
	{
	    lines = iService.inventorySearch(SearchInventoryRequest.create().setName(search).contains());
	}
	else
	{
	    
	    lines = iService.getInventoryLines(InventoryLinesRequest.create());
	}
	
	lines.forEach(il->{
	    	    var product=pService.getProductById(il.productId());
            	    if(product instanceof CardProduct c)
            	    {
            		ret.add(parseStockItem(c,il)); 
            	    }
	 
	});
	
	
	return ret;
    }


    private MTGStockItem parseStockItem(AbstractProduct p, InventoryLine il) {
	var item = new MTGCardStock();
			
		if(p instanceof CardProduct c)
		{
		    
		    try {
			item.setProduct(MTG.getEnabledPlugin(MTGCardsProvider.class).getCardByNumber(c.getPrintNumber(), pService.getExpansionById(c.getExpansionId()).code()));
		    }
		    catch(Exception e)
		    {
			logger.error(e);
			return null;
		    }
		    
		    
		
		    try {
			
			if(!c.getPrices().isEmpty())
			    item.setPrice(c.getPrices().get(il.finish()).cardmarket().marketValue());
			}
			catch(Exception e)
			{
			    logger.error("error gettings price market for {}",c);
			}
		
		}
		
		item.setFoil(il.finish()==EnumFinishes.Foil);
		item.setCondition(aliases.getReversedConditionFor(this, il.condition().getLabel(), EnumCondition.NEAR_MINT));
		item.setDateUpdate(il.updatedAt());
		item.setComment(il.comment());
		item.setQte(il.quantity());
		item.setEtched(il.finish()==EnumFinishes.Etched);
		item.setSigned(il.finish()==EnumFinishes.Signed);
		item.setLanguage(il.language());
		item.getTiersAppIds().put(getName(),il.id());
		
		return item;
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
	throw new IOException("Not Implemented");
    }

    @Override
    public Contact getContactByEmail(String email) throws IOException {
	throw new IOException("Not Implemented");
    }

    @Override
    public List<Contact> listContacts() throws IOException {
	return new ArrayList<>();
    }

    @Override
    public void deleteContact(Contact contact) throws IOException {
	throw new IOException("Not Implemented");

    }

    @Override
    public Contact getContactByLogin(String login, String passw) throws IOException {
	throw new IOException("Not Implemented");
    }

    @Override
    public String saveOrUpdateTransaction(Transaction t) throws IOException {
	throw new IOException("Not Implemented");
    }

    @Override
    public void deleteTransaction(Transaction t) throws IOException {
	throw new IOException("Not Implemented");

    }

    @Override
    public Transaction getTransactionById(String id) throws IOException {
	return parseOrder(oService.getOrder(id));
    }

    @Override
    public List<Transaction> listTransactions(Contact c) throws IOException {
	// TODO Auto-generated method stub
	return new ArrayList<>();
    }

    @Override
    public boolean enableContact(String token) throws IOException {
	return false;
    }

    @Override
    public String getName() {
	return "CardNexus";
    }

    @Override
    protected List<Transaction> loadTransaction() throws IOException {
	
	var ret = new ArrayList<Transaction>();
	
	oService.listOrders(SalesRequest.create()).forEach(o->{
	    ret.add(parseOrder(o));
	});
	return ret;
    }

    private Transaction parseOrder(Order o) {
	 var t = new Transaction();
	    	t.setCurrency(o.currency());
	    	t.setDateCreation(o.placedAt());
	    	t.setDatePayment(o.completedAt());
	    	t.setDateSend(o.shippedAt());
	    	t.setSourceShopId(getName());
	    	t.setContact(parseContact(o.buyer()));
	    	return t;
    }

    private Contact parseContact(User buyer) {
	var c = new Contact();
		
	c.setName(buyer.username());
	c.setCountry(buyer.country());
	
	return c;
    }

  


}
