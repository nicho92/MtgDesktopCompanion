package org.magic.gui.components;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.apache.log4j.Logger;
import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MTGNotification.MESSAGE_TYPE;
import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.abstracts.AbstractCardExport.MODS;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.ThreadManager;

public class JExportButton extends JButton {

	private transient MTGCardsExport exp;
	protected transient Logger logger = MTGLogger.getLogger(JExportButton.class);
	private JMenuItem item;
	
	
	public JExportButton() {
		init();
	}
	
	public void init()
	{
		setIcon(MTGConstants.ICON_EXPORT);
		setToolTipText(MTGControler.getInstance().getLangService().getCapitalize("EXPORT_RESULTS"));
		addActionListener(ae -> {
			JPopupMenu menu = new JPopupMenu();
			for (final MTGCardsExport exporter : MTGControler.getInstance().listEnabled(MTGCardsExport.class)) {
				if (exporter.getMods() == MODS.BOTH || exporter.getMods() == MODS.EXPORT) {
					JMenuItem it = new JMenuItem();
					it.setIcon(exporter.getIcon());
					it.setText(exporter.getName());
					menu.add(it);
				}
			}
			
			Component c = (Component) ae.getSource();
			Point p = c.getLocationOnScreen();
			menu.show(c, 0, 0);
			menu.setLocation(p.x, p.y + c.getHeight());
		});
	}
	
	public JMenuItem getSelectedItem()
	{
		return item;
	}
	
	
	
	public void init(ActionListener l,List<MagicCard> list,AbstractBuzyIndicatorComponent obs) {
		item.addActionListener(l);
		
		JFileChooser jf = new JFileChooser(".");
		jf.setSelectedFile(new File("search" + exp.getFileExtension()));
		int result = jf.showSaveDialog(null);
		final File f = jf.getSelectedFile();
		obs.start(list.size()); 
		exp.addObserver(obs);
		
		if (result == JFileChooser.APPROVE_OPTION)
			ThreadManager.getInstance().execute(() -> {
				try {
					exp.export(list, f);
					obs.end();
					MTGControler.getInstance().notify(new MTGNotification(
							exp.getName() + " "+ MTGControler.getInstance().getLangService().get("FINISHED"),
							MTGControler.getInstance().getLangService().combine("EXPORT", "FINISHED"),
							MESSAGE_TYPE.INFO
							));
				} catch (Exception e) {
					logger.error(e);
					obs.end();
					MTGControler.getInstance().notify(new MTGNotification(MTGControler.getInstance().getLangService().getError(),e));
				}
				finally {
					exp.removeObserver(obs);
				}
			}, "export search " + exp);
		
	}
}
