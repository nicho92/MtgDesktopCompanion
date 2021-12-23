package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.dialog.IPCamAddDialog;
import org.magic.services.MTGConstants;
import org.magic.services.recognition.MatchResult;
import org.magic.services.threads.ThreadManager;
import org.magic.tools.UITools;
import org.utils.webcam.WebcamUtils;

import com.github.sarxos.webcam.Webcam;

public class WebcamSnapShotComponent extends MTGUIComponent {
	
	private List<BufferedImage> snapshotImages;

	private static final long serialVersionUID = 1L;
	private WebcamCanvas webcamCanvas;
	private transient SwingWorker<Void, MatchResult> swWebcamReader;
	private boolean running=false;
	private JButton btnClose ;
	
	
	@Override
	public String getTitle() {
		return "Card Detector";
	}

	@Override
	public void onDestroy() {
		webcamCanvas.close();
		running=false;
		logger.debug("Closing cam done");
		
		if(swWebcamReader!=null)
			swWebcamReader.cancel(true);

	}
	
	public WebcamSnapShotComponent() {
		snapshotImages = new ArrayList<>();
		setLayout(new BorderLayout(0, 0));
		
		var gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0,0.0, 0.0, 0.0};
		
		
		var panelControl = new JPanel();
		var cboWebcams = UITools.createCombobox(WebcamUtils.inst().listWebcam(),MTGConstants.ICON_WEBCAM);
		var controlWebcamPanel = new JPanel();
		var btnStarting = new JButton("Start",MTGConstants.PLAY_ICON);
		var btnSnap = new JButton("Snap",MTGConstants.ICON_WEBCAM);
		btnClose = new JButton(MTGConstants.ICON_IMPORT);
		var btnReloadCams = new JButton(MTGConstants.ICON_REFRESH);
		var btnAddCam = new JButton(MTGConstants.ICON_NEW);
		
		
		webcamCanvas = new WebcamCanvas((Webcam)cboWebcams.getSelectedItem(),null);

		panelControl.setLayout(gridBagLayout);
		controlWebcamPanel.add(cboWebcams);
		controlWebcamPanel.add(btnReloadCams);
		controlWebcamPanel.add(btnAddCam);

		panelControl.add(controlWebcamPanel, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 0, 0));
		panelControl.add(btnStarting, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 0, 1));
		panelControl.add(btnSnap, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 0, 2));
		panelControl.add(btnClose, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 0, 3));
		
		
		add(panelControl, BorderLayout.EAST);
		
		add(webcamCanvas, BorderLayout.CENTER);

		btnReloadCams.addActionListener(al->{
			((DefaultComboBoxModel<Webcam>)cboWebcams.getModel()).removeAllElements();
			((DefaultComboBoxModel<Webcam>)cboWebcams.getModel()).addAll(WebcamUtils.inst().listWebcam());
			cboWebcams.setSelectedItem(webcamCanvas.getWebcam());
		});
		
		btnAddCam.addActionListener(al->{
			var diag = new IPCamAddDialog();
						   diag.setModal(true);
						   diag.setVisible(true);
			if(diag.isHasNew())
				btnReloadCams.doClick();
		});
		
		btnSnap.addActionListener(al->{
			snapshotImages.add(webcamCanvas.lastDrawn());
		});
		
		
		btnStarting.addActionListener(al->{
			webcamCanvas.setWebcam((Webcam)cboWebcams.getSelectedItem());
			webcamCanvas.revalidate();
			ThreadManager.getInstance().runInEdt(swWebcamReader, "Webcam");
		});		
		
		swWebcamReader = new SwingWorker<>()
		{
			
			@Override
			protected Void doInBackground() 
			{
				running=true;
				try {
					logger.info("start " + webcamCanvas.getWebcam() +" " + running);
					while(running) 
					{
						webcamCanvas.draw();
					}
				}
				catch(Exception e)
				{
					logger.error("Error in webcam" ,e);
				}
				
				return null;
			}
			
			@Override
			protected void done() {
				
				try {
					logger.info("Stopping webcam " + webcamCanvas.getWebcam());
					running=false;
					get();
				}
				catch(InterruptedException ex)
				{
					Thread.currentThread().interrupt();
				}
				catch(CancellationException ex)
				{
					logger.error("Cancelling");
				}
				catch (Exception e) {
					logger.error("Error Stopping webcam " + webcamCanvas.getWebcam(),e);
				} 
				
			}
		};
	}
	
	public JButton getBtnClose() {
		return btnClose;
	}
	
	
	public List<BufferedImage> getSnappedImages()
	{
		
		return snapshotImages;
	}
		
}
