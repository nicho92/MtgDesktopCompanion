package org.magic.gui.components.editor;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import org.magic.api.interfaces.MTGTextGenerator;

public class JSuggestedPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	public JSuggestedPanel(JTextPane jTextPane,MTGTextGenerator gen) {
		
		setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel suggestions = new JPanel();
		suggestions.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		
		JButton btn = new JButton("Generate");
		
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
						TagLabel t = new TagLabel(s);
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
