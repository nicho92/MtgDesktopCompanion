package org.magic.game.gui.components.renderer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;

import org.magic.game.model.AbstractSpell;
import org.magic.game.model.abilities.AbstractAbilities;
import org.magic.gui.components.MagicTextPane;
import org.magic.services.MTGConstants;

public class SpellRendererPanel extends JPanel {
	
	private MagicTextPane textPane;
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
		textPane = new MagicTextPane();
		textPane.setEditable(false);
		
		lblIconCard = new JLabel();
		add(lblCardName, BorderLayout.NORTH);
		add(textPane, BorderLayout.CENTER);
		add(lblIconCard, BorderLayout.WEST);
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		if(value instanceof AbstractAbilities)
		{
			AbstractAbilities abs = (AbstractAbilities)value;
			textPane.setText(abs.getEffects().toString());
			lblCardName.setText(abs.getCard().toString());
			lblIconCard.setIcon(MTGConstants.ICON_TAB_MANA);
			setToolTipText(abs.toString());
		}
		else
		{
			textPane.setText(value.getCard().getText());
			lblCardName.setText(value.getCard().getName());
			lblIconCard.setIcon(MTGConstants.ICON_BACK);
		}
		textPane.updateTextWithIcons();
		textPane.setPreferredSize(new Dimension(5,50));
     }
	
	

}
