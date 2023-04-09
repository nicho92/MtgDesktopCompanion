package org.magic.gui.components.renderer;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import org.magic.api.beans.JsonMessage;
import org.magic.services.MTGControler;
import org.magic.services.tools.ImageTools;
import org.magic.services.tools.UITools;
import org.ocpsoft.prettytime.PrettyTime;

public class JsonMessagePanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel lblTime;
	private JTextArea textArea;

	
	
	public JsonMessagePanel(JsonMessage value) {
		setBorder(new LineBorder(value.getColor(),2,true));
		
		int iconSize=25;
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{10, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		
	
		var gbc = UITools.createGridBagConstraints(null, null, 1, 0);
			 gbc.insets = new Insets(0, 0, 5, 5);
		add(new JLabel(new ImageIcon(ImageTools.resize(value.getAuthor().getAvatar(), iconSize, iconSize))), gbc);
		

		var separator = new JPanel();
		separator.setBackground(value.getColor());
		
		GridBagConstraints gbcseparator = new GridBagConstraints();
		gbcseparator.gridheight = 3;
		gbcseparator.anchor = GridBagConstraints.WEST;
		gbcseparator.fill = GridBagConstraints.VERTICAL;
		gbcseparator.insets = new Insets(0, 0, 0, 5);
		gbcseparator.gridx = 0;
		gbcseparator.gridy = 0;
		add(separator, gbcseparator);
		
		
		GridBagConstraints gbc1 = UITools.createGridBagConstraints(GridBagConstraints.WEST, null, 2, 0);
		gbc1.insets = new Insets(0, 0, 5, 0);

		add(new JLabel(value.getAuthor().getName()), gbc1);
		
		
		
		textArea = new JTextArea(value.getMessage());
		GridBagConstraints gbctextArea = new GridBagConstraints();
		gbctextArea.gridwidth = 2;
		gbctextArea.fill = GridBagConstraints.BOTH;
		gbctextArea.insets = new Insets(0, 0, 5, 0);
		gbctextArea.gridx = 1;
		gbctextArea.gridy = 1;
		add(textArea, gbctextArea);
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setFont(MTGControler.getInstance().getFont());
		textArea.setOpaque(false);
		
		lblTime = new JLabel("("+new PrettyTime(MTGControler.getInstance().getLocale()).format(new Date(value.getStart().toEpochMilli()))+")",SwingConstants.RIGHT);
		lblTime.setFont(MTGControler.getInstance().getFont().deriveFont(Font.ITALIC));
		GridBagConstraints gbclblTime = new GridBagConstraints();
		gbclblTime.fill = GridBagConstraints.HORIZONTAL;
		gbclblTime.gridwidth = 2;
		gbclblTime.anchor = GridBagConstraints.NORTH;
		gbclblTime.gridx = 1;
		gbclblTime.gridy = 2;
		add(lblTime, gbclblTime);
	}
	
}
