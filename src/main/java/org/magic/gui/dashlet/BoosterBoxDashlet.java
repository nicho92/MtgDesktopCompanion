package org.magic.gui.dashlet;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
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
import javax.swing.SwingConstants;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.MTGBooster;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.enums.EnumRarity;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.api.interfaces.abstracts.AbstractJDashlet;
import org.magic.gui.models.BoostersTableModel;
import org.magic.gui.renderer.MagicCardListRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.threads.MTGRunnable;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.UITools;

public class BoosterBoxDashlet extends AbstractJDashlet {

	private static final long serialVersionUID = 1L;

	public BoosterBoxDashlet() {
		super();
	}

	@Override
	public ImageIcon getDashletIcon() {
		return MTGConstants.ICON_DOLLARS;
	}

	@Override
	public String getName() {
		return "Booster Box";
	}

	@Override
	public String getCategory() {
		return "Financial";
	}


	@Override
	public void init() {
		// do nothing
	}

	@Override
	public void initGUI() {
		JSpinner boxSizeSpinner;

		JXTable table;
		BoostersTableModel boostersModel;
		DefaultListModel<MagicCard> cardsModel;
		JTextPane txtDetailBox;
		var panneauHaut = new JPanel();
		getContentPane().add(panneauHaut, BorderLayout.NORTH);
		JComboBox<MagicEdition> cboEditions = UITools.createComboboxEditions();
		cboEditions.insertItemAt(null, 0);
		panneauHaut.add(cboEditions);

		var lblBoxSize = new JLabel("Box size: ");
		panneauHaut.add(lblBoxSize);

		boxSizeSpinner = new JSpinner();
		boxSizeSpinner.setModel(new SpinnerNumberModel(36, 0, null, 1));
		panneauHaut.add(boxSizeSpinner);


		boostersModel = new BoostersTableModel();
		cardsModel = new DefaultListModel<>();

		table = UITools.createNewTable(boostersModel);
		getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);

		var panneauBas = new JPanel();
		getContentPane().add(panneauBas, BorderLayout.SOUTH);

		var btnCalculate = new JButton(MTGConstants.ICON_OPEN);
		panneauBas.add(btnCalculate);

		var tabbedPane = new JTabbedPane(SwingConstants.TOP);
		getContentPane().add(tabbedPane, BorderLayout.EAST);

		txtDetailBox = new JTextPane();
		txtDetailBox.setEditable(false);
		tabbedPane.addTab("Box", null, txtDetailBox, null);

		var scrollPane1 = new JScrollPane();
		tabbedPane.addTab("Booster", null, scrollPane1, null);

		JList<MagicCard> list1 = new JList<>();
		list1.setModel(cardsModel);
		list1.setCellRenderer(new MagicCardListRenderer());
		scrollPane1.setViewportView(list1);

		btnCalculate.addActionListener(e -> ThreadManager.getInstance().executeThread(new MTGRunnable() {

			@Override
			protected void auditedRun() {
				try {
					var prices = getEnabledPlugin(MTGDashBoard.class).getShakesForEdition((MagicEdition) cboEditions.getSelectedItem());
					boostersModel.clear();
					double total = 0;
					Map<EnumRarity, Double> priceRarity = new EnumMap<>(EnumRarity.class);

					for (var i = 0; i < (int) boxSizeSpinner.getValue(); i++) {
						var booster = getEnabledPlugin(MTGCardsProvider.class).generateBooster((MagicEdition) cboEditions.getSelectedItem());
						Collections.reverse(booster.getCards());
						booster.setBoosterNumber(String.valueOf(i + 1));

						double price = 0;
						for (MagicCard mc : booster.getCards()) {
							for (CardShake cs : prices)
								if (cs.getName().equalsIgnoreCase(mc.getName())) {
									price += cs.getPrice();
									booster.setPrice(price);
									cs.setCard(mc);

									EnumRarity rarity = mc.getRarity();

									if (priceRarity.get(rarity) != null)
										priceRarity.put(rarity, priceRarity.get(rarity) + cs.getPrice());
									else
										priceRarity.put(rarity, cs.getPrice());
								}
						}
						boostersModel.addItem(booster);
						total = total + booster.getPrice();

						var temp = new StringBuilder();
						temp.append("TOTAL: ").append(UITools.formatDouble(total)).append("\n");

						for (Entry<EnumRarity, Double> s : priceRarity.entrySet())
							temp.append(s.getKey()).append(": ").append(UITools.formatDouble(priceRarity.get(s.getKey())))
									.append("\n");

						txtDetailBox.setText(temp.toString());
					}

				} catch (Exception e1) {
					logger.error(e1);
				}

			}
		}, "Open Box"));

		table.getSelectionModel().addListSelectionListener(event -> {
			if (!event.getValueIsAdjusting()) {

				int viewRow = table.getSelectedRow();
				if (viewRow > -1) {
					int modelRow = table.convertRowIndexToModel(viewRow);
					List<MagicCard> list = ((MTGBooster) table.getModel().getValueAt(modelRow, 0)).getCards();
					cardsModel.clear();
					for (MagicCard mc : list)
						cardsModel.addElement(mc);
				}
			}
		});

		if (getProperties().size() > 0) {
			var r = new Rectangle((int) Double.parseDouble(getString("x")),
					(int) Double.parseDouble(getString("y")), (int) Double.parseDouble(getString("w")),
					(int) Double.parseDouble(getString("h")));
			setBounds(r);
		}
	}

}
