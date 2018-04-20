package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MTGPricesProvider;
import org.magic.gui.models.CardsPriceTableModel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.ThreadManager;
import org.magic.sorters.MagicPricesComparator;

public class DeckPricePanel extends JPanel {

	private JComboBox<MTGPricesProvider> cboPricers;
	private JTable tablePrice;
	private CardsPriceTableModel model;
	private MagicDeck deck;
	private JLabel lblPrice;
	private int total = 0;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());

	public void initDeck(MagicDeck d) {
		this.deck = d;
		try {
			lblPrice.setText(String.valueOf(d.getAveragePrice()));
		} catch (Exception e) {
			lblPrice.setText("");
		}
		model.clear();
	}

	public DeckPricePanel() {
		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		add(panel, BorderLayout.NORTH);

		cboPricers = new JComboBox<>(
				new DefaultComboBoxModel(MTGControler.getInstance().getEnabledPricers().toArray()));
		cboPricers.addItemListener(ie -> {
			if (ie.getStateChange() == ItemEvent.SELECTED) {
				model.setProvider((MTGPricesProvider) cboPricers.getSelectedItem());
			}

		});
		panel.add(cboPricers);

		JButton btnCheckPrice = new JButton(MTGConstants.ICON_EURO);

		btnCheckPrice.addActionListener(ae -> {
			model.clear();

			ThreadManager.getInstance().execute(() -> {
				total = 0;

				for (MagicCard c : deck.getMap().keySet()) {
					try {
						List<MagicPrice> prices = model.getProviders().get(0).getPrice(c.getEditions().get(0), c);
						MagicPrice p = null;
						if (!prices.isEmpty()) {
							Collections.sort(prices, new MagicPricesComparator());
							p = prices.get(0);
							p.setValue(p.getValue() * deck.getMap().get(c));
							p.setSite(c.getName() + "(x" + deck.getMap().get(c) + ")");
						} else {
							p = new MagicPrice();
							p.setValue(0.0);
							p.setSite(c.getName() + "(x" + deck.getMap().get(c) + ") - "
									+ MTGControler.getInstance().getLangService().get("NOT_FOUND"));
							p.setCurrency("");
						}

						model.addPrice(p);
						total += p.getValue();

						lblPrice.setText(String.valueOf(total) + " " + p.getCurrency());

					} catch (Exception e) {
						logger.error("error in " + c, e);
					}

				}
				deck.setAveragePrice(total);
			});

		});
		panel.add(btnCheckPrice);

		lblPrice = new JLabel();
		panel.add(lblPrice);
		lblPrice.setFont(new Font("Tahoma", Font.BOLD, 13));

		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);
		model = new CardsPriceTableModel();
		tablePrice = new JTable(model);
		tablePrice.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent ev) {
				if (ev.getClickCount() == 2 && !ev.isConsumed()) {
					ev.consume();
					try {
						String url = tablePrice.getValueAt(tablePrice.getSelectedRow(), CardsPriceTableModel.ROW_URL)
								.toString();
						Desktop.getDesktop().browse(new URI(url));
					} catch (Exception e) {
						logger.error(e);
					}

				}

			}
		});

		scrollPane.setViewportView(tablePrice);
	}

}
