package org.magic.gui.components;

import javax.swing.JPanel;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MagicPricesProvider;
import org.magic.gui.models.CardsPriceTableModel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.ThreadManager;
import org.magic.tools.MagicPricesComparator;

import com.mysql.cj.api.xdevapi.Collection;

import java.awt.BorderLayout;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.event.ItemListener;
import java.util.Collections;
import java.util.List;
import java.awt.event.ItemEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import java.awt.Font;

public class DeckPricePanel extends JPanel {
	
	private JComboBox<MagicPricesProvider> cboPricers;
	private JTable table;
	private CardsPriceTableModel model;
	private MagicDeck deck;
	private JLabel lblPrice ;
	int total=0;
	
	public void initDeck(MagicDeck d)
	{
		this.deck=d;
		lblPrice.setText(String.valueOf(d.getAveragePrice()));
		model.clear();
	}
	
	public DeckPricePanel() {
		setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.NORTH);
		
		cboPricers = new JComboBox<MagicPricesProvider>(new DefaultComboBoxModel(MTGControler.getInstance().getEnabledPricers().toArray()));
		cboPricers.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				if (ie.getStateChange() == ItemEvent.SELECTED) {
					model.setProvider((MagicPricesProvider)cboPricers.getSelectedItem());
				}
			}
		});
		panel.add(cboPricers);
		
		JButton btnCheckPrice = new JButton(MTGConstants.ICON_EURO);
		
		btnCheckPrice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				model.clear();
				
				ThreadManager.getInstance().execute(new Runnable() {
					
					@Override
					public void run() {
						total=0;
						for(MagicCard c : deck.getMap().keySet())
						{
							try {
								List<MagicPrice> prices = model.getProviders().get(0).getPrice(c.getEditions().get(0), c);
								if(prices.size()>0)
								{
									Collections.sort(prices, new MagicPricesComparator());
									MagicPrice p = prices.get(0);
									
									p.setSite(c.getName() +"(x"+ deck.getMap().get(c)+")");
									p.setValue(p.getValue()*deck.getMap().get(c));
									
									total+=p.getValue();
									model.addPrice(p);
									lblPrice.setText(String.valueOf(total) + " " + p.getCurrency());
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

						}
						
						
						
						
						deck.setAveragePrice(total);
						
					}
				});
				
				
				
			}
		});
		panel.add(btnCheckPrice);
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);
		model = new CardsPriceTableModel();
		table = new JTable(model);
		scrollPane.setViewportView(table);
		
		JPanel panelBottom = new JPanel();
		add(panelBottom, BorderLayout.SOUTH);
		
		lblPrice = new JLabel();
		lblPrice.setFont(new Font("Tahoma", Font.BOLD, 13));
		panelBottom.add(lblPrice);
	}

}
