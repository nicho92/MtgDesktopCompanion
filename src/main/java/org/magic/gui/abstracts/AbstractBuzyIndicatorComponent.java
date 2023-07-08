package org.magic.gui.abstracts;

import java.awt.BorderLayout;

import javax.swing.JComponent;

import org.magic.gui.components.widgets.JBuzyLabel;
import org.magic.gui.components.widgets.JBuzyProgress;
import org.utils.patterns.observer.Observable;
import org.utils.patterns.observer.Observer;

public abstract class AbstractBuzyIndicatorComponent extends JComponent implements Observer {

	private static final long serialVersionUID = 1L;

	public abstract void setText(String s);
	public abstract void setValue(int i) ;
	public abstract void start(int max);
	public abstract void start();
	public abstract void end();
	public abstract int getValue();
	public abstract JComponent getBuzyComponent();

	public static AbstractBuzyIndicatorComponent createLabelComponent()
	{
		var buzy= new JBuzyLabel();
		buzy.setVisible(false);
		return buzy;
	}

	public static AbstractBuzyIndicatorComponent createProgressComponent()
	{
		var buzy= new JBuzyProgress();
		buzy.setVisible(false);
		return buzy;
	}


	protected AbstractBuzyIndicatorComponent() {
		setVisible(false);
		setLayout(new BorderLayout());
	}

	public void progress()
	{
		progress(1);
	}

	public void progressSmooth(int step)
	{
		for(var i=0;i<step;i++)
			progress();
	}


	public void progress(int step)
	{
		setValue(getValue()+step);
	}


	@Override
	public void update(Observable o, Object obj) {
		if(obj instanceof Integer i)
		{
			setValue(i);
		}
		else
		{
			setText(String.valueOf(obj));
			progress();
		}
	}
}
