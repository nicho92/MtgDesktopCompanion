package org.magic.gui.dashlet;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableRowSorter;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.abstracts.AbstractDashBoard.FORMAT;
import org.magic.gui.abstracts.AbstractJDashlet;
import org.magic.gui.models.CardDominanceTableModel;
import org.magic.gui.models.CardsShakerTableModel;
import org.magic.gui.renderer.CardShakeRenderer;
import org.magic.services.MTGControler;
import org.magic.services.ThreadManager;
import javax.swing.JComboBox;
import javax.swing.JLabel;

public class BestCardsDashlet extends AbstractJDashlet{

	private JXTable table;
	private CardDominanceTableModel models;
	JComboBox<FORMAT> cboFormat;
	JComboBox<String> cboFilter ;
	private JLabel lblLoading;
	
	public BestCardsDashlet() {
		super();
		setFrameIcon(new ImageIcon(BestCardsDashlet.class.getResource("/res/up.png")));
		initGUI();
	}
	
	@Override
	public String getName() {
		return "Most Played cards";
	}

	@Override
	public void initGUI() {
		JPanel panneauHaut = new JPanel();
		getContentPane().add(panneauHaut, BorderLayout.NORTH);
		
		cboFormat = new JComboBox<FORMAT>();
		cboFormat.setModel(new DefaultComboBoxModel<>(FORMAT.values()));
		
		panneauHaut.add(cboFormat);
		
		cboFilter = new JComboBox<String>();
		cboFilter.setModel(new DefaultComboBoxModel<>(MTGControler.getInstance().getEnabledDashBoard().getDominanceFilters()));
		panneauHaut.add(cboFilter);
		
		lblLoading = new JLabel("");
		lblLoading.setIcon(new ImageIcon(BestCardsDashlet.class.getResource("/res/load.gif")));
		panneauHaut.add(lblLoading);
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		models = new CardDominanceTableModel();
		table = new JXTable(models);
		scrollPane.setViewportView(table);
		initToolTip(table,0,null);
		
		cboFormat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				init();
			}
		});
		
		cboFilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				init();
			}
		});
		
		if(props.size()>0) {
			Rectangle r = new Rectangle((int)Double.parseDouble(props.getProperty("x")), 
										(int)Double.parseDouble(props.getProperty("y")),
										(int)Double.parseDouble(props.getProperty("w")),
										(int)Double.parseDouble(props.getProperty("h")));
			
			try {
				cboFormat.setSelectedItem(props.getProperty("FORMAT","standard"));
				cboFilter.setSelectedItem(props.getProperty("FILTER","all"));
			} catch (Exception e) {
				logger.error("can't get value",e);
			}
			setBounds(r);
			}
		setVisible(true);
		
		
		
	}

	@Override
	public void init() {
			ThreadManager.getInstance().execute(new Runnable() {
				@Override
				public void run() {
					lblLoading.setVisible(true);
					models.init((FORMAT)cboFormat.getSelectedItem(),cboFilter.getSelectedItem().toString());
					models.fireTableDataChanged();
					table.packAll();
					table.setRowSorter(new TableRowSorter(models) );
					save("FORMAT",cboFormat.getSelectedItem().toString());
					save("FILTER",cboFilter.getSelectedItem().toString());
					lblLoading.setVisible(false);
				}
			}, "init BestCardsDashlet");
		
		//models.init(f, filter);
		
	}


}
