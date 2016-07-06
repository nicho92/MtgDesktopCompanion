package org.magic.gui.game;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.magic.game.GameManager;

public class SearchLibraryFrame extends JFrame {

	ThumbnailPanel pane;
	JScrollPane scPane;
	
	DisplayableCard selectedCard;
	
	public SearchLibraryFrame() {
		scPane = new JScrollPane();
		pane=new ThumbnailPanel();
		pane.setThumbnailSize(179, 240);
		scPane.setViewportView(pane);
		getContentPane().add(scPane);
		pane.initThumbnails(GameManager.getInstance().getPlayer().getLibrary());
		
		pack();
		
	}
	
}
