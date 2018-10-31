package org.magic.gui.components.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;

import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsIndexer;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.gui.components.editor.JTagsPanel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

public class ManualImportDialog extends JDialog {

	
	private static final long serialVersionUID = 1L;
	private JTextPane editorPane;
	private MagicDeck deck;
	private JTagsPanel tagsPanel;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	private int start;
	private int position;
	
	public String getStringDeck() {
		return editorPane.getText();
	}

	public ManualImportDialog() {
		deck = new MagicDeck();
		getContentPane().setLayout(new BorderLayout(0, 0));
		setSize(new Dimension(400, 400));
		setIconImage(MTGConstants.ICON_TAB_IMPORT.getImage());
		setModal(true);
		JPanel panel = new JPanel();
		tagsPanel = new JTagsPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);

		JButton btnImport = new JButton(MTGConstants.ICON_IMPORT);
		btnImport.setToolTipText(MTGControler.getInstance().getLangService().getCapitalize("IMPORT"));
		btnImport.addActionListener(e -> dispose());
		panel.add(btnImport);

		JButton btnCancel = new JButton(MTGConstants.ICON_CANCEL);
		btnCancel.setToolTipText(MTGControler.getInstance().getLangService().getCapitalize("CANCEL"));
		btnCancel.addActionListener(e -> {
			editorPane.setText("");
			dispose();
		});
		panel.add(btnCancel);

		JLabel lblPastYourDeck = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("IMPORT_HELP"));
		

		
		getContentPane().add(lblPastYourDeck, BorderLayout.NORTH);
		
		JPanel panelCenter = new JPanel();
		getContentPane().add(panelCenter, BorderLayout.CENTER);
		panelCenter.setLayout(new BorderLayout(0, 0));
		editorPane = new JTextPane();
		
		editorPane.setPreferredSize(new Dimension(106, 300));
		panelCenter.add(new JScrollPane(editorPane));
		panelCenter.add(tagsPanel, BorderLayout.SOUTH);
		setLocationRelativeTo(null);
		
		tagsPanel.setEditable(false);
		tagsPanel.setFontSize(10);
		tagsPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String name = tagsPanel.getTagAt(e.getPoint());
				try {
					editorPane.getDocument().remove(start,position-start);
					editorPane.getDocument().insertString(start, " "+name+"\n", null);
				} catch (BadLocationException e1) {
					logger.error("error editing at s:" +start +" e:"+(position-start),e1);
				}
				
			}; 
		});
		
		
		editorPane.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent ke) {
				position = editorPane.getCaretPosition();
				try {
					
					if(ke.getKeyCode()==KeyEvent.VK_SPACE && start<=0)
						start=position-1;
					
					if(ke.getKeyCode()==KeyEvent.VK_ENTER)
						start=0;
					
					
					
					if(start>=0)
					{
						String currentName=editorPane.getText(start, (position-start)).trim();
						if(currentName.length()>=4)
							tagsPanel.bind(MTGControler.getInstance().getEnabled(MTGCardsIndexer.class).suggestCardName(currentName));
					
					}
				}
				catch(Exception e)
				{
					logger.error("error",e);
				}
				
			}
		});
		
		
		
		
	}
	
	public static void main(String[] args) {
		MTGControler.getInstance().getEnabled(MTGCardsProvider.class).init();
		ManualImportDialog diag = new ManualImportDialog();
		diag.setVisible(true);
	}
	
	
	

	public MagicDeck getAsDeck() {

		if (editorPane.getText().isEmpty())
			return deck;

		String[] line = editorPane.getText().split("\n");
		for (String l : line) {
			int nb = Integer.parseInt(l.substring(0, l.indexOf(' ')));
			String name = l.substring(l.indexOf(' '), l.length());
			try {
				MagicCard mc;
				if (MagicCard.isBasicLand(name)) {
					MagicEdition ed = new MagicEdition();
					ed.setId(MTGControler.getInstance().get("default-land-deck"));
					mc = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).searchCardByName( name.trim(), ed, true).get(0);
				} else {
					mc = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).searchCardByName( name.trim(), null, true).get(0);
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
