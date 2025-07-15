package org.magic.gui.components.editor;

import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import org.apache.logging.log4j.Logger;
import org.magic.api.interfaces.MTGTextGenerator;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.components.card.MagicTextPane;
import org.magic.services.MTGConstants;
import org.magic.services.logging.MTGLogger;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.MTG;

public class JSuggestedPanel extends JComponent {

	private static final long serialVersionUID = 1L;
	protected Logger logger = MTGLogger.getLogger(this.getClass());
	private AbstractBuzyIndicatorComponent buzy;
	
	
	public JSuggestedPanel(MagicTextPane jTextPane) {

		setLayout(new FlowLayout(FlowLayout.LEFT));
		var suggestions = new JPanel();
		suggestions.setLayout(new FlowLayout(FlowLayout.LEFT));
		buzy = AbstractBuzyIndicatorComponent.createLabelComponent();
		
		
		var btn = new JButton(MTGConstants.ICON_TAB_SUGGESTION);
		btn.setToolTipText("text generator");
		btn.addActionListener(_->{
			
			buzy.start();
			ThreadManager.getInstance().runInEdt(new SwingWorker<String, Void>(){

				@Override
				protected String doInBackground() throws Exception {
					return MTG.getEnabledPlugin(MTGTextGenerator.class).generateText();
				}

				@Override
				protected void done() {
					try {
						jTextPane.setText(get());
					} catch (InterruptedException _) {
						Thread.currentThread().interrupt();
					} catch (ExecutionException e) {
						logger.error(e);
					}	
					
					buzy.end();
					
				}
				
				
				
				
			}, "suggest text");
			
			
			
			
			
		});

		add(btn);
		add(buzy);
		add(suggestions);

		jTextPane.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				update();

			}

			@Override
			public void insertUpdate(DocumentEvent e) {

				update();

			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				update();

			}
			
			
			private String[] caracters ;
			
			public void update()
			{

				if(jTextPane.getText().endsWith(" "))
				{
					int index = jTextPane.getText().lastIndexOf('\n');

					caracters = jTextPane.getText().split(" ");
					if(index>0)
						caracters = jTextPane.getText().substring(index+1).split(" ");

					suggestions.removeAll();
					
					buzy.start();
					ThreadManager.getInstance().runInEdt(new SwingWorker<Void, String>(){

						@Override
						protected Void doInBackground() throws Exception {
							for(String s : MTG.getEnabledPlugin(MTGTextGenerator.class).suggestWords(caracters))
							{
								publish(s);
							}
							return null;
							
						}

						@Override
						protected void process(List<String> chunks) {
							
							for(var s : chunks)
							{
								var t = new TagLabel(s);
								t.addMouseListener(new MouseAdapter() {
									@Override
									public void mouseClicked(MouseEvent e) {
										try {
											jTextPane.getDocument().insertString(jTextPane.getCaretPosition(), t.getText()+" ", null);
										} catch (BadLocationException _) {
											jTextPane.setText(jTextPane.getText() +" " + t.getText()+ " " );
										}
									}
								});
								suggestions.add(t);
							}
						}

						@Override
						protected void done() {
							buzy.end();
							suggestions.revalidate();
							suggestions.repaint();
						}
						
						
						
						
						
					},"suggest keyword");
					
					
					
					
					
					
				}

			}


		});
	}

}
