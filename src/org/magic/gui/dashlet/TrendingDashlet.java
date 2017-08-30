package org.magic.gui.dashlet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;

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
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class TrendingDashlet extends AbstractJDashlet{
	private JXTable table;
	private CardsShakerTableModel modStandard;
	private JComboBox<FORMAT> cboFormats;
	private JLabel lblLoading;
	private JPanel panel;
	private JLabel lblInfoUpdate;
	private JButton btnRefresh;
	

	
	public TrendingDashlet() {
		super();
		
		setTitle(getName());
		setResizable(true);
		setClosable(true);
		setIconifiable(true);
		setMaximizable(true);
		
		
	}
	
	public void initGUI() {
		
		
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
		
		btnRefresh = new JButton("");
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				init();
			}
		});
		btnRefresh.setIcon(new ImageIcon(TrendingDashlet.class.getResource("/res/refresh.png")));
		panneauHaut.add(btnRefresh);
		panneauHaut.add(lblLoading);
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		modStandard = new CardsShakerTableModel();
		table = new JXTable();
		
		scrollPane.setViewportView(table);
		
		if(props.size()>0) {
			Rectangle r = new Rectangle((int)Double.parseDouble(props.getProperty("x")), 
										(int)Double.parseDouble(props.getProperty("y")),
										(int)Double.parseDouble(props.getProperty("w")),
										(int)Double.parseDouble(props.getProperty("h")));
			
			try {
				cboFormats.setSelectedItem(FORMAT.valueOf(props.getProperty("FORMAT").toString()));
			
			} catch (Exception e) {
				e.printStackTrace();
			}
			setBounds(r);
			}
		
		new TableFilterHeader(table, AutoChoices.ENABLED);

		initToolTip(table);
		
		panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);
		
		lblInfoUpdate = new JLabel("");
		panel.add(lblInfoUpdate);
	
		
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
				props.put("FORMAT",((FORMAT)cboFormats.getSelectedItem()).toString());
				modStandard.fireTableDataChanged();
				lblLoading.setVisible(false);
				table.getColumnModel().getColumn(3).setCellRenderer(new CardShakeRenderer());
			//	table.packAll();
				try{
					lblInfoUpdate.setText(MTGControler.getInstance().getEnabledDashBoard().getName() + "(updated : " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(MTGControler.getInstance().getEnabledDashBoard().getUpdatedDate())+")");	
				}catch(Exception e)
				{
					
				}
				
			}
		}, "Init Formats Dashlet");
		
		
		
	}
	
	
	

	@Override
	public String getName() {
		return "Trendings";
	}




}
