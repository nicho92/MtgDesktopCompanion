package org.magic.game.gui.components;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.magic.game.model.Player;
import org.magic.game.transfert.CardTransfertHandler;
import org.magic.services.MTGLogger;

public abstract class DraggablePanel extends JPanel implements Draggable {

	Dimension d;

	protected JPopupMenu menu = new JPopupMenu();
	private transient Logger logger = MTGLogger.getLogger(this.getClass());

	boolean dragging = true;
	protected Player player;

	public void executeDragging(DisplayableCard card, MouseEvent e) {

	}

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

	public DraggablePanel() {
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

}
