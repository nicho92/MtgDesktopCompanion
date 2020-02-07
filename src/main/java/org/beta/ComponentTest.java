package org.beta;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

import javax.swing.JFrame;

import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.SealedStockGUI;
import org.magic.services.MTGControler;

public class ComponentTest {

	public static void main(String[] args) throws SQLException {
		
		MTGControler.getInstance().getEnabled(MTGCardsProvider.class).init();
		MTGControler.getInstance().getEnabled(MTGDao.class).init();
		
		JFrame f = new JFrame();
		f.setSize(new Dimension(640, 480));
		SealedStockGUI p = new SealedStockGUI();
		p.onFirstShowing();
		f.getContentPane().add(p);
		f.setVisible(true);
		
		f.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				MTGControler.getInstance().closeApp();
				
			}
		});
			
	}

}
