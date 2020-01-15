package org.beta;


import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.Border;

import org.apache.log4j.Logger;
import org.magic.services.MTGLogger;


public class FileDrop
{
    private Border normalBorder;
    private DropTargetListener dropListener;
    private static Color defaultBorderColor = new Color( 0f, 0f, 1f, 0.25f );
    private Logger logger = MTGLogger.getLogger(this.getClass());
    private static String ZERO_CHAR_STRING = "" + (char)0;

    
    public static void main(String[] args) {
   	 JPanel myPanel = new JPanel();
   	       new FileDrop( myPanel, new FileDrop.Listener()
   	       {   public void filesDropped( java.io.File[] files )
   	           {   
   	               for(File f : files)
   	            	   System.out.println(f);
   	           }  
   	       });
   	       
   	       
   	       
   	       JFrame f = new JFrame();
   	    		   f.getContentPane().add(myPanel);
   	    		   f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
   	       f.setVisible(true);
   }
    
    
    public FileDrop(final Component c,final Listener listener )
    {   
    	this( c,BorderFactory.createMatteBorder( 2, 2, 2, 2, defaultBorderColor ), true, listener );
    } 
    
    public FileDrop(final Component c, final Border dragBorder,  final boolean recursive, final Listener listener) 
    {   
            dropListener = new DropTargetListener()
            {   
            	public void dragEnter( DropTargetDragEvent evt )
                {      
                    if( isDragOk(evt ) )
                    {
                        if( c instanceof JComponent )
                        {  
                        	JComponent jc = (JComponent) c;
                            normalBorder = jc.getBorder();
                            jc.setBorder( dragBorder );
                        }    
                        evt.acceptDrag(DnDConstants.ACTION_COPY );
                    } 
                    else 
                    { 
                        evt.rejectDrag();
                    } 
                }  

                public void dragOver( DropTargetDragEvent evt ) 
                {   
                	//do nothing
                }   

                public void drop( DropTargetDropEvent evt )
                {   
                	logger.trace( "FileDrop: drop event." );
                    try 
                    {   // Get whatever was dropped
                        Transferable tr = evt.getTransferable();

                        // Is it a file list?
                        if (tr.isDataFlavorSupported (DataFlavor.javaFileListFlavor))
                        {
                            evt.acceptDrop ( DnDConstants.ACTION_COPY );
                            List<File> fileList = (List)tr.getTransferData(DataFlavor.javaFileListFlavor);

                            if( listener != null )
                                listener.filesDropped( fileList.toArray(new File[fileList.size()]) );
                            evt.getDropTargetContext().dropComplete(true);
                            logger.trace( "FileDrop: drop complete." );
                        }
                        else
                        {
                            DataFlavor[] flavors = tr.getTransferDataFlavors();
                            boolean handled = false;
                            for (int zz = 0; zz < flavors.length; zz++) {
                                if (flavors[zz].isRepresentationClassReader()) {
                                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                                    Reader reader = flavors[zz].getReaderForText(tr);
                                    BufferedReader br = new BufferedReader(reader);
                                    if(listener != null)
                                        listener.filesDropped(createFileArray(br));

                                    evt.getDropTargetContext().dropComplete(true);
                                    handled = true;
                                    break;
                                }
                            }
                            if(!handled){
                                logger.error( "FileDrop: not a file list or reader - abort." );
                                evt.rejectDrop();
                            }
                           
                        }  
                    } 
                    catch ( Exception io) 
                    {   
                    	logger.error( "FileDrop: IOException - abort:",io );
                        evt.rejectDrop();
                    }   
                    finally
                    {
                        if( c instanceof JComponent )
                        {   JComponent jc = (JComponent) c;
                            jc.setBorder( normalBorder );
                            logger.trace( "FileDrop: normal border restored." );
                        }
                    } 
                } 

                public void dragExit( DropTargetEvent evt ) 
                {   
                	if( c instanceof JComponent )
                    {   JComponent jc = (JComponent) c;
                        jc.setBorder( normalBorder );
                        logger.trace( "FileDrop: normal border restored." );
                    }
                }  

                public void dropActionChanged( DropTargetDragEvent evt ) 
                {   
                	logger.trace( "FileDrop: dropActionChanged event." );
                    if( isDragOk(evt ) )
                    {  
                        evt.acceptDrag( DnDConstants.ACTION_COPY );
                        logger.trace( "FileDrop: event accepted." );
                    }  
                    else 
                    {   evt.rejectDrag();
                        logger.trace( "FileDrop: event rejected." );
                    }  
                }
            };
            makeDropTarget( c, recursive );
       
    }   
     
     private File[] createFileArray(BufferedReader bReader)
     {
        try { 
            List<File> list = new ArrayList<>();
            String line = null;
            while ((line = bReader.readLine()) != null) {
                try {
                    // kde seems to append a 0 char to the end of the reader
                    if(ZERO_CHAR_STRING.equals(line)) continue; 
                    
                    File file = new File(new URI(line));
                    list.add(file);
                } catch (Exception ex) {
                	logger.error("Error with " + line + ": " + ex.getMessage());
                }
            }

            return list.toArray(new File[list.size()]);
        } catch (IOException ex) {
        	 logger.error("FileDrop: IOException",ex);
        }
        return new File[0];
     }
    
    private void makeDropTarget( final Component c, boolean recursive )
    {
        // Make drop target
        final DropTarget dt = new DropTarget();
        try
        {  
        	dt.addDropTargetListener( dropListener );
        }  
        catch( java.util.TooManyListenersException e )
        {   
        	 logger.trace("FileDrop: Drop will not work due to previous error. Do you have another listener attached?" );
        }  
        
        c.addHierarchyListener( new HierarchyListener(){   
        	public void hierarchyChanged( HierarchyEvent evt )
            {   
        		logger.trace("FileDrop: Hierarchy changed." );
                Component parent = c.getParent();
                if( parent == null )
                {   
                	c.setDropTarget( null );
                	logger.trace("FileDrop: Drop target cleared from component." );
                } 
                else
                {   new DropTarget(c, dropListener);
                    logger.trace("FileDrop: Drop target added to component." );
                }   
            }   
        });
        
        if( c.getParent() != null )
            new DropTarget(c, dropListener);
        
        if( recursive && (c instanceof Container ) )
        {   
            Container cont = (Container) c;
            Component[] comps = cont.getComponents();
            
            // Set it's components as listeners also
            for( int i = 0; i < comps.length; i++ )
                makeDropTarget(comps[i], recursive );
        }   // end if: recursively set components as listener
    }   // end dropListener
    
    
    
    /** Determine if the dragged data is a file list. */
    private boolean isDragOk( final DropTargetDragEvent evt )
    {   
    	boolean ok = false;
        
        // Get data flavors being dragged
        DataFlavor[] flavors = evt.getCurrentDataFlavors();
        
        // See if any of the flavors are a file list
        int i = 0;
        while( !ok && i < flavors.length )
        {   
           final DataFlavor curFlavor = flavors[i];
            if( curFlavor.equals( java.awt.datatransfer.DataFlavor.javaFileListFlavor ) ||
                curFlavor.isRepresentationClassReader()){
                ok = true;
            }
            i++;
        }
        return ok;
    }
    
    /**
     * Removes the drag-and-drop hooks from the component and optionally
     * from the all children. You should call this if you add and remove
     * components after you've set up the drag-and-drop.
     * This will recursively unregister all components contained within
     * <var>c</var> if <var>c</var> is a {@link java.awt.Container}.
     *
     * @param c The component to unregister as a drop target
     * @since 1.0
     */
    public static boolean remove( java.awt.Component c)
    {   return remove( null, c, true );
    }   // end remove
    
    public static boolean remove( PrintStream out, Component c, boolean recursive )
    { 
            c.setDropTarget( null );
            if( recursive && ( c instanceof java.awt.Container ) )
            {   Component[] comps = ((java.awt.Container)c).getComponents();
                for( int i = 0; i < comps.length; i++ )
                    remove( out, comps[i], recursive );
                return true;
            } 
            else 
        	{
            	return false;
        	}
    }  
    
    public static interface Listener {
        public abstract void filesDropped( java.io.File[] files );
    } 
    
    public static class Event extends java.util.EventObject 
    {
		private static final long serialVersionUID = 1L;
		private File[] files;
        public Event(File[] files, Object source ) {
            super( source );
            this.files = files;
        }   
        public File[] getFiles() {
            return files;
        }
    } 
    
    public static class TransferableObject implements java.awt.datatransfer.Transferable
    {
        public final static String MIME_TYPE = "application/x-net.iharder.dnd.TransferableObject";

        public final static java.awt.datatransfer.DataFlavor DATA_FLAVOR = new java.awt.datatransfer.DataFlavor( FileDrop.TransferableObject.class, MIME_TYPE );
        private Fetcher fetcher;
        private Object data;
        private java.awt.datatransfer.DataFlavor customFlavor; 

        
        public TransferableObject( Object data )
        {   this.data = data;
            this.customFlavor = new java.awt.datatransfer.DataFlavor( data.getClass(), MIME_TYPE );
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


    /* ********  T R A N S F E R A B L E   M E T H O D S  ******** */    


        /**
         * Returns a two- or three-element array containing first
         * the custom data flavor, if one was created in the constructors,
         * second the default {@link #DATA_FLAVOR} associated with
         * {@link TransferableObject}, and third the
         * {@link java.awt.datatransfer.DataFlavor.stringFlavor}.
         *
         * @return An array of supported data flavors
         * @since 1.1
         */
        public java.awt.datatransfer.DataFlavor[] getTransferDataFlavors() 
        {   
            if( customFlavor != null )
                return new java.awt.datatransfer.DataFlavor[]
                {   customFlavor,
                    DATA_FLAVOR,
                   DataFlavor.stringFlavor
                };  // end flavors array
            else
                return new java.awt.datatransfer.DataFlavor[]
                {   DATA_FLAVOR,
                    java.awt.datatransfer.DataFlavor.stringFlavor
                };  // end flavors array
        }   // end getTransferDataFlavors



        /**
         * Returns the data encapsulated in this {@link TransferableObject}.
         * If the {@link Fetcher} constructor was used, then this is when
         * the {@link Fetcher#getObject getObject()} method will be called.
         * If the requested data flavor is not supported, then the
         * {@link Fetcher#getObject getObject()} method will not be called.
         *
         * @param flavor The data flavor for the data to return
         * @return The dropped data
         * @since 1.1
         */
        public Object getTransferData( java.awt.datatransfer.DataFlavor flavor )
        throws java.awt.datatransfer.UnsupportedFlavorException, java.io.IOException 
        {   
            // Native object
            if( flavor.equals( DATA_FLAVOR ) )
                return fetcher == null ? data : fetcher.getObject();

            // String
            if( flavor.equals( java.awt.datatransfer.DataFlavor.stringFlavor ) )
                return fetcher == null ? data.toString() : fetcher.getObject().toString();

            // We can't do anything else
            throw new java.awt.datatransfer.UnsupportedFlavorException(flavor);
        }   // end getTransferData




        /**
         * Returns <tt>true</tt> if <var>flavor</var> is one of the supported
         * flavors. Flavors are supported using the <code>equals(...)</code> method.
         *
         * @param flavor The data flavor to check
         * @return Whether or not the flavor is supported
         * @since 1.1
         */
        public boolean isDataFlavorSupported( java.awt.datatransfer.DataFlavor flavor ) 
        {
            // Native object
            if( flavor.equals( DATA_FLAVOR ) )
                return true;

            // String
            if( flavor.equals( java.awt.datatransfer.DataFlavor.stringFlavor ) )
                return true;

            // We can't do anything else
            return false;
        }   // end isDataFlavorSupported


    /* ********  I N N E R   I N T E R F A C E   F E T C H E R  ******** */    

        /**
         * Instead of passing your data directly to the {@link TransferableObject}
         * constructor, you may want to know exactly when your data was received
         * in case you need to remove it from its source (or do anyting else to it).
         * When the {@link #getTransferData getTransferData(...)} method is called
         * on the {@link TransferableObject}, the {@link Fetcher}'s
         * {@link #getObject getObject()} method will be called.
         *
         * @author Robert Harder
         * @copyright 2001
         * @version 1.1
         * @since 1.1
         */
        public static interface Fetcher
        {
            /**
             * Return the object being encapsulated in the
             * {@link TransferableObject}.
             *
             * @return The dropped object
             * @since 1.1
             */
            public abstract Object getObject();
        }   // end inner interface Fetcher



    }   // end class TransferableObject

    
    
    
    
}   // end class FileDrop