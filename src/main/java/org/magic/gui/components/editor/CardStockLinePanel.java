package org.magic.gui.components.editor;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.Image;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

public class CardStockLinePanel extends JPanel {
	  private JTextField txtComment;
	  private JComboBox<EnumCondition> cboState;
	  private JComboBox cboLanguage;
	  private JCheckBox cboSigned;
	  private JCheckBox cboFoil;
	  private JSpinner txtQte;
	  
	  private transient MagicCardStock state;
	  private JCheckBox cboAltered;
	
	  
	   
	public CardStockLinePanel(MagicCard selectedCard, MagicCollection selectedCol) {
		
		setBorder(new LineBorder(Color.BLACK, 1, true));
		setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		
		state= new MagicCardStock();
		state.setMagicCard(selectedCard);
		state.setMagicCollection(selectedCol);
		setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		JLabel lblQuantity = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("QTY")+" :");
		add(lblQuantity);
		
		txtQte = new JSpinner();
		txtQte.setModel(new SpinnerNumberModel(0, 0, null, 1));
		add(txtQte);
		
		cboState = new JComboBox(EnumCondition.values());
		add(cboState);
		
		cboLanguage = new JComboBox(MTGControler.getInstance().getEnabledProviders().getLanguages());
		add(cboLanguage);
		
		JLabel lblComment = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("COMMENTS") +" :");
		add(lblComment);
		
		txtComment = new JTextField();
		add(txtComment);
		txtComment.setColumns(20);
		
		cboFoil = new JCheckBox(MTGControler.getInstance().getLangService().getCapitalize("FOIL"));
		add(cboFoil);
		
		cboSigned = new JCheckBox(MTGControler.getInstance().getLangService().getCapitalize("SIGNED"));
		add(cboSigned);
		
		JButton btnNewButton = new JButton("");
		
		Image img = MTGConstants.ICON_DELETE.getImage() ;  
		Image newimg = img.getScaledInstance( 25, 25,  java.awt.Image.SCALE_SMOOTH ) ;  
		
		btnNewButton.setIcon(new ImageIcon(newimg));
		btnNewButton.addActionListener(e->
				delete()
		);
		
		JButton btnSave = new JButton("");

		Image img2 = MTGConstants.ICON_CHECK.getImage() ;  
		Image newimg2 = img2.getScaledInstance( 25, 25,  java.awt.Image.SCALE_SMOOTH ) ;  
		
		btnSave.setIcon(new ImageIcon(newimg2));	
		
		btnSave.addActionListener(ae->{
				
				try {
					generateState();
					MTGControler.getInstance().getEnabledDAO().saveOrUpdateStock(state);
				} catch (SQLException e) {
					MTGLogger.printStackTrace(e);
				}
		});
		
		cboAltered = new JCheckBox(MTGControler.getInstance().getLangService().getCapitalize("ALTERED"));
		add(cboAltered);
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
		state.setAltered(cboAltered.isSelected());
	}
	
	public void delete() {
		
		generateState();
		try {
			List<MagicCardStock> l = new ArrayList<>();
			l.add(state);
			MTGControler.getInstance().getEnabledDAO().deleteStock(l);
		} catch (SQLException e1) {
			MTGLogger.printStackTrace(e1);
		}
		
		try{
			getParent().remove(this);
			getParent().revalidate();
			getParent().repaint();
		}
		catch(NullPointerException e)
		{
			MTGLogger.printStackTrace(e);
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
		cboAltered.setSelected(state.isAltered());
	}
	
	public MagicCardStock getMagicCardState()
	{
		generateState();
		return state;
	}
	

	
	
}
