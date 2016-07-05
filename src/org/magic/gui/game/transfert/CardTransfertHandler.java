package org.magic.gui.game.transfert;

import java.awt.Color;
import java.awt.MouseInfo;
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

import org.magic.gui.game.DisplayableCard;
import org.magic.gui.game.DraggablePanel;
import org.magic.services.games.GameManager;


public class CardTransfertHandler extends TransferHandler  {

	private final DataFlavor localObjectFlavor;
	private final JWindow window = new JWindow();
	public static JLabel dragIcon = new JLabel();
	
	public CardTransfertHandler() {
		localObjectFlavor = new ActivationDataFlavor(DisplayableCard.class, DataFlavor.javaJVMLocalObjectMimeType, "DisplayableCard");
		window.setBackground(new Color(0, true));
		DragSource.getDefaultDragSource().addDragSourceMotionListener(new DragSourceMotionListener() {
			@Override
			public void dragMouseMoved(DragSourceDragEvent dsde) {
				Point pt = dsde.getLocation();
				pt.translate(5, 5); // offset
				window.setLocation(pt);
				window.add(dragIcon);
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
		window.pack();
		
		Point pt = p.getLocation();
		SwingUtilities.convertPointToScreen(pt, p);
		
		window.setLocation(pt);
		window.setVisible(true);
		
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
			target.addComponent(src);
			target.revalidate();
			return true;
		} catch (UnsupportedFlavorException ufe) {
			ufe.printStackTrace();
		} catch (java.io.IOException ioe) {
			ioe.printStackTrace();
		}
		return false;
	}
	
	@Override
	protected void exportDone(JComponent c, Transferable data, int action) {
		DisplayableCard src = (DisplayableCard) c;
		if (action == TransferHandler.MOVE) {
			DraggablePanel dest = ((DraggablePanel)c.getParent());
			src.setBounds(dest.getMousePosition().x, dest.getMousePosition().y, src.getWidth(), src.getHeight());  
			
			
			src.setOrigine(dest.getOrigine());
			dest.repaint();
			
		}
		
		window.setVisible(false);
	}
}
