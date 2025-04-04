package org.magic.gui.components.card;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGRuling;
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

	private void init(List<MTGRuling> rulings) {
		txtRulesArea.setText("");
		for (MTGRuling mr : rulings) {
			txtRulesArea.append(mr.toString());
			txtRulesArea.append("\n");
		}
	}

	public void init(MTGCard selectedCard) {

		if(selectedCard !=null)
			init(selectedCard.getRulings());

	}

}
