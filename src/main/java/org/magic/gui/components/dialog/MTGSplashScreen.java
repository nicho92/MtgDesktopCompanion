package org.magic.gui.components.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;

import org.apache.logging.log4j.core.LogEvent;
import org.magic.services.MTGConstants;
import org.utils.patterns.observer.Observable;
import org.utils.patterns.observer.Observer;

public class MTGSplashScreen extends JWindow implements Observer {

	private static final long serialVersionUID = 1L;
	private JProgressBar progressBar;

	
	public void start() {
		setVisible(true);
		progressBar.setValue(0);
	}

	public MTGSplashScreen() {
		JPanel panel = new JPanel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				if (g instanceof Graphics2D g2d) {
					final var R = 240;
					final var G = 240;
					final var B = 240;
					Paint p = new GradientPaint(0.0f, 0.0f, new Color(R, G, B, 0), 0.0f, getHeight(),
							new Color(R, G, B, 0), true);
					g2d.setPaint(p);
					g2d.fillRect(0, 0, getWidth(), getHeight());
				}
			}
		};
		setBackground(new Color(0, 0, 0, 0));

		getContentPane().add(panel);
		panel.setLayout(new BorderLayout(0, 0));

		var lblIcons = new JLabel(MTGConstants.ICON_SPLASHSCREEN);
		panel.add(lblIcons, BorderLayout.CENTER);
		lblIcons.setOpaque(false);

		progressBar = new JProgressBar();
		panel.add(progressBar, BorderLayout.SOUTH);
		progressBar.setMinimum(0);
		progressBar.setIndeterminate(true);
		progressBar.setStringPainted(true);
		progressBar.setForeground(new Color(101,13,136));
		pack();
		setLocationRelativeTo(null);
	}

	public void stop() {
		setVisible(false);
		dispose();
	}

	@Override
	public void update(Observable o, Object msg) {
		
		
		if(msg instanceof LogEvent le)
		{
			progressBar.setString(le.getMessage().getFormattedMessage());
		}
		else
		{
			progressBar.setString(String.valueOf(msg));	
		}
		
	}

}
