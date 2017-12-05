package org.magic.game.transfert;

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

import org.apache.log4j.Logger;
import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.Draggable;
import org.magic.game.gui.components.DraggablePanel;
import org.magic.game.model.CardSpell;
import org.magic.game.model.GameManager;
import org.magic.tools.MTGLogger;


public class CardTransfertHandler extends TransferHandler  {

	private final DataFlavor localObjectFlavor;
	private static JWindow window = new JWindow();
	private static JLabel dragLab = new JLabel();
	
	static final Logger logger = MTGLogger.getLogger(CardTransfertHandler.class);

	
	public CardTransfertHandler() {
		
		localObjectFlavor = new ActivationDataFlavor(DisplayableCard.class, DataFlavor.javaJVMLocalObjectMimeType, "DisplayableCard");
		window.add(dragLab);
		window.setBackground(new Color(0,true));
		
		DragSource.getDefaultDragSource().addDragSourceMotionListener(new DragSourceMotionListener() {
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
		dragLab.setIcon(p.toIcon());
		window.setLocation(pt);
		return MOVE;
	}
	
	@Override
	public boolean importData(TransferSupport support)
	{
		if (!canImport(support))
			return false;
		try {
				Draggable target = (Draggable) support.getComponent();
				DisplayableCard src = (DisplayableCard) support.getTransferable().getTransferData(localObjectFlavor);
				if(! ( ((Draggable)src.getParent()).getOrigine() == target.getOrigine()))
				{
					  src.getParent().revalidate();
					  target.updatePanel();
					  src.getParent().repaint();
					  GameManager.getInstance().getStack().put(new CardSpell(src.getName(), src.getText(), src));
					  logger.debug("move " + src.getMagicCard().getName()+ " from " + ((Draggable)src.getParent()).getOrigine() + " to " + target.getOrigine());
					  ((Draggable)src.getParent()).moveCard(src, target.getOrigine());
					  target.addComponent(src);
					 
				}
				else
				{
					target.postTreatment(src);
				}
			//TODO if move only, put card on first position of container's order
			return true;
		} catch (Exception ufe) {
			ufe.printStackTrace();
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
			
			if(c.getParent() instanceof DraggablePanel)
			{	
				DraggablePanel dest = ((DraggablePanel)c.getParent());
				src.setLocation(dest.getMousePosition());
				dest.postTreatment(src);
			}
		}
	}
}
