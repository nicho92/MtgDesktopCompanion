package org.magic.gui.game;

import java.awt.Dimension;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JScrollPane;

import org.magic.api.beans.MagicCard;
import org.magic.game.Player;
import org.magic.game.PositionEnum;

public class SearchLibraryFrame extends JDialog {

	ThumbnailPanel pane;
	JScrollPane scPane;
	
	DisplayableCard selectedCard;
	
	public SearchLibraryFrame(Player p) {
		setSize(new Dimension(800, 600));
		scPane = new JScrollPane();
		pane=new ThumbnailPanel();
		pane.setOrigine(PositionEnum.LIBRARY);
		pane.setThumbnailSize(179, 240);
		scPane.setViewportView(pane);
		getContentPane().add(scPane);
		pane.setPlayer(p);
		pane.initThumbnails(p.getLibrary());
	}
	
	public SearchLibraryFrame(Player p,List<MagicCard> list) {
		setSize(new Dimension(800, 600));
		scPane = new JScrollPane();
		pane=new ThumbnailPanel();
		pane.setOrigine(PositionEnum.LIBRARY);
		pane.setThumbnailSize(179, 240);
		scPane.setViewportView(pane);
		getContentPane().add(scPane);
		pane.setPlayer(p);
		pane.initThumbnails(list);
		
	}
	
}
