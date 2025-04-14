package org.magic.gui.dashlet;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.awt.BorderLayout;
import java.awt.Rectangle;
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
import org.magic.api.beans.EditionsShakers;
import org.magic.api.beans.MTGBooster;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.enums.EnumExtra;
import org.magic.api.beans.enums.EnumRarity;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDashBoard;
import org.magic.api.interfaces.abstracts.AbstractJDashlet;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.models.BoostersTableModel;
import org.magic.gui.renderer.MagicCardListRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.UITools;
import org.magic.services.workers.AbstractObservableWorker;

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
		DefaultListModel<MTGCard> cardsModel;
		JTextPane txtDetailBox;
		var panneauHaut = new JPanel();
		getContentPane().add(panneauHaut, BorderLayout.NORTH);
		JComboBox<MTGEdition> cboEditions = UITools.createComboboxEditions();
		JComboBox<EnumExtra> cboExtras = UITools.createCombobox(EnumExtra.values());
		
		cboEditions.insertItemAt(null, 0);
		panneauHaut.add(cboEditions);
		panneauHaut.add(cboExtras);
	
		var lblBoxSize = new JLabel("Box size: ");
		panneauHaut.add(lblBoxSize);

		var buzy = AbstractBuzyIndicatorComponent.createLabelComponent();
		buzy.setVisible(false);
		
		boxSizeSpinner = new JSpinner();
		boxSizeSpinner.setModel(new SpinnerNumberModel(36, 0, null, 1));
		panneauHaut.add(boxSizeSpinner);
		panneauHaut.add(buzy);

		

		boostersModel = new BoostersTableModel();
		cardsModel = new DefaultListModel<>();

		table = UITools.createNewTable(boostersModel,false);
		getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);

		var panneauBas = new JPanel();
		getContentPane().add(panneauBas, BorderLayout.SOUTH);

		var btnCalculate = new JButton(MTGConstants.ICON_OPEN);
		panneauHaut.add(btnCalculate);

		var tabbedPane = new JTabbedPane(SwingConstants.TOP);
		getContentPane().add(tabbedPane, BorderLayout.EAST);

		txtDetailBox = new JTextPane();
		txtDetailBox.setEditable(false);
		tabbedPane.addTab("Box", null, txtDetailBox, null);

		var scrollPane1 = new JScrollPane();
		tabbedPane.addTab("Booster", null, scrollPane1, null);

		JList<MTGCard> list1 = new JList<>();
		list1.setModel(cardsModel);
		list1.setCellRenderer(new MagicCardListRenderer());
		scrollPane1.setViewportView(list1);
		
	
		
		
		
		btnCalculate.addActionListener(_ -> {
			
			boostersModel.clear();
			cardsModel.clear();
			txtDetailBox.setText("");
			var sw = new AbstractObservableWorker<List<MTGBooster>, MTGBooster, MTGCardsProvider>(buzy,getEnabledPlugin(MTGCardsProvider.class)) {

				private EditionsShakers prices;
				private double total = 0;
				private Map<EnumRarity, Double> priceRarity = new EnumMap<>(EnumRarity.class);
				
				@Override
				protected List<MTGBooster> doInBackground() throws Exception {
						total = 0;
						priceRarity.clear();
						prices = getEnabledPlugin(MTGDashBoard.class).getShakesForEdition((MTGEdition) cboEditions.getSelectedItem());
						return plug.generateBooster((MTGEdition) cboEditions.getSelectedItem(),(EnumExtra)cboExtras.getSelectedItem(), (Integer)boxSizeSpinner.getValue());
				}
			
				@Override
				protected void notifyEnd() {
					int number=1;
					for(var booster : getResult())
					{
						double price = 0;
						
						for (MTGCard mc : booster.getCards()) {
							for (CardShake cs : prices)
								if (cs.getName().equalsIgnoreCase(mc.getName())) {
									price += cs.getPrice().doubleValue();
									booster.setPrice(price);
									EnumRarity rarity = mc.getRarity();

									if (priceRarity.get(rarity) != null)
										priceRarity.put(rarity, priceRarity.get(rarity) + cs.getPrice().doubleValue());
									else
										priceRarity.put(rarity, cs.getPrice().doubleValue());
								}
						}
						booster.setBoosterNumber(number++);
						boostersModel.addItem(booster);
						total = total + booster.getPrice();
					}
					
					
					var temp = new StringBuilder();
					temp.append("TOTAL: ").append(UITools.formatDouble(total)).append("\n");

					for (Entry<EnumRarity, Double> s : priceRarity.entrySet())
						temp.append(s.getKey()).append(": ").append(UITools.formatDouble(priceRarity.get(s.getKey())))
								.append("\n");

					txtDetailBox.setText(temp.toString());
				}
				
				
			};
			
			ThreadManager.getInstance().runInEdt(sw, "Opening " + cboEditions.getSelectedItem() + " with " + boxSizeSpinner.getValue() + " items");	
		});

		table.getSelectionModel().addListSelectionListener(event -> {
			if (!event.getValueIsAdjusting()) {
				int viewRow = table.getSelectedRow();
				if (viewRow > -1) {
					int modelRow = table.convertRowIndexToModel(viewRow);
					List<MTGCard> list = ((MTGBooster) table.getModel().getValueAt(modelRow, 0)).getCards();
					cardsModel.clear();
					for (MTGCard mc : list)
						cardsModel.addElement(mc);
				}
			}
		});

		if (getProperties().size() > 0) {
			var r = new Rectangle((int) Double.parseDouble(getString("x")),
					(int) Double.parseDouble(getString("y")), (int) Double.parseDouble(getString("w")),
					(int) Double.parseDouble(getString("h")));
			setBounds(r);
			
			if(getString("EDITION")!=null)
				cboEditions.setSelectedItem(new MTGEdition(getString("EDITION")));
			
			
		}
	}

}
