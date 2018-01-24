package org.magic.gui.components;

import java.awt.BorderLayout;
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
	
	public JVMemoryPanel() {
		setLayout(new BorderLayout(0, 0));
		progressBar = new JProgressBar();
		progressBar.setMinimum(0);
		progressBar.setMaximum(toMB(Runtime.getRuntime().totalMemory()));
		progressBar.setStringPainted(true);
		progressBar.setToolTipText(tooltip);
		add(progressBar);

		t = new Timer();
		t.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				progressBar.setValue(toMB((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())));
				tooltip=toMB((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()))+"/"+toMB((Runtime.getRuntime().totalMemory()))+"MB";
			}
		},0,delay);
		
	}
	
	private int toMB(double value)
	{
		return (int)(value/1024/1024);
	}
}
