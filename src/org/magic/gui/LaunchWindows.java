package org.magic.gui;

import javax.swing.JWindow;

import org.magic.services.MTGControler;

import java.awt.BorderLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JProgressBar;
import javax.swing.JLabel;
import javax.swing.ImageIcon;

public class LaunchWindows extends JWindow implements Observer{
	
	JProgressBar progressBar;
	
	
	public LaunchWindows() {
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		progressBar = new JProgressBar();
		progressBar.setMinimum(0);
		progressBar.setStringPainted(true);
		getContentPane().add(progressBar, BorderLayout.SOUTH);
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon("D:\\programmation\\GIT\\MtgDesktopCompanion\\magic-logo.png"));
		getContentPane().add(lblNewLabel, BorderLayout.CENTER);
		pack();
	}

	@Override
	public void update(Observable o, Object arg) {

		progressBar.setString(arg.toString());
	}

}
