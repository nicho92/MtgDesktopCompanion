package org.magic.gui.components.dialog;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;

import org.magic.services.MTGConstants;
import org.utils.patterns.observer.Observable;
import org.utils.patterns.observer.Observer;

public class MTGSplashScreen extends JWindow implements Observer{
	
	JProgressBar progressBar;
	
	public void start()
	{
		setVisible(true);
		progressBar.setValue(0);
	}
	
	public MTGSplashScreen() {
		JPanel panel = new JPanel();
		panel.setOpaque(false); 
		
		getContentPane().add(panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		JLabel lblIcons = new JLabel("");
		panel.add(lblIcons, BorderLayout.CENTER);
		lblIcons.setIcon(MTGConstants.ICON_SPLASHSCREEN);
		lblIcons.setOpaque(false);
		
		progressBar = new JProgressBar();
		panel.add(progressBar, BorderLayout.SOUTH);
		progressBar.setMinimum(0);
		progressBar.setIndeterminate(true);
		progressBar.setStringPainted(true);
		pack();
		setLocationRelativeTo(null);
	}

	public void stop() {
		setVisible(false);
		dispose();
	}

	@Override
	public void update(Observable o, Object msg) {
		progressBar.setString(String.valueOf(msg));
		
	}


}
