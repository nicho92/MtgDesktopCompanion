package org.magic.gui.game;

import java.awt.Dimension;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JScrollPane;

import org.magic.api.beans.MagicCard;
import org.magic.game.GameManager;

public class SearchLibraryFrame extends JDialog {

	LibraryPanel pane;
	JScrollPane scPane;
	
	DisplayableCard selectedCard;
	
	public SearchLibraryFrame() {
		setSize(new Dimension(800, 600));
		scPane = new JScrollPane();
		pane=new LibraryPanel();
		pane.setThumbnailSize(179, 240);
		scPane.setViewportView(pane);
		getContentPane().add(scPane);
		pane.initThumbnails(GameManager.getInstance().getPlayer().getLibrary());
		
	}
	
	public SearchLibraryFrame(List<MagicCard> list) {
		setSize(new Dimension(800, 600));
		scPane = new JScrollPane();
		pane=new LibraryPanel();
		pane.setThumbnailSize(179, 240);
		scPane.setViewportView(pane);
		getContentPane().add(scPane);
		pane.initThumbnails(list);
		
	}
	
	
}
