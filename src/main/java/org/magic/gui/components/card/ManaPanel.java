package org.magic.gui.components.card;

import java.awt.FlowLayout;
import java.awt.Image;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.apache.logging.log4j.Logger;
import org.magic.api.beans.enums.EnumCardsPatterns;
import org.magic.services.MTGConstants;
import org.magic.services.logging.MTGLogger;
import org.magic.services.providers.IconsProvider;

public class ManaPanel extends JPanel {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private int rowHeight = MTGConstants.TABLE_ROW_HEIGHT;
	private int rowWidth = MTGConstants.TABLE_ROW_WIDTH;
	protected transient Logger logger = MTGLogger.getLogger(this.getClass());
	
	public int getRowHeight() {
		return rowHeight;
	}

	public int getRowWidth() {
		return rowWidth;
	}

	FlowLayout fl = new FlowLayout();

	int count = 0;

	String manaCost;

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
			lab.setIcon(new ImageIcon(img.getScaledInstance(rowWidth, rowHeight, Image.SCALE_DEFAULT)));
			lab.setHorizontalAlignment(SwingConstants.CENTER);
			add(lab);
		}
	}

	

}
