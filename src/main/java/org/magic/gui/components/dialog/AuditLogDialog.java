package org.magic.gui.components.dialog;

import java.awt.GridLayout;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JDialog;

import org.magic.services.MTGControler;

import javax.swing.JButton;

public class AuditLogDialog extends JDialog {
	public AuditLogDialog() {
		
		getContentPane().setLayout(new GridLayout(5, 2, 0, 0));
		
		var chkjsonqueryinfo = new JCheckBox("jsonqueryinfo");
		getContentPane().add(chkjsonqueryinfo);
		
		var chdaoinfo = new JCheckBox("daoinfo");
		getContentPane().add(chdaoinfo);
		
		var chctaskinfo = new JCheckBox("taskinfo");
		getContentPane().add(chctaskinfo);
		
		var chknetworkinfo = new JCheckBox("networkinfo");
		getContentPane().add(chknetworkinfo);
		
		var chkdiscordinfo = new JCheckBox("discordinfo");
		getContentPane().add(chkdiscordinfo);
		
		var chkfileaccessinfo = new JCheckBox("fileaccessinfo");
		getContentPane().add(chkfileaccessinfo);
		
		var chktalkmessage = new JCheckBox("talkmessage");
		getContentPane().add(chktalkmessage);
		
		var btnOk = new JButton("OK");
		getContentPane().add(btnOk);
		
		
		for (var comp : List.of(chkjsonqueryinfo,chdaoinfo,chctaskinfo,chknetworkinfo,chkdiscordinfo,chkfileaccessinfo,chktalkmessage))
		{
			comp.setSelected(MTGControler.getInstance().get("technical-log/conf/"+comp.getText()).equals("true"));
			comp.addActionListener(_->MTGControler.getInstance().setProperty("technical-log/conf/"+comp.getText(), String.valueOf(comp.isSelected())));
		}
		
		
		btnOk.addActionListener(_->dispose());
		
		setLocationRelativeTo(null);
		pack();
	}
	
	private static final long serialVersionUID = 1L;


}
