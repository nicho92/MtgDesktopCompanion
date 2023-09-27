package org.magic.game.gui.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JMenuItem;
import javax.swing.border.LineBorder;

import org.magic.api.beans.game.ZoneEnum;
import org.magic.game.actions.player.SearchActions;

public class ExilPanel extends DraggablePanel {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public ExilPanel() {
		super();
		setLayout(null);
		setBorder(new LineBorder(Color.BLACK));
		setBackground(Color.GRAY);

		menu.removeAll();
		menu.add(new JMenuItem(new SearchActions(getOrigine())));
	}

	@Override
	public void paintComponent(Graphics g) {
		if (GamePanelGUI.getInstance().getPlayer() != null) {
			g.setFont(new Font("default", Font.BOLD, 12));
			g.setColor(Color.BLACK);
			g.drawString(GamePanelGUI.getInstance().getPlayer().getExil().size() + " exiled cards", 15, 15);
			revalidate();
		}
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
