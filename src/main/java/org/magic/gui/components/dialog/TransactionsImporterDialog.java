package org.magic.gui.components.dialog;

import static org.magic.services.tools.MTG.capitalize;
import static org.magic.services.tools.MTG.listEnabledPlugins;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.logging.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.OrderEntry;
import org.magic.api.beans.technical.RetrievableTransaction;
import org.magic.api.interfaces.MTGShopper;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.models.ShoppingEntryTableModel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.logging.MTGLogger;
import org.magic.services.threads.MTGRunnable;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.UITools;
import org.magic.services.workers.AbstractObservableWorker;
public class TransactionsImporterDialog extends JDialog {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private JXTable table;
	private JComboBox<MTGShopper> cboSniffers;
	private ShoppingEntryTableModel model;
	private AbstractBuzyIndicatorComponent lblLoad = AbstractBuzyIndicatorComponent.createLabelComponent();
	private JButton btnImport;
	private transient MTGShopper selectedSniffer;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	private JPanel panelChoose;
	private transient List<RetrievableTransaction> selectedEntries;

	public TransactionsImporterDialog() {

		setSize(new Dimension(500, 300));
		setTitle(capitalize("SHOP"));
		setIconImage(MTGConstants.ICON_SHOP.getImage());
		setModal(true);
		getContentPane().setLayout(new BorderLayout(0, 0));

		model = new ShoppingEntryTableModel();

		table = UITools.createNewTable(model);

		panelChoose = new JPanel();
		var panel = new JPanel();
		var panelButton = new JPanel();
		var btnClose = new JButton(MTGConstants.ICON_CANCEL);
		var btnLoad = new JButton(MTGConstants.ICON_OPEN);
		btnImport = new JButton(MTGConstants.ICON_CHECK);
		cboSniffers =UITools.createCombobox(MTGShopper.class,false);

		panel.setLayout(new BorderLayout(0, 0));


		getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
		getContentPane().add(panel, BorderLayout.NORTH);

		panel.add(panelChoose, BorderLayout.WEST);
		panelChoose.add(cboSniffers);
		panelChoose.add(btnLoad);
		panelChoose.add(lblLoad);
		getContentPane().add(panelButton, BorderLayout.SOUTH);
		panelButton.add(btnClose);
		panelButton.add(btnImport);

		selectedSniffer = listEnabledPlugins(MTGShopper.class).get(0);



		cboSniffers.addActionListener(e -> selectedSniffer = (MTGShopper) cboSniffers.getSelectedItem());

		btnLoad.addActionListener(ae->{
			AbstractObservableWorker<List<RetrievableTransaction>, RetrievableTransaction, MTGShopper> sw = new AbstractObservableWorker<>(lblLoad,selectedSniffer) {
				@Override
				protected List<RetrievableTransaction> doInBackground() throws Exception {
					return plug.listOrders();
				}
				@Override
				protected void done() {
					super.done();
					model.init(getResult());
				}
			};
			ThreadManager.getInstance().runInEdt(sw, "loading orders");
		});


		btnClose.setToolTipText(capitalize("CANCEL"));

		btnClose.addActionListener(e -> dispose());

		btnImport.setToolTipText(capitalize("IMPORT"));

		btnImport.addActionListener(e ->
			ThreadManager.getInstance().invokeLater(new MTGRunnable() {

				@Override
				protected void auditedRun() {
					try {
						btnImport.setEnabled(false);
						selectedEntries = UITools.getTableSelections(table, 0);
						btnImport.setEnabled(true);
						dispose();
					} catch (Exception e1) {
						logger.error("Error snif",e1);
						MTGControler.getInstance().notify(e1);
						btnImport.setEnabled(true);
					}
					finally
					{
						selectedSniffer.removeObserver(lblLoad);
					}

				}
				}, "Loading Orders Import Dialog")
		);


		setLocationRelativeTo(null);
	}

	public List<RetrievableTransaction> getSelectedEntries() {
		return selectedEntries;
	}

}
