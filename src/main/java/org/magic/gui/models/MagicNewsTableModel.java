package org.magic.gui.models;

import java.io.IOException;

import javax.swing.table.DefaultTableModel;

import org.magic.api.beans.MagicNews;
import org.magic.services.MTGControler;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

public class MagicNewsTableModel extends DefaultTableModel{

	private static final String[] COLUMNS = {MTGControler.getInstance().getLangService().getCapitalize("RSS_TITLE"),
										   MTGControler.getInstance().getLangService().getCapitalize("RSS_DATE")};
	
	private transient SyndFeedInput input;
	private transient SyndFeed feed;
	
	public MagicNewsTableModel() {
		input = new SyndFeedInput();
	}
	
	@Override
	public int getRowCount() {
		if(feed==null)
			return 0;
		
		return feed.getEntries().size();
		
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
			case 0 : return feed.getEntries().get(row).getTitle();
			case 1 : return feed.getEntries().get(row).getPublishedDate();
			default : return "";
		}
	}
	
	public SyndEntry getEntryAt(int row)
	{
		return feed.getEntries().get(row);
	}
	
	
	public void init(MagicNews rssBean) throws FeedException, IOException {
		feed = input.build(new XmlReader(rssBean.getUrl()));
	}

}
