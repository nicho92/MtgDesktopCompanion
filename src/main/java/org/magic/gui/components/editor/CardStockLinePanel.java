package org.magic.gui.components.editor;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.Image;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.LineBorder;

import org.apache.log4j.Logger;
import org.magic.api.beans.EnumCondition;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicCollection;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

public class CardStockLinePanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField txtComment;
	private JComboBox<EnumCondition> cboState;
	private JComboBox cboLanguage;
	private JCheckBox cboSigned;
	private JCheckBox cboFoil;
	private JSpinner txtQte;

	private transient MagicCardStock state;
	private JCheckBox cboAltered;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());

	public CardStockLinePanel(MagicCard selectedCard, MagicCollection selectedCol) {

		setBorder(new LineBorder(Color.BLACK, 1, true));
		setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

		state = MTGControler.getInstance().getDefaultStock();
		state.setMagicCard(selectedCard);
		state.setMagicCollection(selectedCol);
		setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		JLabel lblQuantity = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("QTY") + " :");
		add(lblQuantity);

		txtQte = new JSpinner();
		txtQte.setModel(new SpinnerNumberModel(state.getQte(), 0, null, 1));
		add(txtQte);

		cboState = new JComboBox<>(EnumCondition.values());
		cboState.setSelectedItem(state.getCondition());
		add(cboState);

		cboLanguage = new JComboBox<>(MTGControler.getInstance().getEnabled(MTGCardsProvider.class).getLanguages());
		cboLanguage.setSelectedItem(state.getLanguage());
		add(cboLanguage);

		JLabel lblComment = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("COMMENTS") + " :");
		add(lblComment);

		txtComment = new JTextField();
		add(txtComment);
		txtComment.setColumns(20);

		cboFoil = new JCheckBox(MTGControler.getInstance().getLangService().getCapitalize("FOIL"));
		cboFoil.setSelected(state.isFoil());
		add(cboFoil);

		cboSigned = new JCheckBox(MTGControler.getInstance().getLangService().getCapitalize("SIGNED"));
		cboSigned.setSelected(state.isSigned());
		add(cboSigned);

		JButton btnNewButton = new JButton("");

		Image img = MTGConstants.ICON_DELETE.getImage();
		Image newimg = img.getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH);

		btnNewButton.setIcon(new ImageIcon(newimg));
		btnNewButton.addActionListener(e -> delete());

		JButton btnSave = new JButton("");

		Image img2 = MTGConstants.ICON_CHECK.getImage();
		Image newimg2 = img2.getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH);

		btnSave.setIcon(new ImageIcon(newimg2));

		btnSave.addActionListener(ae -> {

			try {
				generateState();
				MTGControler.getInstance().getEnabled(MTGDao.class).saveOrUpdateStock(state);
			} catch (SQLException e) {
				logger.error(e);
			}
		});

		cboAltered = new JCheckBox(MTGControler.getInstance().getLangService().getCapitalize("ALTERED"));
		cboAltered.setSelected(state.isAltered());
		add(cboAltered);
		add(btnSave);
		add(btnNewButton);
	}

	private void generateState() {
		state.setComment(txtComment.getText());
		state.setCondition((EnumCondition) cboState.getSelectedItem());
		state.setQte((Integer) txtQte.getValue());
		state.setSigned(cboSigned.isSelected());
		state.setFoil(cboFoil.isSelected());
		state.setLanguage(cboLanguage.getSelectedItem().toString());
		state.setAltered(cboAltered.isSelected());
	}

	public void delete() {

		generateState();
		try {
			MTGControler.getInstance().getEnabled(MTGDao.class).deleteStock(state);
		} catch (SQLException e1) {
			logger.error(e1);
		}

		try {
			getParent().remove(this);
			getParent().revalidate();
			getParent().repaint();
		} catch (NullPointerException e) {
			logger.error(getParent() + " remove " + this + " error : " + e);
		}

		
	}

	public void setMagicCardState(MagicCardStock state) {
		this.state = state;
		txtComment.setText(state.getComment());
		txtQte.setValue(state.getQte());
		cboFoil.setSelected(state.isFoil());
		cboSigned.setSelected(state.isSigned());
		cboLanguage.setSelectedItem(state.getLanguage());
		cboState.setSelectedItem(state.getCondition());
		cboAltered.setSelected(state.isAltered());
	}

	public MagicCardStock getMagicCardState() {
		generateState();
		return state;
	}

}
