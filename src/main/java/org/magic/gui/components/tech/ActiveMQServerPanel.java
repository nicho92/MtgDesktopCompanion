package org.magic.gui.components.tech;

import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.magic.api.beans.game.Player;
import org.magic.api.interfaces.abstracts.AbstractTechnicalServiceManager;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.models.conf.ActiveMQMessageTableModel;
import org.magic.gui.renderer.PlayerRenderer;
import org.magic.servers.impl.ActiveMQServer;
import org.magic.services.MTGConstants;
import org.magic.services.tools.UITools;

public class ActiveMQServerPanel extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private JTable table;
	private JList<Player> listPlayers; 
	private ActiveMQMessageTableModel model;
	private DefaultListModel<Player> listModel;

	public ActiveMQServerPanel() {
		setLayout(new BorderLayout(0, 0));
		model = new ActiveMQMessageTableModel();
		listModel = new DefaultListModel<>();
		listPlayers = new JList<>(listModel);
		table = UITools.createNewTable(model,true);
		listPlayers.setCellRenderer(new PlayerRenderer());
		
		add(new JScrollPane(table),BorderLayout.CENTER);
		add(new JScrollPane(listPlayers),BorderLayout.WEST);

		model.bind(AbstractTechnicalServiceManager.inst().getJsonMessages());
		
		
	}

	public ActiveMQMessageTableModel getModel() {
		return model;
	}
	
	public JTable getTable() {
		return table;
	}
	
	
	
	@Override
	public String getTitle() {
		return "ActiveMQ";
	}

	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_TAB_SERVER;
	}

	public void init(ActiveMQServer serv) {
		listModel.removeAllElements();
		listModel.addAll(serv.getPlug().getOnlines().values());
		
		model.fireTableDataChanged();
	}


}

