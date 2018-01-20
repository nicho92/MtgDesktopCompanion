package org.magic.gui.dashlet;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableRowSorter;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.CardShake;
import org.magic.api.interfaces.abstracts.AbstractDashBoard.FORMAT;
import org.magic.gui.abstracts.AbstractJDashlet;
import org.magic.gui.models.CardsShakerTableModel;
import org.magic.gui.renderer.CardShakeRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.ThreadManager;

public class BestTrendingDashlet extends AbstractJDashlet{

	private JXTable table;
	private CardsShakerTableModel modStandard;
	private JSpinner spinner;
	
	private JCheckBox boxS;
	private JCheckBox boxM;
	private JCheckBox boxV;
	private JCheckBox boxL;
	
	
	public BestTrendingDashlet() {
		super();
		setFrameIcon(MTGConstants.ICON_UP);
	}
	
	@Override
	public String getName() {
		return "Winners/Loosers";
	}

	

	@Override
	public void init() {
		ThreadManager.getInstance().execute(new Runnable() {
			
			@Override
			public void run() {
				
				
				try {
					List<CardShake> shakes = new ArrayList<CardShake>();
							
					if(boxM.isSelected())
						shakes.addAll(MTGControler.getInstance().getEnabledDashBoard().getShakerFor(FORMAT.modern.toString()));
					if(boxS.isSelected())	
						shakes.addAll(MTGControler.getInstance().getEnabledDashBoard().getShakerFor(FORMAT.standard.toString()));
					if(boxL.isSelected())		
						shakes.addAll(MTGControler.getInstance().getEnabledDashBoard().getShakerFor(FORMAT.legacy.toString()));
					if(boxV.isSelected())		
						shakes.addAll(MTGControler.getInstance().getEnabledDashBoard().getShakerFor(FORMAT.vintage.toString()));
					
					
					Collections.sort(shakes,new Comparator<CardShake>() {
						public int compare(CardShake o1, CardShake o2) {
							if(o1.getPriceDayChange()>o2.getPriceDayChange())
									return -1;
							
							if(o1.getPriceDayChange()<o2.getPriceDayChange())
									return 1;
							
							return 0;
							
						}
					});
					
					int val = (Integer)spinner.getValue(); 
					save("LIMIT", String.valueOf(val));
					save("STD",String.valueOf(boxS.isSelected()));
					save("MDN",String.valueOf(boxM.isSelected()));
					save("LEG",String.valueOf(boxL.isSelected()));
					save("VIN",String.valueOf(boxV.isSelected()));
					
					
					List<CardShake> ret = new ArrayList<CardShake>();
					ret.addAll(shakes.subList(0, val));//X first
					ret.addAll(shakes.subList(shakes.size()-(val+1), shakes.size())); //x last
					
					modStandard.init(ret);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				table.setModel(modStandard);
				table.setRowSorter(new TableRowSorter(modStandard) );
				table.packAll();
				table.getColumnModel().getColumn(3).setCellRenderer(new CardShakeRenderer());
				modStandard.fireTableDataChanged();
				
				
			}
		}, "Init best Dashlet");
		
	}

	@Override
	public void initGUI() {
		JPanel panneauHaut = new JPanel();
		getContentPane().add(panneauHaut, BorderLayout.NORTH);

		Action a = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				init();
				
			}
		};

		
		boxS= new JCheckBox();
		boxS.setAction(a);
		boxS.setText("STD");
		boxM= new JCheckBox();
		boxM.setAction(a);
		boxM.setText("MDN");
		boxL= new JCheckBox();
		boxL.setAction(a);
		boxL.setText("LEG");
		boxV= new JCheckBox("V");
		boxV.setAction(a);
		boxV.setText("VIN");
		
		spinner = new JSpinner();
		spinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				init();
			}
		});
		
		
		
		
		spinner.setModel(new SpinnerNumberModel(5, 1, null, 1));
		panneauHaut.add(spinner);
		panneauHaut.add(boxS);
		panneauHaut.add(boxM);
		panneauHaut.add(boxL);
		panneauHaut.add(boxV);
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		modStandard = new CardsShakerTableModel();
		table = new JXTable(modStandard);
		scrollPane.setViewportView(table);
		initToolTip(table,0,1);
		
		if(props.size()>0) {
			Rectangle r = new Rectangle((int)Double.parseDouble(props.getProperty("x")), 
										(int)Double.parseDouble(props.getProperty("y")),
										(int)Double.parseDouble(props.getProperty("w")),
										(int)Double.parseDouble(props.getProperty("h")));
			
			try {
				spinner.setValue(Integer.parseInt(props.getProperty("LIMIT","5")));
			} catch (Exception e) {
				logger.error("can't get LIMIT value",e);
			}
			
			try {
				boxS.setSelected(Boolean.parseBoolean(props.getProperty("STD","false")));
				boxM.setSelected(Boolean.parseBoolean(props.getProperty("MDN","true")));
				boxL.setSelected(Boolean.parseBoolean(props.getProperty("LEG","false")));
				boxV.setSelected(Boolean.parseBoolean(props.getProperty("VIN","false")));
			} catch (Exception e) {
				logger.error("can't get LIMIT value",e);
			}
			
			
			
			setBounds(r);
			}
		
		setVisible(true);
	}


}
