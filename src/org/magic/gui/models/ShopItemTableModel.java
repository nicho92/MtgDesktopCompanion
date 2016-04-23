package org.magic.gui.models;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.ShopItem;
import org.magic.api.interfaces.MagicShopper;
import org.magic.tools.MagicFactory;

public class ShopItemTableModel extends DefaultTableModel {

	  static final Logger logger = LogManager.getLogger(ShopItemTableModel.class.getName());

	  String columns[] = new String[]{"Site","Name","Price","date","type","url"};
			
	List<MagicShopper> shopProviders;
	MagicCard mc;
	MagicEdition me;
	
	String search="";
	List<ShopItem> items;
	
	public void init(String search)
	{
		items.clear();
		for(MagicShopper prov : shopProviders)
		{
			try {
				if(prov.isEnable())
					items.addAll(prov.search(search));
			} catch (Exception e) {
				logger.error(e);
				e.printStackTrace();
			}
		}
	}
	

	public ShopItemTableModel() {
		shopProviders = new ArrayList<MagicShopper>();
		items=new ArrayList<ShopItem>();
		shopProviders=MagicFactory.getInstance().getShoppers();
	}
	
	
	
	
	@Override
	public String getColumnName(int column) {
		return columns[column];
	}
	
	@Override
	public int getRowCount() {
		if(items!=null)
			return items.size();
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
		case 1 : return String.class;
		case 2: return Double.class;
		case 3: return Date.class;
		case 4: return String.class;
		case 5 : return URL.class;
		default : return String.class;
		}
	}
	
	public ShopItem getItem(int row)
	{
		return items.get(row);
	}
	
	@Override
	public Object getValueAt(int row, int column) {
		try{
			
		ShopItem mp = items.get(row);
		
		switch(column)
		{
			case 0: return mp.getShopName();
			case 1 : return mp.getName();
			case 2: return mp.getPrice();
			case 3 : return mp.getDate();
			case 4: return mp.getType();
			case 5 : return mp.getUrl();
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
