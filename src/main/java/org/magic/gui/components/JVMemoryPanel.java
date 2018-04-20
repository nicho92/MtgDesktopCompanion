package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class JVMemoryPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JProgressBar progressBar;
	private transient Timer t;
	private int delay = 1000;
	private String tooltip;
	private transient TimerTask task;
	private Color defaultBack;
	private Color defaultFront;

	public void setDelayInMillisecond(int delay) {
		this.delay = delay;
		stop();
		start();
	}

	public void stop() {
		t.cancel();
	}

	public void start() {
		t.scheduleAtFixedRate(task, 0, delay);
	}

	public void setColor(Color foreground, Color background) {
		progressBar.setForeground(foreground);
		progressBar.setBackground(background);
	}

	public JVMemoryPanel() {
		setLayout(new BorderLayout(0, 0));
		progressBar = new JProgressBar();
		progressBar.setMinimum(0);
		progressBar.setMaximum(toMB(Runtime.getRuntime().totalMemory()));
		progressBar.setStringPainted(true);
		progressBar.setToolTipText(tooltip);
		add(progressBar);
		defaultBack = progressBar.getBackground();
		defaultFront = progressBar.getForeground();

		t = new Timer();
		task = new TimerTask() {
			public void run() {
				progressBar.setValue(toMB((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())));
				tooltip = toMB((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())) + "/"
						+ toMB((Runtime.getRuntime().totalMemory())) + " MB";
				progressBar.setToolTipText(tooltip);

				double pc = ((progressBar.getValue() * 100) / progressBar.getMaximum());

				if (pc > 55)
					setColor(Color.ORANGE, Color.ORANGE);
				else if (pc > 75)
					setColor(Color.BLACK, Color.RED);
				else
					setColor(defaultFront, defaultBack);

			}
		};

		start();

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent arg0) {
				start();
			}

			@Override
			public void componentHidden(ComponentEvent arg0) {
				stop();
			}

		});

	}

	private int toMB(double value) {
		return (int) (value / 1024 / 1024);
	}
}
