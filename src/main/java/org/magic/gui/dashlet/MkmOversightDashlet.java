package org.magic.gui.dashlet;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.api.mkm.modele.InsightElement;
import org.api.mkm.services.InsightService;
import org.jdesktop.swingx.JXTable;
import org.magic.api.exports.impl.MKMFileWantListExport;
import org.magic.api.interfaces.abstracts.AbstractJDashlet;
import org.magic.gui.abstracts.GenericTableModel;
import org.magic.tools.UITools;

public class MkmOversightDashlet extends AbstractJDashlet {
	

	private static final long serialVersionUID = 1L;
	private transient InsightService service ;
	private GenericTableModel<InsightElement> model;
	private JComboBox<INSIGHT_SELECTION> comboBox;
	
	
	private enum INSIGHT_SELECTION  { STOCK_REDUCTION,BEST_BARGAIN };
	
	
	
	
	@Override
	public Icon getIcon() {
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
		model=new GenericTableModel<>();
		JPanel panneauHaut = new JPanel();
		getContentPane().add(panneauHaut, BorderLayout.NORTH);
		comboBox = UITools.createCombobox(INSIGHT_SELECTION.values());
		panneauHaut.add(comboBox);
		service = new InsightService();
		JXTable table = new JXTable(model);
		getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
		
		
		comboBox.addItemListener(pcl->loadingItem());
		
	}

	
	private void loadingItem() {
		try {
			
			switch((INSIGHT_SELECTION)comboBox.getSelectedItem())
			{
				case BEST_BARGAIN:
					model.setColumns("cardName","ed","yesterdayPrice","price","changeValue");
					model.init(service.getStartingPriceIncrease(false));
					break;
					
				case STOCK_REDUCTION:
					model.setColumns("cardName","ed","yesterdayStock","stock","changeValue");
					model.init(service.getHighestPercentStockReduction());
					break;
			}
		} catch (IOException e) {
		logger.error(e);
		}
	}


	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

}
