package org.magic.gui.dashlet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.table.TableRowSorter;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.abstracts.AbstractDashBoard.FORMAT;
import org.magic.gui.abstracts.AbstractJDashlet;
import org.magic.gui.components.MagicCardDetailPanel;
import org.magic.gui.models.CardsShakerTableModel;
import org.magic.gui.renderer.CardShakeRenderer;
import org.magic.services.MTGControler;
import org.magic.services.ThreadManager;

import net.coderazzi.filters.gui.AutoChoices;
import net.coderazzi.filters.gui.TableFilterHeader;

public class TrendingDashlet extends AbstractJDashlet{
	private JXTable table;
	private CardsShakerTableModel modStandard;
	private JComboBox<FORMAT> cboFormats;
	private JLabel lblLoading;
	
	static final Logger logger = LogManager.getLogger(TrendingDashlet.class.getName());

	
	public TrendingDashlet() {
		
		setSize(new Dimension(536, 346));
		setTitle(getName());
		setResizable(true);
		setClosable(true);
		setIconifiable(true);
		setMaximizable(true);
		
		JPanel panneauHaut = new JPanel();
		getContentPane().add(panneauHaut, BorderLayout.NORTH);
		
		cboFormats = new JComboBox<FORMAT>(new DefaultComboBoxModel<FORMAT>(FORMAT.values()));
		cboFormats.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				init();
			}
		});
		panneauHaut.add(cboFormats);
		
		lblLoading = new JLabel("");
		lblLoading.setIcon(new ImageIcon(TrendingDashlet.class.getResource("/res/load.gif")));
		lblLoading.setVisible(false);
		panneauHaut.add(lblLoading);
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		modStandard = new CardsShakerTableModel();
		table = new JXTable();
		
		new TableFilterHeader(table, AutoChoices.ENABLED);
		
		initToolTip(table);
		
		scrollPane.setViewportView(table);
		setVisible(true);
		
	}

	public void init()
	{
		ThreadManager.getInstance().execute(new Runnable() {
			
			@Override
			public void run() {
				lblLoading.setVisible(true);
				modStandard.init((FORMAT)cboFormats.getSelectedItem());
				table.setModel(modStandard);
				table.setRowSorter(new TableRowSorter(modStandard) );
				table.packAll();
				table.getColumnModel().getColumn(3).setCellRenderer(new CardShakeRenderer());
				
				modStandard.fireTableDataChanged();
				lblLoading.setVisible(false);
			}
		}, "Init Formats Dashlet");
		
		
		
	}
	
	
	private void initToolTip(final JTable table)
	{
		final MagicCardDetailPanel pane = new MagicCardDetailPanel();
				pane.enableThumbnail(true);
				//pane.setPreferredSize(new Dimension(880, 350));
				
		final JPopupMenu popUp = new JPopupMenu("Customized Tool Tip");

		table.addMouseListener(new MouseAdapter() {
		    
			public void mouseClicked(MouseEvent e) {
				int row = table.rowAtPoint(e.getPoint());
				
				table.setRowSelectionInterval(row, row);
				String cardName = table.getValueAt(row, 0).toString();
				
				String edID = table.getValueAt(row, 1).toString();
				
				MagicEdition ed = new MagicEdition();
				ed.setId(edID);
				try 
				{
					MagicCard mc =  MTGControler.getInstance().getEnabledProviders().searchCardByCriteria("name", cardName,ed).get(0);
					pane.setMagicCard(mc);
					pane.setMagicLogo(edID, mc.getEditions().get(0).getRarity());
						popUp.setBorder(new LineBorder(Color.black));
					    popUp.setVisible(false);
					    popUp.removeAll();
					    popUp.setLayout(new BorderLayout());
					    popUp.add(pane,BorderLayout.CENTER);
					    popUp.show(table, e.getX(), e.getY());// + bounds.height);
					    popUp.setVisible(true);
				}
				catch (Exception ex) 
				{
					logger.error(cardName +" " + edID,ex);
				}
		   }
		});
	}

	@Override
	public String getName() {
		return "Trendings Dashboard";
	}

	@Override
	public void save(String k, Object value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isStartup() {
		// TODO Auto-generated method stub
		return false;
	}


}
