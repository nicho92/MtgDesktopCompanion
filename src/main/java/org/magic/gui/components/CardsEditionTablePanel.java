package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.models.MagicCardTableModel;
import org.magic.gui.renderer.MagicEditionsJLabelRenderer;
import org.magic.gui.renderer.ManaCellRenderer;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.ThreadManager;
import org.magic.sorters.CardsEditionSorter;

import com.mysql.cj.x.protobuf.MysqlxCrud.Collection;


public class CardsEditionTablePanel extends JPanel {
	private JXTable table;
	private MagicCardTableModel model;
	private MagicEdition currentEdition;
	private AbstractBuzyIndicatorComponent buzy;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());

	
	public CardsEditionTablePanel() {
		setLayout(new BorderLayout(0, 0));
		
		JPanel haut = new JPanel();
		model = new MagicCardTableModel();
		table = new JXTable(model);
		buzy=AbstractBuzyIndicatorComponent.createProgressComponent();
		
		table.getColumnModel().getColumn(2).setCellRenderer(new ManaCellRenderer());
		table.getColumnModel().getColumn(6).setCellRenderer(new MagicEditionsJLabelRenderer());

		
		haut.add(buzy);
		add(new JScrollPane(table), BorderLayout.CENTER);
		add(haut,BorderLayout.NORTH);
		
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent componentEvent) {
				init(currentEdition);
			}

		});
	}
	
	public MagicCard getSelectedCard()
	{
		if(table.getSelectedRow()>-1)
			return (MagicCard) table.getValueAt(table.getSelectedRow(), 0);
		
		return null;
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
	
	private void refresh()
	{
		if(currentEdition==null)
			return;
		
		
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
						buzy.progress();
					}
					
				} catch (IOException e) {
					logger.error(e);
				}
				buzy.end();
				MTGControler.getInstance().getEnabled(MTGCardsProvider.class).removeObserver(buzy);
		}, "loading cards from " + currentEdition);
		
		
		
	}
	
	

}
