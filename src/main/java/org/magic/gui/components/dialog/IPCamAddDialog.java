package org.magic.gui.components.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.magic.services.MTGConstants;
import org.magic.tools.UITools;
import org.utils.webcam.WebcamUtils;

import com.github.sarxos.webcam.ds.ipcam.IpCamMode;

public class IPCamAddDialog extends JDialog
{
	private static final long serialVersionUID = 1L;
	private JTextField txtName;
	private JTextField txtUrl;
	private JComboBox<IpCamMode> comboBox;
	private boolean hasNew=false;

	public IPCamAddDialog()
	{
		var gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);

		getContentPane().add(new JLabel("Camera Name :"), UITools.createGridBagConstraints(null, null, 0, 1));
		getContentPane().add(new JLabel("Camera URI : "), UITools.createGridBagConstraints(null, null, 0, 2));
		getContentPane().add(new JLabel("Camera Mode :"), UITools.createGridBagConstraints(null, null, 0, 3));


		txtName = new JTextField(10);
		getContentPane().add(txtName, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 1));

		txtUrl = new JTextField(10);
		getContentPane().add(txtUrl, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 2));

		comboBox = UITools.createCombobox(IpCamMode.values());
		getContentPane().add(comboBox, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 3));


		var btnAdd = new JButton(MTGConstants.ICON_CHECK);
		var btnCancel = new JButton(MTGConstants.ICON_DELETE);
		var panel = new JPanel();
		       panel.add(btnAdd);
		       panel.add(btnCancel);
		getContentPane().add(panel, UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 1, 4));


		setLocationRelativeTo(null);
		pack();


		btnAdd.addActionListener(al->{

			if(!txtName.getText().isEmpty() && !txtUrl.getText().isEmpty())
			{
				WebcamUtils.inst().registerIPCam(txtName.getText(),txtUrl.getText(),(IpCamMode)comboBox.getSelectedItem());
				hasNew=true;
				dispose();
			}

		});

		btnCancel.addActionListener(al->dispose());

	}

	public boolean isHasNew() {
		return hasNew;
	}

}
