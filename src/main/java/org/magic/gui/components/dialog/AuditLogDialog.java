package org.magic.gui.components.dialog;

import java.awt.GridLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;

import org.magic.services.MTGControler;

public class AuditLogDialog extends JDialog {
	public AuditLogDialog() {

		getContentPane().setLayout(new GridLayout(5, 2, 0, 0));

		final var chkjsonqueryinfo = new JCheckBox("jsonqueryinfo");
		getContentPane().add(chkjsonqueryinfo);

		final var chdaoinfo = new JCheckBox("daoinfo");
		getContentPane().add(chdaoinfo);

		final var chctaskinfo = new JCheckBox("taskinfo");
		getContentPane().add(chctaskinfo);

		final var chknetworkinfo = new JCheckBox("networkinfo");
		getContentPane().add(chknetworkinfo);

		final var chkdiscordinfo = new JCheckBox("discordinfo");
		getContentPane().add(chkdiscordinfo);

		final var chkfileaccessinfo = new JCheckBox("fileaccessinfo");
		getContentPane().add(chkfileaccessinfo);

		final var chktalkmessage = new JCheckBox("talkmessage");
		getContentPane().add(chktalkmessage);

		final var btnOk = new JButton("OK");
		getContentPane().add(btnOk);

		for (final var comp : List.of(chkjsonqueryinfo, chdaoinfo, chctaskinfo, chknetworkinfo, chkdiscordinfo,
				chkfileaccessinfo, chktalkmessage)) {
			comp.setSelected(MTGControler.getInstance().get("technical-log/conf/" + comp.getText()).equals("true"));
			comp.addActionListener(_ -> MTGControler.getInstance().setProperty("technical-log/conf/" + comp.getText(),
					String.valueOf(comp.isSelected())));
		}

		btnOk.addActionListener(_ -> dispose());

		setLocationRelativeTo(null);
		pack();
	}

	private static final long serialVersionUID = 1L;

}
