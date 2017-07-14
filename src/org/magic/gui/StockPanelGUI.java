package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.EnumCondition;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.gui.components.MagicCardDetailPanel;
import org.magic.gui.models.CardStockTableModel;
import org.magic.gui.renderer.MagicCardListRenderer;
import org.magic.gui.renderer.MagicDeckQtyEditor;
import org.magic.gui.renderer.MagicStockEditor;
import org.magic.gui.renderer.StockTableRenderer;
import org.magic.services.MTGControler;
import org.magic.services.ThreadManager;

import net.coderazzi.filters.gui.AutoChoices;
import net.coderazzi.filters.gui.TableFilterHeader;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

public class StockPanelGUI extends JPanel {
	private JXTable table;
	private CardStockTableModel model;
	private JTextField txtSearch;
	private DefaultListModel<MagicCard> resultListModel = new DefaultListModel<MagicCard>();
	private JList<MagicCard> listResult ;
	private JComboBox<String> cboAttributs ;
	private JButton btnSearch;

	private JButton btnAdd = new JButton();
	private JButton btnDelete = new JButton();
	private JButton btnSave = new JButton();
	
	private MagicCardDetailPanel magicCardDetailPanel;
	private JSplitPane splitPane;
    private TableFilterHeader filterHeader;

	private MagicCardStock selectedStock;
	private List<MagicCard> selectedCard;
	private JButton btnReload;
    
	static final Logger logger = LogManager.getLogger(StockPanelGUI.class.getName());

	
	public StockPanelGUI() {
		logger.info("init StockManagment GUI");
		
		initGUI();
		
		listResult.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				selectedCard=listResult.getSelectedValuesList();
				
				if(selectedCard!=null)
				{
					btnAdd.setEnabled(true);
					magicCardDetailPanel.setMagicCard(selectedCard.get(0));
				}
				
			}
		});
		
		
		txtSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnSearch.doClick();
			}
		});
		
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
							JOptionPane.showMessageDialog(null, e.getMessage(), "ERREUR", JOptionPane.ERROR_MESSAGE);
						}
					}
				}, "DeckSearchCards");
			}
		});
		
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for(MagicCardStock ms : model.getList())
					if(ms.isUpdate())
						try {
							MTGControler.getInstance().getEnabledDAO().saveOrUpdateStock(ms);
							ms.setUpdate(false);
						} catch (SQLException e1) {
							JOptionPane.showMessageDialog(null, e1.getMessage(),"ERROR ON : " + String.valueOf(ms),JOptionPane.ERROR_MESSAGE);
						}
				
				model.fireTableDataChanged();
			}
			
		});
		
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				for(MagicCard mc : selectedCard)
				{
					MagicCardStock ms = new MagicCardStock();
					ms.setIdstock(-1);
					ms.setUpdate(true);
					ms.setMagicCard(mc);
					model.add(ms);
				}
			}
		});
		
		
		table.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				int viewRow = table.getSelectedRow();
				int modelRow = table.convertRowIndexToModel(viewRow);
				selectedStock = (MagicCardStock)table.getModel().getValueAt(modelRow, 0);
				btnDelete.setEnabled(true);
				magicCardDetailPanel.setMagicCard(selectedStock.getMagicCard());
				
			}
		});
		
		
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(selectedStock==null)
				{
					JOptionPane.showMessageDialog(null, "Choose a stock line before","ERROR",JOptionPane.ERROR_MESSAGE);
				}
				else
				{	
					try {
						int res = JOptionPane.showConfirmDialog(null, "Delete " + selectedStock + " ?","Confirm delete",JOptionPane.YES_NO_OPTION);
						if(res==JOptionPane.YES_OPTION)
						{
							model.remove(selectedStock);
							if(selectedStock.getIdstock()>-1)
								MTGControler.getInstance().getEnabledDAO().deleteStock(selectedStock);
						}
					} catch (SQLException e1) {
						JOptionPane.showMessageDialog(null, e1.getMessage(),"ERROR",JOptionPane.ERROR_MESSAGE);
					}
				}
				
			}
		});
		
		btnReload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int res = JOptionPane.showConfirmDialog(null, "Cancel all changes ?","Confirm Undo",JOptionPane.YES_NO_OPTION);
				if(res==JOptionPane.YES_OPTION)
				{
					model.init();
				}
			}
		});

		
	}
	
	private void initGUI()
	{
		setLayout(new BorderLayout(0, 0));
		txtSearch = new JTextField();
		JPanel leftPanel = new JPanel();
		JScrollPane scrollList = new JScrollPane();
		JPanel searchPanel = new JPanel();
		
		model = new CardStockTableModel();
		
		listResult = new JList<MagicCard>(resultListModel);
		
		
		add(leftPanel, BorderLayout.WEST);
		leftPanel.setLayout(new BorderLayout(0, 0));
		leftPanel.add(scrollList, BorderLayout.CENTER);
		
	
		listResult.setCellRenderer(new MagicCardListRenderer());
		
		scrollList.setViewportView(listResult);
		
		leftPanel.add(searchPanel, BorderLayout.NORTH);
		
		String[] q = MTGControler.getInstance().getEnabledProviders().getQueryableAttributs();
		cboAttributs = new JComboBox<String>(new DefaultComboBoxModel<String>(q));
		searchPanel.add(cboAttributs);
	
		searchPanel.add(txtSearch);
		txtSearch.setColumns(10);
		
		btnSearch = new JButton("");
		
		btnSearch.setIcon(new ImageIcon(StockPanelGUI.class.getResource("/res/search.gif")));
		searchPanel.add(btnSearch);
		
		JPanel centerPanel = new JPanel();
		add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new BorderLayout(0, 0));
		JPanel actionPanel = new JPanel();
		centerPanel.add(actionPanel, BorderLayout.NORTH);
				btnAdd.setEnabled(false);
		
				btnAdd.setIcon(new ImageIcon(StockPanelGUI.class.getResource("/res/new.png")));
				actionPanel.add(btnAdd);
				btnDelete.setEnabled(false);
				
	
				btnDelete.setIcon(new ImageIcon(StockPanelGUI.class.getResource("/res/delete.png")));
				actionPanel.add(btnDelete);
				btnSave.setToolTipText("Batch Save");
				
				
				btnSave.setIcon(new ImageIcon(StockPanelGUI.class.getResource("/res/save.png")));
				actionPanel.add(btnSave);
				
				btnReload = new JButton("");
				
				btnReload.setIcon(new ImageIcon(StockPanelGUI.class.getResource("/res/refresh.png")));
				actionPanel.add(btnReload);
				
		JScrollPane scrollTable = new JScrollPane();
		
		table = new JXTable(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		StockTableRenderer render = new StockTableRenderer();
		
		table.setDefaultRenderer(Object.class,render);
		table.setDefaultEditor(EnumCondition.class, new MagicStockEditor());
		table.setDefaultEditor(Integer.class, new MagicDeckQtyEditor());
		
		table.packAll();
		filterHeader = new TableFilterHeader(table, AutoChoices.ENABLED);
		scrollTable.setViewportView(table);
		
		magicCardDetailPanel = new MagicCardDetailPanel();
		magicCardDetailPanel.enableThumbnail(true);
		
		splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		centerPanel.add(splitPane, BorderLayout.CENTER);
		splitPane.setLeftComponent(scrollTable);
		splitPane.setRightComponent(magicCardDetailPanel);
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
