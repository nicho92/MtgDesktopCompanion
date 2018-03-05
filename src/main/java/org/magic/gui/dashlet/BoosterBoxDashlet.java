package org.magic.gui.dashlet;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.Booster;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.gui.abstracts.AbstractJDashlet;
import org.magic.gui.models.BoostersTableModel;
import org.magic.gui.renderer.MagicCardListRenderer;
import org.magic.gui.renderer.MagicEditionListRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.ThreadManager;

public class BoosterBoxDashlet extends AbstractJDashlet{

	private static final long serialVersionUID = 1L;
	private DecimalFormat doubleFormat;
	
	public BoosterBoxDashlet() {
		super();
		setFrameIcon(MTGConstants.ICON_DOLLARS);
		doubleFormat=new DecimalFormat("#0.00");
	}
	
	@Override
	public String getName() {
		return "Booster Box";
	}

	

	@Override
	public void init() {
		//do nothing
	}

	@Override
	public void initGUI() {
		JSpinner boxSizeSpinner;
		
		JXTable table;
		BoostersTableModel boostersModel;
		DefaultListModel<MagicCard> cardsModel;
		JTextPane txtDetailBox;
		JPanel panneauHaut = new JPanel();
		getContentPane().add(panneauHaut, BorderLayout.NORTH);
	
		List<MagicEdition> eds= new ArrayList<>();
		try {
			eds.addAll(MTGControler.getInstance().getEnabledProviders().loadEditions());
			Collections.sort(eds);
			eds.add(0,null);
		} catch (Exception e) {
			MTGLogger.printStackTrace(e);
		}
		
		JComboBox<MagicEdition> cboEditions = new JComboBox<>(new DefaultComboBoxModel<MagicEdition>(eds.toArray(new MagicEdition[eds.size()])));
		cboEditions.setRenderer(new MagicEditionListRenderer());
		
		
		panneauHaut.add(cboEditions);
		
		JLabel lblBoxSize = new JLabel("Box size: ");
		panneauHaut.add(lblBoxSize);
		
		boxSizeSpinner = new JSpinner();
		boxSizeSpinner.setModel(new SpinnerNumberModel(36, 0, null, 1));
		panneauHaut.add(boxSizeSpinner);
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		boostersModel = new BoostersTableModel();
		cardsModel = new DefaultListModel<>();
		
		table = new JXTable(boostersModel);
		
		
		scrollPane.setViewportView(table);
		
		JPanel panneauBas = new JPanel();
		getContentPane().add(panneauBas, BorderLayout.SOUTH);
		
		JButton btnCalculate = new JButton("Open");
		panneauBas.add(btnCalculate);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, BorderLayout.EAST);
		
		txtDetailBox = new JTextPane();
		txtDetailBox.setEditable(false);
		tabbedPane.addTab("Box", null, txtDetailBox, null);
		
		JScrollPane scrollPane1 = new JScrollPane();
		tabbedPane.addTab("Booster", null, scrollPane1, null);
		
		JList<MagicCard> list1 = new JList<>();
		list1.setModel(cardsModel);
		list1.setCellRenderer(new MagicCardListRenderer());
		scrollPane1.setViewportView(list1);
		
		
		btnCalculate.addActionListener(e->
				ThreadManager.getInstance().execute(()->{
						try {
							List<CardShake> prices = MTGControler.getInstance().getEnabledDashBoard().getShakeForEdition((MagicEdition)cboEditions.getSelectedItem());
							boostersModel.clear();
							double total=0;
							Map<String,Double> priceRarity=new HashMap<>();
							
							for(int i=0;i<(int)boxSizeSpinner.getValue();i++)
							{
								Booster booster =MTGControler.getInstance().getEnabledProviders().generateBooster((MagicEdition) cboEditions.getSelectedItem());
								Collections.reverse(booster.getCards());
								booster.setBoosterNumber(String.valueOf(i+1));
											
								
								double price = 0;
								for(MagicCard mc : booster.getCards())
								{
									for(CardShake cs : prices)
										if(cs.getName().equalsIgnoreCase(mc.getName()))
										{
											price += cs.getPrice();
											booster.setPrice(price);
											cs.setCard(mc);
											
											String rarity=mc.getEditions().get(0).getRarity();
											
											if(priceRarity.get(rarity)!=null)
												priceRarity.put(rarity,priceRarity.get(rarity)+cs.getPrice());
											else
												priceRarity.put(rarity,cs.getPrice());
										}
								}								
								boostersModel.addLine(booster);
								total = total+booster.getPrice();
								
								StringBuilder temp = new StringBuilder();
								temp.append("TOTAL: ").append(doubleFormat.format(total)).append("\n");

								for(Entry<String, Double> s : priceRarity.entrySet())
									temp.append(s.getKey()).append(": ").append(doubleFormat.format(priceRarity.get(s.getKey()))).append("\n");
								
								txtDetailBox.setText(temp.toString());
							}
							
						} catch (Exception e1) {
							logger.error(e1);
						}
				}, "Open Box")
		);
		
		
		table.getSelectionModel().addListSelectionListener(event->{
	        	if(!event.getValueIsAdjusting())
	        	{
	        		
	        			int viewRow = table.getSelectedRow();
			        	if(viewRow>-1)
			        	{
			        		int modelRow = table.convertRowIndexToModel(viewRow);
			        		List<MagicCard> list = ((Booster)table.getModel().getValueAt(modelRow, 0)).getCards();
			        		cardsModel.clear();
			        		for(MagicCard mc : list)
			        			cardsModel.addElement(mc);
			        	}
	        	}
	    });
		
		
		
		
		
		if(getProperties().size()>0) {
			Rectangle r = new Rectangle((int)Double.parseDouble(getProperty("x")), 
										(int)Double.parseDouble(getProperty("y")),
										(int)Double.parseDouble(getProperty("w")),
										(int)Double.parseDouble(getProperty("h")));
			setBounds(r);
			}
		
		setVisible(true);
	}


}

