package org.magic.gui.components;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.magic.api.interfaces.MTGDashBoard;
import org.magic.api.interfaces.MTGPricesProvider;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.MTGControler;
import org.magic.tools.UITools;

public class PriceCheckerComponent extends MTGUIComponent {
	public PriceCheckerComponent() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel panel1 = new JPanel();
		add(panel1);
		
		JRadioButton rdoDashBoard = new JRadioButton("DashBoard");
		panel1.add(rdoDashBoard);
		
		JComboBox<MTGDashBoard> cboDashBoard = UITools.createCombobox(MTGDashBoard.class,false);
		panel1.add(cboDashBoard);
		
		JPanel panel2 = new JPanel();
		add(panel2);
		
		JRadioButton rdoPricer = new JRadioButton("Pricer");
		panel2.add(rdoPricer);
		
		JComboBox<MTGPricesProvider> cboPricer = UITools.createCombobox(MTGPricesProvider.class,false);
		panel2.add(cboPricer);
	}

	
	
	
	public static void main(String[] args) {
		MTGControler.getInstance();
		MTGUIComponent.createJDialog(new PriceCheckerComponent(), false, false).setVisible(true);

	}




	@Override
	public String getTitle() {
		return "Get Price";
	}

}
