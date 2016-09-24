package org.magic.gui.components;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import org.magic.api.beans.EnumCondition;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicCollection;
import org.magic.services.MagicFactory;

public class CardStockLinePanel extends JPanel {
	  private JTextField txtComment;
	  private JComboBox<EnumCondition> cboState;
	  private JComboBox cboLanguage;
	  private JCheckBox cboSigned;
	  private JCheckBox cboFoil;
	  private JSpinner txtQte;
	  
	  private MagicCardStock state;
	  
	   
	public CardStockLinePanel(MagicCard selectedCard, MagicCollection selectedCol) {
		
		setBorder(new LineBorder(Color.BLACK, 1, true));
		setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		
		state= new MagicCardStock();
		state.setMagicCard(selectedCard);
		state.setMagicCollection(selectedCol);
		setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		JLabel lblQuantity = new JLabel("Quantity :");
		add(lblQuantity);
		
		txtQte = new JSpinner();
		txtQte.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		add(txtQte);
		
		cboState = new JComboBox(EnumCondition.values());
		add(cboState);
		
		cboLanguage = new JComboBox(MagicFactory.getInstance().getEnabledProviders().getLanguages());
		add(cboLanguage);
		
		JLabel lblComment = new JLabel("Comment :");
		add(lblComment);
		
		txtComment = new JTextField();
		add(txtComment);
		txtComment.setColumns(20);
		
		cboFoil = new JCheckBox("Foil");
		add(cboFoil);
		
		cboSigned = new JCheckBox("Signed");
		add(cboSigned);
		
		JButton btnNewButton = new JButton("");
		
		Image img = new ImageIcon(CardStockLinePanel.class.getResource("/res/delete.png")).getImage() ;  
		Image newimg = img.getScaledInstance( 25, 25,  java.awt.Image.SCALE_SMOOTH ) ;  
		
		btnNewButton.setIcon(new ImageIcon(newimg));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				delete();
			}
		});
		
		JButton btnSave = new JButton("");

		Image img2 = new ImageIcon(CardStockLinePanel.class.getResource("/res/check.png")).getImage() ;  
		Image newimg2 = img2.getScaledInstance( 25, 25,  java.awt.Image.SCALE_SMOOTH ) ;  
		
		btnSave.setIcon(new ImageIcon(newimg2));	
		
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				
				try {
					generateState();
					MagicFactory.getInstance().getEnabledDAO().saveOrUpdateStock(state);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		add(btnSave);
		add(btnNewButton);
	}
	
	
	private void generateState()
	{
		state.setComment(txtComment.getText());
		state.setCondition((EnumCondition)cboState.getSelectedItem());
		state.setQte((Integer)txtQte.getValue());
		state.setSigned(cboSigned.isSelected());
		state.setFoil(cboFoil.isSelected());
		state.setLanguage(cboLanguage.getSelectedItem().toString());
	}
	
	public void delete() {
		
		generateState();
		try {
			MagicFactory.getInstance().getEnabledDAO().deleteStock(state);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		try{
			getParent().remove(this);
			getParent().revalidate();
			getParent().repaint();
		}
		catch(NullPointerException e)
		{
		
		}
		
	}

	public void setMagicCardState(MagicCardStock state)
	{
		this.state=state;
		txtComment.setText(state.getComment());
		txtQte.setValue(state.getQte());
		cboFoil.setSelected(state.isFoil());
		cboSigned.setSelected(state.isSigned());
		cboLanguage.setSelectedItem(state.getLanguage());
		cboState.setSelectedItem(state.getCondition());
	}
	
	public MagicCardStock getMagicCardState()
	{
		generateState();
		return state;
	}
	

	
	
}
