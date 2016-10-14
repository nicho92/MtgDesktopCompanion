package org.magic.gui.models;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MagicPricesProvider;
import org.magic.services.MagicFactory;

public class CardsPriceTableModel extends DefaultTableModel {

	  static final Logger logger = LogManager.getLogger(CardsPriceTableModel.class.getName());

	  String columns[] = new String[]{"Site","Price","Currency","Seller","quality","langage","url"};
			
	List<MagicPricesProvider> providers;
	MagicCard mc;
	MagicEdition me;
	
	
	List<MagicPrice> prices;
	
	public void init(MagicCard mc,MagicEdition me)
	{
		prices.clear();
		for(MagicPricesProvider prov : providers)
		{
			try {
				if(prov.isEnable())
					prices.addAll(prov.getPrice(me, mc));
			} catch (Exception e) {
				logger.error(e);
				e.printStackTrace();
			}
		}
	}
	

	public CardsPriceTableModel() {
		providers = new ArrayList<MagicPricesProvider>();
		prices=new ArrayList<MagicPrice>();
		providers=MagicFactory.getInstance().getPricers();
	}
	
	
	public void addProvider(MagicPricesProvider provider)
	{
		providers.add(provider);
	}
	
	
	@Override
	public String getColumnName(int column) {
		return columns[column];
	}
	
	@Override
	public int getRowCount() {
		if(prices!=null)
			return prices.size();
		else
			return 0;
	}
	
	@Override
	public int getColumnCount() {
		return columns.length;
	}
	
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch(columnIndex)
		{
		case 0:return String.class;
		case 1 : return Double.class;
		case 2: return String.class;
		case 3 : return String.class;
		case 4 : return String.class;
		case 5 : return String.class;
		default : return URL.class;
		}
	}
	
	
	@Override
	public Object getValueAt(int row, int column) {
		try{
			
		MagicPrice mp = prices.get(row);
		
		switch(column)
		{
			case 0: return mp.getSite();
			case 1 : return mp.getValue();
			case 2: return mp.getCurrency();
			case 3 : return mp.getSeller();
			case 4 : return mp.getQuality();
			case 5 : return mp.getLanguage();
			case 6 : return mp.getUrl();
		default : return 0;
		}
		}catch(IndexOutOfBoundsException ioob)
		{
			logger.error(ioob);
			return null;
		}
	}

	
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	
	
}
