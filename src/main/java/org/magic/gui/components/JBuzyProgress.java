package org.magic.gui.components;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JProgressBar;

import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;

public class JBuzyProgress extends AbstractBuzyIndicatorComponent {

	private JProgressBar progress;
	
	public JBuzyProgress() {
		super();
		progress = new JProgressBar();
		progress.setMinimum(0);
		progress.setStringPainted(true);
		add(progress,BorderLayout.CENTER);
		
	}
	
	@Override
	public JComponent getBuzyComponent() {
		return progress;
	}
	
	@Override
	public void progress()
	{
		setValue(progress.getValue()+1);
	}
	

	
	public void setText(String s)
	{
		if(progress.isIndeterminate())
			progress.setString(s);
	}


	public void start(int max) {
			setText(null);
			progress.setValue(0);
			progress.setMaximum(max);
			progress.setIndeterminate(false);
			setVisible(true);
	}
	
	public void start() {
		progress.setValue(0);
		progress.setIndeterminate(true);
		setVisible(true);
		setText(null);
	}


	public void end() {
		progress.setValue(0);
		setVisible(false);
		
	}

	@Override
	public void setValue(int i) {
		progress.setValue(i);
		
	}

	@Override
	public int getValue() {
		return progress.getValue();
	}


}
