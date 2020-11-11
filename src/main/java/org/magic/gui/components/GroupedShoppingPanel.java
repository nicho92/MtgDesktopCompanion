package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXTreeTable;
import org.magic.api.beans.MagicCardAlert;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MTGPricesProvider;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.models.GroupedPriceTreeTableModel;
import org.magic.gui.renderer.MagicPriceShoppingTreeCellRenderer;
import org.magic.gui.renderer.standard.BooleanCellEditorRenderer;
import org.magic.gui.renderer.standard.DoubleCellEditorRenderer;
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
	private transient List<MagicCardAlert> cards;
	private JButton btnCheckPrice;
	private AbstractBuzyIndicatorComponent buzy;
	
		
	
	public void initList(List<MagicCardAlert> d) {
		this.cards = d;
		enableControle(true);
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
		
		GroupedPriceTreeTableModel treetModel = new GroupedPriceTreeTableModel();
		
		JXTreeTable tree = new JXTreeTable(treetModel);
		tree.setTreeCellRenderer(new MagicPriceShoppingTreeCellRenderer());
		tree.setShowGrid(true, false);
		tree.setDefaultRenderer(Boolean.class, new BooleanCellEditorRenderer());
		tree.setDefaultRenderer(Double.class, new DoubleCellEditorRenderer());
		
		
		add(new JScrollPane(tree), BorderLayout.CENTER);
		
		
		tree.addMouseListener(new MouseAdapter() {
			
			 @Override
			public void mouseClicked(MouseEvent e) {
				 if (e.getClickCount() == 2) 
				 {
					 
					Object o = UITools.getTableSelection(tree, 0);
					
					if(o instanceof MagicPrice)
					{
						try {
							Desktop.getDesktop().browse(new URI(((MagicPrice)o).getSellerUrl()));
						}  
						catch (URISyntaxException | IOException e1) {
							logger.error(e1);
						}
					}
					 
					 
			     }
			}
			
		});
		
		btnCheckPrice.addActionListener(ae -> {
			
			AbstractObservableWorker<Map<String, List<MagicPrice>>, MagicPrice, MTGPricesProvider> sw = new AbstractObservableWorker<>(buzy,(MTGPricesProvider)cboPricers.getSelectedItem(),cards.size()) {

				@Override
				protected Map<String, List<MagicPrice>> doInBackground() throws Exception {
					return plug.getPricesBySeller(cards.stream().map(MagicCardAlert::getCard).collect(Collectors.toList()));
				}
			
				@Override
				protected void done() {
					super.done();
					try {
						treetModel.init(get());
						} 
					catch (Exception e) {
						logger.error("error",e);
					}
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
