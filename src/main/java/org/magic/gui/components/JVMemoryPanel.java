package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class JVMemoryPanel extends JPanel
{
	private JProgressBar progressBar;
	private Timer t;
	private int delay=1000;
	private String tooltip;
	private boolean started=true;
	
	
	public JVMemoryPanel() {
		setLayout(new BorderLayout(0, 0));
		progressBar = new JProgressBar();
		progressBar.setMinimum(0);
		progressBar.setMaximum(toMB(Runtime.getRuntime().totalMemory()));
		progressBar.setStringPainted(true);
		progressBar.setToolTipText(tooltip);
		add(progressBar);
		
		{

			t = new Timer();
			t.scheduleAtFixedRate(new TimerTask() {
				public void run() {
					if(started)
					{ 
					progressBar.setValue(toMB((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())));
					tooltip=toMB((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()))+"/"+toMB((Runtime.getRuntime().totalMemory()))+" MB";
					progressBar.setToolTipText(tooltip);
					}
				}
			},0,delay);
		}
		
	}

	@Override
	public void paintComponents(Graphics arg0) {
		super.paintComponents(arg0);
		
		if(isVisible())
			started=true;
		else
			started=false;
		
		
	}
	
	private int toMB(double value)
	{
		return (int)(value/1024/1024);
	}
}
