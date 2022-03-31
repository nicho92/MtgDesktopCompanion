package org.magic.gui.components;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicRuling;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.MTGConstants;

public class RulesPanel extends MTGUIComponent{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextArea txtRulesArea;
	
	
	public RulesPanel() {
		setLayout(new BorderLayout());
		txtRulesArea = new JTextArea();
		txtRulesArea.setLineWrap(true);
		txtRulesArea.setWrapStyleWord(true); 
		txtRulesArea.setEditable(false);
		
		add(new JScrollPane(txtRulesArea), BorderLayout.CENTER);
		
	}
	
	@Override
	public String getTitle() {
		return "RULES";
	}
	
	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_TAB_RULES;
	}

	private void init(List<MagicRuling> rulings) {
		txtRulesArea.setText("");
		for (MagicRuling mr : rulings) {
			txtRulesArea.append(mr.toString());
			txtRulesArea.append("\n");
		}
	}

	public void init(MagicCard selectedCard) {
		
		if(selectedCard !=null)
			init(selectedCard.getRulings());
		
	}

}
