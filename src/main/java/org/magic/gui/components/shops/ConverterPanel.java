package org.magic.gui.components.shops;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.ConverterItem;
import org.magic.gui.abstracts.GenericTableModel;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.MTGConstants;
import org.magic.services.providers.StockItemConversionManager;
import org.magic.tools.UITools;

public class ConverterPanel extends MTGUIComponent{
	
	private static final long serialVersionUID = 1L;
	private GenericTableModel<ConverterItem> model;
	private JXTable table;
	
	
	public ConverterPanel() {
		setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		
		JButton btnReload = UITools.createBindableJButton("Reload", MTGConstants.ICON_REFRESH, KeyEvent.VK_R, "Reload");
		
		model = new GenericTableModel<>();
		table = UITools.createNewTable(model);
		UITools.initTableFilter(table);
		
		model.setColumns("name","inputId","outputId","lang","source","destination");
		model.init(StockItemConversionManager.inst().getConversionsItems());
		
		panel.add(btnReload);
		add(panel, BorderLayout.NORTH);
		add(new JScrollPane(table), BorderLayout.CENTER);
		
		
		btnReload.addActionListener(el->model.init(StockItemConversionManager.inst().getConversionsItems()));
	}
	
	
	public static void main(String[] args) {
		
		
		JFrame f = new JFrame();
		f.getContentPane().add(new ConverterPanel());
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}


	@Override
	public String getTitle() {
		return "Conversions";
	}

}
