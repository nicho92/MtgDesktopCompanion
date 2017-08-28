package org.magic.gui.abstracts;

import javax.swing.JInternalFrame;

public abstract class AbstractJDashlet extends JInternalFrame  {

	public abstract String getName();
	
	public abstract void save(String k , Object value);
	
	public abstract void init();

	public abstract boolean isStartup();
	
	
	@Override
	public String toString() {
		return getName();
	}
}
