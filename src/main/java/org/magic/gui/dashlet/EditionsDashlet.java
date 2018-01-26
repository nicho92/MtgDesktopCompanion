package org.magic.gui.dashlet;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.TableRowSorter;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MagicEdition;
import org.magic.gui.abstracts.AbstractJDashlet;
import org.magic.gui.models.EditionsShakerTableModel;
import org.magic.gui.renderer.CardShakeRenderer;
import org.magic.gui.renderer.MagicEditionListRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.ThreadManager;

import net.coderazzi.filters.gui.AutoChoices;
import net.coderazzi.filters.gui.TableFilterHeader;

public class EditionsDashlet extends AbstractJDashlet {
	
	private JXTable table;
	private JLabel lblLoading;
	private JComboBox<MagicEdition> cboEditions;
	private EditionsShakerTableModel modEdition;
	
	
	public EditionsDashlet() {
		super();
		setFrameIcon(MTGConstants.ICON_COLLECTION);
	}

	public void initGUI() {
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);
		
		modEdition=new EditionsShakerTableModel();
		
		List<MagicEdition> eds= new ArrayList<>();
		
		try {
			eds.addAll(MTGControler.getInstance().getEnabledProviders().loadEditions());
			Collections.sort(eds);
			eds.add(0,null);
		} catch (Exception e) {
			MTGLogger.printStackTrace(e);
		}
		
		cboEditions = new JComboBox(new DefaultComboBoxModel<MagicEdition>(eds.toArray(new MagicEdition[eds.size()])));
		cboEditions.setRenderer(new MagicEditionListRenderer());
		
		panel.add(cboEditions);
		
		lblLoading = new JLabel("");
		lblLoading.setIcon(MTGConstants.ICON_LOADING);
		lblLoading.setVisible(false);
		panel.add(lblLoading);
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		table = new JXTable(modEdition);
		initToolTip(table,0,1);
		
		table.getColumnModel().getColumn(3).setCellRenderer(new CardShakeRenderer());
		table.getColumnModel().getColumn(5).setCellRenderer(new CardShakeRenderer());
	
		scrollPane.setViewportView(table);
		setVisible(true);
		
		cboEditions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				init();
			}
		});
	
		if(props.size()>0) {
		Rectangle r = new Rectangle((int)Double.parseDouble(props.getProperty("x")), 
									(int)Double.parseDouble(props.getProperty("y")),
									(int)Double.parseDouble(props.getProperty("w")),
									(int)Double.parseDouble(props.getProperty("h")));
		
		MagicEdition ed;
		try {
			ed = MTGControler.getInstance().getEnabledProviders().getSetById(getProperty("EDITION"));
			cboEditions.setSelectedItem(ed);
		} catch (Exception e) {
			logger.error("Error retrieve editions",e);
		}
		
		setBounds(r);
		}
		
		try {
			table.packAll();
		}
		catch(Exception e) 
		{}
		
		new TableFilterHeader(table, AutoChoices.ENABLED);
		
	}
	
	

	@Override
	public String getName() {
		return "Editions Prices";
	}
	
	
	@Override
	public void init() {
		
	
		if(cboEditions.getSelectedItem()!=null)
			ThreadManager.getInstance().execute(new Runnable() {
				@Override
				public void run() {
					lblLoading.setVisible(true);
					MagicEdition ed = (MagicEdition)cboEditions.getSelectedItem();
					modEdition.init(ed);
					try{
						modEdition.fireTableDataChanged();
					}catch(Exception e) 
					{}
					try{
					table.packAll();
					}catch(Exception e) 
					{}
					try{
					table.setRowSorter(new TableRowSorter(modEdition) );
					}catch(Exception e) 
					{}
					save("EDITION",ed.getId());
					lblLoading.setVisible(false);
				}
			}, "init EditionDashLet");
		
	}

}
