package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCardAlert;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MTGServer;
import org.magic.gui.components.MagicCardDetailPanel;
import org.magic.gui.components.charts.HistoryPricesPanel;
import org.magic.gui.components.renderer.MagicPricePanel;
import org.magic.gui.models.CardAlertTableModel;
import org.magic.servers.impl.PricesCheckerTimer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.ThreadManager;

public class AlarmGUI extends JPanel {
	private JTable table;
	private CardAlertTableModel model;
	private MagicCardDetailPanel magicCardDetailPanel ;
	private DefaultListModel<MagicPrice> resultListModel;
	private JList<MagicPrice> list;
	private JSplitPane splitPanel;
	private JPanel panel;
	private JButton btnRefresh;
	private JButton btnDelete;
	private HistoryPricesPanel variationPanel;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	private JTabbedPane tabbedPane;

	
	public AlarmGUI() {
		
		logger.info("init Alarm GUI");
		setLayout(new BorderLayout());
		
		splitPanel = new JSplitPane();
		splitPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
		add(splitPanel, BorderLayout.CENTER);
		
		JScrollPane scrollTable = new JScrollPane();
		scrollTable.setPreferredSize(new Dimension(2, 200));
		splitPanel.setLeftComponent(scrollTable);
		table = new JTable();
		model = new CardAlertTableModel();
		table.setModel(model);
		
		
		table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,boolean hasFocus, int row, int column) {
				Component comp=super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				comp.setForeground(Color.BLACK);
				
				if((Integer)value>0)
					comp.setBackground(Color.GREEN);
				else
					comp.setBackground(table.getBackground());
				return comp;
			}
			
		});
		
		
		
		scrollTable.setViewportView(table);
		
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) 
			{
				resultListModel.removeAllElements();
				MagicCardAlert selected = (MagicCardAlert)table.getValueAt(table.getSelectedRow(), 0);
					magicCardDetailPanel.setMagicCard(selected.getCard());
					variationPanel.init(selected.getCard(), null, selected.getCard().getName());
				for(MagicPrice mp : selected.getOffers())
					resultListModel.addElement(mp);
			}
		});
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		splitPanel.setRightComponent(tabbedPane);
		
		magicCardDetailPanel = new MagicCardDetailPanel();
		variationPanel = new HistoryPricesPanel();
		
		
		tabbedPane.addTab("Card", null, magicCardDetailPanel, null);
		tabbedPane.addTab("Variations", null, variationPanel , null);
		
		magicCardDetailPanel.enableThumbnail(true);
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.EAST);
		
		resultListModel= new DefaultListModel<>();
			
		list = new JList<>(resultListModel);
		
		list.setCellRenderer((JList<? extends MagicPrice> obj, MagicPrice value, int index,boolean isSelected, boolean cellHasFocus)->{
				return new MagicPricePanel(value);
		});
		
		
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					
					if(e.getClickCount() == 2 && (list.getSelectedValue()!=null))
						{
							MagicPrice p = list.getSelectedValue();
							Desktop.getDesktop().browse(new URI(p.getUrl()));
						}
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1,MTGControler.getInstance().getLangService().getCapitalize("ERROR"),JOptionPane.ERROR_MESSAGE);
				}
				
			}
		});
		scrollPane.setViewportView(list);
		
		panel = new JPanel();
		add(panel, BorderLayout.NORTH);
		
		btnRefresh = new JButton();
		btnRefresh.addActionListener(e->{
				
				if(!MTGControler.getInstance().isRunning(new PricesCheckerTimer()))
				{
					int res = JOptionPane.showConfirmDialog(null, MTGControler.getInstance().getLangService().getCapitalize("PRICE_TIMER_LAUNCH"),MTGControler.getInstance().getLangService().getCapitalize("PRICE_TIMER_STOPPED"), JOptionPane.YES_NO_OPTION);
					
					if(res==JOptionPane.YES_OPTION)
						for(MTGServer serv : MTGControler.getInstance().getEnabledServers())
							if(serv.getName().equals(new PricesCheckerTimer().getName()))
								try {
									serv.start();
								} catch (Exception ex) {
									logger.error(ex);
								}
				}
				
				model.fireTableDataChanged();
		});
		btnRefresh.setIcon(MTGConstants.ICON_REFRESH);
		panel.add(btnRefresh);
		
		btnDelete = new JButton("");
		btnDelete.addActionListener(e->{
				int row =table.getSelectedRow();
				if(row>-1)
				{
					try {
						MagicCardAlert alert = (MagicCardAlert)model.getValueAt(row,0);
						MTGControler.getInstance().getEnabledDAO().deleteAlert(alert);
						model.fireTableDataChanged();
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(null, e1,MTGControler.getInstance().getLangService().getCapitalize("ERROR"),JOptionPane.ERROR_MESSAGE);
					}
				}
		});
		
		btnDelete.setIcon(MTGConstants.ICON_DELETE);
		panel.add(btnDelete);
		addComponentListener(new ComponentAdapter() {
			  @Override
		      public void componentShown(ComponentEvent componentEvent) {
		    	  splitPanel.setDividerLocation(.5);
		    	  model.fireTableDataChanged();
		    	  removeComponentListener(this);
		      }

		    });
		
	}

}
