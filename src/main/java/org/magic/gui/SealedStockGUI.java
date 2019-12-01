package org.magic.gui;

import java.awt.BorderLayout;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.Packaging;
import org.magic.api.beans.SealedStock;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.PackagesBrowserPanel;
import org.magic.gui.models.SealedStockModel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;

public class SealedStockGUI extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private PackagesBrowserPanel packagePanel;
	private SealedStockModel model;
	private Packaging selectedItem;
	
	public SealedStockGUI() {
		initGUI();
	}
	
	
	
	
	private void initGUI() {
		
		setLayout(new BorderLayout());
		
		
		model = new SealedStockModel();
		packagePanel = new PackagesBrowserPanel(false);
		JPanel toolsPanel = new JPanel();
		
		
		JButton buttonNew = new JButton(MTGConstants.ICON_NEW);
		JButton buttonDelete = new JButton(MTGConstants.ICON_DELETE);
		JButton buttonUpdate = new JButton(MTGConstants.ICON_REFRESH);
		
		toolsPanel.add(buttonNew);
		toolsPanel.add(buttonDelete);
		toolsPanel.add(buttonUpdate);
		
		add(packagePanel,BorderLayout.WEST);
		add(new JScrollPane(new JXTable(model)),BorderLayout.CENTER);
		add(toolsPanel,BorderLayout.NORTH);
		
		
		
		packagePanel.getTree().addTreeSelectionListener(e-> {
			
			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)packagePanel.getTree().getLastSelectedPathComponent();
			if(selectedNode!=null && (selectedNode.getUserObject() instanceof Packaging))
			{
				selectedItem = (Packaging)selectedNode.getUserObject();
			}
		});
		
		buttonNew.addActionListener(el->{
			try {
				
				SealedStock s = new SealedStock(selectedItem,1);
				MTGControler.getInstance().getEnabled(MTGDao.class).saveOrUpdateStock(s);
				model.addItem(s);
			} catch (SQLException e) {
				MTGControler.getInstance().notify(e);
			}
		});
		
		
		
	}




	@Override
	public String getTitle() {
		return MTGControler.getInstance().getLangService().getCapitalize("PACKAGES");
	}

	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_PACKAGE_SMALL;
	}
	
	@Override
	public void onFirstShowing() {
		packagePanel.initTree();
		
		try {
			model.init(MTGControler.getInstance().getEnabled(MTGDao.class).listSeleadStocks());
		} catch (SQLException e) {
			MTGControler.getInstance().notify(e);
		}
	}
}
