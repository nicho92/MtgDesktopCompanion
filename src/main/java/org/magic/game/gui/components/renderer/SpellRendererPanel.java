package org.magic.game.gui.components.renderer;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.magic.game.model.AbstractSpell;
import org.magic.game.model.abilities.AbstractAbilities;
import org.magic.services.MTGConstants;

public class SpellRendererPanel extends JPanel {
	
	private JTextArea textArea;
	private JLabel lblCardName;
	private JLabel lblIconCard;
	
	
	
	public void setColor(Color fore,Color back)
	{
		setBackground(back);
		setForeground(fore);
	}
	
	public SpellRendererPanel(AbstractSpell value) {
		setLayout(new BorderLayout(0, 0));
		lblCardName = new JLabel();
		textArea = new JTextArea();
		textArea.setEditable(false);
		lblIconCard = new JLabel();
		
		lblCardName.setOpaque(false);
		lblIconCard.setOpaque(false);
		
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		add(lblCardName, BorderLayout.NORTH);
		add(textArea, BorderLayout.CENTER);
		add(lblIconCard, BorderLayout.WEST);
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		if(value instanceof AbstractAbilities)
		{
			AbstractAbilities abs = (AbstractAbilities)value;
			textArea.setText(abs.getEffects().toString());
			lblCardName.setText(abs.getCard().toString());
			lblIconCard.setIcon(MTGConstants.ICON_TAB_MANA);
			setToolTipText(abs.toString());
		}
		else
		{
			textArea.setText(value.getCard().getText());
			lblCardName.setText(value.getCard().getName());
			lblIconCard.setIcon(MTGConstants.ICON_BACK);
		}
	}
	
	

}
