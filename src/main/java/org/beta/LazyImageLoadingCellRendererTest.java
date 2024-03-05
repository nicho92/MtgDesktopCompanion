package org.beta;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
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
import org.magic.api.interfaces.MTGProduct;

public class LazyImageLoadingCellRendererTest
{

    private JPanel mainPanel = new JPanel();
    private JList<MTGProduct> list = new JList<>();
    private JScrollPane scroll = new JScrollPane();

    public LazyImageLoadingCellRendererTest()
    {
        mainPanel.setBackground(new Color(129, 133, 142));

        scroll.setViewportView(list);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setPreferredSize(new Dimension(80, 200));

        list.setCellRenderer(new LazyImageLoadingCellRenderer<>(list,LazyImageLoadingCellRendererTest::loadAndProcessImage));
        DefaultListModel<MTGProduct> model = new DefaultListModel<>();

        for (int i=0; i<1000; i++)
        {
            model.addElement(new MTGCard());
        }
        list.setModel(model);

        mainPanel.add(scroll);
    }

    public static void main(String[] args)
    {

        EventQueue.invokeLater(()->
            {
                JFrame frame = new JFrame("WorkerTest");
                frame.setContentPane(
                    new LazyImageLoadingCellRendererTest().mainPanel);
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.setLocation(300, 300);
                frame.setMinimumSize(new Dimension(160, 255));
                frame.setVisible(true);
        });
    }

    private static final Random random = new Random(0);

    private static BufferedImage loadAndProcessImage(MTGProduct product)
    {
        String id = product.getStoreId();
        int w = 100;
        int h = 20;
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.GREEN);
        g.fillRect(0, 0, w, h);
        g.setColor(Color.BLACK);
        g.drawString(id, 10, 16);
        g.dispose();

        long delay = 500L + random.nextInt(3000);
        try
        {
            Thread.sleep(delay);
        }
        catch (InterruptedException e)
        {
        	Thread.currentThread().interrupt();
            e.printStackTrace();
        }
        return image;
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