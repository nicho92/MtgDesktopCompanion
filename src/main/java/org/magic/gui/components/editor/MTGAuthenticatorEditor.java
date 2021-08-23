package org.magic.gui.components.editor;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.magic.api.beans.AccountAuthenticator;
import org.magic.api.interfaces.MTGPlugin;
import org.magic.gui.models.conf.MapTableModel;
import org.magic.gui.renderer.PluginIconListRenderer;
import org.magic.services.AccountsManager;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.tools.UITools;

public class MTGAuthenticatorEditor extends JPanel {

	private static final long serialVersionUID = 1L;
	private MapTableModel<String, String> tableModel;
	
	public MTGAuthenticatorEditor() {
		
		MTGControler.getInstance().loadAccountsConfiguration();
		
		tableModel = new MapTableModel<>();
		
		var table = UITools.createNewTable(tableModel);
		var panelWest = new JPanel();
	
		var panelButtons = new JPanel();
		var comboBox = UITools.createCombobox(AccountsManager.inst().listAvailablePlugins());
		var btnNewButton = new JButton(MTGConstants.ICON_NEW);
		var listModel = new DefaultListModel<MTGPlugin>();
		var list = new JList<MTGPlugin>(listModel);
		var btnSave = UITools.createBindableJButton("",MTGConstants.ICON_SAVE, KeyEvent.VK_S,"Save");
		
		setLayout(new BorderLayout(0, 0));
		panelWest.setLayout(new BorderLayout(0, 0));
			
		add(new JScrollPane(table), BorderLayout.CENTER);
		add(panelWest, BorderLayout.WEST);
		panelButtons.add(comboBox);
		panelButtons.add(btnNewButton);
		panelWest.add(panelButtons, BorderLayout.NORTH);
		panelWest.add(new JScrollPane(list), BorderLayout.CENTER);
		panelWest.add(btnSave,BorderLayout.SOUTH);
		
		
		tableModel.setWritable(true);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listModel.addAll(AccountsManager.inst().getKeys().keySet());
		comboBox.setRenderer(new PluginIconListRenderer());
		list.setCellRenderer(new PluginIconListRenderer());
		
		
		btnSave.addActionListener(al->AccountsManager.inst().saveConfig());
		
		btnNewButton.addActionListener(al->{
			
			var auth = new AccountAuthenticator();
			for (String k : ((MTGPlugin)comboBox.getSelectedItem()).listAuthenticationAttributes())
			{
					auth.addToken(k, "");
			}
			
			AccountsManager.inst().addAuthentication((MTGPlugin)comboBox.getSelectedItem(), auth);
			listModel.addElement((MTGPlugin)comboBox.getSelectedItem());
			list.updateUI();
			
		});
		
		
		list.addListSelectionListener(al->{
			if(!al.getValueIsAdjusting())
			{
				int idx = list.getSelectedIndex();
				if (idx > -1)
				{
						MTGPlugin plug = listModel.getElementAt(idx);
						tableModel.init(plug.getAuthenticator().getTokens());
				}
	      }
		});
	}

	public void init(AccountAuthenticator account)
	{
		tableModel.init(account.getTokens());
	}
	
	
	
	
}
