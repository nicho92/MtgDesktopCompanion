package org.magic.game.gui.components;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;

import org.magic.game.gui.components.renderer.StackItemRenderer;
import org.magic.game.model.AbstractSpell;
import org.magic.game.model.GameManager;
import org.utils.patterns.observer.Observable;
import org.utils.patterns.observer.Observer;

public class StackPanel extends JPanel implements Observer {
	
	private JList<AbstractSpell> listStack;
	private DefaultListModel<AbstractSpell> model;
	private JLabel lblCounter;
	private Timer timer;
	private static final int SECONDE=10;
	private long startTime=SECONDE;
	
	public StackPanel() {
		model = new DefaultListModel<>();
		
		timer = new Timer(1000,new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startTime=startTime-1;
				lblCounter.setText(String.valueOf(startTime));
				
				if(startTime==0)
				{
					timer.stop();
					if(model.size()>0) {
						lblCounter.setText("RESOLVING " + model.size() + " spell(s)");
						GameManager.getInstance().getStack().resolve();
						
					}
					else
					{
						startTime=SECONDE;
					}
				}
			}
		});

		lblCounter = new JLabel(String.valueOf(startTime));
		JPanel panel = new JPanel();
		listStack = new JList<>(model);
		
		setLayout(new BorderLayout(0, 0));
		listStack.setCellRenderer(new StackItemRenderer());
		
		add(new JScrollPane(listStack ), BorderLayout.CENTER);
		add(panel, BorderLayout.NORTH);
		panel.add(lblCounter);
	}
	
	
	public void enableChrono(boolean b)
	{
		startTime=SECONDE;
		
		if(b)
			timer.start();
		else
			timer.stop();
		
		
	}
	
	public void updateStack()
	{
		model.clear();
		for(AbstractSpell spell : GameManager.getInstance().getStack().toList())
		{
			model.addElement(spell);
		}
		
		if(!model.isEmpty())
			enableChrono(true);
			
		
	}

	@Override
	public void update(Observable o, Object ob) {
		updateStack();
	}
}




