package org.magic.gui.components.card;

import java.awt.BorderLayout;

import javax.swing.SwingConstants;

import org.magic.api.beans.MagicCard;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.tools.UITools;
import org.utils.patterns.observer.Observable;
import org.utils.patterns.observer.Observer;

public class MagicCardDetailPanel extends MTGUIComponent implements Observer{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	MagicCardMainDetailPanel paneMain;
	MagicCardSubDetailPanel paneSub;
	private boolean full;
	
	@Override
	public String getTitle() {
		return "DETAILS";
	}

	public MagicCardDetailPanel(boolean full) {
		this.full = full;
		setLayout(new BorderLayout());
		paneMain = new MagicCardMainDetailPanel();
		
		if(full)
		{
			getContextTabbedPane().setTabPlacement(SwingConstants.LEFT);
			paneSub = new MagicCardSubDetailPanel();
			getContextTabbedPane().addTab("D", paneMain);
			getContextTabbedPane().addTab("T", paneSub);
			add(getContextTabbedPane(),BorderLayout.CENTER);
		}
		else
		{
			add(paneMain,BorderLayout.CENTER);
		}
		
		
	}
	
	

	public void addObserver(Observer panelDetail) {
		paneMain.addObserver(panelDetail);
	}

	
	
	public void enableCollectionLookup(boolean b)
	{
		paneMain.enableCollectionLookup(b);
	}
	
	
	public void enableThumbnail(boolean b)
	{
		paneMain.enableThumbnail(b);
	}
	
	public void init(MagicCard  mc)
	{
		paneMain.init(mc);
		
		if(full)
			paneSub.init(mc);
	}

	@Override
	public void update(Observable o, Object arg) {
		paneMain.update(o, arg);
		
	}
	
	
}
