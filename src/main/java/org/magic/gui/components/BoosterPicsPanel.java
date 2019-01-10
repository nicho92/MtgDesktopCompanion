package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;
import org.magic.api.beans.MagicEdition;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.ThreadManager;
import org.magic.services.extra.BoosterPicturesProvider;
import org.magic.tools.ImageTools;
import org.w3c.dom.NodeList;

public class BoosterPicsPanel extends JTabbedPane {
	
	private transient BoosterPicturesProvider provider;
	private static final long serialVersionUID = 1L;
	static Logger logger = MTGLogger.getLogger(BoosterPicsPanel.class);

	
	public BoosterPicsPanel() {
		setLayout(new BorderLayout(0, 0));
		provider = new BoosterPicturesProvider();
	}
	
	public void setEdition(MagicEdition ed) {
		removeAll();
		revalidate();
		
		if(ed!=null)
		{
			SwingWorker<ImageIcon, ImageIcon> sw = new SwingWorker<ImageIcon, ImageIcon>() {
				int n=1;
				@Override
				protected void process(List<ImageIcon> chunks) {
					addTab(String.valueOf(n++), new JLabel(chunks.get(0)));
				}
				
				@Override
				protected ImageIcon doInBackground() throws Exception {
					
					NodeList l = provider.getBoostersUrl(ed);
					for(int i =0; i<l.getLength();i++)
					{
						try {
							publish(new ImageIcon(resizeBooster(provider.getBoosterFor(ed, i))));
							
						}catch(Exception e)
						{
							logger.error(e);
						}
					}
					return null;
				}
			};
			ThreadManager.getInstance().runInEdt(sw,"load booster pic for " + ed);
		}
	}

	private Image resizeBooster(BufferedImage boosterFor) {
		
		Dimension d= MTGControler.getInstance().getPictureProviderDimension();
		return ImageTools.resize(ImageTools.trimAlpha(boosterFor), (int)d.getHeight(), (int)d.getWidth()-15);
		
	}

}
