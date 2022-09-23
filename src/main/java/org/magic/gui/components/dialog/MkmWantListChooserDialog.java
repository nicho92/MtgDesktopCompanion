package org.magic.gui.components.dialog;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.api.mkm.modele.Wantslist;
import org.api.mkm.services.WantsService;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.tools.UITools;

public class MkmWantListChooserDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private transient WantsService serv;
	private JComboBox<Wantslist> cboWants ;

	public MkmWantListChooserDialog() {
		serv = new WantsService();
		setLayout(new BorderLayout());
		setTitle("Mkm - WantList");
		setLocationRelativeTo(null);
		setModal(true);

		try {

			var pane = new JPanel();
			cboWants = UITools.createCombobox(serv.getWantList());
			var btnNewWantList = new JButton(MTGConstants.ICON_NEW);
			var btnOK = new JButton(MTGConstants.ICON_CHECK);
			var btnCancel = new JButton(MTGConstants.ICON_CANCEL);

			pane.add(btnNewWantList);
			pane.add(btnOK);
			pane.add(btnCancel);

			getContentPane().add(cboWants,BorderLayout.CENTER);
			getContentPane().add(pane,BorderLayout.SOUTH);


			pack();




			btnNewWantList.addActionListener(l->{
				String name = JOptionPane.showInputDialog("Want List Name ? ");
				try {
					Wantslist created = serv.createWantList(name);
					cboWants.addItem(created);
					cboWants.setSelectedItem(created);
				} catch (IOException e) {
					MTGControler.getInstance().notify(e);
				}
			});

			btnOK.addActionListener(l->dispose());
			btnCancel.addActionListener(l->{
				cboWants.setSelectedItem(null);
				dispose();
			});


		} catch (IOException e) {
			MTGControler.getInstance().notify(e);
		}
	}


	public Wantslist getSelectedWantList()
	{

		if(cboWants.getSelectedItem()!=null)
			return (Wantslist)cboWants.getSelectedItem();

		return null;
	}


}
