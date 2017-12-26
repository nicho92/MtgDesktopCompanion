package org.magic.gui.models;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;

import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.services.IconSetProvider;
import org.magic.services.MTGControler;

public class MagicEditionsTableModel extends DefaultTableModel{

	String[] columns = new String[] {
			MTGControler.getInstance().getLangService().getCapitalize("EDITION_CODE"),
			MTGControler.getInstance().getLangService().getCapitalize("EDITION"),
			MTGControler.getInstance().getLangService().getCapitalize("EDITION_SIZE"),
			MTGControler.getInstance().getLangService().getCapitalize("DATE_RELEASE"),
			MTGControler.getInstance().getLangService().getCapitalize("PC_COMPLETE"),
			MTGControler.getInstance().getLangService().getCapitalize("QTY"),
			MTGControler.getInstance().getLangService().getCapitalize("EDITION_TYPE"),
			MTGControler.getInstance().getLangService().getCapitalize("EDITION_BLOCK"),
			MTGControler.getInstance().getLangService().getCapitalize("EDITION_ONLINE")
	};
	
	List<MagicEdition> list;

	private Map<MagicEdition,Integer> mapCount;
	
	int countTotal=0;
	int countDefaultLibrary=0;

	
	public List<MagicEdition> getEditions()
	{
		return list;
	}
	
	public void init(List<MagicEdition> editions ){
		this.list=editions;
		mapCount=new TreeMap<MagicEdition,Integer>();
		
		try {
			calculate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	public void calculate() throws SQLException, Exception {
		
		MagicCollection mc = new MagicCollection(MTGControler.getInstance().get("default-library"));
		Map<String,Integer> temp = MTGControler.getInstance().getEnabledDAO().getCardsCountGlobal(mc);
		countDefaultLibrary=0;
		countTotal=0;
		for(MagicEdition me : list)
		{
			mapCount.put(me, (temp.get(me.getId())==null)?0:temp.get(me.getId()));
			countDefaultLibrary+=mapCount.get(me);
		}
	
		for(MagicEdition me : list)
			countTotal+=me.getCardCount();

	}




	public int getCountTotal() {
		return countTotal;
	}

	public void setCountTotal(int countTotal) {
		this.countTotal = countTotal;
	}

	public int getCountDefaultLibrary() {
		return countDefaultLibrary;
	}

	public void setCountDefaultLibrary(int countDefaultLibrary) {
		this.countDefaultLibrary = countDefaultLibrary;
	}

	public MagicEditionsTableModel() {
		list = new ArrayList<MagicEdition>();
		
	}

	@Override
	public String getColumnName(int column) {
		return columns[column];
	}
	
	@Override
	public int getRowCount() {
		if(list==null)
			return 0;
		
		return list.size();
	}
	
	@Override
	public Object getValueAt(int row, int column) {
		MagicEdition e =  list.get(row);
			if(column==0)
				return IconSetProvider.getInstance().get24(e.getId());
		
			if(column==1)
				return e;

			if(column==2)
				return e.getCardCount();

			if(column==3)
				return e.getReleaseDate();
			
			if(column==4)
			{
				if(e.getCardCount()>0)
					return (double) mapCount.get(e) / e.getCardCount();
				else
					return  (double) mapCount.get(e) / 1;
			}
			
			if(column==5)
				return mapCount.get(e);
			
			if(column==6)
				return e.getType();

			if(column==7)
				return e.getBlock();
			
			if(column==8)
				return e.isOnlineOnly();
			
			
		return "";
		
			
	}
	
	

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		
		switch(columnIndex)
		{
			case 0:return ImageIcon.class;
			case 1 : return MagicEdition.class;
			case 2: return Integer.class;
			case 3 : return String.class;
			case 4 : return double.class;
			case 5 : return Integer.class;
			case 8 : return Boolean.class;
			default : return Object.class;
		}
	}
	
	@Override
	public int getColumnCount() {
		return columns.length;
	}
	
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
}
