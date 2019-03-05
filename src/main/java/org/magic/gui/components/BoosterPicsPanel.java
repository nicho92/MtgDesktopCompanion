package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.Packaging;
import org.magic.api.beans.Packaging.TYPE;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.ThreadManager;
import org.magic.services.extra.PackagesProvider;
import org.magic.tools.ImageTools;

public class BoosterPicsPanel extends JTabbedPane {
	
	private static final long serialVersionUID = 1L;
	static Logger logger = MTGLogger.getLogger(BoosterPicsPanel.class);
	private transient SwingWorker<ImageIcon, SimpleEntry<Packaging, ImageIcon>> sw;
	private MagicEdition ed;
	
	public BoosterPicsPanel() {
		setLayout(new BorderLayout(0, 0));

	}
	

	public MagicEdition getEdition() {
		return ed;
	}
	
	
	public void setEdition(MagicEdition ed) {
		
		this.ed=ed;
		removeAll();
		revalidate();
		
		if(sw!=null && !sw.isDone())
			sw.cancel(true);
		
		
		
		
		if(ed!=null)
		{
			sw = new SwingWorker<ImageIcon, SimpleEntry<Packaging, ImageIcon>>() {
				@Override
				protected void process(List<SimpleEntry<Packaging, ImageIcon>> chunks) {
					chunks.forEach(e->addTab(e.getKey().toString(), new JLabel(e.getValue())));
				}
				
				@Override
				protected ImageIcon doInBackground() {
					
					List<Packaging> l = PackagesProvider.inst().get(ed,TYPE.BOOSTER);
					logger.trace("loading booster :" + l);
					l.forEach(i->
					{
						try {
								publish(new SimpleEntry<>(i,new ImageIcon(resizeBooster(PackagesProvider.inst().get(i)))));
						}catch(Exception e)
						{
							logger.error("error",e);
						}
					});
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
