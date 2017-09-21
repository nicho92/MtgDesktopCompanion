package org.magic.gui.dashlet;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.table.TableRowSorter;

import org.jdesktop.swingx.JXTable;
import org.magic.api.interfaces.abstracts.AbstractDashBoard.FORMAT;
import org.magic.gui.abstracts.AbstractJDashlet;
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
	private JPanel panel;
	private JLabel lblInfoUpdate;
	private JButton btnRefresh;
	

	
	public TrendingDashlet() {
		super();
		setFrameIcon(new ImageIcon(TrendingDashlet.class.getResource("/res/dashboard.png")));
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
				lblLoading.setVisible(false);
				table.getColumnModel().getColumn(3).setCellRenderer(new CardShakeRenderer());
				modStandard.fireTableDataChanged();
				
				try {
					table.packAll();
				}
				catch(Exception e) 
				{}
				
				
				try{
					lblInfoUpdate.setText(MTGControler.getInstance().getEnabledDashBoard().getName() + "(updated : " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(MTGControler.getInstance().getEnabledDashBoard().getUpdatedDate())+")");	
				}catch(Exception e)
				{
					
				}
				
				List<SortKey> keys = new ArrayList<SortKey>();
				SortKey sortKey = new SortKey(3, SortOrder.DESCENDING);//column index 2
				keys.add(sortKey);
				
				((TableRowSorter)table.getRowSorter()).setSortKeys(keys);
				((TableRowSorter)table.getRowSorter()).sort();
				
			}
		}, "Init Formats Dashlet");
		
		
		
	}
	
	
	

	@Override
	public String getName() {
		return "Trendings";
	}




}
