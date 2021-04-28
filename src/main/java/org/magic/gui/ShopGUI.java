package org.magic.gui;

import static org.magic.tools.MTG.getPlugin;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;

import org.magic.api.beans.Proposition;
import org.magic.api.interfaces.MTGServer;
import org.magic.gui.abstracts.GenericTableModel;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.ServerStatePanel;
import org.magic.services.MTGConstants;
import org.magic.tools.UITools;

public class ShopGUI extends MTGUIComponent {
	private JTable table;
	private GenericTableModel<Proposition> model;
	
	
	public ShopGUI() {
		setLayout(new BorderLayout(0, 0));
		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		ServerStatePanel serverConsolePanel = new ServerStatePanel(false,getPlugin("Shopping Server", MTGServer.class));
		JPanel panneauBas = new JPanel();
		JPanel panneauHaut = new JPanel();
		model = new GenericTableModel<>();
		JButton btnValidate = new JButton(MTGConstants.ICON_CHECK);
		JButton btnDecline = new JButton(MTGConstants.ICON_DELETE);
		table = UITools.createNewTable(model);
	
		model.setColumns("id","contact","dateProposition","items");
		panneauBas.setLayout(new BorderLayout());
		
		
		add(splitPane, BorderLayout.CENTER);
		splitPane.setLeftComponent(new JScrollPane(table));
		add(panneauBas, BorderLayout.SOUTH);
		add(panneauHaut, BorderLayout.NORTH);
		panneauHaut.add(btnValidate);
		panneauHaut.add(btnDecline);
		panneauBas.add(serverConsolePanel,BorderLayout.CENTER);
		
		for(int i = 0; i <10; i++)
			model.addItem(new Proposition(i));
		
		
	}

	@Override
	public String getTitle() {
		return "Shop";
	}

	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_SHOP;
	}
	
	public static void main(String[] args) {
		JFrame f = new JFrame();
			   f.getContentPane().add(new ShopGUI());
			   f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			   f.pack();
			   f.setVisible(true);
	}

}
