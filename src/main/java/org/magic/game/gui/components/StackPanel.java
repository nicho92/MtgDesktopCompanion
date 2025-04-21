package org.magic.game.gui.components;

import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;

import org.magic.api.beans.game.AbstractSpell;
import org.magic.api.beans.game.GameManager;
import org.magic.game.gui.components.renderer.StackItemRenderer;
import org.magic.gui.abstracts.MTGUIComponent;
import org.utils.patterns.observer.Observable;
import org.utils.patterns.observer.Observer;

public class StackPanel extends MTGUIComponent implements Observer {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private static final String START = "Start";
	private static final String PAUSE = "Pause";
	private JList<AbstractSpell> listStack;
	private DefaultListModel<AbstractSpell> model;
	private JLabel lblCounter;
	private Timer timer;
	private static final int SECONDE=10;
	private long startTime=SECONDE;
	private JButton btnPause;
	private boolean enabledChrono = true;


	public StackPanel(boolean enableChrono) {
		model = new DefaultListModel<>();
		setLayout(new BorderLayout(0, 0));
		this.enabledChrono=enableChrono;

		if(enableChrono)
		{
			timer = new Timer(1000,_->{
				startTime=startTime-1;
				lblCounter.setText(String.valueOf(startTime));

				if(startTime==0)
				{
					timer.stop();
					if(model.size()>0)
					{
						lblCounter.setText("RESOLVING " + model.size() + " spell(s)");
						GameManager.getInstance().getStack().unstack();

					}
					else
					{
						startTime=SECONDE;
					}
				}
			});

			lblCounter = new JLabel(String.valueOf(startTime));
			var panelChrono = new JPanel();

			add(panelChrono, BorderLayout.NORTH);
			panelChrono.add(lblCounter);

			btnPause = new JButton(PAUSE);

			btnPause.addActionListener(_->{

					if(timer.isRunning())
					{
						timer.stop();
						btnPause.setText(START);
					}
					else
					{
						timer.start();
						btnPause.setText(PAUSE);
					}
			});
			panelChrono.add(btnPause);
		}

		listStack = new JList<>(model);

		listStack.setCellRenderer(new StackItemRenderer());

		add(new JScrollPane(listStack ), BorderLayout.CENTER);


	}


	public void enableChrono(boolean b)
	{

		if(!enabledChrono)
			return;


		startTime=SECONDE;

		if(b)
		{
			timer.start();
			btnPause.setText(PAUSE);
		}
		else
		{
			timer.stop();
			btnPause.setText(START);
		}


	}

	public void addStack(AbstractSpell sp)
	{
		model.add(0,sp);


		if(!model.isEmpty())
		{
			enableChrono(true);
		}
	}

	public void removeStack()
	{
		try {
		model.removeElementAt(0);
		}
		catch(ArrayIndexOutOfBoundsException e) {
			//do nothing
			}
	}



	@Override
	public void update(Observable o, Object ob) {

		if(ob!=null)
			addStack((AbstractSpell)ob);
		else
			removeStack();
	}


	@Override
	public String getTitle() {
		return "STACK";
	}
}




