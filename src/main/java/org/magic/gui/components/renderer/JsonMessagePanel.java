package org.magic.gui.components.renderer;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.abstracts.AbstractMessage;
import org.magic.api.beans.abstracts.AbstractMessage.MSG_TYPE;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.messages.DeckMessage;
import org.magic.api.beans.messages.SearchMessage;
import org.magic.api.beans.messages.TalkMessage;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.api.interfaces.extra.MTGProduct;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.network.URLTools;
import org.magic.services.tools.ImageTools;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;
import org.ocpsoft.prettytime.PrettyTime;

public class JsonMessagePanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel lblTime;
	private JLabel lblAuthor;
	private JLabel lblAvatar;
	private JLabel lblIcon;
	private JTextPane textArea;
	private int iconSize=25;
	private JPanel separator;
	
	
	public JsonMessagePanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{10, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);

		textArea = new JTextPane();
		textArea.setContentType("text/html");
		textArea.setEditable(false);
		textArea.setFont(MTGControler.getInstance().getFont());
		textArea.setOpaque(false);
		
		lblAvatar = new JLabel();
		lblAuthor = new JLabel();
		lblTime = new JLabel();
		lblIcon = new JLabel();
		separator = new JPanel();
		
		separator.add(lblIcon);
		
		lblTime.setFont(MTGControler.getInstance().getFont().deriveFont(Font.ITALIC));
		lblTime.setHorizontalAlignment(SwingConstants.RIGHT);
		
		add(lblAvatar, UITools.createGridBagConstraints(null, null, 1, 0));
		add(lblAuthor, UITools.createGridBagConstraints(GridBagConstraints.WEST, null, 2, 0));
		
		var gbcseparator = UITools.createGridBagConstraints(GridBagConstraints.WEST, GridBagConstraints.VERTICAL, 0, 0);
		gbcseparator.gridheight = 3;
		gbcseparator	.insets = new Insets(0, 0, 0, 5);
		add(separator, gbcseparator);
		
		var gbctextArea = UITools.createGridBagConstraints(null,GridBagConstraints.BOTH,1,1);
		gbctextArea.gridwidth = 2;
		add(textArea, gbctextArea);

		var gbclblTime = UITools.createGridBagConstraints(GridBagConstraints.NORTH,GridBagConstraints.HORIZONTAL,1,2);
		gbclblTime.gridwidth = 2;
		add(lblTime, gbclblTime);
		
		setOpaque(true);
		
	}
	
	
	
	public void init(AbstractMessage value)
	{
	
		setBorder(new LineBorder(value.getColor(),2,true));
		lblIcon.setIcon(null);
		textArea.setText(URLTools.toHtmlFromMarkdown(value.getMessage()));
		lblAuthor.setText(value.getAuthor().getName());
		separator.setBackground(value.getColor());
	
		lblTime.setText("("+new PrettyTime(MTGControler.getInstance().getLocale()).format(new Date(value.getStart().toEpochMilli()))+")");		
		
		if(value.getAuthor().getAvatar()!=null)	 
			lblAvatar.setIcon(new ImageIcon(ImageTools.resize(value.getAuthor().getAvatar(), iconSize, iconSize)));
		
		
		if(value.getTypeMessage()==MSG_TYPE.SEARCH)
		{
			var item = ((SearchMessage)value).getItem();
			try {
				lblIcon.setIcon(read(item));
			} catch (IOException _) {
				//do nothing
			}
		}

		if(value.getTypeMessage()==MSG_TYPE.TALK)
		{
			var item = ((TalkMessage)value).getMagicCard();
			if(item!=null)
				try {
					lblIcon.setIcon(read(item));
				} catch (IOException _) {
					//do nothing
				}
		}
		
		if(value.getTypeMessage()==MSG_TYPE.DECK)
		{
			var item = ((DeckMessage)value).getMagicDeck();
			if(item!=null)
				{
				lblIcon.setIcon(MTGConstants.ICON_DECK);
				textArea.setText(item.getName() + " " + item.getColors() + " ("+item.getMainAsList().size()+")");
				}
		}
	}

	private ImageIcon read(MTGProduct item) throws IOException 
	{
		
		BufferedImage bi = null;
		
		if(item.getTypeProduct()==EnumItems.CARD)
			bi=MTG.getEnabledPlugin(MTGPictureProvider.class).getPicture((MTGCard)item);
		else
			bi = URLTools.extractAsImage(item.getUrl());
		
		bi=ImageTools.scaleResize(bi, iconSize*2);
		return new ImageIcon(bi);
	
	}
	
}
