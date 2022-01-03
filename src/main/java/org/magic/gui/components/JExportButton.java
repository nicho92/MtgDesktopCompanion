package org.magic.gui.components;

import static org.magic.tools.MTG.listEnabledPlugins;

import java.awt.Point;
import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.magic.api.beans.MagicCardAlert;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.MTGCardsExport.MODS;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.threads.ThreadManager;
import org.magic.services.workers.CardExportWorker;
import org.magic.services.workers.StockExportWorker;

public class JExportButton extends JButton {
	private static final long serialVersionUID = 1L;
	private MODS mod;

	public JExportButton(MODS mod) {
		this.mod=mod;
		setName(mod.toString());
		
		if(mod == MODS.EXPORT)
			setIcon(MTGConstants.ICON_EXPORT);
		else
			setIcon(MTGConstants.ICON_IMPORT);
	}
	
	public void initAlertsExport(Callable<List<MagicCardAlert>> callable, AbstractBuzyIndicatorComponent lblLoading ) {
		
		addActionListener(ae -> {
			var menu = new JPopupMenu();

			for (MTGCardsExport exp : listEnabledPlugins(MTGCardsExport.class)) {
				if (exp.getMods() == MODS.BOTH || exp.getMods() == mod) {
					var it = new JMenuItem(exp.getName(), exp.getIcon());
					it.addActionListener(exportEvent -> {
						int result = JFileChooser.CANCEL_OPTION;
						File f = null;
						List<MagicCardAlert> export  = null;
						
						try {
							export = callable.call();
						}
						catch(Exception e)
						{
							MTGControler.getInstance().notify(e);
							return;
						}
						
						if(exp.needFile())
						{
							var jf = new JFileChooser(".");
							jf.setSelectedFile(new File("alerts" + exp.getFileExtension()));
							result = jf.showSaveDialog(null);
							f = jf.getSelectedFile();
						}
						else
						{
							result = JFileChooser.APPROVE_OPTION;
						}
						
						if (result == JFileChooser.APPROVE_OPTION)
						{
								lblLoading.start(export.size()); 
								ThreadManager.getInstance().runInEdt(new CardExportWorker(exp,export.stream().map(MagicCardAlert::getCard).toList(), lblLoading, f), "export alerts " + exp);
						}
					});

					menu.add(it);
				}
				
			}
			Point p = this.getLocationOnScreen();
			menu.show(this, 0, 0);
			menu.setLocation(p.x, p.y + this.getHeight());
		});
	}
	
	public void initCardsExport(Callable<MagicDeck> callable, AbstractBuzyIndicatorComponent lblLoading ) {
		
		addActionListener(ae -> {
			var menu = new JPopupMenu();

			for (MTGCardsExport exp : listEnabledPlugins(MTGCardsExport.class)) {
				if (exp.getMods() == MODS.BOTH || exp.getMods() == mod) {
					var it = new JMenuItem(exp.getName(), exp.getIcon());
					it.addActionListener(exportEvent -> {
						int result = JFileChooser.CANCEL_OPTION;
						File f = null;
						MagicDeck export  = null;
						
						try {
							export = callable.call();
						}
						catch(Exception e)
						{
							MTGControler.getInstance().notify(e);
							return;
						}
						
						if(exp.needFile())
						{
							var jf = new JFileChooser(".");
							jf.setSelectedFile(new File(export.getName() + exp.getFileExtension()));
							result = jf.showSaveDialog(null);
							f = jf.getSelectedFile();
						}
						else
						{
							result = JFileChooser.APPROVE_OPTION;
						}
						
						if (result == JFileChooser.APPROVE_OPTION)
						{
								lblLoading.start(export.getMainAsList().size()); 
								ThreadManager.getInstance().runInEdt(new CardExportWorker(exp, export, lblLoading, f), "export search " + exp);
						}
					});

					menu.add(it);
				}
				
			}
			Point p = this.getLocationOnScreen();
			menu.show(this, 0, 0);
			menu.setLocation(p.x, p.y + this.getHeight());
		});
	}
	
	public void initStockExport(Callable<List<MagicCardStock>> callable,AbstractBuzyIndicatorComponent lblLoading) {
		
		addActionListener(ae -> {
			var menu = new JPopupMenu();

			for (MTGCardsExport exp : listEnabledPlugins(MTGCardsExport.class)) {
				if (exp.getMods() == MODS.BOTH || exp.getMods() == mod) {
					var it = new JMenuItem(exp.getName(), exp.getIcon());
					it.addActionListener(exportEvent -> {
						int result = JFileChooser.CANCEL_OPTION;
						File f = null;
						List<MagicCardStock> export  = null;
						
						try {
							export = callable.call();
						}
						catch(Exception e)
						{
							MTGControler.getInstance().notify(e);
							return;
						}
						
						
						if(exp.needFile())
						{
							var jf = new JFileChooser(".");
							jf.setSelectedFile(new File("stocks" + exp.getFileExtension()));
							result = jf.showSaveDialog(null);
							f = jf.getSelectedFile();
						}
						else
						{
							result = JFileChooser.APPROVE_OPTION;
						}
						
						
						if (result == JFileChooser.APPROVE_OPTION)
						{
							
								lblLoading.start(export.size()); 
								ThreadManager.getInstance().runInEdt(new StockExportWorker(exp, export, lblLoading, f), "export search " + exp);
						}
					});

					menu.add(it);
				}
				
			}
			Point p = this.getLocationOnScreen();
			menu.show(this, 0, 0);
			menu.setLocation(p.x, p.y + this.getHeight());
		});
		
	}


	
	
}
