package org.magic.game.gui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JMenuItem;
import javax.swing.border.LineBorder;

import org.magic.api.beans.game.ZoneEnum;
import org.magic.game.actions.player.SearchActions;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;

public class ExilPanel extends DraggablePanel {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public ExilPanel() {
		super();
		setLayout(null);
		setBorder(new LineBorder(Color.BLACK));
		setPreferredSize(new Dimension(170, 95));
		menu.removeAll();
		menu.add(new JMenuItem(new SearchActions(getOrigine())));
	}

	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(MTGConstants.ICON_GAME_EXILE,0,0,null);
		if (GamePanelGUI.getInstance().getPlayer() != null) {
			g.setFont(MTGControler.getInstance().getFont().deriveFont(Font.BOLD, 12));
			g.drawString(GamePanelGUI.getInstance().getPlayer().getExil().size() + " exiled cards", 15, 15);
		}
		revalidate();
		repaint();
	}

	@Override
	public ZoneEnum getOrigine() {
		return ZoneEnum.EXIL;
	}

	@Override
	public void addComponent(DisplayableCard i) {
		i.setPosition(getOrigine());
		add(i);
	}

	@Override
	public void moveCard(DisplayableCard mc, ZoneEnum to) {
		switch (to) {
		case BATTLEFIELD:
			player.playCardFromExile(mc.getMagicCard());
			break;
		case HAND:
			player.returnCardFromExile(mc.getMagicCard());
			break;
		case GRAVEYARD:
			player.discardCardFromExile(mc.getMagicCard());
			break;
		case LIBRARY:
			player.putCardInLibraryFromExile(mc.getMagicCard(), true);
			break;
		default:
			break;
		}
	}

	@Override
	public void postTreatment(DisplayableCard c) {
		remove(c);
		revalidate();
		repaint();

	}

}
