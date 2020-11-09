package org.magic.gui.components;

import java.awt.BorderLayout;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MTGPricesProvider;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.models.CardsPriceTableModel;
import org.magic.services.MTGConstants;
import org.magic.services.threads.ThreadManager;
import org.magic.services.workers.AbstractObservableWorker;
import org.magic.tools.UITools;

public class GroupedShoppingPanel extends MTGUIComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JComboBox<MTGPricesProvider> cboPricers;
	private List<MagicCard> cards;
	private JButton btnCheckPrice;
	private DefaultTreeModel treeModel;
	private DefaultMutableTreeNode root;
	private AbstractBuzyIndicatorComponent buzy;
	
	
	public void initList(List<MagicCard> d) {
		this.cards = d;
		try {
			enableControle(true);
		} catch (Exception e) {
			
		}
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
		buzy = AbstractBuzyIndicatorComponent.createProgressComponent();
		btnCheckPrice = new JButton(MTGConstants.ICON_EURO);
		JPanel panel = new JPanel();
		
		add(panel, BorderLayout.NORTH);

		cboPricers = UITools.createCombobox(MTGPricesProvider.class,false);
		panel.add(cboPricers);

		
		enableControle(false);
		
		panel.add(btnCheckPrice);
		panel.add(buzy);
		
		root = new DefaultMutableTreeNode("Shoppings");
		treeModel = new DefaultTreeModel(root);
		
		
		JTree tree = new JTree(treeModel);
		add(new JScrollPane(tree), BorderLayout.CENTER);
		
		btnCheckPrice.addActionListener(ae -> {
			root.removeAllChildren();
			buzy.start();
			AbstractObservableWorker<Map<String, List<MagicPrice>>, MagicPrice, MTGPricesProvider> sw = new AbstractObservableWorker<>((MTGPricesProvider)cboPricers.getSelectedItem()) {

				@Override
				protected Map<String, List<MagicPrice>> doInBackground() throws Exception {
					return plug.getPricesBySeller(cards);
				}
				
				@Override
				protected void done() {
					try {
						get().entrySet().forEach(e->{
							
							DefaultMutableTreeNode user = new DefaultMutableTreeNode(e.getKey() +" (" + e.getValue().size()+" items at "+ e.getValue().stream().mapToDouble(MagicPrice::getValue).sum() +")");
							e.getValue().forEach(v->user.add(new DefaultMutableTreeNode(v + " " + v.getValue() + " " + v.getCurrency())));
							root.add(user);
							treeModel.reload();
						});
						buzy.end();
					} 
					catch (Exception e) {
						logger.error(e);
						buzy.end();
					}
					
				}
				
				@Override
				protected void process(List<MagicPrice> p) {
					
				}
				
			};

			ThreadManager.getInstance().runInEdt(sw, "loading deck price");

		});
		
	}

	@Override
	public String getTitle() {
		return "GROUPED_BUY";
	}

}
