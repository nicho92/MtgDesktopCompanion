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
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.ShopItem;
import org.magic.gui.models.ShopItemTableModel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.ThreadManager;

import net.coderazzi.filters.gui.AutoChoices;
import net.coderazzi.filters.gui.TableFilterHeader;

public class ShopperGUI extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField txtSearch;
	private JXTable tableItemShop;

	private JButton btnSearch = new JButton(MTGConstants.ICON_SEARCH);
	private JPanel panel = new JPanel();
	private JLabel lblSearch = new JLabel(MTGControler.getInstance().getLangService().get("SEARCH_MODULE") + " :");
	private JScrollPane shopItemScrollPane = new JScrollPane();
	private ShopItemTableModel mod;
	private final JPanel panneauCentral = new JPanel();
	private final JPanel panneauEast = new JPanel();
	private final JLabel lblPicShopItem = new JLabel("");
	private TableFilterHeader filterHeader;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());

	public ShopperGUI() {

		logger.info("init shopper GUI");
		setLayout(new BorderLayout(0, 0));

		add(panel, BorderLayout.NORTH);

		panel.add(lblSearch);

		txtSearch = new JTextField();
		panel.add(txtSearch);
		txtSearch.setColumns(35);
		txtSearch.addActionListener(e -> btnSearch.doClick());

		panel.add(btnSearch);
		mod = new ShopItemTableModel();

		DefaultRowSorter sorter = new TableRowSorter<DefaultTableModel>(mod);

		add(panneauCentral, BorderLayout.CENTER);
		tableItemShop = new JXTable(mod);
		tableItemShop.setRowSorter(sorter);
		filterHeader = new TableFilterHeader(tableItemShop, AutoChoices.ENABLED);

		filterHeader.setSelectionBackground(Color.LIGHT_GRAY);
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

		btnSearch.addActionListener(ae -> ThreadManager.getInstance().execute(() -> mod.init(txtSearch.getText()), "Search Shop Item"));

	}

}
