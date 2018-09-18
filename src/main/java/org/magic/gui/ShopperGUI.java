package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.DefaultRowSorter;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import org.jdesktop.swingx.JXSearchField;
import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.ShopItem;
import org.magic.gui.abstracts.MTGUIPanel;
import org.magic.gui.models.ShopItemTableModel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.ThreadManager;
import org.magic.tools.UITools;

public class ShopperGUI extends MTGUIPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JXSearchField txtSearch;
	private JXTable tableItemShop;
	private JPanel panel = new JPanel();
	private JLabel lblSearch = new JLabel(MTGControler.getInstance().getLangService().get("SEARCH_MODULE") + " :");
	private JScrollPane shopItemScrollPane = new JScrollPane();
	private ShopItemTableModel mod;
	private final JPanel panneauCentral = new JPanel();
	private final JPanel panneauEast = new JPanel();
	private final JLabel lblPicShopItem = new JLabel("");

	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_SHOP;
	}
	
	@Override
	public String getTitle() {
		return MTGControler.getInstance().getLangService().getCapitalize("SHOPPING_MODULE");
	}
	
	
	
	public ShopperGUI() {
		setLayout(new BorderLayout(0, 0));

		add(panel, BorderLayout.NORTH);

		panel.add(lblSearch);

		txtSearch = new JXSearchField(MTGControler.getInstance().getLangService().getCapitalize("SEARCH_MODULE"));
		txtSearch.setBackground(Color.WHITE);
		txtSearch.setSearchMode(MTGConstants.SEARCH_MODE);
		txtSearch.setColumns(35);
		txtSearch.addActionListener(ae -> ThreadManager.getInstance().execute(() -> mod.init(txtSearch.getText()), "Search Shop Item"));

		
		panel.add(txtSearch);
	
		mod = new ShopItemTableModel();

		DefaultRowSorter sorter = new TableRowSorter<DefaultTableModel>(mod);

		add(panneauCentral, BorderLayout.CENTER);
		tableItemShop = new JXTable(mod);
		tableItemShop.setRowSorter(sorter);
		UITools.initTableFilter(tableItemShop);
		tableItemShop.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			SimpleDateFormat f = new SimpleDateFormat("dd/MM/yy HH:mm");

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				if (value instanceof Date) {
					value = f.format(value);
				}
				return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			}
		});

		tableItemShop.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent ev) {
				try {
					int modelrow = tableItemShop.convertRowIndexToModel(tableItemShop.getSelectedRow());
					ShopItem it = (ShopItem) tableItemShop.getModel().getValueAt(modelrow, 1);
					if (ev.getClickCount() == 2 && !ev.isConsumed()) {
						ev.consume();
						Desktop.getDesktop().browse(it.getUrl().toURI());
					} else {
						lblPicShopItem.setIcon(new ImageIcon(it.getImage()));
					}
				} catch (Exception e) {
					logger.error("error loading ", e);
				}

			}
		});
		panneauCentral.setLayout(new BorderLayout(0, 0));
		panneauCentral.add(shopItemScrollPane);
		shopItemScrollPane.setViewportView(tableItemShop);

		panneauCentral.add(panneauEast, BorderLayout.EAST);
		panneauEast.setLayout(new BorderLayout(0, 0));

		panneauEast.add(lblPicShopItem, BorderLayout.NORTH);

	}

}
