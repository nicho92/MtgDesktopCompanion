package org.magic.gui.components.tech;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class JVMemoryPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JProgressBar progressBar;
	private String tooltip;
	private Color defaultBack;
	private Color defaultFront;

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
		refresh();
	}

	private int toMB(double value) {
		return (int) (value / 1024 / 1024);
	}

	public void refresh() {
		progressBar.setValue(toMB((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())));
		tooltip = toMB((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())) + "/"+ toMB((Runtime.getRuntime().totalMemory())) + " MB";
		progressBar.setToolTipText(tooltip);

		double pc = ((progressBar.getValue() * 100) / progressBar.getMaximum());

		if (pc > 55)
			setColor(Color.ORANGE, Color.ORANGE);
		else if (pc > 75)
			setColor(Color.BLACK, Color.RED);
		else
			setColor(defaultFront, defaultBack);

	}
}
