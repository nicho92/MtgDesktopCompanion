package org.magic.gui.components;

import javax.swing.JProgressBar;

import org.utils.patterns.observer.Observable;
import org.utils.patterns.observer.Observer;

public class JBuzyProgress extends JProgressBar implements Observer{

	
	public JBuzyProgress() {
		setMinimum(0);
		setStringPainted(true);
		setVisible(false);
	}
	
	
	public void progress()
	{
		setValue(getValue()+1);
	}
	
	
	@Override
	public void update(Observable o, Object obj) {
		if(obj instanceof Integer)
		{
			setValue((Integer)obj);
		}
		else
		{
			setString(String.valueOf(obj));
			progress();
		}
	}
	
	public void setText(String s)
	{
		setString(s);
	}


	public void start(int max) {
		setValue(0);
		setMaximum(max);
		setIndeterminate(false);
		setVisible(true);
	}
	
	public void start() {
		setValue(0);
		setIndeterminate(true);
		setVisible(true);
	}


	public void end() {
		setValue(0);
		setVisible(false);
		
	}
	
	

}
