package org.magic.game.gui.components.dialog;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGDeck;
import org.magic.game.gui.components.LightDescribeCardPanel;
import org.magic.gui.renderer.MagicCardListRenderer;
import org.magic.services.MTGConstants;

public class DeckSideBoardSwitcherDialog extends JDialog {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private MTGDeck savedDeck;
	private MTGDeck bckDeck;
	private JLabel lblDecksize;
	private LightDescribeCardPanel lightDescribeCardPanel;
	private JList<MTGCard> listMain;
	private JList<MTGCard> listSide;
	private DefaultListModel<MTGCard> modMain;
	private DefaultListModel<MTGCard> modSide;

	public DeckSideBoardSwitcherDialog(MTGDeck deck) {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setIconImage(MTGConstants.ICON_DECK.getImage());
		this.savedDeck = deck;
		this.bckDeck = deck;

		setTitle("Switch Sideboard " + deck.getName());
		lblDecksize = new JLabel();
		lblDecksize.setAlignmentX(Component.CENTER_ALIGNMENT);
		modMain = new DefaultListModel<>();
		modSide = new DefaultListModel<>();

		init();


		listMain = new JList<>(modMain);

		listMain.setCellRenderer(new MagicCardListRenderer());
		getContentPane().add(new JScrollPane(listMain), BorderLayout.WEST);
		listSide = new JList<>(modSide);
		listSide.setCellRenderer(new MagicCardListRenderer());
		getContentPane().add(new JScrollPane(listSide), BorderLayout.EAST);


		var panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		var btnAdd = new JButton(">>>");
		btnAdd.addActionListener(_ -> {
			for (MTGCard mc : listMain.getSelectedValuesList()) {
				modSide.addElement(mc);
				modMain.removeElement(mc);

				savedDeck.getMain().put(mc, savedDeck.getMain().get(mc) - 1);

				if (savedDeck.getSideBoard().get(mc) == null)
					savedDeck.getSideBoard().put(mc, 1);
				else
					savedDeck.getSideBoard().put(mc, savedDeck.getSideBoard().get(mc) + 1);

				refresh();
			}
		});
		btnAdd.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(btnAdd);

		var btnRemove = new JButton("<<<");
		btnRemove.addActionListener(_ -> {
			for (MTGCard mc : listSide.getSelectedValuesList()) {
				modMain.addElement(mc);
				modSide.removeElement(mc);

				savedDeck.getSideBoard().put(mc, savedDeck.getSideBoard().get(mc) - 1);

				if (savedDeck.getMain().get(mc) == null)
					savedDeck.getMain().put(mc, 1);
				else
					savedDeck.getMain().put(mc, savedDeck.getMain().get(mc) + 1);

				refresh();
			}
		});
		btnRemove.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(btnRemove);

		var btnRestore = new JButton("Restore");
		btnRestore.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(btnRestore);
		btnRestore.addActionListener(_ -> {
			savedDeck = bckDeck;
			init();
		});
		panel.add(lblDecksize);

		lightDescribeCardPanel = new LightDescribeCardPanel();
		panel.add(lightDescribeCardPanel);

		var panel1 = new JPanel();
		getContentPane().add(panel1, BorderLayout.SOUTH);

		var btnOk = new JButton(MTGConstants.ICON_CHECK);
		btnOk.addActionListener(_ -> dispose());

		panel1.add(btnOk);

		var btnCancel = new JButton(MTGConstants.ICON_CANCEL);
		btnCancel.addActionListener(_ -> {
			savedDeck = bckDeck;
			dispose();
		});
		panel1.add(btnCancel);


		initDescribe(listMain);
		initDescribe(listSide);

		pack();
	}


	private void initDescribe(JList<MTGCard> list)
	{
		list.addListSelectionListener(_->lightDescribeCardPanel.setCard(list.getSelectedValue()));
	}


	private void refresh() {
		lblDecksize.setText("DeckSize : " + savedDeck.getNbCards());

	}

	private void init() {

		modMain.removeAllElements();
		modSide.removeAllElements();

		for (MTGCard mc : bckDeck.getMainAsList())
			modMain.addElement(mc);

		for (MTGCard mc : bckDeck.getSideAsList())
			modSide.addElement(mc);

		refresh();
	}

	public MTGDeck getDeck() {
		return savedDeck;
	}

}
