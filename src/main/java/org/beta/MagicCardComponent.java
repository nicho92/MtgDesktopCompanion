package org.beta;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

import org.magic.api.beans.MagicCard;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.MagicCardDetailPanel;
import org.magic.gui.components.MagicEditionDetailPanel;
import org.magic.services.MTGConstants;
import org.magic.services.tools.UITools;

public class MagicCardComponent extends MTGUIComponent {

	private static final long serialVersionUID = 1L;


	public void init(MagicCard mc)
	{
		panelCard.init(mc);
		panelEdition.init(mc.getCurrentSet());
	}
	
	
	private MagicCardDetailPanel panelCard;
	private MagicEditionDetailPanel panelEdition;
	
	
	public MagicCardComponent(boolean enableThumbnail, boolean booster, boolean collectionLookup)
	{
		setLayout(new BorderLayout());
		add(getContextTabbedPane(),BorderLayout.CENTER);
		
		
		getContextTabbedPane().setTabPlacement(SwingConstants.LEFT);
		
		panelCard = new MagicCardDetailPanel();
		panelCard.enableThumbnail(enableThumbnail);
		panelCard.enableCollectionLookup(collectionLookup);
		panelEdition = new MagicEditionDetailPanel(booster);
		
		UITools.addTab(getContextTabbedPane(), panelCard);
		UITools.addTab(getContextTabbedPane(), panelEdition);
		
	}
	
	
	
	@Override
	public String getTitle() {
		return "DETAILS";
	}
	
	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_TAB_DETAILS;
	}

}
