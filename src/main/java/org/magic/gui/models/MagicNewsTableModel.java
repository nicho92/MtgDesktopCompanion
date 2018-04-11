package org.magic.gui.models;

import java.io.IOException;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.magic.api.beans.MagicNews;
import org.magic.api.beans.MagicNewsContent;
import org.magic.services.MTGControler;

public class MagicNewsTableModel extends DefaultTableModel{

	private static final String[] COLUMNS = {
											 MTGControler.getInstance().getLangService().getCapitalize("RSS_TITLE"),
										     MTGControler.getInstance().getLangService().getCapitalize("RSS_DATE"),
										     MTGControler.getInstance().getLangService().getCapitalize("RSS_AUTHOR")
										     };
	private transient List<MagicNewsContent> ret;

	
	
	@Override
	public int getRowCount() {
		if(ret==null)
			return 0;
		
		return ret.size();
		
	}
	
	@Override
	public int getColumnCount() {
		return COLUMNS.length;
	}
	
	@Override
	public String getColumnName(int column) {
		return COLUMNS[column];
	}
	
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
	
	@Override
	public Object getValueAt(int row, int column) {
		switch(column){
			case 0 : return ret.get(row).getTitle();
			case 1 : return ret.get(row).getDate();
			case 2 : return ret.get(row).getAuthor();
			default : return "";
		}
	}
	
	public MagicNewsContent getEntryAt(int row)
	{
		return ret.get(row);
	}
	
	
	public void init(MagicNews rssBean) throws IOException {
		ret = rssBean.getProvider().listNews(rssBean);
	}

}
