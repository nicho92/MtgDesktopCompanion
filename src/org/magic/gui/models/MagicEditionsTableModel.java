package org.magic.gui.models;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.db.MagicDAO;

public class MagicEditionsTableModel extends DefaultTableModel{

	String[] columns = new String[] {"code","edition","Count","date","%"};
	
	List<MagicEdition> list;

	private MagicDAO dao;
	
	public void init(List<MagicEdition> editions ){
		this.list=editions;
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
				MagicCollection mc = new MagicCollection();
							 mc.setName("Library");
				try {
					return (dao.getCardsFromCollection(mc,e).size()*100)/e.getCardCount();
				} catch (SQLException e1) {
					return -1;
				}
			}
			
			
		return "";
		
			
	}
	
	@Override
	public int getColumnCount() {
		return columns.length;
	}
	
	
}
