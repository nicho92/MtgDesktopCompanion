package org.magic.gui;

import java.awt.BorderLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;

public class LaunchWindows extends JWindow implements Observer{
	
	JProgressBar progressBar;
	
	public void start()
	{
		setVisible(true);
		progressBar.setValue(0);
	}
	
	public LaunchWindows() {
		
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		progressBar = new JProgressBar();
		progressBar.setMinimum(0);
		progressBar.setIndeterminate(true);
		progressBar.setStringPainted(true);
		
		getContentPane().add(progressBar, BorderLayout.SOUTH);
		//setOpacity(0.55f);
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon(LaunchWindows.class.getResource("/data/magic-logo.png")));
		lblNewLabel.setOpaque(false);
		getContentPane().add(lblNewLabel, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(null);
	}

//	public void update(Object msg) {
//		progressBar.setString(msg.toString());
//	}

	public void stop() {
		setVisible(false);
		dispose();
	}

	@Override
	public void update(Observable o, Object msg) {
		progressBar.setString(String.valueOf(msg));
		
	}


}
