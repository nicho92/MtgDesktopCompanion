package org.magic.gui.models;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;
import org.magic.api.beans.ShopItem;
import org.magic.api.interfaces.MTGShopper;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

public class ShopItemTableModel extends DefaultTableModel {

	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	
	private String[] columns = new String[]{
				MTGControler.getInstance().getLangService().getCapitalize("WEBSITE"),
				MTGControler.getInstance().getLangService().getCapitalize("SHOP_NAME"),
				MTGControler.getInstance().getLangService().getCapitalize("PRICE"),
				MTGControler.getInstance().getLangService().getCapitalize("SHOP_DATE"),
				MTGControler.getInstance().getLangService().getCapitalize("SHOP_TYPE"),
				MTGControler.getInstance().getLangService().getCapitalize("URL")
	};
	
	private transient List<ShopItem> items;
	
	public void init(String search)
	{
		items.clear();
		for(MTGShopper prov : MTGControler.getInstance().getEnabledShoppers())
		{
			try {
				items.addAll(prov.search(search));
				fireTableDataChanged();
			} catch (Exception e) {
				logger.error("error in init " + search,e);
			}
		}
	}
	

	public ShopItemTableModel() {
		items=new ArrayList<>();
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
		case 1 : return ShopItem.class;
		case 2: return Double.class;
		case 3: return Date.class;
		case 4: return String.class;
		case 5 : return URL.class;
		default : return String.class;
		}
	}
	
	@Override
	public Object getValueAt(int row, int column) {
		try{
			
		ShopItem mp = items.get(row);
		
		switch(column)
		{
			case 0: return mp.getShopName();
			case 1 : return mp;
			case 2: return mp.getPrice();
			case 3 : return mp.getDate();
			case 4: return mp.getType();
			case 5 : return mp.getUrl();
			default : return 0;
		}
		}catch(Exception ioob)
		{
			logger.error(ioob);
			return null;
		}
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return (column==6);
	}

	
	
}
