package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;

import org.apache.logging.log4j.Logger;
import org.magic.api.beans.MTGSealedProduct;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.enums.EnumItems;
import org.magic.services.MTGControler;
import org.magic.services.logging.MTGLogger;
import org.magic.services.providers.SealedProductProvider;
import org.magic.services.threads.ThreadManager;
import org.magic.tools.ImageTools;

public class BoosterPicsPanel extends JTabbedPane {

	private static final long serialVersionUID = 1L;
	static Logger logger = MTGLogger.getLogger(BoosterPicsPanel.class);
	private transient SwingWorker<ImageIcon, SimpleEntry<MTGSealedProduct, ImageIcon>> sw;
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
			sw = new SwingWorker<>() {
				@Override
				protected void process(List<SimpleEntry<MTGSealedProduct, ImageIcon>> chunks) {
					chunks.forEach(e->addTab(e.getKey().toString(), new JLabel(e.getValue())));
				}

				@Override
				protected ImageIcon doInBackground() {

					List<MTGSealedProduct> l = SealedProductProvider.inst().get(ed,EnumItems.BOOSTER);
					logger.trace("loading booster : {}",l);
					l.forEach(i->
					{
						try {
							BufferedImage img = SealedProductProvider.inst().get(i);
							if(img!=null)
								publish(new SimpleEntry<>(i,new ImageIcon(resizeBooster(img))));


						}catch(Exception e)
						{
							logger.error("error ",e);
						}
					});
					return null;
				}
			};
			ThreadManager.getInstance().runInEdt(sw,"load booster pic for " + ed);
		}
	}

	private Image resizeBooster(BufferedImage boosterFor) {

		var d= MTGControler.getInstance().getPictureProviderDimension();
		return ImageTools.resize(ImageTools.trimAlpha(boosterFor), (int)d.getHeight(), (int)d.getWidth()-15);

	}


}
