package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.api.mkm.exceptions.MkmException;
import org.api.mkm.exceptions.MkmNetworkException;
import org.api.mkm.modele.Wantslist;
import org.api.mkm.services.WantsService;
import org.api.mkm.tools.MkmAPIConfig;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.pricers.impl.MagicCardMarketPricer2;
import org.magic.gui.components.LazyLoadingTree;
import org.magic.services.MTGControler;
import org.mkm.gui.modeles.WantListTableModel;

public class MkmGUI extends JFrame{
	private JTable table;
	private WantsService serv;
	private WantListTableModel model;
	
	private MagicEdition selectedEdition;
	
	public MkmGUI() throws IOException, MkmException, MkmNetworkException, ClassNotFoundException {
		
		
		serv = new WantsService();
		model= new WantListTableModel();
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.WEST);
		
		LazyLoadingTree tree;
		try {
			tree = new LazyLoadingTree();
			tree.addTreeSelectionListener(new TreeSelectionListener() {
				public void valueChanged(TreeSelectionEvent tse) {
					TreePath path = tse.getPath();
					final DefaultMutableTreeNode curr = (DefaultMutableTreeNode) path.getLastPathComponent();
					if(curr.getUserObject() instanceof MagicCollection)
					{
						
						
					}
					
					if(curr.getUserObject() instanceof MagicEdition)
					{
						selectedEdition = (MagicEdition) curr.getUserObject();
						List<MagicCard> list;
						try {
							list = MTGControler.getInstance().getEnabledDAO().getCardsFromCollection(new MagicCollection(path.getPath()[1].toString()), selectedEdition);
							
							
							System.out.println(list);
							
							
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					
				}
			});
			scrollPane.setViewportView(tree);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		
		JComboBox<Wantslist> comboBox = new JComboBox<Wantslist>(new DefaultComboBoxModel(serv.getWantList().toArray()));
		comboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent event) {
				 if (event.getStateChange() == ItemEvent.SELECTED) {
			          Wantslist item = (Wantslist)event.getItem();
			          try {
						serv.loadItems(item);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (MkmException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (MkmNetworkException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			          model.init(item);
			       }
				
			}
		});
		
		
		
		panel.add(comboBox, BorderLayout.NORTH);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		panel.add(scrollPane_1, BorderLayout.CENTER);
		
		table = new JTable(model);
		scrollPane_1.setViewportView(table);
		// TODO Auto-generated constructor stub
	}
	
	public static void main(String[] args) throws Exception {
		MTGControler.getInstance().getEnabledProviders().init();
		MTGControler.getInstance().getEnabledDAO().init();
	
		MagicCardMarketPricer2 pricer = new MagicCardMarketPricer2();
		
		MkmAPIConfig.getInstance().init(pricer.getProperty("APP_ACCESS_TOKEN_SECRET").toString(),
				pricer.getProperty("APP_ACCESS_TOKEN").toString(),
				pricer.getProperty("APP_SECRET").toString(),
				pricer.getProperty("APP_TOKEN").toString());
		
		MkmGUI gui = new MkmGUI();
		gui.pack();
		gui.setVisible(true);
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
}
