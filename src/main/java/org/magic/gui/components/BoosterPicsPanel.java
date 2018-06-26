package org.magic.gui.components;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;

import org.magic.api.beans.MagicEdition;
import org.magic.services.ThreadManager;
import org.magic.services.extra.BoosterPicturesProvider;
import org.w3c.dom.NodeList;

public class BoosterPicsPanel extends JTabbedPane {
	
	private transient BoosterPicturesProvider provider;
	
	
	public BoosterPicsPanel() {
		setLayout(new BorderLayout(0, 0));
		provider = new BoosterPicturesProvider();
	}

	private static final long serialVersionUID = 1L;
	
	public void setEdition(MagicEdition ed) {
		
		removeAll();
		revalidate();
		
		
		if(ed!=null)
			ThreadManager.getInstance().execute(() -> adds(ed,provider.getBoostersUrl(ed)),"load booster pic for " + ed);
	}

	private void adds(MagicEdition ed, NodeList boostersUrl) {
		for(int i =0; i<boostersUrl.getLength();i++)
		{
			addTab(String.valueOf(i+1), new JLabel(new ImageIcon(provider.getBoosterFor(ed, i))));
		}
	}

}
