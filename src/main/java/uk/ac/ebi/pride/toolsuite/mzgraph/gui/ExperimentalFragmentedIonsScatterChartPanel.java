package uk.ac.ebi.pride.toolsuite.mzgraph.gui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import uk.ac.ebi.pride.utilities.iongen.model.PeakSet;
import uk.ac.ebi.pride.utilities.iongen.model.PrecursorIon;
import uk.ac.ebi.pride.utilities.mol.ProductIonPair;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.axis.DiffDaltonAxis;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation.IonAnnotation;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.graph.MzGraphConstants;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.tooltip.ExperimentalFragmentedIonsScatterChartTooltipGenerator;
import uk.ac.ebi.pride.toolsuite.mzgraph.gui.data.ExperimentalFragmentedIonsDataset;
import uk.ac.ebi.pride.toolsuite.mzgraph.gui.data.ExperimentalFragmentedIonsTableModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Creator: Qingwei-XU
 * Date: 11/10/12
 */

public class ExperimentalFragmentedIonsScatterChartPanel extends JPanel implements PropertyChangeListener {
    /**
     * m/z fraction, the values is {@value}.
     */
    private ChartPanel chartPanel;

    private ExperimentalFragmentedIonsDataset dataset;

    private void init(ExperimentalFragmentedIonsDataset dataset) {
        this.dataset = dataset;

        JFreeChart chart = ChartFactory.createScatterPlot(
                null,
                "Error (Da)",
                "M/Z (Da)",
                dataset,
                PlotOrientation.HORIZONTAL,
                true,
                true,
                false);

        XYPlot plot = (XYPlot) chart.getPlot();

        dataset.addChangeListener(plot);
        plot.setBackgroundPaint(Color.white);
        plot.setRangeZeroBaselineVisible(false);
        plot.setRangeGridlinesVisible(false);
        plot.setRangeCrosshairVisible(false);
        plot.setOutlineVisible(false);

        final Marker startMarker = new ValueMarker(-0.5);
        startMarker.setPaint(Color.black);
        final Marker zeroMarker = new ValueMarker(0);
        zeroMarker.setPaint(Color.black);
        final Marker endMarker = new ValueMarker(0.5);
        endMarker.setPaint(Color.black);
        plot.addRangeMarker(startMarker);
        plot.addRangeMarker(zeroMarker);
        plot.addRangeMarker(endMarker);

        XYItemRenderer renderer = plot.getRenderer();
        renderer.setSeriesPaint(0, Color.red);
        renderer.setSeriesPaint(1, Color.blue);
        renderer.setBaseToolTipGenerator(new ExperimentalFragmentedIonsScatterChartTooltipGenerator());

        chart.removeLegend();

        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        yAxis.setTickUnit(new NumberTickUnit(200d));
        yAxis.setAutoRange(true);
        plot.setDomainAxis(yAxis);

        DiffDaltonAxis xAxis = new DiffDaltonAxis(-1, 1);
        xAxis.setRange(-1, 1);
        xAxis.setTickUnit(new NumberTickUnit(MzGraphConstants.INTERVAL_RANGE));
        xAxis.setAutoRange(true);
        xAxis.setTickLabelsVisible(true);
        plot.setRangeAxis(xAxis);

        this.chartPanel = new ChartPanel(chart);
        setLayout(new BorderLayout());
        add(chartPanel, BorderLayout.CENTER);
    }

    public ExperimentalFragmentedIonsScatterChartPanel(PrecursorIon precursorIon, ProductIonPair ionPair,
                                                       PeakSet peakSet) {
        ExperimentalFragmentedIonsTableModel tableModel = new ExperimentalFragmentedIonsTableModel(precursorIon, ionPair);
        tableModel.setPeaks(peakSet);
        ExperimentalFragmentedIonsDataset dataset = new ExperimentalFragmentedIonsDataset(tableModel);
        init(dataset);
    }

    public ExperimentalFragmentedIonsScatterChartPanel(PrecursorIon precursorIon, ProductIonPair ionPair,
                                                       double[] mzArray, double[] intensityArray) {
        ExperimentalFragmentedIonsTableModel tableModel = new ExperimentalFragmentedIonsTableModel(precursorIon, ionPair);
        tableModel.setPeaks(mzArray, intensityArray);
        ExperimentalFragmentedIonsDataset dataset = new ExperimentalFragmentedIonsDataset(tableModel);
        init(dataset);
    }

    public ExperimentalFragmentedIonsScatterChartPanel(ExperimentalFragmentedIonsDataset dataset) {
        init(dataset);
    }

    public ExperimentalFragmentedIonsScatterChartPanel(PrecursorIon precursorIon, double[] mzArray, double[] intensityArray) {
        this(precursorIon, ProductIonPair.B_Y, mzArray, intensityArray);
    }

    public ExperimentalFragmentedIonsScatterChartPanel(PrecursorIon precursorIon, PeakSet peakSet) {
        this(precursorIon, ProductIonPair.B_Y, peakSet);
    }

    public ExperimentalFragmentedIonsScatterChartPanel(PrecursorIon precursorIon, ProductIonPair ionPair,
                                                       java.util.List<IonAnnotation> manualAnnotations) {
        ExperimentalFragmentedIonsTableModel tableModel = new ExperimentalFragmentedIonsTableModel(precursorIon, ionPair, manualAnnotations);
        ExperimentalFragmentedIonsDataset dataset = new ExperimentalFragmentedIonsDataset(tableModel);
        init(dataset);
    }

    public ExperimentalFragmentedIonsScatterChartPanel(PrecursorIon precursorIon, java.util.List<IonAnnotation> manualAnnotations) {
        this(precursorIon, ProductIonPair.B_Y, manualAnnotations);
    }

    public ChartPanel getChartPanel() {
        return chartPanel;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(ExperimentalFragmentedIonsTable.FLUSH_TABLEMODEL)) {
            ExperimentalFragmentedIonsTableModel tableModel = (ExperimentalFragmentedIonsTableModel) evt.getNewValue();
            dataset.update(tableModel);

            validate();
            repaint();
        }
    }
}
