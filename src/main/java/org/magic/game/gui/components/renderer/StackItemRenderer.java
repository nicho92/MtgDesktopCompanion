package org.magic.game.gui.components.renderer;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.magic.game.model.AbstractSpell;

public class StackItemRenderer extends JLabel implements ListCellRenderer<AbstractSpell>
{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	SpellRendererPanel render = new SpellRendererPanel();

	@Override
	public Component getListCellRendererComponent(JList<? extends AbstractSpell> list, AbstractSpell value, int index,boolean isSelected, boolean cellHasFocus) {

		try{
				render.setSpell(value);
				return render;

		}
		catch(Exception e)
		{
			return new JLabel(value.toString());
		}

	}
}
