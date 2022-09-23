package org.magic.game.gui.components.dialog;

import java.awt.Dimension;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JScrollPane;

import org.magic.api.beans.MagicCard;
import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.game.gui.components.HandPanel;
import org.magic.game.model.Player;
import org.magic.game.model.ZoneEnum;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;

public class SearchCardFrame extends JDialog {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	HandPanel pane;
	DisplayableCard selectedCard;

	private void init(Player p, final ZoneEnum source) {
		setSize(new Dimension(800, 600));
		setIconImage(MTGConstants.ICON_SEARCH.getImage());
		pane = new HandPanel() {
			private static final long serialVersionUID = 1L;
			@Override
			public ZoneEnum getOrigine() {
				return source;
			}

			@Override
			public void moveCard(DisplayableCard mc, ZoneEnum to) {
				switch (source) {
				case LIBRARY:
					GamePanelGUI.getInstance().getPanelLibrary().moveCard(mc, to);
					break;
				case EXIL:
					GamePanelGUI.getInstance().getExilPanel().moveCard(mc, to);
					break;
				case GRAVEYARD:
					GamePanelGUI.getInstance().getPanelGrave().moveCard(mc, to);
					break;
				default:
					break;
				}
			}

		};
		pane.setPlayer(p);
		getContentPane().add(new JScrollPane(pane));
	}

	// used by SearchAction.
	public SearchCardFrame(Player p, ZoneEnum source) {
		init(p, source);
		setTitle(p.getName() + "'s" + source.toString());
		pane.setThumbnailSize(MTGControler.getInstance().getCardsGameDimension());

		switch (source) {
		case GRAVEYARD:
			pane.initThumbnails(p.getGraveyard().getCards(), true, true);
			break;
		case LIBRARY:
			pane.initThumbnails(p.getLibrary().getCards(), true, true);
			break;
		case BATTLEFIELD:
			pane.initThumbnails(p.getBattlefield().getCards(), true, true);
			break;
		case EXIL:
			pane.initThumbnails(p.getExil().getCards(), true, true);
			break;
		default:
			break;
		}
	}

	// used by ScryActions.
	public SearchCardFrame(Player p, List<MagicCard> list, ZoneEnum source) {
		init(p, source);
		pane.setThumbnailSize(MTGControler.getInstance().getCardsGameDimension());
		pane.initThumbnails(list, true, true);
	}

}
