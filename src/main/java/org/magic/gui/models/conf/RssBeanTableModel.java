package org.magic.gui.models.conf;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.magic.api.beans.RSSBean;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

public class RssBeanTableModel extends DefaultTableModel{

	private final static String[] COLUMN_NAMES = {"Name","URL","Categorie"};
	
	List<RSSBean> listRSS;
	
	public RssBeanTableModel() {
		listRSS = MTGControler.getInstance().getRss();
	}
	
	@Override
	public int getRowCount() {
		if(listRSS==null)
			return 0;
		
		return listRSS.size();
		
	}
	
	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}
	
	@Override
	public String getColumnName(int column) {
		return COLUMN_NAMES[column];
	}
	
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return true;
	}
	
	@Override
	public Object getValueAt(int row, int column) {
		switch(column){
			case 0 : return listRSS.get(row);	
			case 2 : return listRSS.get(row).getCategorie();
			case 1 : return listRSS.get(row).getUrl();
			default : return "";
		}
	}
	
	@Override
	public void setValueAt(Object aValue, int row, int column) {
		switch (column) {
		case 2: listRSS.get(row).setCategorie(aValue.toString());break;
		case 1: try {
				listRSS.get(row).setUrl(new URL(aValue.toString()));
			} catch (MalformedURLException e) {
				MTGLogger.printStackTrace(e);
			}break;
		case 0: listRSS.get(row).setName(aValue.toString());break;
		default:break;
		}
		
	}
	

}
