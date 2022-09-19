package org.magic.game.transfert;

import java.awt.Color;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragSource;
import java.io.IOException;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import org.apache.logging.log4j.Logger;
import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.gui.components.Draggable;
import org.magic.game.gui.components.DraggablePanel;
import org.magic.services.logging.MTGLogger;

public class CardTransfertHandler extends TransferHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final DataFlavor localObjectFlavor;
	private static JWindow window = new JWindow();
	private static JLabel dragLab = new JLabel();

	private transient Logger logger = MTGLogger.getLogger(this.getClass());

	public CardTransfertHandler() {

		localObjectFlavor = new ActivationDataFlavor(DisplayableCard.class, DataFlavor.javaJVMLocalObjectMimeType,"DisplayableCard");
		window.add(dragLab);
		window.setBackground(new Color(0, true));

		DragSource.getDefaultDragSource().addDragSourceMotionListener(dsde -> {
			Point pt = dsde.getLocation();
			pt.translate(5, 5);
			window.setLocation(pt);
			window.setVisible(true);
			window.pack();
		});
	}

	@Override
	protected Transferable createTransferable(JComponent c) {
		final var dh = new DataHandler(c, localObjectFlavor.getMimeType());
		return new Transferable() {
			@Override
			public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
				if (flavor.equals(localObjectFlavor)) {
					return dh.getTransferData(flavor);
				}
				return null;
			}

			@Override
			public DataFlavor[] getTransferDataFlavors() {
				return dh.getTransferDataFlavors();
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
	public boolean canImport(TransferSupport support) {
		return support.isDrop();
	}

	@Override
	public int getSourceActions(JComponent c) {
		DisplayableCard p = (DisplayableCard) c;
		Point pt = p.getLocation();
		SwingUtilities.convertPointToScreen(pt, p);
		dragLab.setIcon(p.toIcon());
		window.setLocation(pt);
		return MOVE;
	}

	@Override
	public boolean importData(TransferSupport support) {
		if (!canImport(support))
			return false;
		try {
			Draggable target = (Draggable) support.getComponent();
			DisplayableCard src = (DisplayableCard) support.getTransferable().getTransferData(localObjectFlavor);
			if ((((Draggable) src.getParent()).getOrigine() != target.getOrigine())) {
				src.getParent().revalidate();
				target.updatePanel();
				src.getParent().repaint();
				
				
				logger.debug("move {} from {} to {}",src.getMagicCard().getName(),((Draggable) src.getParent()).getOrigine() ,target.getOrigine());
				((Draggable) src.getParent()).moveCard(src, target.getOrigine());
				target.addComponent(src);

			} else {
				target.postTreatment(src);
			}
			return true;
		} catch (Exception ufe) {
			logger.error("Error transfert", ufe);
		}
		return false;
	}

	@Override
	protected void exportDone(JComponent c, Transferable data, int action) {
		DisplayableCard src = (DisplayableCard) c;
		if (action == TransferHandler.MOVE) {
			dragLab.setIcon(null);
			window.setVisible(false);

			if (c.getParent() instanceof DraggablePanel dc) {
				DraggablePanel dest = dc;
				if (dest.getMousePosition() != null)
					src.setLocation(dest.getMousePosition());
				dest.postTreatment(src);
			}
		}
	}
}
