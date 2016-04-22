package org.magic.gui.models;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.DefaultTableModel;

import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MagicDAO;

public class MagicEditionsTableModel extends DefaultTableModel{

	String[] columns = new String[] {"code","edition","cards numbers","date","%","qte"};
	
	List<MagicEdition> list;

	private MagicDAO dao;
	private Map<MagicEdition,Integer> mapCount;
		
	public List<MagicEdition> getEditions()
	{
		return list;
	}
	
	public void init(List<MagicEdition> editions ){
		this.list=editions;
		mapCount=new HashMap<MagicEdition,Integer>();
		
		try {
			calculate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	
	public void calculate() throws SQLException, Exception {
		
		MagicCollection mc = new MagicCollection();
						mc.setName("Library");
		 
		for(MagicEdition me : list)
		{
			mapCount.put(me, dao.getCardsFromCollection(mc, me).size());
		}
		
	}




	public MagicEditionsTableModel(MagicDAO dao) {
		list = new ArrayList<MagicEdition>();
		this.dao =dao;
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
				return e.getId();
		
			if(column==1)
				return e;

			if(column==2)
				return e.getCardCount();

			if(column==3)
				return e.getReleaseDate();
			
			if(column==4)
			{
				if(e.getCardCount()>0)
					return 100 * mapCount.get(e) / e.getCardCount();
				else
					return 100 * mapCount.get(e) / 1;
			}
			
			if(column==5)
				return mapCount.get(e);
			
		return "";
		
			
	}
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		
		switch(columnIndex)
		{
		case 0:return String.class;
		case 1 : return MagicEdition.class;
		case 2: return Integer.class;
		case 3 : return String.class;
		case 4 : return double.class;
		case 5 : return Integer.class;
		default : return Object.class;
		}
		
		//return super.getColumnClass(columnIndex);
	}
	
	@Override
	public int getColumnCount() {
		return columns.length;
	}
	
	
}
