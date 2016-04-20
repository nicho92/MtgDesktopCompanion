package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextPane;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MagicCardsProvider;
import org.magic.api.interfaces.MagicDAO;
import javax.swing.DefaultComboBoxModel;

public class MassCollectionImporterDialog extends JDialog{
	
	private MagicCardsProvider provider;
	private MagicDAO dao;
	private List<MagicEdition> list;
	private String[] ids;
	
	
	public MassCollectionImporterDialog(MagicDAO dao,MagicCardsProvider provider,List<MagicEdition> list) {
		setSize(new Dimension(646, 290));
		setTitle("Mass Cards Importer");
		
		this.dao=dao;
		this.provider=provider;
		this.list=list;
		
		try {
			initGUI();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initGUI() throws Exception {
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel panelCollectionInput = new JPanel();
		getContentPane().add(panelCollectionInput, BorderLayout.NORTH);
		
		JLabel lblImport = new JLabel("import ");
		panelCollectionInput.add(lblImport);
		
		final JComboBox cboEditions = new JComboBox(list.toArray());
		panelCollectionInput.add(cboEditions);
		
		List lc = dao.getCollections();
		
		JLabel lblNewLabel = new JLabel("by");
		panelCollectionInput.add(lblNewLabel);
		
		final JComboBox cboByType = new JComboBox();
		cboByType.setModel(new DefaultComboBoxModel(new String[] {"number", "name"}));
		panelCollectionInput.add(cboByType);
		
		JLabel lblIn = new JLabel("in");
		panelCollectionInput.add(lblIn);
		final JComboBox cboCollections = new JComboBox(lc.toArray());
		panelCollectionInput.add(cboCollections);
		
		JPanel panneauBas = new JPanel();
		getContentPane().add(panneauBas, BorderLayout.SOUTH);
		
		final JTextPane txtNumbersInput = new JTextPane();
		getContentPane().add(txtNumbersInput, BorderLayout.CENTER);
		final JProgressBar progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		
		final JCheckBox checkNewOne = new JCheckBox("import a serie after this one");
		
		JButton btnInverse = new JButton("Inverse");
		btnInverse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				MagicEdition ed = (MagicEdition)cboEditions.getSelectedItem();
				int max = ed.getCardCount();
				List<String> ids = Arrays.asList(txtNumbersInput.getText().replaceAll("\n", " ").replaceAll("  ", " ").trim().split(" "));
				List<String> edList = new ArrayList<>();
				for(int i=1;i<=max;i++)
					edList.add(String.valueOf(i));
					
				edList.removeAll(ids);
				
				StringBuffer temp = new StringBuffer();
				for(String s : edList)
					temp.append(s).append(" ");
				
				txtNumbersInput.setText(temp.toString());
				
			}
		});
		panneauBas.add(btnInverse);
		
		
		
		
		panneauBas.add(checkNewOne);
		
		
		JButton btnImport = new JButton("Import");
		btnImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final MagicEdition ed = (MagicEdition)cboEditions.getSelectedItem();
				final MagicCollection col = (MagicCollection)cboCollections.getSelectedItem();
				
				if(cboByType.getSelectedItem().equals("number"))
					ids = txtNumbersInput.getText().replaceAll("\n", " ").replaceAll("  ", " ").trim().split(" ");
				else
					ids = txtNumbersInput.getText().split("\n");
				progressBar.setMaximum(ids.length);
				
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						int i=1;
						for(String id : ids)
						{
							try {
								MagicCard mc = null;
								
								if(cboByType.getSelectedItem().equals("number"))
									mc=provider.getCardByNumber(id, ed);
								else
									mc=provider.searchCardByCriteria("name", id.replaceAll("\n", " ").replaceAll("  ", " ").trim(),(MagicEdition)cboEditions.getSelectedItem()).get(0);
								
								
								dao.saveCard(mc, col);
								progressBar.setValue(i++);
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}
						JOptionPane.showMessageDialog(null, "Finished import "+ ids.length +" cards","Import",JOptionPane.INFORMATION_MESSAGE);
						if(!checkNewOne.isSelected())
						{
							setVisible(false);
							progressBar.setValue(0);
						}
						
					}
				}).start();
				
				
			}
		});
		panneauBas.add(btnImport);
		
		panneauBas.add(progressBar);
		
		
		setModal(true);
		
		
	}
	
	

}
