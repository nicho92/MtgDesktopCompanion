package org.magic.game.actions.cards;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;

import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.GamePanelGUI;

public class FlashbackActions extends AbstractAction {

	
	private DisplayableCard card;
	private String cost;
	private String k = "Flashback";
	
	
	public FlashbackActions(DisplayableCard card) {
			super("Flashback");
			putValue(SHORT_DESCRIPTION,"Flashback");
	        putValue(MNEMONIC_KEY, KeyEvent.VK_F);
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
				cost=text.substring(text.indexOf(k+"—")+k.length(),text.indexOf("("));
			
		}
		catch(Exception e)
		{
			cost="";
		}
		return cost;
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		GamePanelGUI.getInstance().getPlayer().playCardFromGraveyard(card.getMagicCard());
		GamePanelGUI.getInstance().getPlayer().logAction("Flashback " + card +" for " + parse());
		GamePanelGUI.getInstance().getPanelGrave().remove(card);
		GamePanelGUI.getInstance().getPanelGrave().postTreatment();
		GamePanelGUI.getInstance().getPanelBattleField().addComponent(card);
		GamePanelGUI.getInstance().getPanelBattleField().revalidate();
		GamePanelGUI.getInstance().getPanelBattleField().repaint();
		
		
	}

}
