package org.magic.gui.components.card;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGRuling;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.MTGConstants;
import org.magic.services.tools.UITools;

public class RulesPanel extends MTGUIComponent{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private JTextPane txtRulesArea;


	public RulesPanel() {
		setLayout(new BorderLayout());
		txtRulesArea = new JTextPane();
		txtRulesArea.setEditable(false);
		txtRulesArea.setContentType("text/html");
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
		var builder = new StringBuilder("<html>");
		for (MTGRuling mr : rulings) {
			builder.append("<b>");
			builder.append(UITools.formatDate(mr.getDate()));
			builder.append("</b>:");
			builder.append(mr.getText());
			builder.append("<br/><br/>");
		}
		
		builder.append("</html>");
		
		txtRulesArea.setText(builder.toString());
		
		
	}

	public void init(MTGCard selectedCard) {

		if(selectedCard !=null)
			init(selectedCard.getRulings());

	}

}
