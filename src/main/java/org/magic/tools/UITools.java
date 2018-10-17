package org.magic.tools;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.border.LineBorder;

import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.gui.components.MagicCardDetailPanel;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

import net.coderazzi.filters.gui.AutoChoices;
import net.coderazzi.filters.gui.TableFilterHeader;

public class UITools {

	private UITools() {}
	protected static Logger logger = MTGLogger.getLogger(UITools.class);
	
	
	public static String formatDouble(Double f)
	{
		return new DecimalFormat("#0.##").format(f);
	}
	
	
	public static void initTableFilter(JTable table)
	{
		TableFilterHeader filterHeader = new TableFilterHeader(table, AutoChoices.ENABLED);
		filterHeader.setSelectionBackground(Color.LIGHT_GRAY);
	}
	
	public static void initToolTip(final JTable table, final Integer cardPos, final Integer edPos) {
		MagicCardDetailPanel pane = new MagicCardDetailPanel();
		pane.enableThumbnail(true);
		final JPopupMenu popUp = new JPopupMenu();

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = table.rowAtPoint(e.getPoint());

				if (row > -1) {
					table.setRowSelectionInterval(row, row);
					String cardName = table.getValueAt(row, cardPos.intValue()).toString();

					if (cardName.indexOf('(') >= 0)
						cardName = cardName.substring(0, cardName.indexOf('(')).trim();

					MagicEdition ed = null;
					if (edPos != null) {
						String edID = table.getValueAt(row, edPos).toString();
						ed = new MagicEdition();
						ed.setId(edID);
					}

					try {
						MagicCard mc = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).searchCardByName( cardName, ed, true).get(0);
						pane.setMagicCard(mc);
						popUp.setBorder(new LineBorder(Color.black));
						popUp.setVisible(false);
						popUp.removeAll();
						popUp.setLayout(new BorderLayout());
						popUp.add(pane, BorderLayout.CENTER);
						popUp.show(table, e.getX(), e.getY());
						popUp.setVisible(true);

					} catch (Exception ex) {
						logger.error("Error on " + cardName, ex);
					}

				}
			}
		});
	}

	public static <T>  List<T> getSelects(JTable tableCards,int columnID) {
		int[] viewRow = tableCards.getSelectedRows();
		List<T> listCards = new ArrayList<>();
		for (int i : viewRow) {
			int modelRow = tableCards.convertRowIndexToModel(i);
			T mc = (T) tableCards.getModel().getValueAt(modelRow, columnID);
			listCards.add(mc);
		}
		return listCards;
	}
	
}
