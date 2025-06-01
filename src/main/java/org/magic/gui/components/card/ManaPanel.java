package org.magic.gui.components.card;

import java.awt.FlowLayout;
import java.awt.Image;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.magic.api.beans.enums.EnumCardsPatterns;
import org.magic.services.MTGConstants;
import org.magic.services.providers.IconsProvider;

public class ManaPanel extends JPanel {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private FlowLayout fl = new FlowLayout();
	private String manaCost;

	public ManaPanel() {
		fl.setAlignment(FlowLayout.LEFT);
		setLayout(fl);
	}
	public String getManaCost() {
		return manaCost;
	}

	public void setManaCost(String manaCost) {
		
		this.removeAll();
		this.revalidate();
		this.repaint();
		if (manaCost == null)
			return;
		
		manaCost=manaCost.replace("{}", "");
		
		
		var p = Pattern.compile(EnumCardsPatterns.MANA_PATTERN.getPattern());
		var m = p.matcher(manaCost);

		fl.setVgap(0);
		fl.setHgap(0);
		while (m.find()) {
			var lab = new JLabel();
			var img = IconsProvider.getInstance().getManaSymbol(m.group(1));
			
			var w = MTGConstants.TABLE_ROW_HEIGHT;
			
			if(m.group(1).equals("100"))
				w=36;
				else
					if(m.group(1).equals("1000000"))
						w=90;
			
			lab.setIcon(new ImageIcon(img.getScaledInstance(w, MTGConstants.TABLE_ROW_HEIGHT, Image.SCALE_DEFAULT)));
			lab.setHorizontalAlignment(SwingConstants.CENTER);
			add(lab);
			
		}
	}

	

}
