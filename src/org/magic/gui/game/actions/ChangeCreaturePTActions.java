package org.magic.gui.game.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.magic.gui.game.DisplayableCard;

public class ChangeCreaturePTActions  extends AbstractAction{

	public enum TypeCounter {Strength,Toughness,Both};
	
	private DisplayableCard card;
	private TypeCounter type;
	private int val;
	
	
	public ChangeCreaturePTActions(DisplayableCard card,int val,TypeCounter type) {
		
		if(type==TypeCounter.Strength)
		{
			if(val>0)
			{
				putValue(NAME,"put a +"+val+"/+0 counter");
				putValue(SHORT_DESCRIPTION,"Add +"+ val +" strength counter");
				putValue(MNEMONIC_KEY,KeyEvent.VK_C);
			}
			else
			{
				putValue(NAME,"put a "+val+"/+0 counter");
				putValue(SHORT_DESCRIPTION,"Add "+ val +" strength counter");
				putValue(MNEMONIC_KEY,KeyEvent.VK_MINUS);
			}
		}
		if(type==TypeCounter.Toughness)
		{
			if(val>0)
			{
				putValue(NAME,"put a +0/+"+val);
				putValue(SHORT_DESCRIPTION,"Add +"+ val +" toughness counter");
		
			}
			else
			{
				putValue(NAME,"put a +0/"+val);
				putValue(SHORT_DESCRIPTION,"Add "+ val +" toughness counter");
		
			}
			
		}
		if(type==TypeCounter.Both)
		{
				putValue(NAME,"Fix Power/toughness");
				putValue(SHORT_DESCRIPTION,"Fix Power/toughness");
		}
		
		
	    this.card = card;
        this.type=type;
        this.val=val;
}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(type==TypeCounter.Strength)
			card.getMagicCard().setPower(""+(Integer.parseInt(card.getMagicCard().getPower())+val));
		if(type==TypeCounter.Toughness)
			card.getMagicCard().setToughness(""+(Integer.parseInt(card.getMagicCard().getToughness())+val));
		if(type==TypeCounter.Both)
		{
			String res = JOptionPane.showInputDialog("Change F/T with ");
			String[] counts = res.split("/");
			card.getMagicCard().setPower(counts[0]);
			card.getMagicCard().setToughness(counts[1]);
		}
		
		card.showPT(true);
	}

}
