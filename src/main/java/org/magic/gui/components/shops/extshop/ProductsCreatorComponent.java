package org.magic.gui.components.shops.extshop;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import org.magic.api.beans.shop.Category;
import org.magic.api.interfaces.MTGExternalShop;
import org.magic.api.interfaces.MTGProduct;
import org.magic.api.interfaces.abstracts.extra.AbstractStockItem;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.renderer.ProductListRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.UITools;
import org.magic.services.workers.AbstractObservableWorker;


public class ProductsCreatorComponent extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private JTextField txtSearchProduct;
	private JComboBox<MTGExternalShop> cboInput;
	private JComboBox<MTGExternalShop> cboOutput;

	private JList<MTGProduct> listInput;
	private DefaultListModel<MTGProduct> modelInput;

	private JList<MTGProduct> listOutput;
	private DefaultListModel<MTGProduct> modelOutput;

	private AbstractBuzyIndicatorComponent buzy;
	private JPanel panel;
	private JButton btnSend;
	private JComboBox<Category> cboCategory;
	private JCheckBox chkSearchInput;
	private JCheckBox chkSearchOutput;


	public ProductsCreatorComponent() {
		setLayout(new BorderLayout(0, 0));

		panel = new JPanel();
		btnSend = UITools.createBindableJButton("Export", MTGConstants.ICON_EXPORT, KeyEvent.VK_S,"sendProduct");
		var btnSearch = UITools.createBindableJButton("", MTGConstants.ICON_SEARCH_24, KeyEvent.VK_F,"searchProduct");

		var panelNorth = new JPanel();
		var panelWest = new JPanel();
		var panelNorthWest = new JPanel();
		var panelNorthEast = new JPanel();

		panelWest.setLayout(new BorderLayout());
		var panelEast = new JPanel();
		panelEast.setLayout(new BorderLayout());

		cboInput = UITools.createComboboxPlugins(MTGExternalShop.class,true);
		cboOutput= UITools.createComboboxPlugins(MTGExternalShop.class,true);
		cboCategory = UITools.createCombobox(new ArrayList<>());

		chkSearchInput = new JCheckBox();
		chkSearchInput.setSelected(true);
		chkSearchOutput = new JCheckBox();
		chkSearchOutput.setSelected(true);


		buzy = AbstractBuzyIndicatorComponent.createLabelComponent();
		txtSearchProduct = new JTextField(25);
		modelInput = new DefaultListModel<>();
		listInput = new JList<>(modelInput);
		modelOutput= new DefaultListModel<>();
		listOutput = new JList<>(modelOutput);
		listInput.setCellRenderer(new ProductListRenderer());
		listOutput.setCellRenderer(new ProductListRenderer());


		panelNorth.add(txtSearchProduct);
		panelNorth.add(btnSearch);
		panelNorth.add(buzy);

		add(panelNorth, BorderLayout.NORTH);
		add(panelWest,BorderLayout.WEST);
		add(panelEast,BorderLayout.EAST);


		panelNorthWest.add(chkSearchInput);
		panelNorthWest.add(cboInput);

		panelNorthEast.add(chkSearchOutput);
		panelNorthEast.add(cboOutput);

		panelWest.add(panelNorthWest, BorderLayout.NORTH);
		panelEast.add(panelNorthEast, BorderLayout.NORTH);

		panelWest.add(new JScrollPane(listInput), BorderLayout.CENTER);
		panelEast.add(new JScrollPane(listOutput), BorderLayout.CENTER);


		add(panel, BorderLayout.CENTER);
		panel.add(cboCategory);
		panel.add(btnSend);


		btnSearch.addActionListener(e->loadProducts());
		txtSearchProduct.addActionListener(e->loadProducts());
		btnSend.addActionListener(e->sendProducts());
		btnSend.setEnabled(false);


		cboOutput.addItemListener(il->{
			 if (il.getStateChange() == ItemEvent.SELECTED) {
					initCategory();
		       }
		});

		listInput.addListSelectionListener(lll->{
			btnSend.setEnabled(listInput.getSelectedIndex()>=0);
			btnSend.setText("Insert "+ listInput.getSelectedValuesList().size() + " items");
		});
	}


	@Override
	public void onFirstShowing() {
		initCategory();
	}


	private void initCategory() {

		cboCategory.removeAllItems();
		var sw = new SwingWorker<List<Category>, Void>() {
			@Override
			protected List<Category> doInBackground() throws Exception {
				return ((MTGExternalShop)cboOutput.getSelectedItem()).listCategories();
			}

			@Override
			protected void done() {
				try {
					((DefaultComboBoxModel<Category>)cboCategory.getModel()).addAll(get().stream().sorted(Comparator.comparing(Category::getCategoryName)).toList());
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} catch (Exception e) {
					logger.error(e);
				}
			}
		};
		ThreadManager.getInstance().runInEdt(sw,"load categories");

	}


	private void sendProducts() {

		List<MTGProduct> list = listInput.getSelectedValuesList();


		AbstractObservableWorker<Void,MTGProduct,MTGExternalShop> sw = new AbstractObservableWorker<>(buzy,(MTGExternalShop)cboOutput.getSelectedItem(),list.size())
		{
			@Override
			protected Void doInBackground() throws Exception {
					for(MTGProduct p : list)
					{
							AbstractStockItem<MTGProduct> it = AbstractStockItem.generateDefault();
							it.setProduct(p);
							plug.saveOrUpdateStock(it,false);
							publish(p);
					}
					return null;
			}
			@Override
			protected void process(List<MTGProduct> chunks) {
				super.process(chunks);
				modelOutput.addAll(chunks);
			}
			@Override
			protected void done() {
				super.done();
				listOutput.updateUI();

			}
		};

		ThreadManager.getInstance().runInEdt(sw,"search Products");
	}


	private void loadProducts() {

		String search = txtSearchProduct.getText();

		if(chkSearchInput.isSelected()) {
			modelInput.removeAllElements();
			AbstractObservableWorker<List<MTGProduct>,MTGProduct,MTGExternalShop> sw = new AbstractObservableWorker<>(buzy,(MTGExternalShop)cboInput.getSelectedItem())
			{
				@Override
				protected List<MTGProduct> doInBackground() throws Exception {
						return plug.listProducts(search);
				}
				
				@Override
				protected void done() {
						super.done();
						modelInput.addAll(getResult());
				}

			};
			ThreadManager.getInstance().runInEdt(sw,"search Products");
		}

		if(chkSearchOutput.isSelected()) {
			modelOutput.removeAllElements();
			AbstractObservableWorker<List<MTGProduct>,MTGProduct,MTGExternalShop> sw2 = new AbstractObservableWorker<>(buzy,(MTGExternalShop)cboOutput.getSelectedItem())
			{
				@Override
				protected List<MTGProduct> doInBackground() throws Exception {
						return plug.listProducts(search);
				}

				@Override
				protected void done() {
					try {
						super.done();
						modelOutput.addAll(get());
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					} catch (ExecutionException e) {
						logger.error(e);
					}
				}

			};
			ThreadManager.getInstance().runInEdt(sw2,"search Products");
		}


	}

	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_TAB_CARD;
	}

	@Override
	public String getTitle() {
		return "Product Creation";
	}

}
