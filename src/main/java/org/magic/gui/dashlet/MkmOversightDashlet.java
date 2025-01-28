package org.magic.gui.dashlet;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.util.concurrent.Callable;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.commons.lang3.RegExUtils;
import org.api.mkm.modele.InsightElement;
import org.api.mkm.services.InsightService;
import org.api.mkm.tools.MkmConstants;
import org.magic.api.exports.impl.MKMFileWantListExport;
import org.magic.api.interfaces.abstracts.AbstractJDashlet;
import org.magic.gui.abstracts.GenericTableModel;
import org.magic.services.network.URLTools;
import org.magic.services.tools.UITools;

public class MkmOversightDashlet extends AbstractJDashlet {


	private static final String INSIGHT_SELECTION_KEY = "INSIGHT_SELECTION";
	private static final String CHANGE_VALUE = "changeValue";
	private static final String PRICE = "price";
	private static final String YESTERDAY_PRICE = "yesterdayPrice";
	private static final String YESTERDAY_STOCK = "yesterdayStock";
	private static final String ED = "ed";
	private static final String CARD_NAME = "cardName";
	private static final long serialVersionUID = 1L;
	private transient InsightService service ;
	private GenericTableModel<InsightElement> model;
	private JComboBox<INSIGHT_SELECTION> comboBox;


	private enum INSIGHT_SELECTION  { STOCK_REDUCTION,BEST_BARGAIN,TOP_CARDS,BIGGEST_START_PRICE,BIGGEST_START_PRICE_FOIL,BIGGEST_AVG_SALES,BIGGEST_AVG_SALES_FOIL }


	@Override
	public String getCategory() {
		return "Market";
	}

	@Override
	public ImageIcon getDashletIcon() {
		return new ImageIcon(MKMFileWantListExport.class.getResource("/icons/plugins/magiccardmarket.png"));
	}


	@Override
	public String getName() {
		return "Mkm Oversight";
	}

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

	@Override
	public void initGUI()
	{
		getContentPane().setLayout(new BorderLayout(0, 0));
		model=new GenericTableModel<InsightElement>();
		var panneauHaut = new JPanel();
		getContentPane().add(panneauHaut, BorderLayout.NORTH);
		comboBox = UITools.createCombobox(INSIGHT_SELECTION.values());
		panneauHaut.add(comboBox);
		service = new InsightService();
		var table = UITools.createNewTable(model,false);
		getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
		comboBox.addItemListener(pcl->{

			if (pcl.getStateChange() == ItemEvent.SELECTED) {
				init();
			}
		});

		UITools.initCardToolTipTable(table,0,null,null, new Callable<Void>()
				{

					@Override
					public Void call() throws Exception {
						String cardName  = UITools.getTableSelection(table, 0);
						String setName  = UITools.getTableSelection(table, 1);

						cardName=RegExUtils.replaceAll(cardName,"'","");
						cardName=RegExUtils.replaceAll(cardName," \\(V\\.","-V");
						cardName=RegExUtils.replaceAll(cardName,"\\)","");
						cardName=RegExUtils.replaceAll(cardName," ","-");


						UITools.browse(MkmConstants.MKM_SITE_URL+"/en/Magic/Products/Singles/"+URLTools.encode(setName)+"/"+URLTools.encode(cardName));

						return null;
					}
			});

		if (getProperties().size() > 0) {
			var r = new Rectangle((int) Double.parseDouble(getString("x")),
					(int) Double.parseDouble(getString("y")), (int) Double.parseDouble(getString("w")),
					(int) Double.parseDouble(getString("h")));
			setBounds(r);

			if(getString(INSIGHT_SELECTION_KEY)!=null)
					comboBox.setSelectedItem(INSIGHT_SELECTION.valueOf(getString(INSIGHT_SELECTION_KEY)));
		}

	}

	@Override
	public void init() {
		try {


			setProperty(INSIGHT_SELECTION_KEY, comboBox.getSelectedItem().toString());

			switch((INSIGHT_SELECTION)comboBox.getSelectedItem())
			{
				case BEST_BARGAIN:
					model.setColumns(CARD_NAME,ED,PRICE);
					model.init(service.getBestBargain());
					break;

				case STOCK_REDUCTION:
					model.setColumns(CARD_NAME,ED,YESTERDAY_STOCK,"stock",CHANGE_VALUE);
					model.init(service.getHighestPercentStockReduction());
					break;
				case BIGGEST_AVG_SALES:
					model.setColumns(CARD_NAME,ED,YESTERDAY_PRICE,PRICE,CHANGE_VALUE);
					model.init(service.getBiggestAvgSalesPriceIncrease(false));
					break;
				case BIGGEST_AVG_SALES_FOIL:
					model.setColumns(CARD_NAME,ED,YESTERDAY_PRICE,PRICE,CHANGE_VALUE);
					model.init(service.getBiggestAvgSalesPriceIncrease(true));
					break;
				case BIGGEST_START_PRICE:
					model.setColumns(CARD_NAME,ED,YESTERDAY_PRICE,PRICE,CHANGE_VALUE);
					model.init(service.getStartingPriceIncrease(false));
					break;
				case BIGGEST_START_PRICE_FOIL:
					model.setColumns(CARD_NAME,ED,YESTERDAY_PRICE,PRICE,CHANGE_VALUE);
					model.init(service.getStartingPriceIncrease(true));
					break;
				case TOP_CARDS:
					model.setColumns(CARD_NAME,ED,PRICE);
					model.init(service.getTopCards(1));
					break;
			default:
				break;
			}
		} catch (IOException e) {
		logger.error(e);
		}
	}

}
