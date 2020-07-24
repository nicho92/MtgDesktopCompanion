package org.magic.gui.components;

import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.enums.MTGColor;
import org.magic.api.beans.enums.MTGLayout;
import org.magic.api.beans.enums.MTGRarity;
import org.magic.api.criterias.CardAttribute;
import org.magic.api.criterias.MTGCrit;
import org.magic.api.criterias.MTGCrit.OPERATOR;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.services.MTGControler;
import org.magic.tools.UITools;


public class CriteriaComponent extends JComponent{
	


	private static final long serialVersionUID = 1L;

	private JComponent selector;
	
	private CardAttribute c;
	private transient Object val;
	private JComboBox<OPERATOR> cboOperator;
	
	public CriteriaComponent() {
		initGui();
	}

	private void initGui() {
		
		setLayout(new FlowLayout(FlowLayout.LEFT));
		
		JComboBox<CardAttribute> cboAttributes = UITools.createCombobox(MTGControler.getInstance().getEnabled(MTGCardsProvider.class).getQueryableAttributs());
		cboOperator = UITools.createCombobox(MTGCrit.OPERATOR.values());
			
		
		c=new CardAttribute("name", String.class);
		selector = getComponentFor(c); 
	
		add(cboAttributes);
		add(cboOperator);
		add(selector);
		
		
		cboAttributes.addItemListener(il->{
			if(il.getStateChange() == ItemEvent.SELECTED)
			{
				
				remove(selector);
				c = ((CardAttribute)cboAttributes.getSelectedItem());
				
				SwingUtilities.invokeLater(()->{
					selector = getComponentFor(c);
					add(selector);
					revalidate();
					repaint();
				});
				
			
			}
		});
			
		
	}

	private JComponent getComponentFor(CardAttribute c) {
		
	
		
		if(c.getType() == Integer.class || c.getType() == Float.class)
		{
			JSpinner s= new JSpinner(new SpinnerNumberModel(0,0,100,1));
			s.setValue(0);
			s.addChangeListener(l->val = s.getValue());
			return s;
		}
	
		if(c.getType() == Boolean.class)
		{
			JCheckBox ch = new JCheckBox();
			ch.addItemListener(l->val=ch.isSelected());
			return ch;
		}
		
		if(c.getType() == MagicEdition.class)
			return init(UITools.createComboboxEditions());
		
		if(c.getType() == MagicCollection.class)
			return init(UITools.createComboboxCollection());
		
		if(c.getType() == MTGColor.class)
			return init(UITools.createCombobox(MTGColor.values()));
		
		if(c.getType() == MTGLayout.class)
			return init(UITools.createCombobox(MTGLayout.values()));
		
		if(c.getType() == MTGRarity.class)
			return init(UITools.createCombobox(MTGRarity.values()));
		
		//else
		
		JTextField f= new JTextField(25);
		f.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				val=f.getText();
			}
		});
		return f;
	}
	
	private JComponent init(JComboBox<?> createCombobox) {
		createCombobox.addItemListener(il->{
			if(il.getStateChange() == ItemEvent.SELECTED)
				val=createCombobox.getSelectedItem();
		});
		
		return createCombobox;
	}

	public MTGCrit<?> getMTGCriteria(){
		return new MTGCrit<>(c, OPERATOR.valueOf(cboOperator.getSelectedItem().toString()), val);
	}

	public void addDeletableButton(JButton delete) {
		add(delete,null,0);
		
	}
	
}
