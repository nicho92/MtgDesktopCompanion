package org.magic.gui.components.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;

import org.magic.api.beans.MTGSealedProduct;
import org.magic.gui.components.PackagesBrowserPanel;
import org.magic.services.MTGConstants;
import org.magic.services.threads.ThreadManager;

public class SealedImportDialog extends JDialog {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private JButton btnSelectionn;
	private PackagesBrowserPanel packagePanel;

	public SealedImportDialog() {
		getContentPane().setLayout(new BorderLayout(0, 0));
		setModal(true);
		setIconImage(MTGConstants.ICON_SEARCH.getImage());
		btnSelectionn = new JButton(MTGConstants.ICON_IMPORT);
		packagePanel = new PackagesBrowserPanel(true);
		btnSelectionn.addActionListener(e -> dispose());

		getContentPane().add(packagePanel, BorderLayout.CENTER);
		getContentPane().add(btnSelectionn, BorderLayout.SOUTH);

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		
		
		
		SwingWorker<Void, Void> sw  = new SwingWorker<>(){
			@Override
			protected Void doInBackground() throws Exception {
				packagePanel.initTree();
				return null;
			}
			@Override
			protected void done() {
				packagePanel.reload();
			}
		};
		ThreadManager.getInstance().runInEdt(sw, "Loading sealed tree");
		
		setPreferredSize(new Dimension(800,600));
		pack();
		
		
	}

	public MTGSealedProduct getSelected() {
		return packagePanel.getSelected();
	}

}
