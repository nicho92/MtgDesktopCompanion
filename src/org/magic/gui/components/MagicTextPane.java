package org.magic.gui.components;

import java.awt.Dimension;
import java.awt.Image;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import org.magic.api.beans.MagicCard;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MagicTextPane extends JTextPane{
	
	ManaPanel manaPanel;
	MagicCard card;
	
	public MagicTextPane() {
		manaPanel=new ManaPanel();
		setPreferredSize(new Dimension(200,150));
		
		addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				updateTextWithIcons();
			}
		});
	}
	

	public void updateTextWithIcons() {
		
		setText(getText().replaceAll("CHAOS", "{CHAOS}"));
		
		String regex ="\\{(.*?)\\}";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(getText());
		
		String text = getText();
		 StyleContext context = new StyleContext();
		 StyledDocument document = new DefaultStyledDocument(context);
		 
		 Style labelStyle = context.getStyle(StyleContext.DEFAULT_STYLE);
		
		 Style italic =context.addStyle("italicStyle", labelStyle); 
		 StyleConstants.setItalic(italic, true);
				 
		 int cumule=0;
		 try {
			document.insertString(0, text, null);
			while(m.find()) {
				 Image ic = manaPanel.getManaSymbol(m.group());
				
				 int width=15;
				 if(m.group().equals("{100}"))
					 width=30;
				 
				
				 JLabel label = new JLabel(new ImageIcon(ic.getScaledInstance(width, 15, Image.SCALE_DEFAULT)));
				 		label.setAlignmentY(JLabel.TOP);
				 
				 StyleConstants.setComponent(labelStyle, label);

				 document.remove(m.start()+cumule, (m.end()-m.start()));
				 document.insertString(m.start()+cumule, m.group(), labelStyle);
			}
			setDocument(document);
			setCaretPosition(document.getLength());
		 } 
		 catch (BadLocationException e) {
				setText(text);
		}
	}
}
