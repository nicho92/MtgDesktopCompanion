package org.beta;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;

import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardRecognition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractRecognitionStrategy;
import org.magic.game.gui.components.WebcamCanvas;
import org.magic.gui.abstracts.AbstractRecognitionArea;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.renderer.MagicEditionIconListRenderer;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.recognition.MatchResult;
import org.magic.services.recognition.area.AutoDetectAreaStrat;
import org.magic.services.recognition.area.ManualAreaStrat;
import org.magic.services.threads.ThreadManager;
import org.magic.tools.UITools;
import org.utils.webcam.WebcamUtils;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.ds.ipcam.IpCamMode;


public class WebcamCardImportComponent extends MTGUIComponent {
	
	
	
	private static final long serialVersionUID = 1L;
	private transient MTGCardRecognition strat;
	private WebcamCanvas webcamPanel;
	private SwingWorker<Void, MatchResult> sw;
	private boolean running=false;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	private DefaultListModel<MagicEdition> listModel;
	private Set<MagicCard> findedCards;
	private MagicCard currentCard;
	
	
	@Override
	public String getTitle() {
		return "Card Detector";
	}
	
	public static void main(String[] args) {
		
		MTGControler.getInstance().getEnabled(MTGCardsProvider.class).init();
		WebcamUtils.inst().registerIPCam("Emulated IPcam", "https://images-na.ssl-images-amazon.com/images/I/416Sh21qFgL._AC_.jpg", IpCamMode.PULL);
		
		SwingUtilities.invokeLater(()->{
			JFrame f = new JFrame();
			f.getContentPane().add(new WebcamCardImportComponent());
			f.pack();
			f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			f.setSize(1024, 768);
			f.setVisible(true);
		});
		
	}
	
	@Override
	public void onDestroy() {
		if(webcamPanel.getWebcam().isOpen())
			webcamPanel.getWebcam().close();
		
		running=false;
		
	}
	
	public WebcamCardImportComponent() {
		setLayout(new BorderLayout(0, 0));
		
		findedCards = new LinkedHashSet<>();
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.EAST);
		GridBagLayout gblpanel = new GridBagLayout();
		gblpanel.columnWidths = new int[]{0};
		gblpanel.rowHeights = new int[]{0, 0, 0, 0, 0};
		gblpanel.columnWeights = new double[]{0.0};
		gblpanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
		panel.setLayout(gblpanel);
		
		JComboBox<Webcam> cboWebcams = UITools.createCombobox(WebcamUtils.inst().listWebcam());
		panel.add(cboWebcams, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 0, 0));
		
		JComboBox<MTGCardRecognition> cboRecognition = UITools.createCombobox(MTGCardRecognition.class,true);
		panel.add(cboRecognition, UITools.createGridBagConstraints(GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, 0, 1));
				
		JComboBox<AbstractRecognitionArea> cboAreaDetector = UITools.createCombobox(new AbstractRecognitionArea[] { new AutoDetectAreaStrat(),new ManualAreaStrat()});
		panel.add(cboAreaDetector, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 0, 2));
		
		JSlider sldThreshold = new JSlider(0,100,27);
		JLabel lblThreshHoldValue = new JLabel(String.valueOf(sldThreshold.getValue()));
		JPanel thrsh = new JPanel();
		thrsh.add(sldThreshold);
		thrsh.add(lblThreshHoldValue);
		panel.add(thrsh, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 0, 3));
		
		JButton btnStarting = new JButton("Run");
		panel.add(btnStarting, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 0, 4));

		
		listModel = new DefaultListModel<>();
		
		try {
			MTGControler.getInstance().getEnabled(MTGCardsProvider.class).loadEditions().stream().sorted().forEach(listModel::addElement);
			
		} catch (IOException e1) {
			logger.error(e1);
		}
		
		JList<MagicEdition> listEds = new JList<>(listModel);
		listEds.setCellRenderer(new MagicEditionIconListRenderer());
		panel.add(new JScrollPane(listEds), UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 0, 5));
		
		webcamPanel = new WebcamCanvas((Webcam)cboWebcams.getSelectedItem(),(AbstractRecognitionArea)cboAreaDetector.getSelectedItem());
		add(webcamPanel, BorderLayout.CENTER);
	
		sldThreshold.addChangeListener(cl->lblThreshHoldValue.setText(String.valueOf(sldThreshold.getValue())));
		cboAreaDetector.addActionListener(il->webcamPanel.setAreaStrat((AbstractRecognitionArea)cboAreaDetector.getSelectedItem()));
		cboRecognition.addActionListener(il->strat = ((AbstractRecognitionStrategy)cboRecognition.getSelectedItem()));

		
		strat = (MTGCardRecognition)cboRecognition.getSelectedItem();
		
		sw = new SwingWorker<>()
		{
			@Override
			protected Void doInBackground() 
			{
				running=true;
				try {
					logger.info("start " + webcamPanel.getWebcam() +" " + running);
					while(running) 
					{
						webcamPanel.draw();
						BufferedImage img = webcamPanel.lastDrawn();
						if(strat!=null && img!=null) 
						{
							List<MatchResult> matches = webcamPanel.getAreaRecognitionStrategy().recognize(img, strat,sldThreshold.getValue());
								MatchResult res = !matches.isEmpty() ? matches.get(0):null;
								if(res!=null)
								{
									publish(res);
								}
							
							
							logger.info(matches);
						}
					}
				}
				catch(Exception e)
				{
					logger.error("Error in webcam" ,e);
				}
				
				return null;
			}
			
			@Override
			protected void process(List<MatchResult> chunks) {
				addResult( chunks.get(0));
			}
			
			@Override
			protected void done() {
				
				try {
					get();
					logger.info("Stopping webcam " + webcamPanel.getWebcam());
					running=false;
				} catch (Exception e) {
					logger.error("Stopping webcam " + webcamPanel.getWebcam(),e);
				} 
				
			}
		};
		
		
		listEds.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				
				if(me.getClickCount()==2) 
				{
						MagicEdition ed = listEds.getSelectedValue();
						strat.loadDatasForSet(ed);
				}
				
			}
		});
			
		
		btnStarting.addActionListener(al->{
			strat = (MTGCardRecognition)cboRecognition.getSelectedItem();
			webcamPanel.setWebcam((Webcam)cboWebcams.getSelectedItem());
			webcamPanel.revalidate();
			repaint();
			if(sw.isCancelled() || sw.isDone())
			{
				logger.info("Killing current sw");
				running=false;
				sw.cancel(true);
			}
		
			
			
			ThreadManager.getInstance().runInEdt(sw, "Webcam");
				
		});		
		
		
				
	}

	protected void addResult(MatchResult r) {
		webcamPanel.setLastResult(r);
		try {
			
			if(currentCard==null || !currentCard.getName().equalsIgnoreCase(r.name))
			{
				currentCard = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).searchCardByName(r.name, new MagicEdition(r.setCode), true).get(0);
				findedCards.add(currentCard);
			}
			
		} catch (IOException e) {
			logger.error(e);
		}
		
	}
	
	public List<MagicCard> getFindedCards()
	{
		return new ArrayList<>(findedCards);
	}
	

	
	
}
