package org.magic.gui.components.prices;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.magic.api.interfaces.MTGDashBoard;
import org.magic.api.interfaces.MTGPriceSuggester;
import org.magic.api.interfaces.MTGPricesProvider;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.MTGConstants;
import org.magic.services.tools.UITools;

public class PriceSuggesterComponent extends MTGUIComponent {

	private static final long serialVersionUID = 1L;

	JRadioButton rdoPricer;
	JRadioButton rdoDashBoard;
	JComboBox<MTGPricesProvider> cboPricer;
	JComboBox<MTGDashBoard> cboDashBoard;
	JButton btnValidate;

	public PriceSuggesterComponent() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		var panel1 = new JPanel();
		var panel2 = new JPanel();
		var panel3 = new JPanel();

		btnValidate = new JButton(MTGConstants.ICON_OPEN);

		rdoDashBoard = new JRadioButton("DashBoard");
		cboDashBoard = UITools.createComboboxPlugins(MTGDashBoard.class,true);
		rdoPricer = new JRadioButton("Pricer");
		cboPricer = UITools.createComboboxPlugins(MTGPricesProvider.class,false);


		var group = new ButtonGroup();
					group.add(rdoDashBoard);
					group.add(rdoPricer);


		panel1.add(rdoDashBoard);
		panel1.add(cboDashBoard);
		panel2.add(rdoPricer);
		panel2.add(cboPricer);
		panel3.add(btnValidate);


		add(panel1);
		add(panel2);
		add(panel3);



		rdoPricer.addItemListener(i->{
			cboDashBoard.setEnabled(!rdoPricer.isEnabled());
			cboPricer.setEnabled(rdoPricer.isEnabled());

		});

		rdoDashBoard.addItemListener(i->{
			cboPricer.setEnabled(!rdoDashBoard.isEnabled());
			cboDashBoard.setEnabled(rdoDashBoard.isEnabled());
		});


		rdoDashBoard.doClick();
	}


	public JButton getBtnValidate() {
		return btnValidate;
	}


	public MTGPriceSuggester getSelectedPlugin()
	{
		if(rdoDashBoard.isSelected())
			return (MTGDashBoard) cboDashBoard.getSelectedItem();
		else
			return (MTGPricesProvider)cboPricer.getSelectedItem();
	}

	@Override
	public String getTitle() {
		return "Prices Suggester";
	}


	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_DOLLARS;
	}

}
