package org.magic.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicFormat;
import org.magic.gui.models.CardStockTableModel;
import org.magic.gui.renderer.MagicCardListRenderer;
import org.magic.services.MTGControler;
import org.magic.services.ThreadManager;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import java.awt.event.ActionListener;
import java.net.URLEncoder;
import java.awt.event.ActionEvent;

public class StockPanelGUI extends JPanel {
	private JTable table;
	private CardStockTableModel model;
	private JTextField txtSearch;
	private DefaultListModel<MagicCard> resultListModel = new DefaultListModel<MagicCard>();
	private JList<MagicCard> listResult ;
	private JComboBox<String> cboAttributs ;
	private JButton btnSearch;
	
	
	public StockPanelGUI() {
		setLayout(new BorderLayout(0, 0));
		
		model = new CardStockTableModel();
		
		JPanel leftPanel = new JPanel();
		add(leftPanel, BorderLayout.WEST);
		leftPanel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollList = new JScrollPane();
		leftPanel.add(scrollList, BorderLayout.CENTER);
		
		listResult = new JList<MagicCard>(resultListModel);
		listResult.setCellRenderer(new MagicCardListRenderer());
		
		scrollList.setViewportView(listResult);
		
		JPanel searchPanel = new JPanel();
		leftPanel.add(searchPanel, BorderLayout.NORTH);
		
		
		String[] q = MTGControler.getInstance().getEnabledProviders().getQueryableAttributs();
	
		cboAttributs = new JComboBox<String>(new DefaultComboBoxModel<String>(q));
		
		
		
		searchPanel.add(cboAttributs);
		
		txtSearch = new JTextField();
		txtSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnSearch.doClick();
			}
		});
		searchPanel.add(txtSearch);
		txtSearch.setColumns(10);
		
		btnSearch = new JButton("");
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				if (txtSearch.getText().equals(""))
					return;

				resultListModel.removeAllElements();

				ThreadManager.getInstance().execute(new Runnable() {
					public void run() {
						try {
							String searchName = URLEncoder.encode(txtSearch.getText(), "UTF-8");
							List<MagicCard> cards = MTGControler.getInstance().getEnabledProviders().searchCardByCriteria(cboAttributs.getSelectedItem().toString(), searchName, null);
							for (MagicCard m : cards) 
									resultListModel.addElement(m);
							
							listResult.updateUI();
						} catch (Exception e) {
							e.printStackTrace();
							JOptionPane.showMessageDialog(null, e.getMessage(), "ERREUR", JOptionPane.ERROR_MESSAGE);
						}
					}
				}, "DeckSearchCards");
			}
		});
		btnSearch.setIcon(new ImageIcon(StockPanelGUI.class.getResource("/res/search.gif")));
		searchPanel.add(btnSearch);
		
		JPanel centerPanel = new JPanel();
		add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollTable = new JScrollPane();
		centerPanel.add(scrollTable);
		
		table = new JTable(model);
		scrollTable.setViewportView(table);
		
		JPanel actionPanel = new JPanel();
		centerPanel.add(actionPanel, BorderLayout.NORTH);
		
		JButton btnAdd = new JButton("");
		btnAdd.setIcon(new ImageIcon(StockPanelGUI.class.getResource("/res/new.png")));
		actionPanel.add(btnAdd);
		
		JButton btnSave = new JButton("");
		btnSave.setIcon(new ImageIcon(StockPanelGUI.class.getResource("/res/save.png")));
		actionPanel.add(btnSave);
		
		JButton btnDelete = new JButton("");
		btnDelete.setIcon(new ImageIcon(StockPanelGUI.class.getResource("/res/delete.png")));
		actionPanel.add(btnDelete);
	}

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		MTGControler.getInstance().getEnabledProviders().init();
		MTGControler.getInstance().getEnabledDAO().init();
		f.getContentPane().add(new StockPanelGUI());
		f.pack();
		f.setVisible(true);

	}

	
	
	
	
}
