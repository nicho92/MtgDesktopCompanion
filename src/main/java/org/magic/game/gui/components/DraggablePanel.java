package org.magic.game.gui.components;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPopupMenu;

import org.apache.commons.beanutils.BeanUtils;
import org.magic.api.beans.game.Player;
import org.magic.game.transfert.CardTransfertHandler;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.MTGControler;

public abstract class DraggablePanel extends MTGUIComponent implements Draggable
{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	Dimension d;

	protected JPopupMenu menu = new JPopupMenu();

	boolean dragging = true;
	protected Player player;


	public boolean isDragging() {
		return dragging;
	}

	public void enableDragging(boolean dragging) {
		this.dragging = dragging;
	}

	public void setThumbnailSize(Dimension d) {
		this.d = d;
	}

	public int getCardWidth() {
		return (int) d.getWidth();
	}

	public int getCardHeight() {
		return (int) d.getHeight();
	}

	public Dimension getCardsDimension()
	{
		if (d == null)
			d = MTGControler.getInstance().getCardsGameDimension();

		return d;
	}


	protected DraggablePanel() {
		super();
		setTransferHandler(new CardTransfertHandler());
		setComponentPopupMenu(menu);
	}

	public List<DisplayableCard> lookupCardBy(String prop, String value) {
		List<DisplayableCard> ret = new ArrayList<>();

		for (Component c : getComponents()) {
			try {
				DisplayableCard card = (DisplayableCard) c;
				if (BeanUtils.describe(card.getMagicCard()).get(prop).equalsIgnoreCase(value))
					ret.add(card);
			} catch (Exception e) {
				logger.error("error lookup", e);
			}
		}
		return ret;
	}

	@Override
	public void updatePanel() {
		revalidate();
		repaint();

	}

	public void setPlayer(Player p) {
		this.player = p;
	}

	public Player getPlayer() {
		return player;
	}

	@Override
	public String getTitle() {
		return "Draggable Panel : " + getOrigine() ;
	}

}
