package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.stream.Collectors;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MTGNotification.MESSAGE_TYPE;
import org.magic.api.beans.technical.AccountAuthenticator;
import org.magic.api.interfaces.MTGPlugin;
import org.magic.gui.models.MapTableModel;
import org.magic.gui.renderer.PluginIconListRenderer;
import org.magic.services.AccountsManager;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.tools.FileTools;
import org.magic.tools.UITools;

public class MTGAuthenticatorEditor extends JPanel {

	private static final long serialVersionUID = 1L;
	private MapTableModel<String, String> tableModel;
	
	public MTGAuthenticatorEditor() {
			tableModel = new MapTableModel<>() {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public boolean isCellEditable(int row, int column) {
				return column>0;
			}
		};
		
		var table = UITools.createNewTable(tableModel);
		var panelWest = new JPanel();
		var panelSouth = new JPanel();
	
		var panelButtons = new JPanel();
		var comboBox = UITools.createCombobox(AccountsManager.inst().listAvailablePlugins());
		var btnNewButton = new JButton(MTGConstants.ICON_NEW);
		var listModel = new DefaultListModel<MTGPlugin>();
		var list = new JList<MTGPlugin>(listModel);
		var btnSave = UITools.createBindableJButton("",MTGConstants.ICON_SAVE, KeyEvent.VK_S,"Save");
		var btnExportConfig = UITools.createBindableJButton("",MTGConstants.ICON_EXPORT, KeyEvent.VK_E,"Export");
		var btnImportConfig = UITools.createBindableJButton("",MTGConstants.ICON_IMPORT, KeyEvent.VK_I,"Import");
		var btnDeleteConfig = UITools.createBindableJButton("",MTGConstants.ICON_DELETE, KeyEvent.VK_DELETE,"Delete");
		var btnKeyGenerator = UITools.createBindableJButton("",MTGConstants.ICON_TAB_LOCK, KeyEvent.VK_K,"Create KeyFile");
		setLayout(new BorderLayout(0, 0));
		panelWest.setLayout(new BorderLayout(0, 0));
			
		add(new JScrollPane(table), BorderLayout.CENTER);
		add(panelWest, BorderLayout.WEST);
		panelButtons.add(btnKeyGenerator);
		panelButtons.add(comboBox);
		panelButtons.add(btnNewButton);
		panelWest.add(panelButtons, BorderLayout.NORTH);
		panelWest.add(new JScrollPane(list), BorderLayout.CENTER);
		panelWest.add(panelSouth,BorderLayout.SOUTH);
		panelSouth.add(btnSave);
		panelSouth.add(btnDeleteConfig);
		panelSouth.add(btnImportConfig);
		panelSouth.add(btnExportConfig);
		
		btnDeleteConfig.setEnabled(false);
		tableModel.setWritable(true);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listModel.addAll(AccountsManager.inst().listAuthEntries().keySet().stream().sorted().collect(Collectors.toSet()));
		comboBox.setRenderer(new PluginIconListRenderer());
		list.setCellRenderer(new PluginIconListRenderer());
		table.packAll();
	
		
		btnSave.addActionListener(al->{
			
			try {
				AccountsManager.inst().getKey();
				AccountsManager.inst().saveConfig();
			} catch (IOException e) {
				MTGControler.getInstance().notify(e);
			}
		});
		
		
		btnKeyGenerator.addActionListener(al->{
			
			String key = JOptionPane.showInputDialog("Define a Key Pass :");
			String key2 = JOptionPane.showInputDialog("Confirm Key Pass :");
			
			if(!key.equals(key2))
			{
				MTGControler.getInstance().notify(new MTGNotification("KeyPass", "KeyPass are different",MESSAGE_TYPE.ERROR));
			}
			else
			{
				try {
					AccountsManager.inst().setKey(key);
				} catch (Exception e) {
					MTGControler.getInstance().notify(e);
				}
			}
			
		});
		
		btnExportConfig.addActionListener(al->{
			
			try {
				AccountsManager.inst().getKey();
			} catch (IOException e1) {
				MTGControler.getInstance().notify(e1);
				return;
			}
			
			JFileChooser f = new JFileChooser();
			 			 f.showSaveDialog(this);
					 
			if(f.getSelectedFile()!=null)
			{
				try {
					FileTools.saveFile(f.getSelectedFile(), AccountsManager.inst().exportConfig());
					MTGControler.getInstance().notify(new MTGNotification("Export", "File saved at " + f.getSelectedFile().getAbsolutePath(),MESSAGE_TYPE.INFO));
				} catch (IOException e) {
					MTGControler.getInstance().notify(e);
				}
			}
		});
		
		btnImportConfig.addActionListener(al->{
			try {
				AccountsManager.inst().getKey();
			} catch (IOException e1) {
				MTGControler.getInstance().notify(e1);
				return;
			}
			
			JFileChooser f = new JFileChooser();
						 f.showOpenDialog(this);
			
						 
						 
			if(f.getSelectedFile()!=null)
			{
				try {
					
					AccountsManager.inst().loadConfig(FileTools.readFile(f.getSelectedFile()));
					AccountsManager.inst().saveConfig();
					
					listModel.removeAllElements();
					listModel.addAll(AccountsManager.inst().listAuthEntries().keySet());
					
					
				} catch (Exception e) {
					MTGControler.getInstance().notify(e);
				
				} 
			}
			
			
		});
		
		
		btnDeleteConfig.addActionListener(al->{
			
			AccountsManager.inst().removeEntry(list.getSelectedValue());
			listModel.removeElementAt(list.getSelectedIndex());
			list.updateUI();
		});
		
		
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
				
				btnDeleteConfig.setEnabled(idx>-1);
				
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
