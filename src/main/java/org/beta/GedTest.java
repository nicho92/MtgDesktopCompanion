package org.beta;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.magic.gui.components.GedPanel;
import org.magic.services.MTGControler;

public class GedTest {

	public static void main(String[] args) {
		
		JFrame f = new JFrame();
		f.setSize(new Dimension(640, 480));
		GedPanel p = new GedPanel<>();
		p.onFirstShowing();
		f.getContentPane().add(p);
		
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		f.setVisible(true);
		
		f.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				MTGControler.getInstance().closeApp();
				
			}
		});
			
	}

}
