package uk.ac.ebi.pride.toolsuite.mzgraph.chart.graph;

import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.util.MzGraphDatasetUtils;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.NumberFormat;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 21-Jun-2010
 * Time: 15:31:04
 */
public class ChromatogramPanel extends MzGraphPanel {

    private XYSeriesCollection chromaDataset;
    private XYAreaRenderer chromaRenderer;

    public ChromatogramPanel() {
        this(MzGraphDatasetUtils.createXYDataset(MzGraphConstants.DEFAULT_CHROMATOGRAM_DATASET_NAME, null, null));
    }

    public ChromatogramPanel(double[] time, double[] intensity) {
        this(MzGraphDatasetUtils.createXYDataset(MzGraphConstants.DEFAULT_CHROMATOGRAM_DATASET_NAME, time, intensity));
    }

    public ChromatogramPanel(XYSeriesCollection dataset) {
        super(MzGraphConstants.DEFAULT_SPECTRUM_CHART_TITLE,
                MzGraphConstants.DEFAULT_TIME_AXIS_LABEL,
                MzGraphConstants.DEFAULT_INTENSITY_AXIS_LABEL,
                MzGraphConstants.DEFAULT_PLOT_ORIENTATION,
                MzGraphConstants.DEFAULT_LEGEND_VISIBILITY,
                MzGraphConstants.DEFAULT_GRID_LINE_VISIBILITY,
                MzGraphConstants.DEFAULT_X_AXIS_UPPER_MARGIN,
                MzGraphConstants.DEFAULT_Y_AXIS_UPPER_MARGIN,
                MzGraphConstants.DEFAULT_CHART_THEME);

        this.chromaDataset = dataset;
        setType("Chromatogram");
        paintGraph();
    }

    public void removeAllData() {
        chromaDataset.removeAllSeries();
    }

    @Override
    protected Number[][] getGraphDataset() {
        // get the raw peak list
        XYSeries chromas = chromaDataset.getSeries(MzGraphConstants.DEFAULT_CHROMATOGRAM_DATASET_NAME);
        return MzGraphDatasetUtils.getXYSeriesData(chromas);
    }

    public void setGraphData(double[] x, double[] y) {
        removeAllData();
        XYSeries series = MzGraphDatasetUtils.createXYSeries(MzGraphConstants.DEFAULT_CHROMATOGRAM_DATASET_NAME, x, y);
        chromaDataset.addSeries(series);
    }

    @Override
    protected void setupGraph() {
        super.setupGraph();
        plot.setForegroundAlpha(0.6f);
    }

    protected void setGraphDataset() {
        plot.setDataset(chromaDataset);
    }

    protected void setGraphRenderer() {
        chromaRenderer = new XYAreaRenderer(XYAreaRenderer.AREA_AND_SHAPES);
        chromaRenderer.setOutline(true);
        chromaRenderer.setSeriesOutlinePaint(0, Color.red, false);
        // display each data point
        Shape rectangle = new RoundRectangle2D.Float(-4.0f, -4.0f, 8.0f, 8.0f, 4.0f, 4.0f);
        chromaRenderer.setSeriesShape(0, rectangle, false);

        XYToolTipGenerator toolTip = new StandardXYToolTipGenerator("Time: {1}, Intensity: {2}",
                NumberFormat.getNumberInstance(), NumberFormat.getNumberInstance());
        chromaRenderer.setBaseToolTipGenerator(toolTip);
        plot.setRenderer(chromaRenderer);
    }
}
