package org.magic.gui.components.widgets;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.magic.services.MTGConstants;

public class JResizerPanel extends JComponent {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private JLabel lblDimension;
	private Dimension dimension;
	private JSpinner spinner;

	public JResizerPanel(Dimension d) {
		dimension=d;
		init();
	}

	public Dimension getDimension() {
		return dimension;
	}



	private void init() {
		setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		spinner = new JSpinner();
		spinner.setPreferredSize(new Dimension(60, 20));
		spinner.setModel(new SpinnerNumberModel(0, null, null, 1));
		add(spinner);
		lblDimension = new JLabel("");
		add(lblDimension);

		update();
		spinner.addChangeListener(ce-> {
			Number val = (Number)spinner.getValue();
			int w = (int) (dimension.getWidth()+val.intValue());
			int h = (int) (w*MTGConstants.CARD_PICS_RATIO);
			dimension.setSize(w, h);
			update();
		});
	}


	private void update()
	{
		lblDimension.setText((int)dimension.getWidth()+"x"+(int)dimension.getHeight());
	}

	public void setValue(int i) {
		spinner.setValue(i);
	}

}
