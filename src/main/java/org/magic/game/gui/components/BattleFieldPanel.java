package org.magic.game.gui.components;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.magic.game.actions.battlefield.ChangeBackGroundActions;
import org.magic.game.actions.battlefield.FlipaCoinActions;
import org.magic.game.actions.battlefield.RollAD20Actions;
import org.magic.game.actions.battlefield.SelectedTapActions;
import org.magic.game.actions.battlefield.UnselectAllAction;
import org.magic.game.model.CardSpell;
import org.magic.game.model.GameManager;
import org.magic.game.model.ZoneEnum;
import org.magic.game.model.factories.AbilitiesFactory;
import org.magic.services.MTGControler;
import org.magic.services.tools.ImageTools;

public class BattleFieldPanel extends DraggablePanel {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	JPopupMenu battlefieldMenu = new JPopupMenu();
	private transient BufferedImage image;

	public List<DisplayableCard> getCards() {
		List<DisplayableCard> selected = new ArrayList<>();
		for (Component c : getComponents()) {
			DisplayableCard card = (DisplayableCard) c;
			selected.add(card);
		}
		return selected;
	}

	public List<DisplayableCard> getSelectedCards() {
		List<DisplayableCard> selected = new ArrayList<>();
		for (DisplayableCard card : getCards()) {
			if (card.isSelected())
				selected.add(card);
		}

		return selected;

	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (image != null) {
			g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null);
		}
	}

	public BattleFieldPanel() {

		super();
		setLayout(null);

		if (MTGControler.getInstance().get("/game/player-profil/background") != null)
			try {
				BufferedImage im = ImageTools.read(new File(MTGControler.getInstance().get("/game/player-profil/background")));
				setBackgroundPicture(im);
			} catch (IOException e1) {
				//do nothing
			}

		battlefieldMenu.removeAll();
		battlefieldMenu.add(new JMenuItem(new UnselectAllAction()));
		battlefieldMenu.add(new JMenuItem(new SelectedTapActions()));
		battlefieldMenu.add(new JMenuItem(new FlipaCoinActions()));
		battlefieldMenu.add(new JMenuItem(new RollAD20Actions()));
		battlefieldMenu.add(new JMenuItem(new ChangeBackGroundActions()));

		setComponentPopupMenu(battlefieldMenu);
	}

	@Override
	public void addComponent(DisplayableCard c) {
		this.add(c);
		c.setPosition(getOrigine());
		c.initActions();
		GameManager.getInstance().getStack().put(new CardSpell(c));
		AbilitiesFactory.getInstance().getTriggeredAbility(c.getMagicCard()).forEach(ta->GameManager.getInstance().getStack().put(ta));
	}

	@Override
	public ZoneEnum getOrigine() {
		return ZoneEnum.BATTLEFIELD;
	}

	@Override
	public void moveCard(DisplayableCard mc, ZoneEnum to) {
		switch (to) {
		case GRAVEYARD:
			player.discardCardFromBattleField(mc.getMagicCard());
			break;
		case EXIL:
			player.exileCardFromBattleField(mc.getMagicCard());
			break;
		case HAND:
			player.returnCardFromBattleField(mc.getMagicCard());
			break;
		case LIBRARY:
			player.putCardInLibraryFromBattlefield(mc.getMagicCard(), true);
			break;
		default:
			break;
		}

	}

	@Override
	public void postTreatment(DisplayableCard c) {
		setComponentZOrder(c, 0);
	}

	public void setBackgroundPicture(BufferedImage im) {
		this.image = im;

	}

}
