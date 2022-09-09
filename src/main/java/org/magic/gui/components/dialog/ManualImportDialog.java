package org.magic.gui.components.dialog;

import static org.magic.tools.MTG.capitalize;
import static org.magic.tools.MTG.getEnabledPlugin;
import static org.magic.tools.MTG.getPlugin;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;

import org.apache.logging.log4j.Logger;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.MTGCardsIndexer;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.AbstractDelegatedImporterDialog;
import org.magic.gui.components.editor.JTagsPanel;
import org.magic.services.MTGConstants;
import org.magic.services.logging.MTGLogger;
import org.magic.services.threads.ThreadManager;
import org.magic.services.workers.DeckImportWorker;
public class ManualImportDialog extends AbstractDelegatedImporterDialog {

	
	private static final long serialVersionUID = 1L;
	private JTextPane editorPane;
	private JTagsPanel tagsPanel;
	private int start;
	private int position;
	private AbstractBuzyIndicatorComponent lblLoading;
	private MagicDeck importedDeck;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());

	
	
	public String getStringDeck() {
		return editorPane.getText();
	}

	public ManualImportDialog() {
		getContentPane().setLayout(new BorderLayout(0, 0));
		setSize(new Dimension(400, 400));
		setTitle(capitalize("MANUAL_IMPORT"));
		setIconImage(MTGConstants.ICON_TAB_IMPORT.getImage());
		setModal(true);
		var panel = new JPanel();
		tagsPanel = new JTagsPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);

		var btnImport = new JButton(MTGConstants.ICON_SAVE);
		btnImport.setToolTipText(capitalize("IMPORT"));
		panel.add(btnImport);

		var btnCancel = new JButton(MTGConstants.ICON_CANCEL);
		btnCancel.setToolTipText(capitalize("CANCEL"));
		btnCancel.addActionListener(e -> {
			editorPane.setText("");
			dispose();
		});
		panel.add(btnCancel);
		
		lblLoading = AbstractBuzyIndicatorComponent.createLabelComponent();
		panel.add(lblLoading);

		var lblPastYourDeck = new JLabel(capitalize("IMPORT_HELP"));
		
		getContentPane().add(lblPastYourDeck, BorderLayout.NORTH);
		
		var panelCenter = new JPanel();
		getContentPane().add(panelCenter, BorderLayout.CENTER);
		panelCenter.setLayout(new BorderLayout(0, 0));
		editorPane = new JTextPane();
		
		
	
		
		editorPane.setPreferredSize(new Dimension(106, 300));
		panelCenter.add(new JScrollPane(editorPane));
		panelCenter.add(tagsPanel, BorderLayout.SOUTH);
		setLocationRelativeTo(null);
		
		tagsPanel.setEditable(false);
		tagsPanel.setFontSize(10);
		tagsPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				var name = tagsPanel.getTagAt(e.getPoint());
				try {
					editorPane.getDocument().remove(start,position-start);
					editorPane.getDocument().insertString(start, " "+name, null);
					editorPane.requestFocus();
					var r = new Robot();
					r.keyPress(KeyEvent.VK_ENTER);
					start=0;
					
				} catch (BadLocationException e1) {
					logger.error("error editing at s:" +start +" e:"+(position-start),e1);
				} catch (AWTException e1) {
					logger.error("Error loading key enter",e1);
				}
				
			} 
		});
		
		editorPane.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent ke) {
				position = editorPane.getCaretPosition();
				try {
					
					if(ke.getKeyCode()==KeyEvent.VK_SPACE && start<=0)
						start=position-1;
					
					if(ke.getKeyCode()==KeyEvent.VK_ENTER)
						start=0;
					
					
					
					if(start>=0)
					{
						String currentName=editorPane.getText(start, (position-start)).trim();
						if(currentName.length()>=3)
						{
							tagsPanel.bind(getEnabledPlugin(MTGCardsIndexer.class).suggestCardName(currentName));
						}
					
					}
				}
				catch(Exception e)
				{
					logger.error("error"+e);
				}
				
			}
		});
		
		btnImport.addActionListener(e ->{
			
			DeckImportWorker sw = new DeckImportWorker(getPlugin(MTGConstants.MANUAL_IMPORT_SYNTAX, MTGCardsExport.class), lblLoading,null)
										{
			
											@Override
											protected MagicDeck doInBackground() {
												
												try {
													importedDeck= exp.importDeck(editorPane.getText(),"manual");
												} catch (Exception e) {
													err=e;
													logger.error("error export with " + exp,e);
												}
												return importedDeck;
											}
											
											@Override
											protected void done()
											{
												super.done();
												dispose();
											}
											
										};
									lblLoading.start();
									ThreadManager.getInstance().runInEdt(sw,"import decks");
			
			
			
			
		});

		
		
	}


	public MagicDeck getSelectedDeck() {

		return importedDeck;
	}

}
