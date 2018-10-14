package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.models.MagicCardTableModel;
import org.magic.gui.renderer.MagicCollectionIconListRenderer;
import org.magic.gui.renderer.MagicEditionsJLabelRenderer;
import org.magic.gui.renderer.ManaCellRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.ThreadManager;
import org.magic.sorters.CardsEditionSorter;
import org.magic.tools.UITools;


public class CardsEditionTablePanel extends JPanel {
	private JXTable table;
	private MagicCardTableModel model;
	private MagicEdition currentEdition;
	private AbstractBuzyIndicatorComponent buzy;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	private JButton btnImport;
	private JComboBox<MagicCollection> cboCollection;
	private DefaultComboBoxModel<MagicCollection> modelCol;
	
	public CardsEditionTablePanel() {
		setLayout(new BorderLayout(0, 0));
		
		JPanel panneauHaut = new JPanel();
		model = new MagicCardTableModel();
		modelCol= new DefaultComboBoxModel<>();
		
		table = new JXTable(model);
		buzy=AbstractBuzyIndicatorComponent.createProgressComponent();
		
		table.getColumnModel().getColumn(2).setCellRenderer(new ManaCellRenderer());
		table.getColumnModel().getColumn(6).setCellRenderer(new MagicEditionsJLabelRenderer());
		table.setColumnControlVisible(true);
		table.getColumnExt(model.getColumnName(1)).setVisible(false);
		table.getColumnExt(model.getColumnName(6)).setVisible(false);
		table.getColumnExt(model.getColumnName(8)).setVisible(false);
		table.getColumnExt(model.getColumnName(9)).setVisible(false);
		UITools.initTableFilter(table);
		
		panneauHaut.add(buzy);
		add(new JScrollPane(table), BorderLayout.CENTER);
		add(panneauHaut,BorderLayout.NORTH);
		
		JPanel panneauBas = new JPanel();
		add(panneauBas, BorderLayout.SOUTH);
		
		try {
			MTGControler.getInstance().getEnabled(MTGDao.class).getCollections().forEach(collection->modelCol.addElement(collection));
		} catch (SQLException e1) {
			logger.error(e1);
		}
		
		cboCollection =  new JComboBox<>(modelCol);
		cboCollection.setRenderer(new MagicCollectionIconListRenderer());
		panneauBas.add(cboCollection);
		
		btnImport = new JButton(MTGConstants.ICON_MASS_IMPORT_SMALL);
		btnImport.setEnabled(false);
		panneauBas.add(btnImport);
		
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent componentEvent) {
				init(currentEdition);
			}

		});
		
		btnImport.addActionListener(ae->{
			List<MagicCard> list = getSelectedCards();
			
			int res = JOptionPane.showConfirmDialog(null,MTGControler.getInstance().getLangService().getCapitalize("COLLECTION_IMPORT") + " :" + list.size() + " cards in " + cboCollection.getSelectedItem());
			if(res==JOptionPane.YES_OPTION)
			{
				buzy.start(list.size());
				ThreadManager.getInstance().execute(()->{
					for(MagicCard mc : list)
						try {
							MTGControler.getInstance().saveCard(mc, (MagicCollection)cboCollection.getSelectedItem());
							buzy.progress();
						} catch (SQLException e) {
							logger.error("couln't save " + mc,e);
						}
					buzy.end();
				}, "import cards in "+cboCollection.getSelectedItem());
			}
		});
	}
	
	public MagicCard getSelectedCard()
	{
		if(table.getSelectedRow()>-1)
			return (MagicCard) table.getValueAt(table.getSelectedRow(), 0);
		
		return null;
	}
	
	public List<MagicCard> getSelectedCards()
	{
		return UITools.getSelects(table);
	}
	
	
	public JXTable getTable() {
		return table;
	}
	
	public void init(MagicEdition ed)
	{
		this.currentEdition=ed;
		if(isVisible())
			refresh();
	}
	
	public void enabledImport(boolean t)
	{
		btnImport.setEnabled(t);
	}
	
	private void refresh()
	{
		if(currentEdition==null)
			return;
		
		btnImport.setEnabled(false);
		ThreadManager.getInstance().execute(()->{
				buzy.start(currentEdition.getCardCount());
				try {
					model.clear();
					MTGControler.getInstance().getEnabled(MTGCardsProvider.class).addObserver(buzy);
					List<MagicCard> list = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).searchCardByEdition(currentEdition);
					Collections.sort(list, new CardsEditionSorter() );
					for(MagicCard mc : list )
					{
						model.addCard(mc);
					}
				} catch (IOException e) {
					logger.error(e);
				}
				buzy.end();
				MTGControler.getInstance().getEnabled(MTGCardsProvider.class).removeObserver(buzy);
		}, "loading cards from " + currentEdition);
		
		
		
	}
	
	

}
