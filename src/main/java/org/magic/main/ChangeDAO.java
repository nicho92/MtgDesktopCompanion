package org.magic.main;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;

public class ChangeDAO {
	public static void main(String[] args) 
	{
		MTGControler.getInstance().loadAccountsConfiguration();
		
		var cboDao  = UITools.createComboboxPlugins(MTGDao.class, true);
		var cboProvider = UITools.createComboboxPlugins(MTGCardsProvider.class, true);
		
		var btnValid = new JButton("Validate");
		
		var pane = new JPanel();
		pane.setLayout(new BorderLayout());		
		pane.add(UITools.createFlowCenterPanel(cboProvider,cboDao),BorderLayout.CENTER);
		pane.add(btnValid,BorderLayout.SOUTH);
		
		
		var diag = MTGUIComponent.createJDialog(MTGUIComponent.build(pane, "Change Providers and DAO", MTGConstants.ICON_TAB_DAO), false, false);
		diag.setVisible(true);
		
		
		btnValid.addActionListener(al->{
			
			var selectedDao = (MTGDao)cboDao.getSelectedItem();
			selectedDao.enable(true);
			MTGControler.getInstance().setProperty(selectedDao, selectedDao.isEnable());
			
			MTG.listPlugins(MTGDao.class).stream().filter(p->p!=selectedDao).forEach(p->{
					p.enable(false);
					MTGControler.getInstance().setProperty(p, p.isEnable());
			});
			
			var selectedProvider = (MTGCardsProvider)cboProvider.getSelectedItem();
			selectedProvider.enable(true);
			MTGControler.getInstance().setProperty(selectedProvider, selectedProvider.isEnable());
			
			MTG.listPlugins(MTGCardsProvider.class).stream().filter(p->p!=selectedProvider).forEach(p->{
					p.enable(false);
					MTGControler.getInstance().setProperty(p, p.isEnable());
			});
			
			
			
			System.exit(0);
			
		});
		
		
		
	}

}
