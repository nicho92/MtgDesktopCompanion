package org.magic.gui.components.editor;

import java.util.List;

import org.japura.gui.CheckComboBox;

public class JCheckableListBox<T> extends CheckComboBox {
	
	
	public JCheckableListBox() {
		super();
	}
	
	public void addElement(T element,boolean checked)
	{
		getModel().addElement(element);
		if(checked)
			getModel().addCheck(element);
	}
	
	
	public List<T> getSelectedElements()
	{
		return (List<T>) getModel().getCheckeds();
	}
	
	public void setSelectedElements(List<T> elements)
	{
		for(T e : elements)
			getModel().addCheck(e);
		
	}
	
	public void unselectAll()
	{
		getModel().removeChecks();
	}
	
	

}
