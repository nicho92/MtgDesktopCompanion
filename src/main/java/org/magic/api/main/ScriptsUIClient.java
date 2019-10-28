package org.magic.api.main;

import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.components.ScriptPanel;
import org.magic.services.MTGControler;
import org.magic.services.threads.ThreadManager;

public class ScriptsUIClient {

	public static void main(String[] args) throws SQLException {
		
		MTGControler.getInstance().getEnabled(MTGCardsProvider.class).init();
		MTGControler.getInstance().getEnabled(MTGDao.class).init();
		
		ThreadManager.getInstance().invokeLater(() -> {
			ScriptPanel p = new ScriptPanel();
			
			
			JFrame f = new JFrame(p.getTitle());
			f.setIconImage(p.getIcon().getImage());
			f.getContentPane().add(p);
			f.setVisible(true);
			f.pack();
			f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		});
		
		
		
	}
	
}
