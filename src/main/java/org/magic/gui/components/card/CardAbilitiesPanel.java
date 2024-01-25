package org.magic.gui.components.card;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.magic.api.beans.MTGCard;
import org.magic.game.model.factories.AbilitiesFactory;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.MTGConstants;
import org.magic.services.keywords.AbstractKeyWordsManager;

public class CardAbilitiesPanel extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private JTextPane textPane ;
	private MTGCard mc;


	public CardAbilitiesPanel() {
		setLayout(new BorderLayout(0, 0));

		textPane = new JTextPane();
		add(new JScrollPane(textPane),BorderLayout.CENTER);
	}

	@Override
	public String getTitle() {
		return "ABILITIES";
	}

	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_GAME_TRIGGER;
	}

	@Override
	public void onVisible() {
		init(mc);
	}


	public void init(MTGCard mc)
	{
		this.mc=mc;

		if(!isVisible() || (mc==null))
			return;


		var temp = new StringBuilder();
			temp.append("KEYWORDS : \n");
			AbstractKeyWordsManager.getInstance().getKeywordsFrom(mc).forEach(kw->{
				temp.append("\t").append(kw.getKeyword()).append(" ");
				if(kw.getEvent()!=null)
					temp.append(kw.getEvent());

				temp.append("\n");

			});

			temp.append("\nABILITIES:\n").append(AbilitiesFactory.getInstance().getAbilities(mc));
			textPane.setText(temp.toString());
	}


}
