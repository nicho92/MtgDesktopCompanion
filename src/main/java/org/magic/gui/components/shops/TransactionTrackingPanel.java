package org.magic.gui.components.shops;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.magic.api.beans.shop.Transaction;
import org.magic.api.interfaces.MTGTrackingService;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.MTGConstants;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;

public class TransactionTrackingPanel extends MTGUIComponent {
	
	private static final long serialVersionUID = 1L;

	private Transaction transaction;

	private JTextField textField ;
	private JTextArea textArea;
	private JComboBox<MTGTrackingService> comboBox;
	
	
	public TransactionTrackingPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {0, 168, 2};
		gridBagLayout.rowHeights = new int[] {0, 0, 0, 0, 2};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		

		add(new JLabel("Tracking Service : "), UITools.createGridBagConstraints(null, null, 0,1));
		add(new JLabel("Tracking Number :"), UITools.createGridBagConstraints(null, null, 0,2));

		
		comboBox = UITools.createComboboxPlugins(MTGTrackingService.class,false);
		add(comboBox, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1,1));

		textField = new JTextField();
		add(textField, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1,2));
		
		var btnTrack = new JButton("Track");
		add(btnTrack, UITools.createGridBagConstraints(null,null, 0,3));

		textArea = new JTextArea();
		add(new JScrollPane(textArea), UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 1,3));
		
		
		
		btnTrack.addActionListener(al->{
			textArea.setText("");
			try {
				var t = ((MTGTrackingService)comboBox.getSelectedItem()).track(transaction.getTransporterShippingCode());
				
				t.getSteps().forEach(ts->{
					textArea.append(UITools.formatDateTime(ts.getDateStep()));
					textArea.append(" : ");
					textArea.append(ts.getDescriptionStep());
					textArea.append("\n");
				});
				
			} catch (IOException e) {
				logger.error(e);
			}
			
		});
		
	}

	@Override
	public String getTitle() {
		return "TRACKING";
	}
	
	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_TAB_DELIVERY;
	}

	public void init(Transaction transaction) {
		this.transaction = transaction;
		textArea.setText("");
		textField.setText(transaction.getTransporterShippingCode());
		
		try {
			
			if(transaction.getTransporter()!=null)
				comboBox.setSelectedItem(MTG.getPlugin(transaction.getTransporter(), MTGTrackingService.class));
		}
		catch(Exception e)
		{
			logger.error("No tracking service found for {}",transaction.getTransporter());
		}
		
	}

	
	
	
}
