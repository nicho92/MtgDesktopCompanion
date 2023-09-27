package org.magic.game.gui.components.renderer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.magic.api.beans.enums.EnumColors;
import org.magic.api.beans.game.AbstractSpell;
import org.magic.game.model.abilities.AbstractAbilities;
import org.magic.gui.components.card.MagicTextPane;
import org.magic.services.MTGConstants;

public class SpellRendererPanel extends JPanel {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private MagicTextPane textPane;
	private JLabel lblCardName;
	private JLabel lblIconCard;

	public void setColor(Color fore,Color back)
	{
		lblIconCard.setBackground(back);
		lblIconCard.setForeground(fore);
		lblCardName.setBackground(back);
		lblCardName.setForeground(fore);
	}

	public SpellRendererPanel()
	{
		setLayout(new BorderLayout(0, 0));

		lblCardName = new JLabel();
		textPane = new MagicTextPane();
		lblIconCard = new JLabel();

		add(lblCardName, BorderLayout.NORTH);
		add(textPane, BorderLayout.CENTER);
		add(lblIconCard, BorderLayout.WEST);

		textPane.setEditable(false);
		textPane.setPreferredSize(new Dimension(5,60));
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		lblIconCard.setOpaque(true);
		lblCardName.setOpaque(true);

	}


	public void setSpell(AbstractSpell value) {

		lblCardName.setText(value.getTitle());


		if(value instanceof AbstractAbilities abs)
		{
			textPane.setText(abs.getEffects().toString());

			if(abs.isActivated())
			{
				lblIconCard.setIcon(MTGConstants.ICON_GAME_ACTIVATED);
			}
			else if(abs.isTriggered())
			{
				lblIconCard.setIcon(MTGConstants.ICON_GAME_TRIGGER);
			}
		}
		else
		{
			textPane.setText(value.getCard().getText());
			lblIconCard.setIcon(MTGConstants.ICON_TAB_BACK);
		}

		textPane.updateTextWithIcons();
		var c = EnumColors.determine(value.getCard().getColors()).toColor();
		setColor(Color.BLACK,c);


		if(c.equals(Color.BLACK))
			setColor(Color.WHITE, Color.GRAY);
     }



}
