package org.magic.game.gui.components;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JMenuItem;

import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.game.actions.cards.ScryActions;
import org.magic.game.actions.library.DrawActions;
import org.magic.game.actions.library.DrawHandActions;
import org.magic.game.actions.library.MoveGraveyardActions;
import org.magic.game.actions.library.ShuffleActions;
import org.magic.game.actions.player.SearchActions;
import org.magic.game.model.ZoneEnum;
import org.magic.services.MTGControler;

public class LibraryPanel extends DraggablePanel {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private transient Image i;

	public LibraryPanel() {
		super();
		menu.add(new JMenuItem(new DrawHandActions()));
		menu.add(new JMenuItem(new DrawActions()));
		menu.add(new JMenuItem(new SearchActions(getOrigine())));
		menu.add(new JMenuItem(new ScryActions(null)));
		menu.add(new JMenuItem(new ShuffleActions()));
		menu.add(new JMenuItem(new MoveGraveyardActions()));

		try {
			i = getEnabledPlugin(MTGPictureProvider.class).getBackPicture(null).getScaledInstance(
					(int) MTGControler.getInstance().getCardsGameDimension().getWidth(),
					(int) MTGControler.getInstance().getCardsGameDimension().getHeight(), Image.SCALE_SMOOTH);
			setPreferredSize(new Dimension(i.getWidth(null), i.getHeight(null)));

		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Override
	public void addComponent(DisplayableCard i) {
		add(i);
		i.setPosition(getOrigine());
	}

	@Override
	public void moveCard(DisplayableCard mc, ZoneEnum to) {

		switch (to) {
		case BATTLEFIELD:
			player.playCardFromLibrary(mc.getMagicCard());
			break;
		case EXIL:
			player.exileCardFromLibrary(mc.getMagicCard());
			break;
		case HAND:
			player.searchCardFromLibrary(mc.getMagicCard());
			break;
		case LIBRARY:
			player.reoderCardInLibrary(mc.getMagicCard(), true);
			break;
		case GRAVEYARD:
			player.discardCardFromLibrary(mc.getMagicCard());
			break;
		default:
			break;
		}

	}

	@Override
	public ZoneEnum getOrigine() {
		return ZoneEnum.LIBRARY;
	}

	@Override
	public void paint(Graphics g) {

		try {
			g.drawImage(i, 0, 0, null);
		} catch (Exception e) {
			// do nothing
		}

	}

	@Override
	public void postTreatment(DisplayableCard c) {
		remove(c);

	}
}
