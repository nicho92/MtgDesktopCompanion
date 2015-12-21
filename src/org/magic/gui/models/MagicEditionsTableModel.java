package org.magic.gui.models;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.DefaultTableModel;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MagicCardsProvider;
import org.magic.db.MagicDAO;

public class MagicEditionsTableModel extends DefaultTableModel{

	String[] columns = new String[] {"code","edition","Count","date","%"};
	
	List<MagicEdition> list;

	private MagicDAO dao;
	private Map<MagicEdition,Integer> mapCount;

	private MagicCardsProvider provider; 
	
	
	public void init(List<MagicEdition> editions ){
		this.list=editions;
		mapCount=new HashMap<MagicEdition,Integer>();
		
		try {
			calculate();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	
	public void calculate() throws SQLException, Exception {
		
		MagicCollection mc = new MagicCollection();
						mc.setName("Library");
		 
		for(MagicEdition me : list)
		{
			mapCount.put(me, 100 *dao.getCardsFromCollection(mc, me).size() / me.getCardCount());
		}
		
	}




	public MagicEditionsTableModel(MagicDAO dao,MagicCardsProvider provider) {
		list = new ArrayList<MagicEdition>();
		this.dao =dao;
		this.provider=provider;
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
				return mapCount.get(e);
			
		return "";
		
			
	}
	
	@Override
	public int getColumnCount() {
		return columns.length;
	}
	
	
}
