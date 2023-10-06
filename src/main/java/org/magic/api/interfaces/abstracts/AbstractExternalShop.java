package org.magic.api.interfaces.abstracts;

import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.shop.Transaction;
import org.magic.api.interfaces.MTGExternalShop;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.services.tools.MTG;

public abstract class AbstractExternalShop extends AbstractMTGPlugin implements MTGExternalShop {


	protected abstract List<Transaction> loadTransaction() throws IOException;
	protected abstract List<MTGStockItem> loadStock(String search) throws IOException;
	protected abstract void saveOrUpdateStock(List<MTGStockItem> it) throws IOException ;

	protected Map<MTGStockItem, Map.Entry<Integer,Double>> itemsBkcp;




	@Override
	public PLUGINS getType() {
		return PLUGINS.EXTERNAL_SHOP;
	}

	protected AbstractExternalShop() {
		itemsBkcp = new HashMap<>();
	}

	@Override
	public List<Transaction> listTransaction() throws IOException {

		return loadTransaction();
	}

	@Override
	public List<MTGStockItem> listStock(String search) throws IOException {
		var list= loadStock(search);
		itemsBkcp.clear();
		list.forEach(item->{
			itemsBkcp.put(item, new SimpleEntry<>(item.getQte(), item.getPrice()) );
		});
		return list;
	}

	@Override
	public void saveOrUpdateStock(List<MTGStockItem> items,boolean allShop) throws IOException {
			saveOrUpdateStock(items);

			if(allShop)
			{
				for(var it :items )
				{
					for(var extComName :  it.getTiersAppIds().keySet().stream().filter(s->!s.equalsIgnoreCase(getName())).toList())
					{
						logger.debug("Updating {} on {} with id={}",it.getProduct().getName(),extComName,it.getTiersAppIds(extComName));
						it.setId(Integer.parseInt( it.getTiersAppIds(extComName)));
						MTG.getPlugin(extComName, MTGExternalShop.class).saveOrUpdateStock(it, false);
					}


				}
			}
	}


	@Override
	public void saveOrUpdateStock(MTGStockItem it,boolean allShop) throws IOException {
		saveOrUpdateStock(List.of(it),allShop);
	}



	//TODO make a function to update stock in cascade with transaction
	@Override
	public void updateStockFromTransaction(Transaction t) throws IOException {
		t.getItems().forEach(msi->{

			if(msi.getTiersAppIds(getName())!=null)
			{
				logger.info("{} is synchronized with {}",msi.getProduct(), getName());
			}

		});



	}

	@Override
	public void deleteTransaction(List<Transaction> list) throws IOException {
		for(Transaction t : list)
			deleteTransaction(t);
	}


}


