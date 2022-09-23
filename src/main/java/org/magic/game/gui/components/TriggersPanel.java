package org.magic.game.gui.components;

import java.awt.BorderLayout;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.magic.game.gui.components.renderer.StackItemRenderer;
import org.magic.game.model.AbstractSpell;
import org.magic.game.model.GameManager;
import org.magic.game.model.TriggerManager.TRIGGERS;
import org.utils.patterns.observer.Observable;
import org.utils.patterns.observer.Observer;

public class TriggersPanel extends JPanel implements Observer {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private JList<AbstractSpell> listStack;
	private DefaultListModel<AbstractSpell> model;

	public TriggersPanel() {
		model = new DefaultListModel<>();
		listStack = new JList<>(model);
		setLayout(new BorderLayout(0, 0));
		listStack.setCellRenderer(new StackItemRenderer());
		add(new JScrollPane(listStack ), BorderLayout.CENTER);

	}


	public void updateElements()
	{
		model.clear();
		for(Entry<TRIGGERS, List<AbstractSpell>> e : GameManager.getInstance().getTriggers().list())
			for(AbstractSpell sp : e.getValue())
				model.addElement(sp);

	}


	@Override
	public void update(Observable o, Object arg) {
		updateElements();

	}

}




