package org.magic.gui.components.dialog.importer;

import java.util.List;

import javax.swing.JComponent;

import org.magic.api.beans.MTGSealedProduct;
import org.magic.gui.abstracts.AbstractDelegatedImporterDialog;
import org.magic.gui.components.PackagesBrowserPanel;

public class SealedStockChooseDialog extends AbstractDelegatedImporterDialog<MTGSealedProduct> {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private PackagesBrowserPanel packagePanel;

	@Override
	public JComponent getSelectComponent() {
		packagePanel=  new PackagesBrowserPanel(true);
		
		return packagePanel;
	}
	@Override
	public List<MTGSealedProduct> getSelectedItems() {
		return packagePanel.getSelecteds();
	}

}
