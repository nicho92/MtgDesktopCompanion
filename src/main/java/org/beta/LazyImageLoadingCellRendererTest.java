package org.beta;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;

import org.magic.api.beans.MTGCard;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGProduct;
import org.magic.services.MTGControler;
import org.magic.services.network.URLTools;
import org.magic.services.tools.MTG;

public class LazyImageLoadingCellRendererTest extends JPanel
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JList<MTGProduct> list = new JList<>();

    public LazyImageLoadingCellRendererTest() throws Exception
    {
        setLayout(new BorderLayout());
        var search = MTG.getEnabledPlugin(MTGCardsProvider.class).searchCardByName("Zendikar", null, false);
        
        list.setCellRenderer(new LazyImageLoadingCellRenderer<>(list,this::loadAndProcessImage));
        DefaultListModel<MTGProduct> model = new DefaultListModel<>();

        for (var p : search)
            model.addElement(p);

        list.setModel(model);
        add(new JScrollPane(list),BorderLayout.CENTER);
    }
    
    private BufferedImage loadAndProcessImage(MTGProduct product)
    {
        BufferedImage image = null;
		try {
			image = URLTools.extractAsImage(product.getUrl());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
        return image;
    }

    public static void main(String[] args) throws SQLException
    {
        MTGControler.getInstance().init();
        
        EventQueue.invokeLater(()->
            {
                JFrame frame = new JFrame("WorkerTest");
                try {
					frame.setContentPane(new LazyImageLoadingCellRendererTest());
				} catch (Exception e) {
					e.printStackTrace();
				}
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.setVisible(true);
        });
    }
}

class LazyImageLoadingCellRenderer<T> extends JLabel  implements ListCellRenderer<T>
{
    private static final long serialVersionUID = 1L;
	private final JList<?> owner;
    private final transient Function<? super T, ? extends BufferedImage> imageLookup;
    private final transient Set<T> pendingImages;
    private final transient Map<T, BufferedImage> loadedImages;

    public LazyImageLoadingCellRenderer(JList<?> owner, Function<? super T, ? extends BufferedImage> imageLookup)
    {
        this.owner = Objects.requireNonNull(owner, "The owner may not be null");
        this.imageLookup = Objects.requireNonNull(imageLookup,"The imageLookup may not be null");
        this.loadedImages = new ConcurrentHashMap<>();
        this.pendingImages = Collections.newSetFromMap(new ConcurrentHashMap<T, Boolean>());
        setOpaque(false);
    }

    class ImageLoadingWorker extends SwingWorker<BufferedImage, Void>
    {
        private final T element;

        ImageLoadingWorker(T element)
        {
            this.element = element;
            pendingImages.add(element);
        }

        @Override
        protected BufferedImage doInBackground() throws Exception
        {
            try
            {
                BufferedImage image = imageLookup.apply(element);
                loadedImages.put(element, image);
                pendingImages.remove(element);
                return image;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void done()
        {
            owner.repaint();
        }
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends T> list, T value, int index, boolean isSelected, boolean cellHasFocus)
    {
        BufferedImage image = loadedImages.get(value);
        if (image == null)
        {
            if (!pendingImages.contains(value))
            {
                var worker = new ImageLoadingWorker(value);
                worker.execute();
            }
            setText("Loading...");
            setIcon(null);
        }
        else
        {
            setText(null);
            setIcon(new ImageIcon(image));
        }
        return this;
    }
}