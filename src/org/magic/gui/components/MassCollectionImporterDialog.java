package org.magic.gui.components;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MagicCardsProvider;
import org.magic.db.MagicDAO;

import com.itextpdf.text.log.Logger;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JProgressBar;
import java.awt.Dimension;
import javax.swing.JLabel;

public class MassCollectionImporterDialog extends JDialog{
	
	private MagicCardsProvider provider;
	private MagicDAO dao;
	private List<MagicEdition> list;

	public MassCollectionImporterDialog(MagicDAO dao,MagicCardsProvider provider,List<MagicEdition> list) {
		setSize(new Dimension(500, 290));
		setTitle("Mass Card Importer");
		
		this.dao=dao;
		this.provider=provider;
		this.list=list;
		
		try {
			initGUI();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initGUI() throws Exception {
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel panelCollectionInput = new JPanel();
		getContentPane().add(panelCollectionInput, BorderLayout.NORTH);
		
		JLabel lblImport = new JLabel("import ");
		panelCollectionInput.add(lblImport);
		
		JComboBox cboEditions = new JComboBox(list.toArray());
		panelCollectionInput.add(cboEditions);
		
		List lc = dao.getCollections();
		
		JLabel lblIn = new JLabel("in");
		panelCollectionInput.add(lblIn);
		JComboBox cboCollections = new JComboBox(lc.toArray());
		panelCollectionInput.add(cboCollections);
		
		JLabel lblThisNumber = new JLabel(" this number : ");
		panelCollectionInput.add(lblThisNumber);
		
		JPanel panneauBas = new JPanel();
		getContentPane().add(panneauBas, BorderLayout.SOUTH);
		
		JTextPane txtNumbersInput = new JTextPane();
		getContentPane().add(txtNumbersInput, BorderLayout.CENTER);
		JProgressBar progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		
		JCheckBox checkNewOne = new JCheckBox("import a serie after this one");
		
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
				MagicEdition ed = (MagicEdition)cboEditions.getSelectedItem();
				MagicCollection col = (MagicCollection)cboCollections.getSelectedItem();
				String[] ids = txtNumbersInput.getText().replaceAll("\n", " ").replaceAll("  ", " ").trim().split(" ");
				progressBar.setMaximum(ids.length);
				
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						int i=1;
						for(String id : ids)
						{
							try {
								MagicCard mc = provider.getCardByNumber(id, ed);
								dao.saveCard(mc, col);
								progressBar.setValue(i++);
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}
						
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
