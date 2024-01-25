package org.magic.gui.components.deck;

import static org.magic.services.tools.MTG.capitalize;
import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGCollection;
import org.magic.api.beans.MTGDeck;
import org.magic.api.interfaces.MTGCardsExport.MODS;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.widgets.JExportButton;
import org.magic.gui.models.DeckStockComparisonModel;
import org.magic.gui.renderer.standard.NumberCellEditorRenderer;
import org.magic.services.MTGControler;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.UITools;
public class DeckStockComparatorPanel extends MTGUIComponent {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private JComboBox<MTGCollection> cboCollections;
	private MTGDeck currentDeck;
	private DeckStockComparisonModel model;
	private JButton btnCompare;
	private AbstractBuzyIndicatorComponent buzyLabel;
	private DeckPricePanel pricesPan;
	private JCheckBox chkEditionStrict ;
	private JExportButton btnExportMissing;
	private JCheckBox chkCollectionCheck;

	public void setCurrentDeck(MTGDeck c) {
		this.currentDeck = c;
	}

	public DeckStockComparatorPanel() {
		initGUI();
		initActions();
	}

	private void initGUI() {

		setLayout(new BorderLayout(0, 0));
		btnCompare = new JButton("Compare");
		var panneauHaut = new JPanel();
		cboCollections = UITools.createComboboxCollection();
		buzyLabel = AbstractBuzyIndicatorComponent.createProgressComponent();
		model = new DeckStockComparisonModel();
		btnExportMissing = new JExportButton(MODS.EXPORT);
		btnExportMissing.setText("Export Missing");
		UITools.bindJButton(btnExportMissing, KeyEvent.VK_M, "ExportMissing");

		var pan = new JSplitPane();
		pan.setDividerLocation(0.5);
		pan.setResizeWeight(0.5);

		pan.setOrientation(JSplitPane.VERTICAL_SPLIT);
		pricesPan = new DeckPricePanel();

		JXTable table = UITools.createNewTable(model);
		UITools.initCardToolTipTable(table, 0,null,null,null);

		add(panneauHaut, BorderLayout.NORTH);
		panneauHaut.add(cboCollections);

		chkCollectionCheck = new JCheckBox(capitalize("CHECK_COLLECTION"));
		panneauHaut.add(chkCollectionCheck);

		chkEditionStrict = new JCheckBox(capitalize("EDITION_STRICT"));
		panneauHaut.add(chkEditionStrict);


		panneauHaut.add(btnCompare);
		panneauHaut.add(buzyLabel);

		btnExportMissing.setEnabled(false);
		btnExportMissing.initCardsExport(new Callable<MTGDeck>() {

			@Override
			public MTGDeck call() throws Exception {

				var d = new MTGDeck();
				d.setName(currentDeck.getName());
				d.setDescription("Missing cards for deck " + d.getName());
				model.getItems().forEach(l->d.getMain().put(l.getMc(), l.getResult()));

				return d;
			}
		}, buzyLabel);

		panneauHaut.add(btnExportMissing);

		pan.setLeftComponent(new JScrollPane(table));
		pan.setRightComponent(pricesPan);

		add(pan,BorderLayout.CENTER);

		table.setDefaultRenderer(Integer.class, (JTable t, Object value, boolean isSelected, boolean hasFocus,int row, int column)->{
			var val = (Integer)value;
				if(column==4)
				{
					var c = new JLabel(value.toString(),SwingConstants.CENTER);
					c.setOpaque(true);
					if(val==0)
					{
						c.setBackground(Color.GREEN);
						c.setForeground(Color.BLACK);
					}

					else
					{
						c.setBackground(Color.RED);
						c.setForeground(Color.WHITE);
					}

					return c;

				}
				return new NumberCellEditorRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus,row,column);
		});


		try {
			cboCollections.setSelectedItem(new MTGCollection(MTGControler.getInstance().get("default-library")));
		} catch (Exception e) {
			logger.error("Error retrieving collections",e);
		}

		table.packAll();

	}

	private void initActions() {

		btnCompare.addActionListener(ae-> {
			model.clear();
			if(currentDeck!=null)
			{
				MTGCollection col = (MTGCollection)cboCollections.getSelectedItem();
				buzyLabel.start(currentDeck.getMain().entrySet().size());
				SwingWorker<Void, MTGCard> sw = new SwingWorker<>()
						{
						@Override
						protected Void doInBackground() throws Exception {
							currentDeck.getMain().entrySet().forEach(entry->
							{
								try {
									var has = false;

									if(chkCollectionCheck.isSelected())
										has = getEnabledPlugin(MTGDao.class).listCollectionFromCards(entry.getKey()).contains(col);

									List<MTGCardStock> stocks = getEnabledPlugin(MTGDao.class).listStocks(entry.getKey(), col,chkEditionStrict.isSelected());
									var qty = currentDeck.getMain().get(entry.getKey());
									model.addItem(entry.getKey(),qty,has, stocks);
									publish(entry.getKey());
								} catch (SQLException e) {
									logger.error("Error SQL",e);
								}
							});

							return null;
						}

						@Override
						protected void done() {
							buzyLabel.end();

							List<MTGCard> pricList = new ArrayList<>();
							model.getItems().stream().filter(l->l.getResult()>0).forEach(l->{
								for(var i=0;i<l.getResult();i++)
									pricList.add(l.getMc());
							});

							pricesPan.init(MTGDeck.toDeck(pricList));
							btnExportMissing.setEnabled(!model.isEmpty());
						}

						@Override
						protected void process(List<MTGCard> chunks) {
							buzyLabel.progressSmooth(chunks.size());
						}
				};


				ThreadManager.getInstance().runInEdt(sw, "compare deck and stock");


			}
		});

	}

	@Override
	public String getTitle() {
		return "Stock Comparison";
	}


}
