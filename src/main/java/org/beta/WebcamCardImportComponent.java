package org.beta;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardRecognition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractRecognitionStrategy;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.AbstractRecognitionArea;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.WebcamCanvas;
import org.magic.gui.models.MagicCardTableModel;
import org.magic.gui.renderer.MagicEditionsJLabelRenderer;
import org.magic.gui.renderer.ManaCellRenderer;
import org.magic.gui.tools.JListFilterDecorator;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.extra.IconSetProvider;
import org.magic.services.recognition.LoadedRecognitionEdition;
import org.magic.services.recognition.MatchResult;
import org.magic.services.recognition.area.AutoDetectAreaStrat;
import org.magic.services.recognition.area.ManualAreaStrat;
import org.magic.services.threads.ThreadManager;
import org.magic.services.workers.AbstractObservableWorker;
import org.magic.tools.UITools;
import org.utils.webcam.WebcamUtils;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.ds.ipcam.IpCamMode;


public class WebcamCardImportComponent extends MTGUIComponent {
	
	
	
	private static final long serialVersionUID = 1L;
	private transient MTGCardRecognition strat;
	private WebcamCanvas webcamPanel;
	private transient SwingWorker<Void, MatchResult> sw;
	private boolean running=false;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());
	private DefaultListModel<LoadedRecognitionEdition> listModel;
	private MagicCard currentCard;
	private AbstractBuzyIndicatorComponent buzy;
	private MagicCardTableModel modelCards;
	private JXTable tableResults;
	private boolean pause=false;
	
	@Override
	public String getTitle() {
		return "Card Detector";
	}

	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_WEBCAM;
	}
	
	
	public static void main(String[] args) {
		
		MTGControler.getInstance().getEnabled(MTGCardsProvider.class).init();
		WebcamUtils.inst().registerIPCam("Emulated IPcam", "https://images-na.ssl-images-amazon.com/images/I/416Sh21qFgL._AC_.jpg", IpCamMode.PULL);
		
		SwingUtilities.invokeLater(()->{
			WebcamCardImportComponent c = new WebcamCardImportComponent();
			MTGUIComponent.createJDialog(c, true, true).setVisible(true);
		});
		
	}
	
	
	public WebcamCardImportComponent() {
		setLayout(new BorderLayout(0, 0));
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 59, 0, 0,0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0,Double.MIN_VALUE};
		
		
		buzy = AbstractBuzyIndicatorComponent.createProgressComponent();
		JPanel panelControl = new JPanel();
		JComboBox<Webcam> cboWebcams = UITools.createCombobox(WebcamUtils.inst().listWebcam(),MTGConstants.ICON_WEBCAM);
		JComboBox<MTGCardRecognition> cboRecognition = UITools.createCombobox(MTGCardRecognition.class,true);
		JComboBox<AbstractRecognitionArea> cboAreaDetector = UITools.createCombobox(new AbstractRecognitionArea[] { new AutoDetectAreaStrat(),new ManualAreaStrat()});
		JCheckBox chkpause = new JCheckBox("Pause");
		JSlider sldThreshold = new JSlider(0,100,27);
		JLabel lblThreshHoldValue = new JLabel(String.valueOf(sldThreshold.getValue()));
		JPanel thrsh = new JPanel();
		JButton btnStarting = new JButton("Detect");
		JPanel panneauBas = new JPanel();
		JPanel panneauBasButtons = new JPanel();
		JButton btnRemove = new JButton(MTGConstants.ICON_DELETE);
		JButton btnClose = new JButton(MTGConstants.ICON_IMPORT);
		
		modelCards = new MagicCardTableModel();
		listModel = new DefaultListModel<>();
		strat = (MTGCardRecognition)cboRecognition.getSelectedItem();
		
		try {
			MTGControler.getInstance().getEnabled(MTGCardsProvider.class).loadEditions().stream().sorted().map(ed->new LoadedRecognitionEdition(ed,strat.isCached(ed))).forEach(listModel::addElement);
		} catch (IOException e1) {
			logger.error(e1);
		}
		tableResults = new JXTable(modelCards);
		JList<LoadedRecognitionEdition> listEds = new JList<>(listModel);
		JListFilterDecorator deco = JListFilterDecorator.decorate(listEds,(LoadedRecognitionEdition t, String u)->t.getEdition().getSet().toLowerCase().contains(u.toLowerCase()));

		
		webcamPanel = new WebcamCanvas((Webcam)cboWebcams.getSelectedItem(),(AbstractRecognitionArea)cboAreaDetector.getSelectedItem());

		
		
		panelControl.setLayout(gridBagLayout);
		panneauBasButtons.setLayout(new BoxLayout(panneauBasButtons, BoxLayout.Y_AXIS));
		panneauBas.setLayout(new BorderLayout());

		
		
		panelControl.add(cboWebcams, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 0, 0));
		panelControl.add(cboRecognition, UITools.createGridBagConstraints(GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, 0, 1));
		panelControl.add(cboAreaDetector, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 0, 2));
		thrsh.add(sldThreshold);
		thrsh.add(lblThreshHoldValue);
		thrsh.add(chkpause);
		panelControl.add(thrsh, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 0, 3));
		panelControl.add(btnStarting, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 0, 4));
		panelControl.add(new JScrollPane(deco.getContentPanel()), UITools.createGridBagConstraints(null, GridBagConstraints.BOTH, 0, 5));
		panelControl.add(buzy, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 0, 6));
		panneauBasButtons.add(btnRemove);
		panneauBasButtons.add(btnClose);
		panneauBas.add(panneauBasButtons,BorderLayout.EAST);
		panneauBas.add(new JScrollPane(tableResults),BorderLayout.CENTER);

		add(panelControl, BorderLayout.EAST);
		add(panneauBas,BorderLayout.SOUTH);
		add(webcamPanel, BorderLayout.CENTER);

		
		
	
		
		listEds.setCellRenderer(new ListCellRenderer<>() {
			JLabel l = new JLabel();
			@Override
			public Component getListCellRendererComponent(JList<? extends LoadedRecognitionEdition> list,LoadedRecognitionEdition value, int index, boolean isSelected, boolean cellHasFocus) {
				if (value != null) 
				{
					ImageIcon ic= IconSetProvider.getInstance().get16(value.getEdition().getId());
					
					l.setText(value.getEdition().getSet());
					l.setIcon(ic);
					
					l.setOpaque(true);
					if (isSelected) {
						l.setBackground(list.getSelectionBackground());
						l.setForeground(list.getSelectionForeground());
					} else {
						l.setBackground(list.getBackground());
						l.setForeground(list.getForeground());
					}
					
					if(value.isCached())
						l.setBackground(Color.CYAN);
					
					if(value.isLoaded())
						l.setBackground(Color.GREEN);

					
					
				}
				
				return l;

			}
		});
		
	
		
		
	
		sldThreshold.addChangeListener(cl->lblThreshHoldValue.setText(String.valueOf(sldThreshold.getValue())));
		cboAreaDetector.addActionListener(il->webcamPanel.setAreaStrat((AbstractRecognitionArea)cboAreaDetector.getSelectedItem()));
		cboRecognition.addActionListener(il->strat = ((AbstractRecognitionStrategy)cboRecognition.getSelectedItem()));

		
		
		
		tableResults.getColumnModel().getColumn(2).setCellRenderer(new ManaCellRenderer());
		tableResults.getColumnModel().getColumn(6).setCellRenderer(new MagicEditionsJLabelRenderer());
		

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
						if(strat!=null && img!=null && !pause) 
						{
							List<MatchResult> matches = webcamPanel.getAreaRecognitionStrategy().recognize(img, strat,sldThreshold.getValue());
								MatchResult res = !matches.isEmpty() ? matches.get(0):null;
								if(res!=null)
								{
									publish(res);
								}
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
		
		btnClose.addActionListener(l->onDestroy());
		
		
		
		chkpause.addChangeListener(l->pause=chkpause.isSelected());
		

		
		btnRemove.addActionListener(l->{
			List<MagicCard> cards = UITools.getTableSelections(tableResults,0);
			
			if(!cards.isEmpty())
				modelCards.removeItem(cards);
			
		});
		
		
		listEds.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				if(me.getClickCount()==2) 
				{
						LoadedRecognitionEdition ed = listEds.getSelectedValue();
						buzy.start(ed.getEdition().getCardCount());		
						pause=true;
						AbstractObservableWorker<Void, Void, MTGCardRecognition> work = new AbstractObservableWorker<>(buzy,strat,ed.getEdition().getCardCount()) {
							@Override
							protected Void doInBackground() throws Exception {
								plug.loadDatasForSet(ed.getEdition());
								return null;
							}
							
							@Override
							protected void done() {
								super.done();
								plug.finalizeLoad();
								ed.setLoaded(true);
								pause=false;
							}
						};
						
						ThreadManager.getInstance().runInEdt(work, "building " + ed + " recognition");
				}
				
			}
		});
			
		
		btnStarting.addActionListener(al->{
			strat = (MTGCardRecognition)cboRecognition.getSelectedItem();
			webcamPanel.setWebcam((Webcam)cboWebcams.getSelectedItem());
			webcamPanel.revalidate();
			
//			if(sw.isCancelled() || sw.isDone())
//			{
//				logger.info("Killing current sw");
//				running=false;
//				sw.cancel(true);
//			}
			ThreadManager.getInstance().runInEdt(sw, "Webcam");
		});		

				
	}
	
	@Override
	public void onDestroy() {
		if(webcamPanel.getWebcam().isOpen())
			webcamPanel.getWebcam().close();
		
		running=false;
		logger.debug("Closing cam done");
	}
	
	protected void addResult(MatchResult r) {
		try {
			
			if(currentCard==null || !currentCard.getName().equalsIgnoreCase(r.name))
			{
				currentCard = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).searchCardByName(r.name, new MagicEdition(r.setCode), true).get(0);
				modelCards.addItem(currentCard);
				tableResults.scrollRectToVisible(tableResults.getCellRect(tableResults.getRowCount()-1, 0, true));
			}
			
		} catch (IOException e) {
			logger.error(e);
		}
		
	}
	
	public List<MagicCard> getFindedCards()
	{
		return modelCards.getItems();
	}

	
	public MagicDeck getSelectedDeck() {
		MagicDeck d = new MagicDeck();
		d.setDescription("Imported from " + getTitle());
		
		getFindedCards().forEach(d::add);
		return d;
	}
	

	
	
}
