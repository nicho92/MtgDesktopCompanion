package org.magic.gui.components;

import java.awt.BorderLayout;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MTGPricesProvider;
import org.magic.gui.models.CardsPriceTableModel;
import org.magic.services.MTGConstants;
import org.magic.services.threads.ThreadManager;
import org.magic.services.workers.AbstractObservableWorker;
import org.magic.tools.UITools;

public class GroupedShoppingPanel extends JComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JComboBox<MTGPricesProvider> cboPricers;
	private CardsPriceTableModel model;
	private List<MagicCard> cards;
	private JButton btnCheckPrice;
	
	
	public void initList(List<MagicCard> d) {
		this.cards = d;
		try {
			enableControle(true);
		} catch (Exception e) {
			
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


	public GroupedShoppingPanel() {
		setLayout(new BorderLayout(0, 0));

		btnCheckPrice = new JButton(MTGConstants.ICON_EURO);
		JPanel panel = new JPanel();
		
		add(panel, BorderLayout.NORTH);

		cboPricers = UITools.createCombobox(MTGPricesProvider.class,false);
		panel.add(cboPricers);

		
		enableControle(false);
		
		panel.add(btnCheckPrice);
		
		model = new CardsPriceTableModel();

		add(new JScrollPane(), BorderLayout.CENTER);

		btnCheckPrice.addActionListener(ae -> {
			model.clear();
			
			AbstractObservableWorker<Map<String, List<MagicPrice>>, MagicPrice, MTGPricesProvider> sw = new AbstractObservableWorker<>((MTGPricesProvider)cboPricers.getSelectedItem()) {

				@Override
				protected Map<String, List<MagicPrice>> doInBackground() throws Exception {
					return plug.getPricesBySeller(cards);
				}
				
				@Override
				protected void done() {
					try {
						get().entrySet().forEach(e->{
							
							logger.debug("-------------"+e.getKey());
							logger.debug(e.getValue());
							
						});
					} 
					catch (Exception e) {
						logger.error(e);
					}
				}
				
				@Override
				protected void process(List<MagicPrice> p) {
					
				}
				
			};

			ThreadManager.getInstance().runInEdt(sw, "loading deck price");

		});
		
	}

}
