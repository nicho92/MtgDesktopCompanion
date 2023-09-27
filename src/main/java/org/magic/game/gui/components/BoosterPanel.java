package org.magic.game.gui.components;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JPanel;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.game.GameManager;
import org.magic.api.beans.game.ZoneEnum;
import org.magic.services.MTGControler;

public class BoosterPanel extends JPanel {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	int lastColumn = -1;
	private List<MagicCard> list;

	public void clear() {
		lastColumn = -1;
		removeAll();
		revalidate();
	}

	public BoosterPanel() {
		super();
		var flowLayout = new FlowLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		setLayout(flowLayout);
	}

	public void addComponent(DisplayableCard i, int column) {
		GraveyardPanel p;
		if (lastColumn < column) {
			p = new GraveyardPanel() {

				private static final long serialVersionUID = 1L;

				@Override
				public ZoneEnum getOrigine() {
					return ZoneEnum.BOOSTER;
				}

				@Override
				public void moveCard(DisplayableCard mc, ZoneEnum to) {

					if (to == ZoneEnum.DECK)
						list.remove(mc.getMagicCard());
				}
			};
			add(p);
			lastColumn = column;
		} else {
			p = getColumnAt(lastColumn);
		}
		p.setPlayer(GameManager.getInstance().getCurrentPlayer());
		p.setPreferredSize(new Dimension((int) MTGControler.getInstance().getCardsGameDimension().getWidth() + 5,
				(int) (MTGControler.getInstance().getCardsGameDimension().getHeight() * 30)));
		p.addComponent(i);
		p.postTreatment(i);

		revalidate();

	}

	@Override
	public String toString() {
		return "BoosterPanel";
	}

	public GraveyardPanel getColumnAt(int i) {
		return (GraveyardPanel) getComponent(i - 1);
	}

	public void setList(List<MagicCard> list) {
		this.list = list;
	}

}
