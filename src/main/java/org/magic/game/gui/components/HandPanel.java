package org.magic.game.gui.components;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.border.LineBorder;

import org.magic.api.beans.MagicCard;
import org.magic.game.model.ZoneEnum;

public class HandPanel extends DraggablePanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GridBagConstraints c;
	private int index = 0;
	private int val = 7;
	private transient Thread t;
	private ZoneEnum origine = ZoneEnum.HAND;

	@Override
	public void moveCard(DisplayableCard mc, ZoneEnum to) {

		switch (to) {
		case BATTLEFIELD:
			player.playCard(mc.getMagicCard());
			break;
		case EXIL:
			player.exileCardFromHand(mc.getMagicCard());
			break;
		case GRAVEYARD:
			player.discardCardFromHand(mc.getMagicCard());
			break;
		case LIBRARY:
			player.putCardInLibraryFromHand(mc.getMagicCard(), true);
			break;
		default:
			break;
		}
	}

	public void setRupture(int val) {
		this.val = val;
	}

	public HandPanel() {
		super();
		setLayout(new GridBagLayout());
		c = new GridBagConstraints();
	}

	public void addComponent(DisplayableCard i) {
		if (index >= val) {
			c.gridy = c.gridy + 1;
			c.gridx = 0;
			index = 0;
		}
		c.gridx = c.gridx + 1;
		i.setHorizontalTextPosition(JLabel.CENTER);
		i.setVerticalTextPosition(JLabel.BOTTOM);
		i.enableDrag(dragging);

		if (i.isTapped())
			i.tap(false);

		add(i, c);
		index++;
		i.setPosition(getOrigine());
	}

	public void initThumbnails(final List<MagicCard> cards, final boolean activateCards, final boolean rightClick) {

		if (t != null && t.isAlive())
			t.interrupt();

		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(2, 2, 2, 2);
		c.anchor = GridBagConstraints.NORTHWEST;

		this.removeAll();
		index = 0;

		t = new Thread(() -> {

			for (MagicCard mc : cards) {
				

				DisplayableCard lab = new DisplayableCard(mc, getCardsDimension(), activateCards, rightClick);
				lab.setTappable(activateCards);

				try {
				
					addComponent(lab);
					revalidate();
				} catch (Exception e) {
					lab.setText(mc.getName());
					lab.setBorder(new LineBorder(Color.BLACK));
				}

			}
		});

		t.start();
	}

	@Override
	public ZoneEnum getOrigine() {
		return origine;
	}

	public void setOrigine(ZoneEnum or) {
		origine = or;

	}

	@Override
	public void postTreatment(DisplayableCard c) {
		// do nothing

	}

	public void setMaxCardsRow(int i) {
		val = i;

	}

	@Override
	public String toString() {
		return "ThumbnailPanel";
	}

}
