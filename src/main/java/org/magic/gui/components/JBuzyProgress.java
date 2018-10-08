package org.magic.gui.components;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.utils.patterns.observer.Observable;
import org.utils.patterns.observer.Observer;

public class JBuzyProgress extends JPanel implements Observer{

	private JProgressBar progress;
	
	public JBuzyProgress() {
		progress = new JProgressBar();
		progress.setMinimum(0);
		progress.setStringPainted(true);
		setLayout(new BorderLayout());
		add(progress,BorderLayout.CENTER);
		
		setVisible(false);
	}
	
	
	public void progress()
	{
		progress.setValue(progress.getValue()+1);
	}
	
	
	@Override
	public void update(Observable o, Object obj) {
		if(obj instanceof Integer)
		{
			progress.setValue((Integer)obj);
		}
		else
		{
			setText(String.valueOf(obj));
			progress();
		}
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
	
	

}
