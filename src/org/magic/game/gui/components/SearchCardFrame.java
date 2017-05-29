package org.magic.game.gui.components;

import java.awt.Dimension;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JScrollPane;

import org.magic.api.beans.MagicCard;
import org.magic.game.model.Player;
import org.magic.game.model.PositionEnum;

public class SearchCardFrame extends JDialog {

	ThumbnailPanel pane;
	JScrollPane scPane;
	
	DisplayableCard selectedCard;
	
	private void init(Player p,PositionEnum source)
	{
		setSize(new Dimension(800, 600));
		scPane = new JScrollPane();
		pane=new ThumbnailPanel();
		pane.setPlayer(p);
		pane.setOrigine(source);
		scPane.setViewportView(pane);
		getContentPane().add(scPane);
	}
	
	//used by SearchAction.
	public SearchCardFrame(Player p,PositionEnum source) {
		init(p,source);
		pane.setThumbnailSize(GamePanelGUI.CARD_WIDTH, GamePanelGUI.CARD_HEIGHT);
		switch(source)
		{
			case GRAVEYARD:pane.initThumbnails(p.getGraveyard().getCards(),true);break;
			case LIBRARY:pane.initThumbnails(p.getLibrary().getCards(),true);break;
			case BATTLEFIELD:pane.initThumbnails(p.getBattlefield().getCards(),true);break;
			case EXIL:pane.initThumbnails(p.getExil(),true);break;
			default:break;
		}
	}
	
	//used by ScryActions.
	public SearchCardFrame(Player p,List<MagicCard> list,PositionEnum source) {
		init(p,source);
		pane.setThumbnailSize(179, 240);
		pane.initThumbnails(list,true);
	}
	
}
