package org.magic.gui.components.shops.extshop;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import org.jdesktop.swingx.JXTable;
import org.magic.api.interfaces.MTGExternalShop;
import org.magic.api.interfaces.MTGStockItem;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.models.StockItemTableModel;
import org.magic.services.MTGConstants;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;
import org.magic.services.workers.AbstractObservableWorker;

public class StockShopperComponent extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private JComboBox<MTGExternalShop> cboInput;
	private JXTable tableInput;
	private StockItemTableModel modelInput;
	private AbstractBuzyIndicatorComponent buzy;
	private MTGStockItem selectedItem;
	JButton btnSave;
	JButton btnBind;


	public StockShopperComponent() {
		setLayout(new BorderLayout(0, 0));

		var panelCenter = new JPanel();
		var txtSearch = new JTextField(15);
		var panelNorth = new JPanel();
		var panelSouth =new JPanel();
		var btnLoad = UITools.createBindableJButton("", MTGConstants.ICON_SEARCH, KeyEvent.VK_F ,"search stocks");

		btnSave = UITools.createBindableJButton("", MTGConstants.ICON_SAVE, KeyEvent.VK_S ,"save stocks");
		var btnReload= UITools.createBindableJButton("", MTGConstants.ICON_REFRESH, KeyEvent.VK_R ,"reload stocks");
		btnBind= UITools.createBindableJButton("", MTGConstants.ICON_MERGE, KeyEvent.VK_B ,"Bind with");
		btnBind.setEnabled(false);
		panelCenter.setLayout(new BorderLayout());

		cboInput = UITools.createComboboxPlugins(MTGExternalShop.class,true);
		buzy = AbstractBuzyIndicatorComponent.createProgressComponent();
		modelInput = new StockItemTableModel();
		tableInput = UITools.createNewTable(modelInput,true);
		tableInput.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


		for(var i : new int[] {4,5,8})
			tableInput.getColumnExt(modelInput.getColumnName(i)).setVisible(false);

		add(panelNorth, BorderLayout.NORTH);
		add(panelCenter,BorderLayout.CENTER);
		add(panelSouth,BorderLayout.SOUTH);

		panelNorth.add(cboInput);
		panelNorth.add(txtSearch);
		panelNorth.add(btnLoad);
		panelNorth.add(btnReload);
		panelNorth.add(btnBind);
		panelNorth.add(buzy);
		panelNorth.add(btnSave);

		panelCenter.add(new JScrollPane(tableInput), BorderLayout.CENTER);


		btnLoad.addActionListener(_->loadProducts((MTGExternalShop)cboInput.getSelectedItem(),modelInput,txtSearch.getText()));
		btnReload.addActionListener(_->loadProducts((MTGExternalShop)cboInput.getSelectedItem(),modelInput,txtSearch.getText()));

		txtSearch.addActionListener(_->btnLoad.doClick());


		tableInput.getSelectionModel().addListSelectionListener(_->btnBind.setEnabled(tableInput.getSelectedRow()>-1));


		btnBind.addActionListener(_->{
			var menu = new JPopupMenu();
			for (MTGExternalShop exp : MTG.listPlugins(MTGExternalShop.class)) {
				var it = new JMenuItem(exp.getName(), exp.getIcon());
				menu.add(it);
				it.addActionListener(_->{

					MTGStockItem sourceItem = UITools.getTableSelection(tableInput,0);

					var comp = new StockShopperComponent();
					comp.setSelectedProvider(exp);
					var diag = MTGUIComponent.createJDialog(comp, true, true);
					comp.enableSelectionMode(true,diag);
					diag.setVisible(true);

					MTGStockItem destItem = comp.getSelectedItem();

					if(destItem!=null)
					{
						sourceItem.getTiersAppIds().put(exp.getName(), String.valueOf(destItem.getId()));
						sourceItem.setUpdated(true);
					}

				});


			}
			var p = btnBind.getLocationOnScreen();
			menu.show(btnBind, 0, 0);
			menu.setLocation(p.x, p.y + btnBind.getHeight());
		});


		btnSave.addActionListener(_->{
			var ret = modelInput.getItems().stream().filter(MTGStockItem::isUpdated).toList();

			var shop = (MTGExternalShop)cboInput.getSelectedItem();

			var rets= JOptionPane.showConfirmDialog(this, "Update " + ret.size() + " items ?","Update", JOptionPane.YES_NO_OPTION);

			if(rets == JOptionPane.YES_OPTION)
				try {
					shop.saveOrUpdateStock(ret,true);
				} catch (IOException e1) {
					logger.error(e1);
				}
		});
	}

	public MTGStockItem getSelectedItem() {
		return selectedItem;
	}

	private void enableSelectionMode(boolean b, JDialog diag) {
		btnSave.setVisible(!b);
		btnBind.setVisible(!b);
		cboInput.setEnabled(false);
		if(b)
		{
			tableInput.addMouseListener(new MouseAdapter() {

					@Override
					public void mouseClicked(MouseEvent e) {
						if(e.getClickCount()>=2)
						{
							selectedItem = UITools.getTableSelection(tableInput, 0);
							diag.dispose();
						}
					}

			});



		}

	}

	private void setSelectedProvider(MTGExternalShop exp) {
		cboInput.setSelectedItem(exp);

	}

	private void loadProducts(MTGExternalShop ext,StockItemTableModel model,String search) {
		model.clear();

		var sw = new AbstractObservableWorker<List<MTGStockItem>,MTGStockItem,MTGExternalShop>(buzy,ext)
		{
			@Override
			protected List<MTGStockItem> doInBackground() throws Exception {
					return plug.listStock(search);
			}

			@Override
			protected void done() {
				try {
					super.done();
					model.addItems(get());
				} catch (InterruptedException _) {
					Thread.currentThread().interrupt();
				} catch (ExecutionException e) {
					logger.error(e);
				}
			}
		};

		ThreadManager.getInstance().runInEdt(sw,"load stock");
	}

	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_TAB_STOCK;
	}

	@Override
	public String getTitle() {
		return "STOCK";
	}

}
