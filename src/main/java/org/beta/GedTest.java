package org.beta;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.magic.gui.components.GedPanel;

public class GedTest {

	public static void main(String[] args) {
		
		JFrame f = new JFrame();
		f.setSize(new Dimension(640, 480));
		f.getContentPane().add(new GedPanel<>());
		
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		f.setVisible(true);
		
		
	}

}
