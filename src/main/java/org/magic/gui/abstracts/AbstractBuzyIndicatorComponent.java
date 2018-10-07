package org.magic.gui.abstracts;

import javax.swing.JComponent;

import org.utils.patterns.observer.Observable;
import org.utils.patterns.observer.Observer;

public abstract class AbstractBuzyIndicatorComponent extends JComponent implements Observer {

	protected int val=0;
	
	public abstract void setText(String s);
	public abstract void setValue(int i) ;
	public abstract void start(int max);
	public abstract void start();
	public abstract void end();
	public abstract void setString(String valueOf);
	public abstract int getValue();
	
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
}
