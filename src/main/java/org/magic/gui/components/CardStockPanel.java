package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicCollection;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.components.editor.CardStockLinePanel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

public class CardStockPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel content;
	private GridLayout layout = new GridLayout();
	private transient Logger logger = MTGLogger.getLogger(this.getClass());

	private JButton btnAdd;
	private MagicCollection selectedCol;
	private MagicCard selectedCard;

	public void enabledAdd(boolean b) {
		btnAdd.setEnabled(b);
	}

	public CardStockPanel() {
		setLayout(new BorderLayout(0, 0));
		layout = new GridLayout(1, 1, 0, 0);

		content = new JPanel();
		add(content, BorderLayout.CENTER);
		content.setLayout(layout);
		JPanel panneauHaut = new JPanel();
		add(panneauHaut, BorderLayout.NORTH);

		btnAdd = new JButton("");
		btnAdd.setEnabled(false);
		btnAdd.setIcon(MTGConstants.ICON_NEW);
		btnAdd.addActionListener(ae -> {
			layout.setRows(layout.getRows() + 1);
			content.add(new CardStockLinePanel(selectedCard, selectedCol));
			content.revalidate();
		});
		panneauHaut.add(btnAdd);
	}

	public void initMagicCardStock(MagicCard mc, MagicCollection col) {
		selectedCard = mc;
		selectedCol = col;
		content.removeAll();
		content.setLayout(layout);
		try {
			for (MagicCardStock stat : MTGControler.getInstance().getEnabled(MTGDao.class).listStocks(mc, col,true)) {
				CardStockLinePanel pane = new CardStockLinePanel(mc, col);
				pane.setMagicCardState(stat);
				content.add(pane);
			}
			content.revalidate();
			content.repaint();
		} catch (Exception e) {
			logger.error(e);
		}

	}

}
