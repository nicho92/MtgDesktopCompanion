package org.magic.gui.components.shops.extshop;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.magic.api.beans.shop.Transaction;
import org.magic.api.interfaces.MTGExternalShop;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.shops.TransactionsPanel;
import org.magic.services.MTGConstants;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.UITools;
import org.magic.services.workers.AbstractObservableWorker;

public class TransactionCreatorComponent extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private final JComboBox<MTGExternalShop> cboInput;
	private final JComboBox<MTGExternalShop> cboOutput;

	private final JList<Transaction> listOutput;
	private final DefaultListModel<Transaction> modelOutput;

	private final AbstractBuzyIndicatorComponent buzy;
	private final JButton btnSend;

	private final TransactionsPanel panelTransactions;

	public TransactionCreatorComponent() {
		setLayout(new BorderLayout(0, 0));

		btnSend = UITools.createBindableJButton("Export", MTGConstants.ICON_EXPORT, KeyEvent.VK_S, "searchProduct");
		final var btnSearch = UITools.createBindableJButton("", MTGConstants.ICON_SEARCH_24, KeyEvent.VK_F, "searchProduct");

		final var panelNorth = new JPanel();
		final var panelWest = new JPanel();
		panelWest.setLayout(new BorderLayout());
		final var panelEast = new JPanel();
		panelEast.setLayout(new BorderLayout());

		cboInput = UITools.createComboboxPlugins(MTGExternalShop.class, true);
		cboOutput = UITools.createComboboxPlugins(MTGExternalShop.class, true);

		buzy = AbstractBuzyIndicatorComponent.createProgressComponent();
		modelOutput = new DefaultListModel<>();
		listOutput = new JList<>(modelOutput);

		panelTransactions = new TransactionsPanel();
		panelTransactions.disableCommands();
		panelNorth.add(btnSearch);
		panelNorth.add(btnSend);
		panelNorth.add(buzy);

		add(panelNorth, BorderLayout.NORTH);
		add(panelWest, BorderLayout.WEST);
		add(panelEast, BorderLayout.EAST);

		panelWest.add(cboInput, BorderLayout.NORTH);
		panelEast.add(cboOutput, BorderLayout.NORTH);

		panelEast.add(new JScrollPane(listOutput), BorderLayout.CENTER);

		add(panelTransactions, BorderLayout.CENTER);

		btnSearch.addActionListener(_ -> loadTransactions());
		btnSend.addActionListener(_ -> sendTransaction());
		btnSend.setEnabled(false);

		panelTransactions.getTable().getSelectionModel().addListSelectionListener(event -> {
			if (!event.getValueIsAdjusting()) {
				btnSend.setEnabled(!UITools.getTableSelections(panelTransactions.getTable(), 0).isEmpty());
				btnSend.setText(
						"send " + UITools.getTableSelections(panelTransactions.getTable(), 0).size() + " items");
			}
		});
	}

	private void sendTransaction() {

		final List<Transaction> list = UITools.getTableSelections(panelTransactions.getTable(), 0);

		final AbstractObservableWorker<Void, Transaction, MTGExternalShop> sw = new AbstractObservableWorker<>(buzy,
				(MTGExternalShop) cboOutput.getSelectedItem(), list.size()) {
			@Override
			protected Void doInBackground() throws Exception {
				for (final Transaction t : list) {

					if (!t.getSourceShopName().equals(plug.getName())) {
					    t.setId(-1);
					}

					if (t.getContact().getId() < 0) {
						final var cId = plug.saveOrUpdateContact(t.getContact());
						t.getContact().setId(cId);
					}
					plug.saveOrUpdateTransaction(t);
					publish(t);
				}
				return null;
			}
			@Override
			protected void process(List<Transaction> chunks) {
				super.process(chunks);
				modelOutput.addAll(chunks);
			}
			@Override
			protected void done() {
				super.done();
				listOutput.updateUI();
			}
		};

		ThreadManager.getInstance().runInEdt(sw, "search Products");
	}

	private void loadTransactions() {
		panelTransactions.getModel().clear();

		final AbstractObservableWorker<List<Transaction>, Transaction, MTGExternalShop> sw = new AbstractObservableWorker<>(
				buzy, (MTGExternalShop) cboInput.getSelectedItem()) {
			@Override
			protected List<Transaction> doInBackground() throws Exception {
				return plug.listTransaction();
			}

			@Override
			protected void done() {
				super.done();
				try {
					panelTransactions.init(get());
				} catch (InterruptedException | ExecutionException _) {
					Thread.currentThread().interrupt();
				}
			}
		};

		ThreadManager.getInstance().runInEdt(sw, "search Transaction");
	}

	@Override
	public String getTitle() {
		return "Transactions Importer";
	}

	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_TAB_PRICES;
	}

}
