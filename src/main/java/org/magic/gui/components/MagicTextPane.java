package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import org.magic.tools.CardsPatterns;

public class MagicTextPane extends JComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private transient KeyAdapter translation;
	
	ManaPanel manaPanel;
	JTextPane textPane;
	
	public MagicTextPane() {
		init();
		enableTranslate(true);
	}
	
	public MagicTextPane(boolean enable) {
		init();
		enableTranslate(enable);
	}
	
	
	private void init() {
		setLayout(new BorderLayout());
		textPane = new JTextPane();
		add(textPane,BorderLayout.CENTER);
		
		manaPanel = new ManaPanel();
		setPreferredSize(new Dimension(200, 150));
		textPane.getDocument().putProperty(DefaultEditorKit.EndOfLineStringProperty, "\n");

		translation=new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				int pos = textPane.getCaretPosition();
				updateTextWithIcons();
				textPane.setCaretPosition(pos);
			}
		};
	}

	public void enableTranslate(boolean b)
	{
		
		if(b)
			addKeyListener(translation);
		else
			removeKeyListener(translation);
	}
	

	public void updateTextWithIcons() {

		textPane.setText(textPane.getText().replaceAll("(?m)^[ \t]*\r?\n", ""));

		Pattern p = Pattern.compile(CardsPatterns.MANA_PATTERN.getPattern());
		Matcher m = p.matcher(textPane.getText());

		String text = textPane.getText();
		StyleContext context = new StyleContext();
		StyledDocument document = new DefaultStyledDocument(context);

		Style labelStyle = context.getStyle(StyleContext.DEFAULT_STYLE);

		Style italic = context.addStyle("italicStyle", labelStyle);
		StyleConstants.setItalic(italic, true);

		int cumule = 0;
		try {
			document.insertString(0, text, null);
			while (m.find()) {
				Image ic = manaPanel.getManaSymbol(m.group());

				int width = 15;
				if (m.group().equals("{100}"))
					width = 30;

				JLabel label = new JLabel(new ImageIcon(ic.getScaledInstance(width, 15, Image.SCALE_DEFAULT)));
				label.setAlignmentY(JLabel.TOP);

				StyleConstants.setComponent(labelStyle, label);

				document.remove(m.start() + cumule, (m.end() - m.start()));
				document.insertString(m.start() + cumule, m.group(), labelStyle);
			}

			textPane.setDocument(document);

		} catch (BadLocationException e) {
			textPane.setText(text);
		}
	}

	public void setEditable(boolean b) {
		textPane.setEditable(b);
		
	}

	public void setText(String string) {
		textPane.setText(string);
		
	}

	public Document getDocument() {
		return textPane.getDocument();
	}
	
	public String getText()
	{
		return textPane.getText();
	}
	
	public int getCaretPosition()
	{
		return textPane.getCaretPosition();
	}
	
}
