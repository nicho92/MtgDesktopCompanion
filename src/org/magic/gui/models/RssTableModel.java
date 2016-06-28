package org.magic.gui.models;

import java.io.IOException;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.magic.api.beans.RSSBean;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

public class RssTableModel extends DefaultTableModel{

	private final static String[] COLUMN_NAMES = {"Date","Title"};
	
	SyndFeedInput input;
	
	SyndFeed feed;
	
	public RssTableModel() {
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
		return COLUMN_NAMES.length;
	}
	
	@Override
	public String getColumnName(int column) {
		return COLUMN_NAMES[column];
	}
	
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
	
	@Override
	public Object getValueAt(int row, int column) {
		switch(column){
			case 0 : return feed.getEntries().get(row).getUpdatedDate();
			case 1 : return feed.getEntries().get(row).getTitle();
			default : return "";
		}
	}
	
	public SyndEntry getEntryAt(int row)
	{
		return feed.getEntries().get(row);
	}
	
	
	public void init(RSSBean rssBean) throws IllegalArgumentException, FeedException, IOException {
		feed = input.build(new XmlReader(rssBean.getUrl()));
	}

}
