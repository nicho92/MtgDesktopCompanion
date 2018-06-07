package org.magic.gui.components;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class JResizerPanel extends JPanel {
	
	private JSpinner spinW;
	private JSpinner spinH;
	
	private double ratio = 1.39;
	
	
	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.getContentPane().add(new JResizerPanel());
		f.setVisible(true);
		
	}
	
	public JResizerPanel() {
		init();
	}
	
	
	public JResizerPanel(int w, int h) {
		init();
		
		spinH.setValue(h);
		spinW.setValue(w);
	}
	
	public JResizerPanel(Dimension d) {
		init();
		
		spinH.setValue(d.getHeight());
		spinW.setValue(d.getWidth());
	}
	
	public void init() {
		
		JLabel lblW = new JLabel("W:");
		add(lblW);
		
		spinW = new JSpinner();
		spinW.setPreferredSize(new Dimension(60, 20));
		spinW.addChangeListener(ce-> {
			Integer val = (Integer)spinW.getValue();
			spinH.setValue((val/ratio));
			
		});
		

		
		spinW.setModel(new SpinnerNumberModel(0, 0, null, 1));
		add(spinW);
		
		JLabel lblH = new JLabel("H:");
		add(lblH);
		
		spinH = new JSpinner();
		spinH.setPreferredSize(new Dimension(60, 20));
		spinH.setModel(new SpinnerNumberModel(0, 0, null, 1));
		add(spinH);
		
		spinH.addChangeListener(ce-> {
			Integer val = (Integer)spinH.getValue();
			spinW.setValue((val/ratio));
			
		});
	}

}
