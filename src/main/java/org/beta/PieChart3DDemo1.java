package org.beta;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.jfree.chart3d.Chart3D;
import org.jfree.chart3d.Chart3DFactory;
import org.jfree.chart3d.Chart3DPanel;
import org.jfree.chart3d.Orientation;
import org.jfree.chart3d.TitleAnchor;
import org.jfree.chart3d.data.PieDataset3D;
import org.jfree.chart3d.data.StandardPieDataset3D;
import org.jfree.chart3d.graphics3d.RenderedElement;
import org.jfree.chart3d.graphics3d.swing.DisplayPanel3D;
import org.jfree.chart3d.interaction.Chart3DMouseEvent;
import org.jfree.chart3d.interaction.Chart3DMouseListener;
import org.jfree.chart3d.legend.LegendAnchor;

/**
 * A demo showing a simple pie chart in 3D.
 */
@SuppressWarnings("serial")
public class PieChart3DDemo1 extends JFrame {

    /**
     * Creates a new test app.
     *
     * @param title  the frame title.
     */
    public PieChart3DDemo1(String title) {
        super(title);
        
        getContentPane().add(createDemoPanel());
    }

    /**
     * Returns a panel containing the content for the demo.  This method is
     * used across all the individual demo applications to allow aggregation 
     * into a single "umbrella" demo (OrsonChartsDemo).
     * 
     * @return A panel containing the content for the demo.
     */
    public static JPanel createDemoPanel() {
        final JPanel content = new JPanel(new BorderLayout());
        
        PieDataset3D<String> dataset = createDataset();
        Chart3D chart = createChart(dataset);
        Chart3DPanel chartPanel = new Chart3DPanel(chart);
        chartPanel.setMargin(0.05);
        chartPanel.addChartMouseListener(new Chart3DMouseListener() {

            @Override
            public void chartMouseClicked(Chart3DMouseEvent event) {
                RenderedElement element = event.getElement();
                if (element != null) {
                    JOptionPane.showMessageDialog(content, 
                            Chart3D.renderedElementToString(event.getElement()));
                }
            }

            @Override
            public void chartMouseMoved(Chart3DMouseEvent event) {
                
            }
        });
        content.add(chartPanel);
        content.add(new DisplayPanel3D(chartPanel));
        
        return content;
    }
    
    /**
     * Creates a pie chart based on the supplied dataset.
     * 
     * @param dataset  the dataset.
     * 
     * @return A pie chart. 
     */
    private static Chart3D createChart(PieDataset3D<String> dataset) {
        Chart3D chart = Chart3DFactory.createPieChart(
                "New Zealand Exports 2012", 
                "http://www.stats.govt.nz/browse_for_stats/snapshots-of-nz/nz-in-profile-2013.aspx", 
                dataset);
        chart.setTitleAnchor(TitleAnchor.TOP_LEFT);
        chart.setLegendPosition(LegendAnchor.BOTTOM_CENTER,
                Orientation.HORIZONTAL);
        return chart;
    }
    
    /**
     * Creates a sample dataset (hard-coded for the purpose of keeping the
     * demo self-contained - in practice you would normally read your data
     * from a file, database or other source).
     * 
     * @return A sample dataset.
     */
    private static PieDataset3D<String> createDataset() {
        StandardPieDataset3D<String> dataset = new StandardPieDataset3D<>();
        dataset.add("Milk Products", 11625);
        dataset.add("Meat", 5114);
        dataset.add("Wood/Logs", 3060);
        dataset.add("Crude Oil", 2023);
        dataset.add("Machinery", 1865);
        dataset.add("Fruit", 1587);
        dataset.add("Fish", 1367);
        dataset.add("Wine", 1177);
        dataset.add("Other", 18870);
        return dataset; 
    }

    /**
     * Starting point for the app.
     *
     * @param args  command line arguments (ignored).
     */
    public static void main(String[] args) {
        var app = new PieChart3DDemo1(
                "OrsonCharts: PieChart3DDemo1.java");
        app.pack();
        app.setVisible(true);
    }

}