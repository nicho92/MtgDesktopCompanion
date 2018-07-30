package org.magic.game.gui.components;

import org.jdesktop.swingx.JXPanel;
import org.magic.game.model.AbstractSpell;
import org.magic.game.model.GameManager;
import org.utils.patterns.observer.Observable;
import org.utils.patterns.observer.Observer;

import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.DefaultListModel;
import javax.swing.JList;

public class StackPanel extends JXPanel implements Observer {
	
	private JList<AbstractSpell> listStack;
	private DefaultListModel<AbstractSpell> model;
	
	
	public StackPanel() {
		setLayout(new BorderLayout(0, 0));
		model = new DefaultListModel<>();
		listStack = new JList<>(model);
		add(new JScrollPane(listStack ), BorderLayout.CENTER);
	}
	
	public void updateStack()
	{
		model.clear();
		for(AbstractSpell spell : GameManager.getInstance().getStack().toList())
			model.addElement(spell);
		
		
	}

	@Override
	public void update(Observable o, Object arg) {
		updateStack();
		
	}
	

}
