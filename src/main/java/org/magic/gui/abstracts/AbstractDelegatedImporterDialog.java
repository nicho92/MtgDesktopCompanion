package org.magic.gui.abstracts;

import static org.magic.services.tools.MTG.capitalize;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.threads.MTGRunnable;
import org.magic.services.threads.ThreadManager;

public abstract class AbstractDelegatedImporterDialog<T> extends JDialog {

	private static final long serialVersionUID = 1L;
	private transient List<T> selectedItem;
	protected JPanel commandePanel;
	private boolean selected=false;
	
	
	public T getSelectedItem()
	{
		if(selectedItem.isEmpty())
			return null;
		
		return selectedItem.get(0);
	}
	
	public List<T> getSelectedItems()
	{
		return selectedItem;
	}

	public void setSelectedItem(List<T> selectedItem) {
		this.selectedItem = selectedItem;
	}
	
	public boolean hasSelected()
	{
		return selected;
	}
	
	public void onDestroy()
	{
		
	}
	
	
	
	public abstract JComponent getSelectComponent();
	
	
	protected AbstractDelegatedImporterDialog() {
		
		selectedItem= new ArrayList<>();
		
		setSize(new Dimension(354, 295));
		setIconImage(MTGConstants.ICON_IMPORT.getImage());
		setModal(true);
		setLocationRelativeTo(null);

		getContentPane().setLayout(new BorderLayout(0, 0));
		
		ThreadManager.getInstance().invokeLater(new MTGRunnable() {
			
			@Override
			protected void auditedRun() {
				getContentPane().add(getSelectComponent(),BorderLayout.CENTER);	
				pack();
			}
		},"create chooser dialog");
		
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				selectedItem.clear();
				onDestroy();
			}

			@Override
			public void windowClosing(WindowEvent e) {
				selectedItem.clear();
				onDestroy();
			}
		});

		
		commandePanel = new JPanel();
		getContentPane().add(commandePanel, BorderLayout.SOUTH);

		
		
		var btnSelect = new JButton(MTGConstants.ICON_OPEN);
		btnSelect.setToolTipText(capitalize("OPEN"));
		btnSelect.addActionListener(_ -> {
			if (getSelectedItem()==null && getSelectedItems().isEmpty())
			{
				selected=false;
				MTGControler.getInstance().notify(new NullPointerException(capitalize("CHOOSE_ITEM")));
			}
			else
			{
				selected=true;
				dispose();
			}
		});
		

		var btnCancel = new JButton(MTGConstants.ICON_CANCEL);
		btnCancel.setToolTipText(capitalize("CANCEL"));
		btnCancel.addActionListener(_ -> {
			selectedItem.clear();
			selected=false;
			dispose();
		});
		
		commandePanel.add(btnSelect);
		commandePanel.add(btnCancel);
		
		validate();

	}
	
	


}
