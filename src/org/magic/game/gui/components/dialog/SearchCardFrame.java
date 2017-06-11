package org.magic.game.gui.components.dialog;

import java.awt.Dimension;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JScrollPane;

import org.magic.api.beans.MagicCard;
import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.DraggablePanel;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.game.gui.components.HandPanel;
import org.magic.game.gui.components.LibraryPanel;
import org.magic.game.model.Player;
import org.magic.game.model.PositionEnum;
import org.magic.gui.MagicGUI;
import org.magic.services.MTGControler;

public class SearchCardFrame extends JDialog {

	HandPanel pane;
	JScrollPane scPane;
	
	DisplayableCard selectedCard;
	
	private void init(Player p,final PositionEnum source)
	{
		setSize(new Dimension(800, 600));
		scPane = new JScrollPane();
		pane=new HandPanel() {
			@Override
			public PositionEnum getOrigine() {
				return source;
			}
			
			@Override
			public void moveCard(DisplayableCard mc, PositionEnum to) {
				switch (source) {
				case LIBRARY:GamePanelGUI.getInstance().getPanelLibrary().moveCard(mc, to);break;
				case EXIL:GamePanelGUI.getInstance().getExilPanel().moveCard(mc, to);break;
				case GRAVEYARD:GamePanelGUI.getInstance().getPanelGrave().moveCard(mc, to);break;
				default:break;
				}
			}
			
			
		};
		pane.setPlayer(p);
		
		scPane.setViewportView(pane);
		getContentPane().add(scPane);
	}
	
	//used by SearchAction.
	public SearchCardFrame(Player p,PositionEnum source) {
		init(p,source);
		setTitle(p.getName() +"'s" + source.toString());
		pane.setThumbnailSize(MTGControler.getInstance().getCardsDimension());
		
		switch(source)
		{
			case GRAVEYARD:pane.initThumbnails(p.getGraveyard().getCards(),true);break;
			case LIBRARY:pane.initThumbnails(p.getLibrary().getCards(),true);break;
			case BATTLEFIELD:pane.initThumbnails(p.getBattlefield().getCards(),true);break;
			case EXIL:pane.initThumbnails(p.getExil().getCards(),true);break;
			default:break;
		}
	}
	
	//used by ScryActions.
	public SearchCardFrame(Player p,List<MagicCard> list,PositionEnum source) {
		init(p,source);
		pane.setThumbnailSize(MTGControler.getInstance().getCardsDimension());
		pane.initThumbnails(list,true);
	}
	
}
