package org.magic.gui.components.widgets;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.services.MTGConstants;

public class JBuzyLabel extends AbstractBuzyIndicatorComponent {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private JLabel label;
	private int max;
	private int currentVal;


	@Override
	public JComponent getBuzyComponent() {
		return label;
	}

	public JBuzyLabel() {
		super();
		label = new JLabel(MTGConstants.ICON_LOADING);
		label.setHorizontalAlignment(SwingConstants.LEFT);
		add(label,BorderLayout.CENTER);

		setValue(0);
		max=-1;

	}

	@Override
	public void setText(String s) {
		label.setText(s);
	}

	@Override
	public void setValue(int i) {
		currentVal=i;
		if(max>-1)
			setText(currentVal+"/"+max);
	}

	@Override
	public void start(int max) {
		this.max=max;
		setValue(0);
		setVisible(true);
		setText(null);
	}

	@Override
	public void start() {
		setVisible(true);
		setText(null);
		setValue(0);
		max=-1;
	}

	@Override
	public void end() {
		setVisible(false);
		setText(null);
		setValue(-1);
		max=-1;
	}

	@Override
	public int getValue() {

		if(currentVal>=max)
			return max;

		return currentVal;
	}
}
