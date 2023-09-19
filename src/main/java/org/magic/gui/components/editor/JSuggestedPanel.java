package org.magic.gui.components.editor;

import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import org.magic.api.interfaces.MTGTextGenerator;
import org.magic.gui.components.card.MagicTextPane;
import org.magic.services.MTGConstants;

public class JSuggestedPanel extends JComponent {

	private static final long serialVersionUID = 1L;
	public JSuggestedPanel(MagicTextPane jTextPane,MTGTextGenerator gen) {

		setLayout(new FlowLayout(FlowLayout.LEFT));
		var suggestions = new JPanel();
		suggestions.setLayout(new FlowLayout(FlowLayout.LEFT));

		var btn = new JButton(MTGConstants.ICON_TAB_SUGGESTION);
		btn.setToolTipText("text generator");
		btn.addActionListener(al->jTextPane.setText(gen.generateText()));

		add(btn);
		add(suggestions);

		jTextPane.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				update();

			}

			@Override
			public void insertUpdate(DocumentEvent e) {

				update();

			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				update();

			}

			public void update()
			{

				if(jTextPane.getText().endsWith(" "))
				{
					int index = jTextPane.getText().lastIndexOf('\n');

					String[] caracters = jTextPane.getText().split(" ");
					if(index>0)
						caracters = jTextPane.getText().substring(index+1).split(" ");

					suggestions.removeAll();

					for(String s : gen.suggestWords(caracters))
					{
						var t = new TagLabel(s);
						t.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								try {
									jTextPane.getDocument().insertString(jTextPane.getCaretPosition(), t.getText()+" ", null);
								} catch (BadLocationException e1) {
									jTextPane.setText(jTextPane.getText() +" " + t.getText()+ " " );
								}
							}
						});
						suggestions.add(t);
					}
					suggestions.revalidate();
					suggestions.repaint();
				}

			}


		});
	}

}
