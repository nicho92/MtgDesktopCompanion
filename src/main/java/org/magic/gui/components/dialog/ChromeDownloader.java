package org.magic.gui.components.dialog;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.apache.logging.log4j.Logger;
import org.magic.api.beans.technical.MTGNotification;
import org.magic.api.beans.technical.MTGNotification.MESSAGE_TYPE;
import org.magic.gui.components.widgets.JTextFieldFileChooser;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.logging.MTGLogger;
import org.magic.services.tools.FileTools;

public class ChromeDownloader extends JDialog {

	private transient Logger logger = MTGLogger.getLogger(this.getClass());

	public ChromeDownloader() {

		setTitle("Chrome Plugin");
		setIconImage(MTGConstants.ICON_CHROME.getImage());
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);

		var panelCenter = new JPanel();
		getContentPane().add(panelCenter, BorderLayout.CENTER);
		txtDirectory = new JTextFieldFileChooser(30,JFileChooser.DIRECTORIES_ONLY,System.getProperty("user.home"));
		panelCenter.add(txtDirectory);

		var panelButtons = new JPanel();
		getContentPane().add(panelButtons, BorderLayout.SOUTH);

		var btnCancel = new JButton(MTGConstants.ICON_DELETE);
		btnCancel.addActionListener(_->dispose());

		panelButtons.add(btnCancel);

		var btnExport = new JButton(MTGConstants.ICON_EXPORT);
		btnExport.addActionListener(_-> {
			try {
				FileTools.copyDirJarToDirectory(MTGConstants.MTG_CHROME_PLUGIN_DIR, txtDirectory.getFile());
				MTGControler.getInstance().notify(new MTGNotification("Export", "Plugin copied in " + txtDirectory.getFile(), MESSAGE_TYPE.INFO));
				dispose();
			} catch (Exception e1) {
				logger.error("error extracting files ",e1);
				MTGControler.getInstance().notify(e1);
			}

		});
		panelButtons.add(btnExport);

		pack();
	}

	private static final long serialVersionUID = 1L;
	private JTextFieldFileChooser txtDirectory;

}
