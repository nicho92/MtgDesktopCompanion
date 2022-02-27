package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MTGPricesProvider;
import org.magic.gui.models.CardsPriceTableModel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.threads.ThreadManager;
import org.magic.services.workers.AbstractObservableWorker;
import org.magic.tools.UITools;

public class DeckPricePanel extends JComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JComboBox<MTGPricesProvider> cboPricers;
	private JXTable tablePrice;
	private CardsPriceTableModel model;
	private MagicDeck deck;
	private JLabel lblPrice;
	private double total = 0;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	private JButton btnCheckPrice;
	
	
	public void initDeck(MagicDeck d) {
		this.deck = d;
		try {
			lblPrice.setText(UITools.formatDouble(d.getAveragePrice()));
			enableControle(true);
		} catch (Exception e) {
			lblPrice.setText("");
		}
		model.clear();
	}
	
	public void enableControle(boolean b)
	{
		cboPricers.setEnabled(b);
		btnCheckPrice.setEnabled(b);
	}
	
	public JButton getBtnCheckPrice() {
		return btnCheckPrice;
	}
	
	
	public void updatePrice()
	{
		lblPrice.setText(String.valueOf(UITools.formatDouble(total)) + " " + MTGControler.getInstance().getCurrencyService().getCurrentCurrency().getCurrencyCode());
	}
	

	public DeckPricePanel() {
		setLayout(new BorderLayout(0, 0));

		btnCheckPrice = new JButton(MTGConstants.ICON_EURO);
		var panel = new JPanel();
		
		add(panel, BorderLayout.NORTH);

		cboPricers = UITools.createCombobox(MTGPricesProvider.class,false);
		panel.add(cboPricers);

		
		enableControle(false);
		

		btnCheckPrice.addActionListener(ae -> {
			model.clear();
			
			AbstractObservableWorker<List<MagicPrice>, MagicPrice, MTGPricesProvider> sw = new AbstractObservableWorker<>((MTGPricesProvider)cboPricers.getSelectedItem()) {

				@Override
				protected List<MagicPrice> doInBackground() throws Exception {
					return plug.getPrice(deck, false);
				}
				
				@Override
				protected void done() {
					
						try {
							total = get().stream().mapToDouble(MagicPrice::getValue).sum();
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
							logger.error("Interruption");
						} catch (ExecutionException e) {
							logger.error("error getting prices",e);
						}
					
						deck.setAveragePrice(total);
						
					updatePrice();
				}
				
				@Override
				protected void process(List<MagicPrice> p) {
					model.addItems(p);
				}
				
			};

			ThreadManager.getInstance().runInEdt(sw, "loading deck price");

		});
		panel.add(btnCheckPrice);

		lblPrice = new JLabel();
		panel.add(lblPrice);
		lblPrice.setFont(MTGControler.getInstance().getFont().deriveFont( Font.BOLD, 13));

		
		model = new CardsPriceTableModel();
		tablePrice = UITools.createNewTable(model);
		tablePrice.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent ev) {
				if (ev.getClickCount() == 2 && !ev.isConsumed()) {
					ev.consume();
					try {
						
						MagicPrice url = UITools.getTableSelection(tablePrice, 0);
						UITools.browse(url.getUrl());
					} catch (Exception e) {
						logger.error(e);
					}

				}

			}
		});

		add(new JScrollPane(tablePrice), BorderLayout.CENTER);
	}

}
