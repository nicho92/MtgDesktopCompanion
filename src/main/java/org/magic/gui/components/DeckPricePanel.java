package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MTGPricesProvider;
import org.magic.gui.models.CardsPriceTableModel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.threads.ThreadManager;
import org.magic.sorters.MagicPricesComparator;
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
	private int total = 0;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	private JButton btnCheckPrice;
	
	
	public void initDeck(MagicDeck d) {
		this.deck = d;
		try {
			lblPrice.setText(String.valueOf(d.getAveragePrice()));
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
	

	public DeckPricePanel() {
		setLayout(new BorderLayout(0, 0));

		btnCheckPrice = new JButton(MTGConstants.ICON_EURO);
		JPanel panel = new JPanel();
		
		add(panel, BorderLayout.NORTH);

		cboPricers = UITools.createCombobox(MTGPricesProvider.class,false);
		panel.add(cboPricers);

		
		enableControle(false);
		

		btnCheckPrice.addActionListener(ae -> {
			model.clear();
			SwingWorker<Void, MagicPrice> sw = new SwingWorker<>() {
				@Override
				protected void done() {
					lblPrice.setText(String.valueOf(total) + " " + MTGControler.getInstance().getCurrencyService().getCurrentCurrency().getCurrencyCode());
					deck.setAveragePrice(total);
				}

				@Override
				protected void process(List<MagicPrice> p) {
					model.addItems(p);
					
				}

				@Override
				protected Void doInBackground(){
					for (MagicCard c : deck.getMain().keySet()) {
						try {
							List<MagicPrice> prices = ((MTGPricesProvider)cboPricers.getSelectedItem()).getPrice(c.getCurrentSet(), c);
							MagicPrice p = null;
							if (!prices.isEmpty()) {
								Collections.sort(prices, new MagicPricesComparator());
								p = prices.get(0);
								p.setValue(p.getValue() * deck.getMain().get(c));
								p.setSite(c.getName() + "(x" + deck.getMain().get(c) + ")");
							} else {
								p = new MagicPrice();
								p.setValue(0.0);
								p.setSite(c.getName() + "(x" + deck.getMain().get(c) + ") - "+ MTGControler.getInstance().getLangService().get("NOT_FOUND"));
							}
							
							publish(p);
							total += p.getValue();
							

						} catch (Exception e) {
							logger.error("error in " + c, e);
						}

					}
					return null;
				}
			};
			ThreadManager.getInstance().runInEdt(sw, "loading deck price");

		});
		panel.add(btnCheckPrice);

		lblPrice = new JLabel();
		panel.add(lblPrice);
		lblPrice.setFont(MTGControler.getInstance().getFont().deriveFont( Font.BOLD, 13));

		
		model = new CardsPriceTableModel();
		tablePrice = new JXTable(model);
		tablePrice.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent ev) {
				if (ev.getClickCount() == 2 && !ev.isConsumed()) {
					ev.consume();
					try {
						String url = tablePrice.getValueAt(tablePrice.getSelectedRow(), CardsPriceTableModel.COLUMUM_URL)
								.toString();
						Desktop.getDesktop().browse(new URI(url));
					} catch (Exception e) {
						logger.error(e);
					}

				}

			}
		});

		add(new JScrollPane(tablePrice), BorderLayout.CENTER);
	}

}
