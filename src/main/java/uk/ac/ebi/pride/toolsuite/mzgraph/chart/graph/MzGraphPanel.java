package uk.ac.ebi.pride.toolsuite.mzgraph.chart.graph;

import org.jfree.chart.*;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.utilities.util.Tuple;
import uk.ac.ebi.pride.toolsuite.gui.io.FileExtension;
import uk.ac.ebi.pride.toolsuite.gui.io.SaveComponentUtils;
import uk.ac.ebi.pride.toolsuite.gui.io.SaveImageDialog;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.legend.LegendFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Generic mz graph panel
 * <p/>
 * User: rwang
 * Date: 21-Jun-2010
 * Time: 15:35:22
 */
public abstract class MzGraphPanel extends JPanel implements ChartMouseListener, ActionListener {
    private static final Logger logger = LoggerFactory.getLogger(MzGraphPanel.class);

    public static final String GRID_LINE_COMMAND = "GRID_LINE";
    public static final String SAVE_AS = "SAVE";
    public static final String PRINT_COMMAND = "PRINT";
    public static final String EXPORT = "EXPORT";
    public static final String ZOOM_OUT = "ZOOM_OUT";

    private String title;
    private String type;
    private String source;
    private Comparable id;
    private String xAxisLabel;
    private String yAxisLabel;
    private PlotOrientation orientation;
    private boolean legendVisibility;
    private boolean gridLineVisibility;
    private double xAxisUpperMargin;
    private double yAxisUpperMargin;

    protected ChartTheme chartTheme;
    protected JFreeChart chart;
    protected ValueAxis xAxis;
    protected ValueAxis yAxis;
    protected ChartPanel chartPanel;
    protected XYPlot plot;
    private XYTextAnnotation overflowAnnotation1;
    private XYTextAnnotation overflowAnnotation2;

    public MzGraphPanel(String title,
                        String xAxisLabel,
                        String yAxisLabel,
                        PlotOrientation orientation,
                        boolean legendVisibility,
                        boolean gridLineVisibility,
                        double xAxisUpperMargin,
                        double yAxisUpperMargin,
                        ChartTheme chartTheme) {
        this.title = title;
        this.xAxisLabel = xAxisLabel;
        this.yAxisLabel = yAxisLabel;
        this.orientation = orientation;
        this.legendVisibility = legendVisibility;
        this.gridLineVisibility = gridLineVisibility;
        this.xAxisUpperMargin = xAxisUpperMargin;
        this.yAxisUpperMargin = yAxisUpperMargin;
        this.chartTheme = chartTheme;
        this.chart = null;
        this.xAxis = null;
        this.yAxis = null;
        this.chartPanel = null;
        this.plot = null;
        // set the layout        
        this.setLayout(new BorderLayout());
    }

    /**
     * Reset the MzGraphPanel
     */
    public void reset() {
        // This will reset the zoom status of the panel
        doZoomOut();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        if (chart != null) {
            chart.setTitle(title);
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void addDeltaOverflowAnnotation() {
        java.util.List<XYAnnotation> annotations = plot.getAnnotations();
        for (XYAnnotation annotation : annotations) {
            plot.removeAnnotation(annotation, true);
        }

        ValueAxis domainAxis = plot.getDomainAxis();
        ValueAxis rangeAxis = plot.getRangeAxis();
        double lx = domainAxis.getRange().getLength() * 0.98;
        double ly = rangeAxis.getRange().getLength() * 0.95;
        overflowAnnotation1 = new XYTextAnnotation(MzGraphConstants.DELTA_MZ_OVERFLOW1, lx, ly);
        overflowAnnotation1.setTextAnchor(TextAnchor.TOP_RIGHT);
        plot.addAnnotation(overflowAnnotation1, true);

        ly = rangeAxis.getRange().getLength() * 0.90;
        overflowAnnotation2 = new XYTextAnnotation(MzGraphConstants.DELTA_MZ_OVERFLOW2, lx, ly);
        overflowAnnotation2.setTextAnchor(TextAnchor.TOP_RIGHT);
        plot.addAnnotation(overflowAnnotation2, true);
    }

    public void removeDeltaOverflowAnnotation() {
        if (this.overflowAnnotation1 != null) {
            plot.removeAnnotation(this.overflowAnnotation1, true);
        }
        if (this.overflowAnnotation2 != null) {
            plot.removeAnnotation(this.overflowAnnotation2, true);
        }
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Comparable getId() {
        return id;
    }

    public void setId(Comparable id) {
        this.id = id;
    }

    public String getxAxisLabel() {
        return xAxisLabel;
    }

    public void setxAxisLabel(String xAxisLabel) {
        this.xAxisLabel = xAxisLabel;
        if (xAxis != null) {
            xAxis.setLabel(xAxisLabel);
        }
    }

    public String getyAxisLabel() {
        return yAxisLabel;
    }

    public void setyAxisLabel(String yAxisLabel) {
        this.yAxisLabel = yAxisLabel;
        if (yAxis != null) {
            yAxis.setLabel(yAxisLabel);
        }
    }

    public PlotOrientation getOrientation() {
        return orientation;
    }

    public void setOrientation(PlotOrientation orientation) {
        this.orientation = orientation;
        if (plot != null) {
            plot.setOrientation(orientation);
        }
    }

    public boolean isLegendVisible() {
        return legendVisibility;
    }

    public void setLegendVisibility(boolean legendVisibility) {
        this.legendVisibility = legendVisibility;
        // todo: implement update plot
    }

    public boolean isGridLineVisibility() {
        return gridLineVisibility;
    }

    public void setGridLineVisibility(boolean gridLineVisibility) {
        this.gridLineVisibility = gridLineVisibility;
        if (plot != null) {
            plot.setRangeGridlinesVisible(gridLineVisibility);
            plot.setDomainGridlinesVisible(gridLineVisibility);
        }
    }

    public double getxAxisUpperMargin() {
        return xAxisUpperMargin;
    }

    public void setxAxisUpperMargin(double xAxisUpperMargin) {
        this.xAxisUpperMargin = xAxisUpperMargin;
        if (xAxis != null) {
            xAxis.setUpperMargin(xAxisUpperMargin);
        }
    }

    public double getyAxisUpperMargin() {
        return yAxisUpperMargin;
    }

    public void setyAxisUpperMargin(double yAxisUpperMargin) {
        this.yAxisUpperMargin = yAxisUpperMargin;
        if (yAxis != null) {
            yAxis.setUpperMargin(yAxisUpperMargin);
        }
    }

    public void paintGraph() {
        // create plot
        createGraph();
        // set axises
        setPlotAxis();
        // add datasets
        setGraphDataset();
        // add renderers
        setGraphRenderer();
        // setup and configure plot and chart
        setupGraph();
    }

    protected void createGraph() {
        plot = new XYPlot();
        chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legendVisibility);
        chartPanel = new ChartPanel(chart);
        chartPanel.setMouseZoomable(true, true);
//        chartPanel.setRangeZoomable(false);
        //chartPanel.setMouseWheelEnabled(true);
        chartPanel.setZoomAroundAnchor(true);
        chartPanel.setDisplayToolTips(true);
        chartPanel.addChartMouseListener(this);
        // change the scaling of the chart
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        // get screen size
        Dimension screenDim = toolkit.getScreenSize();
        chartPanel.setMaximumDrawWidth(screenDim.width);
        chartPanel.setMaximumDrawHeight(screenDim.height);
        // not popup menu
        chartPanel.setPopupMenu(null);
        this.add(chartPanel);
    }

    protected void setupGraph() {
        chartTheme.apply(chart);
        // set plot orientation
        plot.setOrientation(orientation);
        plot.setBackgroundPaint(Color.white);
        plot.setRangeGridlinePaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.lightGray);
        setGridLineVisibility(gridLineVisibility);
        // set rendering order
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
    }

    protected void setPlotAxis() {
        // tick unit
        TickUnitSource units = NumberAxis.createIntegerTickUnits();
        // x axis
        xAxis = new NumberAxis(xAxisLabel);
        xAxis.setStandardTickUnits(units);
        xAxis.setUpperMargin(xAxisUpperMargin);
        // todo: remove zero

        // y axis
        yAxis = new NumberAxis(yAxisLabel);
        yAxis.setStandardTickUnits(units);
        yAxis.setUpperMargin(yAxisUpperMargin);

        // add axis
        plot.setDomainAxis(xAxis);
        plot.setRangeAxis(yAxis);

        // set axis offset
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
    }

    protected abstract void setGraphDataset();

    protected abstract Number[][] getGraphDataset();

    protected abstract void setGraphRenderer();

    public void chartMouseClicked(ChartMouseEvent chartMouseEvent) {
        MouseEvent mouseEvent = chartMouseEvent.getTrigger();
        switch (mouseEvent.getModifiers()) {
            case InputEvent.BUTTON3_MASK: {
                doZoomOut();
                break;
            }
        }
    }

    public void chartMouseMoved(ChartMouseEvent chartMouseEvent) {}

    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (GRID_LINE_COMMAND.equals(command)) {
            doHideGridLine();
        } else if (SAVE_AS.equals(command)) {
            doSave();
        } else if (PRINT_COMMAND.equals(command)) {
            doPrint();
        } else if (EXPORT.equals(command)) {
            doExport();
        } else if (ZOOM_OUT.equals(command)) {
            doZoomOut();
        }
    }

    public void doHideGridLine() {
        setGridLineVisibility(!plot.isRangeGridlinesVisible());
    }

    private void doSave() {
        try {
            // store existing legend
            LegendTitle existingLegend = chart.getLegend();
            // create a temporary legend
            Map<Tuple<String, String>, Paint> entries = new LinkedHashMap<Tuple<String, String>, Paint>();
            if (source != null) {
                entries.put(new Tuple<String, String>("Source", source), Color.white);
            }

            if (id != null) {
                // Note: this is hack
                String idStr = getType() + " ID";
                entries.put(new Tuple<String, String>(idStr, id.toString()), Color.white);
            }

            if (!entries.isEmpty()) {
                LegendTitle tmpLegend = LegendFactory.createLegendFromMap(entries);
                tmpLegend.setPosition(RectangleEdge.BOTTOM);
                chart.addLegend(tmpLegend);
            }
            // file chooser
            SaveImageDialog saveImageDialog = new SaveImageDialog(new File(System.getProperty("user.home")), id == null ? null : id.toString());
            int result = saveImageDialog.showSaveDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                String outputFile = saveImageDialog.getSelectedFile().getAbsolutePath();
                String extensionDesc = saveImageDialog.getFileFilter().getDescription();
                if (FileExtension.PDF.getExtensionDescription().equals(extensionDesc)) {
                    if (!outputFile.endsWith(FileExtension.PDF.getExtension())) {
                        outputFile += FileExtension.PDF.getExtension();
                    }
                    SaveComponentUtils.writeAsPDF(new File(outputFile), this);
                } else if (FileExtension.SVG.getExtensionDescription().equals(extensionDesc)) {
                    if (!outputFile.endsWith(FileExtension.SVG.getExtension())) {
                        outputFile += FileExtension.SVG.getExtension();
                    }
                    SaveComponentUtils.writeAsSVG(new File(outputFile), this);
                } else if (FileExtension.PNG.getExtensionDescription().equals(extensionDesc)) {
                    if (!outputFile.endsWith(FileExtension.PNG.getExtension())) {
                        outputFile += FileExtension.PNG.getExtension();
                    }
                    SaveComponentUtils.writeAsPNG(new File(outputFile), this);
                } else if (FileExtension.JPEG.getExtensionDescription().equals(extensionDesc)) {
                    if (!outputFile.endsWith(FileExtension.JPEG.getExtension())) {
                        outputFile += FileExtension.JPEG.getExtension();
                    }
                    SaveComponentUtils.writeAsJPEG(new File(outputFile), this);
                } else if (FileExtension.GIF.getExtensionDescription().equals(extensionDesc)) {
                    if (!outputFile.endsWith(FileExtension.GIF.getExtension())) {
                        outputFile += FileExtension.GIF.getExtension();
                    }
                    SaveComponentUtils.writeAsGIF(new File(outputFile), this);
                }
            }
            // remove temporary legend
            chart.removeLegend();
            if (existingLegend != null) {
                chart.addLegend(existingLegend);
            }
        } catch (IOException e) {
            logger.warn("Failed to save the graph as an image", e);
        }
    }

    private void doPrint() {
        chartPanel.createChartPrintJob();
    }

    private void doZoomOut() {
        chartPanel.restoreAutoBounds();
    }

    private void doExport() {
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(false);
        int returnVal = chooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            PrintWriter writer = null;
            try {
                writer = new PrintWriter(new FileWriter(file));
                Number[][] dataSet = getGraphDataset();
                for (Number[] data : dataSet) {
                    writer.println(data[0] + "\t" + data[1]);
                }
                writer.flush();
                writer.close();
            } catch (IOException e) {
                logger.warn("Failed to export the peak list", e);
            } finally {
                if (writer != null) {
                    writer.close();
                }
            }
        }
    }
}
