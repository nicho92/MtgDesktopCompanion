package org.magic.gui.components.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

public class ManualImportDialog extends JDialog {
	
	
	private JEditorPane editorPane;
	private MagicDeck deck;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	
	public String getStringDeck()
	{
		return editorPane.getText();
	}
	
	public ManualImportDialog() {
		deck=new MagicDeck();
		getContentPane().setLayout(new BorderLayout(0, 0));
		setSize(new Dimension(400, 400));
		setModal(true);
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);
		
		JButton btnImport = new JButton(MTGControler.getInstance().getLangService().getCapitalize("IMPORT"));
		btnImport.addActionListener(e->dispose());
		panel.add(btnImport);
		
		JButton btnCancel = new JButton(MTGControler.getInstance().getLangService().getCapitalize("CANCEL"));
		btnCancel.addActionListener(e->{
				editorPane.setText("");
				dispose();
		});
		panel.add(btnCancel);
		
		JLabel lblPastYourDeck = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("IMPORT_HELP"));
		getContentPane().add(lblPastYourDeck, BorderLayout.NORTH);
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		editorPane = new JEditorPane();
		editorPane.setPreferredSize(new Dimension(106, 300));
		scrollPane.setViewportView(editorPane);
		setLocationRelativeTo(null);
	}
	
	public MagicDeck getAsDeck() {
		
				if(editorPane.getText().length()==0)
					return deck;
		
				String[] line = editorPane.getText().split("\n");
				for (String l : line) 
				{
					int nb = Integer.parseInt(l.substring(0, l.indexOf(' ')));
					String name = l.substring(l.indexOf(' '), l.length());
					try {
						MagicCard mc;
						if (name.trim().equalsIgnoreCase("Plains") || name.trim().equalsIgnoreCase("Island")|| name.trim().equalsIgnoreCase("Swamp") || name.trim().equalsIgnoreCase("Mountain")|| name.trim().equalsIgnoreCase("Forest")) 
						{
							MagicEdition ed = new MagicEdition();
							ed.setId(MTGControler.getInstance().get("default-land-deck"));
							mc = MTGControler.getInstance().getEnabledProviders().searchCardByCriteria("name", name.trim(), ed,true).get(0);
						} 
						else {
							mc = MTGControler.getInstance().getEnabledProviders().searchCardByCriteria("name", name.trim(), null,true).get(0);
						}

						if (mc != null) {
							deck.getMap().put(mc, nb);
						}
					} catch (Exception e) {
						logger.error(e);
					}
				}
				return deck;
	}

}
