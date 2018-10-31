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
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

public class ManualImportDialog extends JDialog {

	
	private static final long serialVersionUID = 1L;
	private JEditorPane editorPane;
	private MagicDeck deck;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());

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

		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		editorPane = new JEditorPane();
		editorPane.setPreferredSize(new Dimension(106, 300));
		scrollPane.setViewportView(editorPane);
		setLocationRelativeTo(null);
	}

	public MagicDeck getAsDeck() {

		if (editorPane.getText().length() == 0)
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
