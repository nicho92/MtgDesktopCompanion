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
import org.magic.api.interfaces.extra.MTGProduct;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.renderer.ProductListRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.ProductFactory;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.UITools;
import org.magic.services.workers.AbstractObservableWorker;

public class ProductsCreatorComponent extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private final JTextField txtSearchProduct;
	private final JComboBox<MTGExternalShop> cboInput;
	private final JComboBox<MTGExternalShop> cboOutput;

	private final JList<MTGProduct> listInput;
	private final DefaultListModel<MTGProduct> modelInput;

	private final JList<MTGProduct> listOutput;
	private final DefaultListModel<MTGProduct> modelOutput;

	private final AbstractBuzyIndicatorComponent buzy;
	private final AbstractBuzyIndicatorComponent buzy2;
	private final JPanel panel;
	private final JButton btnSend;
	private final JComboBox<Category> cboCategory;
	private final JCheckBox chkSearchInput;
	private final JCheckBox chkSearchOutput;

	public ProductsCreatorComponent() {
		setLayout(new BorderLayout(0, 0));

		panel = new JPanel();
		btnSend = UITools.createBindableJButton("Export", MTGConstants.ICON_EXPORT, KeyEvent.VK_S, "sendProduct");
		final var btnSearch = UITools.createBindableJButton("", MTGConstants.ICON_SEARCH_24, KeyEvent.VK_F, "searchProduct");

		final var panelNorth = new JPanel();
		final var panelWest = new JPanel();
		final var panelNorthWest = new JPanel();
		final var panelNorthEast = new JPanel();

		panelWest.setLayout(new BorderLayout());
		final var panelEast = new JPanel();
		panelEast.setLayout(new BorderLayout());

		cboInput = UITools.createComboboxPlugins(MTGExternalShop.class, true);
		cboOutput = UITools.createComboboxPlugins(MTGExternalShop.class, true);
		cboCategory = UITools.createCombobox(new ArrayList<>());

		chkSearchInput = new JCheckBox();
		chkSearchInput.setSelected(true);
		chkSearchOutput = new JCheckBox();
		chkSearchOutput.setSelected(true);

		buzy = AbstractBuzyIndicatorComponent.createLabelComponent();
		buzy2 = AbstractBuzyIndicatorComponent.createLabelComponent();
		txtSearchProduct = new JTextField(25);
		modelInput = new DefaultListModel<>();
		listInput = new JList<>(modelInput);
		modelOutput = new DefaultListModel<>();
		listOutput = new JList<>(modelOutput);
		listInput.setCellRenderer(new ProductListRenderer());
		listOutput.setCellRenderer(new ProductListRenderer());

		panelNorth.add(txtSearchProduct);
		panelNorth.add(btnSearch);

		add(panelNorth, BorderLayout.NORTH);
		add(panelWest, BorderLayout.WEST);
		add(panelEast, BorderLayout.EAST);

		panelNorthWest.add(chkSearchInput);
		panelNorthWest.add(cboInput);
		panelNorthWest.add(buzy);

		panelNorthEast.add(chkSearchOutput);
		panelNorthEast.add(cboOutput);
		panelNorthEast.add(buzy2);

		panelWest.add(panelNorthWest, BorderLayout.NORTH);
		panelEast.add(panelNorthEast, BorderLayout.NORTH);

		panelWest.add(new JScrollPane(listInput), BorderLayout.CENTER);
		panelEast.add(new JScrollPane(listOutput), BorderLayout.CENTER);

		add(panel, BorderLayout.CENTER);
		panel.add(cboCategory);
		panel.add(btnSend);

		btnSearch.addActionListener(_ -> loadProducts());
		txtSearchProduct.addActionListener(_ -> loadProducts());
		btnSend.addActionListener(_ -> sendProducts());
		btnSend.setEnabled(false);

		cboOutput.addItemListener(il -> {
			if (il.getStateChange() == ItemEvent.SELECTED) {
				initCategory();
			}
		});

		listInput.addListSelectionListener(_ -> {
			btnSend.setEnabled(listInput.getSelectedIndex() >= 0);
			btnSend.setText("Insert " + listInput.getSelectedValuesList().size() + " items");
		});
	}

	@Override
	public void onFirstShowing() {
		initCategory();
	}

	private void initCategory() {

		cboCategory.removeAllItems();
		final var sw = new SwingWorker<List<Category>, Void>() {
			@Override
			protected List<Category> doInBackground() throws Exception {
				return ((MTGExternalShop) cboOutput.getSelectedItem()).listCategories();
			}

			@Override
			protected void done() {
				try {
					((DefaultComboBoxModel<Category>) cboCategory.getModel())
							.addAll(get().stream().sorted(Comparator.comparing(Category::getCategoryName)).toList());
				} catch (final InterruptedException _) {
					Thread.currentThread().interrupt();
				} catch (final Exception e) {
					logger.error(e);
				}
			}
		};
		ThreadManager.getInstance().runInEdt(sw, "load categories");

	}

	private void sendProducts() {

		final List<MTGProduct> list = listInput.getSelectedValuesList();

		final var sw = new AbstractObservableWorker<Void, MTGProduct, MTGExternalShop>(buzy,
				(MTGExternalShop) cboOutput.getSelectedItem(), list.size()) {
			@Override
			protected Void doInBackground() throws Exception {
				for (final MTGProduct p : list) {
					final var it = ProductFactory.generateStockItem(p);
					plug.saveOrUpdateStock(it, false);
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

		ThreadManager.getInstance().runInEdt(sw, "send Products");
	}

	private void loadProducts() {

		final String search = txtSearchProduct.getText();

		if (chkSearchInput.isSelected()) {
			modelInput.removeAllElements();
			final var sw = new AbstractObservableWorker<List<MTGProduct>, MTGProduct, MTGExternalShop>(buzy,
					(MTGExternalShop) cboInput.getSelectedItem()) {
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
			ThreadManager.getInstance().runInEdt(sw, "load Products");
		}

		if (chkSearchOutput.isSelected()) {
			modelOutput.removeAllElements();
			final var sw2 = new AbstractObservableWorker<List<MTGProduct>, MTGProduct, MTGExternalShop>(buzy2,
					(MTGExternalShop) cboOutput.getSelectedItem()) {
				@Override
				protected List<MTGProduct> doInBackground() throws Exception {
					return plug.listProducts(search);
				}

				@Override
				protected void done() {
					try {
						super.done();
						modelOutput.addAll(get());
					} catch (final InterruptedException _) {
						Thread.currentThread().interrupt();
					} catch (final ExecutionException e) {
						logger.error(e);
					}
				}

			};
			ThreadManager.getInstance().runInEdt(sw2, "search Products");
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
