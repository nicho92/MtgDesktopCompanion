package org.magic.gui.abstracts;

import java.awt.BorderLayout;

import javax.swing.JComponent;

import org.magic.gui.components.JBuzyLabel;
import org.magic.gui.components.JBuzyProgress;
import org.utils.patterns.observer.Observable;
import org.utils.patterns.observer.Observer;

public abstract class AbstractBuzyIndicatorComponent extends JComponent implements Observer {

	public abstract void setText(String s);
	public abstract void setValue(int i) ;
	public abstract void start(int max);
	public abstract void start();
	public abstract void end();
	public abstract int getValue();
	public abstract JComponent getBuzyComponent();
	
	public static AbstractBuzyIndicatorComponent createLabelComponent()
	{
		return new JBuzyLabel();
	}
	
	public static AbstractBuzyIndicatorComponent createProgressComponent()
	{
		return new JBuzyProgress();
	}
	
	
	public AbstractBuzyIndicatorComponent() {
		setVisible(false);
		setLayout(new BorderLayout());
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
			setText(String.valueOf(obj));
			progress();
		}
	}
}
