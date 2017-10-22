package org.magic.gui.dashlet;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.Booster;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicEdition;
import org.magic.gui.abstracts.AbstractJDashlet;
import org.magic.gui.models.BoostersTableModel;
import org.magic.gui.models.CardsShakerTableModel;
import org.magic.gui.renderer.MagicCardListRenderer;
import org.magic.gui.renderer.MagicEditionListRenderer;
import org.magic.services.MTGControler;
import org.magic.services.ThreadManager;
import javax.swing.JList;

public class BoosterBoxDashlet extends AbstractJDashlet{
	private JSpinner boxSizeSpinner;
	private JComboBox<MagicEdition> cboEditions;
	private JXTable table;
	private BoostersTableModel boostersModel;
	private DefaultListModel<MagicCard> cardsModel;
	private JLabel lblTotal;
	
	public BoosterBoxDashlet() {
		super();
		setFrameIcon(new ImageIcon(BoosterBoxDashlet.class.getResource("/res/up.png")));
		initGUI();
	}
	
	@Override
	public String getName() {
		return "Booster Box";
	}

	

	@Override
	public void init() {
	
		
	}

	@Override
	public void initGUI() {
		JPanel panneauHaut = new JPanel();
		getContentPane().add(panneauHaut, BorderLayout.NORTH);
	
		List<MagicEdition> eds= new ArrayList<>();
		try {
			eds.addAll(MTGControler.getInstance().getEnabledProviders().loadEditions());
			Collections.sort(eds);
			eds.add(0,null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		cboEditions = new JComboBox(new DefaultComboBoxModel<MagicEdition>(eds.toArray(new MagicEdition[eds.size()])));
		cboEditions.setRenderer(new MagicEditionListRenderer());
		
		
		panneauHaut.add(cboEditions);
		
		JLabel lblBoxSize = new JLabel("Box size: ");
		panneauHaut.add(lblBoxSize);
		
		boxSizeSpinner = new JSpinner();
		boxSizeSpinner.setModel(new SpinnerNumberModel(new Integer(36), new Integer(0), null, new Integer(1)));
		panneauHaut.add(boxSizeSpinner);
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		boostersModel = new BoostersTableModel();
		cardsModel = new DefaultListModel<MagicCard>();
		
		table = new JXTable(boostersModel);
		
		
		scrollPane.setViewportView(table);
		
		JPanel panneauBas = new JPanel();
		getContentPane().add(panneauBas, BorderLayout.SOUTH);
		
		JButton btnCalculate = new JButton("Calculate");
		panneauBas.add(btnCalculate);
		
		lblTotal = new JLabel("Total");
		panneauBas.add(lblTotal);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		getContentPane().add(scrollPane_1, BorderLayout.EAST);
		
		JList<MagicCard> list = new JList<MagicCard>();
		list.setModel(cardsModel);
		list.setCellRenderer(new MagicCardListRenderer());
		scrollPane_1.setViewportView(list);
		
		
		
		
		
		btnCalculate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				
				ThreadManager.getInstance().execute(new Runnable() {
					
					@Override
					public void run() {
						try {
							List<CardShake> prices = MTGControler.getInstance().getEnabledDashBoard().getShakeForEdition((MagicEdition)cboEditions.getSelectedItem());
							boostersModel.clear();
							double total=0;
							
							for(int i=0;i<(int)boxSizeSpinner.getValue();i++)
							{
								List<MagicCard> booster =MTGControler.getInstance().getEnabledProviders().openBooster((MagicEdition) cboEditions.getSelectedItem());
								Collections.reverse(booster);
								Booster line = new Booster();
											line.setBoosterNumber(String.valueOf(i+1));
											line.setCards(booster);
								
								double price = 0;
								for(MagicCard mc : booster)
									for(CardShake cs : prices)
										if(cs.getName().equalsIgnoreCase(mc.getName()))
										{
											price += cs.getPrice();
											line.setPrice(price);
											cs.setCard(mc);
										}
								
								boostersModel.addLine(line);
								total = total+line.getPrice();
								lblTotal.setText("Total : $ " + new DecimalFormat("#0.00").format(total));
							}
							//table.packAll();
							
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						
						
					}
				}, "Open Box");
				
				
				
				
			}
		});
		
		
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
	        public void valueChanged(ListSelectionEvent event) {
	        	if(!event.getValueIsAdjusting())
	        	{
	        		
	        			int viewRow = table.getSelectedRow();
			        	if(viewRow>-1)
			        	{
			        		int modelRow = table.convertRowIndexToModel(viewRow);
			        		List<MagicCard> list = (List)table.getModel().getValueAt(modelRow, 1);
			        		cardsModel.clear();
			        		
			        		for(MagicCard mc : list)
			        			cardsModel.addElement(mc);
			        		
			        		
			        	}
	        	}
	        }
	    });
		
		
		
		
		
		if(props.size()>0) {
			Rectangle r = new Rectangle((int)Double.parseDouble(props.getProperty("x")), 
										(int)Double.parseDouble(props.getProperty("y")),
										(int)Double.parseDouble(props.getProperty("w")),
										(int)Double.parseDouble(props.getProperty("h")));
			setBounds(r);
			}
		
		setVisible(true);
	}


}

