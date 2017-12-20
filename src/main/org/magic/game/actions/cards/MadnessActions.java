package org.magic.game.actions.cards;

import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MadnessActions extends AbstractAction {

	
	private DisplayableCard card;
	private String cost;
	private String k = "Madness";
	
	
	public MadnessActions(DisplayableCard card) {
			super("Madness");
			putValue(SHORT_DESCRIPTION,k);
	        putValue(MNEMONIC_KEY, KeyEvent.VK_M);
	        this.card = card;
	}
	
	private String parse()
	{
		try{
			String regex = "/*"+k+" \\{(.*?)\\ ";
			String text = card.getMagicCard().getText();
			Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
			Matcher m =p.matcher(text);
			
			if(m.find())
				cost=m.group().replaceAll(k, "").trim();
			else
				cost=text.substring(text.indexOf(k+ "\ufffd")+k.length(),text.indexOf("("));
			
		}
		catch(Exception e)
		{
			cost="";
		}
		return cost;
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		GamePanelGUI.getInstance().getPlayer().exileCardFromHand(card.getMagicCard());
		GamePanelGUI.getInstance().getPlayer().logAction("Play madness capacity of " + card +" for " + parse());
		GamePanelGUI.getInstance().getHandPanel().remove(card);
		GamePanelGUI.getInstance().getHandPanel().postTreatment(card);
		GamePanelGUI.getInstance().getExilPanel().addComponent(card);
		GamePanelGUI.getInstance().getExilPanel().postTreatment(card);
		GamePanelGUI.getInstance().getHandPanel().updatePanel();
	}

}
