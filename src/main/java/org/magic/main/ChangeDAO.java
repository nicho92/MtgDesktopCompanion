package org.magic.main;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

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
		
		var cbo  = UITools.createComboboxPlugins(MTGDao.class, true);
		var btnValid = new JButton("Validate");
		
		var pane = new JPanel();
		pane.setLayout(new BorderLayout());		
		pane.add(cbo,BorderLayout.CENTER);
		pane.add(btnValid,BorderLayout.SOUTH);
		
		
		var diag = MTGUIComponent.createJDialog(MTGUIComponent.build(pane, "Change DAO", MTGConstants.ICON_TAB_DAO), false, false);
		diag.setVisible(true);
		
		
		btnValid.addActionListener(al->{
			
			var selectedProvider = (MTGDao)cbo.getSelectedItem();
			selectedProvider.enable(true);
			MTGControler.getInstance().setProperty(selectedProvider, selectedProvider.isEnable());
			
			MTG.listPlugins(MTGDao.class).stream().filter(p->p!=selectedProvider).forEach(p->{
					p.enable(false);
					MTGControler.getInstance().setProperty(p, p.isEnable());
			});
			
			
			System.exit(0);
			
		});
		
		
		
	}

}
