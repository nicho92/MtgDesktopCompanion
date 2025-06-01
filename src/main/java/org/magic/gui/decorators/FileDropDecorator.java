package org.magic.gui.decorators;


import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.SystemColor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.TooManyListenersException;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;

import org.apache.logging.log4j.Logger;
import org.magic.services.logging.MTGLogger;


public class FileDropDecorator
{
    private Border normalBorder;
    private DropTargetListener dropListener;
    private static Color defaultBorderColor = new Color( 0f, 0f, 1f, 0.25f );
    private Logger logger = MTGLogger.getLogger(this.getClass());
    private Color defaultColor;

    public void init(final Component c,final Listener listener)
    {
            dropListener = new DropTargetListener()
            {
            	@Override
				public void dragEnter( DropTargetDragEvent evt )
                {
                    if( isDragOk(evt ) )
                    {
                        if( c instanceof JComponent jc )
                        {
                        	normalBorder = jc.getBorder();
                            jc.setBorder( BorderFactory.createMatteBorder( 2, 2, 2, 2, defaultBorderColor ) );
                            defaultColor = jc.getBackground();
                        }
                        evt.acceptDrag(DnDConstants.ACTION_COPY);
                    }
                    else
                    {
                        evt.rejectDrag();
                    }
                }

                @Override
				public void dragOver( DropTargetDragEvent evt )
                {
                	c.setBackground(SystemColor.textHighlight);
                }

                @Override
				public void drop( DropTargetDropEvent evt )
                {
                    try
                    {
                    	var tr = evt.getTransferable();
                        if (tr.isDataFlavorSupported (DataFlavor.javaFileListFlavor))
                        {
                            evt.acceptDrop ( DnDConstants.ACTION_COPY );
							@SuppressWarnings("unchecked")
							var fileList = (List<File>)tr.getTransferData(DataFlavor.javaFileListFlavor);

                            if( listener != null )
                                listener.filesDropped( fileList.toArray(new File[fileList.size()]) );

                            evt.getDropTargetContext().dropComplete(true);
                            c.setBackground(defaultColor);
                        }

                    }
                    catch ( Exception io)
                    {
                    	logger.error( "FileDrop: IOException - abort:",io );
                        evt.rejectDrop();
                    }
                    finally
                    {
                        if( c instanceof JComponent jc)
                        {
                        	jc.setBorder( normalBorder );
                        }
                    }
                }

                @Override
				public void dragExit( DropTargetEvent evt )
                {
                	if( c instanceof JComponent jc)
                    {
                		jc.setBorder( normalBorder );
                        c.setBackground(defaultColor);
                    }

                }

                @Override
				public void dropActionChanged( DropTargetDragEvent evt )
                {
                    if( isDragOk(evt ) )
                        evt.acceptDrag( DnDConstants.ACTION_COPY );
                    else
                    	evt.rejectDrag();
                }
            };
            makeDropTarget( c );

    }

    private void makeDropTarget(final Component c)
    {
        final var dt = new DropTarget();
        try
        {
        	dt.addDropTargetListener( dropListener );
        }
        catch(TooManyListenersException e )
        {
        	 logger.error("Do you have another listener attached ?",e);
        }

        c.addHierarchyListener(_->{
						                Component parent = c.getParent();
						                if( parent == null )
						                	c.setDropTarget( null );
						                else
						                	new DropTarget(c, dropListener);
						        });

        if( c.getParent() != null )
            new DropTarget(c, dropListener);

        if( c instanceof Container cont)
        {
            for(Component cp : cont.getComponents())
                makeDropTarget(cp );
        }
    }

    private boolean isDragOk( final DropTargetDragEvent evt )
    {
    	var ok = false;
        DataFlavor[] flavors = evt.getCurrentDataFlavors();
        var i = 0;
        while( !ok && i < flavors.length )
        {
           final DataFlavor curFlavor = flavors[i];
            if( curFlavor.equals( DataFlavor.javaFileListFlavor ) ||
                curFlavor.isRepresentationClassReader()){
                ok = true;
            }
            i++;
        }
        return ok;
    }

    public static boolean remove( Component c, boolean recursive )
    {
            c.setDropTarget( null );
            if( recursive && ( c instanceof Container cont) ){
            	
            	Component[] comps = cont.getComponents();
               		for (Component comp : comps)
               			remove( comp, recursive );
               		
                return true;
            }
            else
        	{
            	return false;
        	}
    }

    public static interface Listener {
        public abstract void filesDropped( File[] files );

    }


    public static class TransferableObject implements Transferable
    {
        public static final  String MIME_TYPE = "application/x-net.iharder.dnd.TransferableObject";

        public static final  DataFlavor DATA_FLAVOR = new DataFlavor( FileDropDecorator.TransferableObject.class, MIME_TYPE );
        private Fetcher fetcher;
        private Object data;
        private DataFlavor customFlavor;


        public TransferableObject( Object data )
        {
        	this.data = data;
            this.customFlavor = new DataFlavor( data.getClass(), MIME_TYPE );
        }

        public TransferableObject( Fetcher fetcher )
        {   this.fetcher = fetcher;
        }

        public TransferableObject( Class dataClass, Fetcher fetcher )
        {   this.fetcher = fetcher;
            this.customFlavor = new DataFlavor( dataClass, MIME_TYPE );
        }

        public DataFlavor getCustomDataFlavor()
        {
        	return customFlavor;
        }

        @Override
		public DataFlavor[] getTransferDataFlavors()
        {
            if( customFlavor != null )
                return new DataFlavor[]
                {
                   customFlavor,
                   DATA_FLAVOR,
                   DataFlavor.imageFlavor
                };
            else
                return new DataFlavor[]
                {
                	DATA_FLAVOR,
                    DataFlavor.imageFlavor
                };
        }

        @Override
		public Object getTransferData( DataFlavor flavor ) throws UnsupportedFlavorException, IOException
        {
            if( flavor.equals( DATA_FLAVOR ) )
                return fetcher == null ? data : fetcher.getObject();

                  throw new UnsupportedFlavorException(flavor);
        }

        @Override
		public boolean isDataFlavorSupported( DataFlavor flavor )
        {
           return ( flavor.equals( DATA_FLAVOR ));
        }

        public static interface Fetcher
        {
            public abstract Object getObject();
        }
    }
}