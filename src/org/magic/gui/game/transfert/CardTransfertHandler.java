package org.magic.gui.game.transfert;

import java.awt.Color;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceMotionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.gui.game.components.DisplayableCard;
import org.magic.gui.game.components.Draggable;
import org.magic.gui.game.components.DraggablePanel;


public class CardTransfertHandler extends TransferHandler  {

	private final DataFlavor localObjectFlavor;
	private static JWindow window = new JWindow();
	private static JLabel dragLab = new JLabel();
	
	static final Logger logger = LogManager.getLogger(CardTransfertHandler.class.getName());

	
	public CardTransfertHandler() {
		localObjectFlavor = new ActivationDataFlavor(DisplayableCard.class, DataFlavor.javaJVMLocalObjectMimeType, "DisplayableCard");
		window.add(dragLab);
		window.setBackground(new Color(0,true));
		DragSource.getDefaultDragSource().addDragSourceMotionListener(new DragSourceMotionListener() {
			@Override
			public void dragMouseMoved(DragSourceDragEvent dsde) {
				
				Point pt = dsde.getLocation();
				pt.translate(5, 5); // offset
				window.setLocation(pt);
				window.setVisible(true);
				window.pack();
			}
		});
		
		
	}
	
	@Override
	protected Transferable createTransferable(JComponent c)
	{
		final DataHandler dh = new DataHandler(c, localObjectFlavor.getMimeType());
		return new Transferable()
		{
			@Override
			public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
				if (flavor.equals(localObjectFlavor)) {
					return dh.getTransferData(flavor);
				} 
				return null;
			}

			@Override
			public DataFlavor[] getTransferDataFlavors() {
				ArrayList<DataFlavor> list = new ArrayList<>();
				for (DataFlavor f : dh.getTransferDataFlavors()) {
					list.add(f);
				}
				return list.toArray(dh.getTransferDataFlavors());
			}

			@Override
			public boolean isDataFlavorSupported(DataFlavor flavor) {
				for (DataFlavor f : getTransferDataFlavors()) {
					if (flavor.equals(f)) {
						return true;
					}
				}
				return false;
			}
	
		};
		
	}
	
	
	@Override
	public boolean canImport(TransferSupport support)
	{
		if (!support.isDrop()) {
			return false;
		}
		return true;
		
	}
	
	

	@Override
	public int getSourceActions(JComponent c)
	{
		DisplayableCard p = (DisplayableCard) c;
		Point pt = p.getLocation();
		SwingUtilities.convertPointToScreen(pt, p);
		dragLab.setIcon(p.getIcon());
		window.setLocation(pt);
	
		return MOVE;
	}
	
	@Override
	public boolean importData(TransferSupport support)
	{
		
		if (!canImport(support))
			return false;
		
		DraggablePanel target = (DraggablePanel) support.getComponent();
		try {
			DisplayableCard src = (DisplayableCard) support.getTransferable().getTransferData(localObjectFlavor);
			((Draggable)src.getParent()).moveCard(src.getMagicCard(), target.getOrigine());
			
			
			logger.info("move " + src.getMagicCard().getName()+ " from " + ((Draggable)src.getParent()).getOrigine() + " to " + target.getOrigine());
					
			src.getParent().revalidate();
			target.revalidate();
			target.repaint();
			src.getParent().repaint();
			
			target.addComponent(src);
			return true;
		} catch (Exception ufe) {
			logger.error(ufe);
		} 
		return false;
	}
	
	@Override
	protected void exportDone(JComponent c, Transferable data, int action) {
		DisplayableCard src = (DisplayableCard) c;
		if (action == TransferHandler.MOVE) {
			dragLab.setIcon(null);
			window.setVisible(false);
			DraggablePanel dest = ((DraggablePanel)c.getParent());
	
			
			
			src.setLocation(dest.getMousePosition());
			src.getParent().revalidate();
			dest.revalidate();
			dest.repaint();
			src.getParent().repaint();
			dest.postTreatment();
			
			
		}
	}
}
