package uk.ac.ebi.pride.toolsuite.mzgraph.chart.graph;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.data.xy.*;
import uk.ac.ebi.pride.utilities.mol.PTModification;
import uk.ac.ebi.pride.utilities.iongen.ion.FragmentIonType;
import uk.ac.ebi.pride.utilities.iongen.ion.FragmentIonTypeColor;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation.AminoAcidAnnotationGenerator;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation.IonAnnotation;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.util.MzGraphDatasetUtils;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.label.AminoAcidAnnotationLabelGenerator;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.label.IonAnnotationLabelGenerator;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.label.MassDiffRangeLabelGenerator;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.renderer.MassDiffRenderer;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.renderer.PeakRangeRenderer;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.renderer.PeakRenderer;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.tooltip.PeakToolTipGenerator;
import uk.ac.ebi.pride.toolsuite.mzgraph.gui.filter.FilterActionEvent;
import uk.ac.ebi.pride.toolsuite.mzgraph.gui.setting.MassErrorToleranceEvent;
import uk.ac.ebi.pride.toolsuite.mzgraph.gui.setting.ShowMassDifferentEvent;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static uk.ac.ebi.pride.toolsuite.mzgraph.chart.graph.MzGraphConstants.*;

/**
 * SpectrumPanel draw a spectrum and its annotations
 * <p/>
 * User: rwang
 * Date: 21-Jun-2010
 * Time: 16:36:15
 */
public class SpectrumPanel extends MzGraphPanel implements PropertyChangeListener {

    /**
     * Below are the action commands
     */
    public final static String HIDE_PEAK_LIST = "HIDE_PEAK_LIST";
    public final static String CLEAR_MASS_DIFF = "CLEAR_MASS_DIFF";

    /**
     * Renderer to draw the peaks
     */
    private PeakRenderer peakRenderer;
    /**
     * Renderer to draw mass differences
     */
    private MassDiffRenderer massDiffRenderer;
    /**
     * Renderer to draw ion series
     */
    private PeakRenderer ionRenderer;
    /**
     * Renderer to draw amino acid annotations
     */
    private PeakRangeRenderer aminoAcidRenderer;
    /**
     * Boolean to indicate the visibility of the raw spectrum
     */
    private boolean basePeakVisibility;
    /**
     * Data model behind this spectrum panel
     */
    private SpectrumPanelModel spectrumPanelModel;

    public SpectrumPanel() {
        this(null, null);
    }

    public SpectrumPanel(double[] mz, double[] intensity) {
        super(DEFAULT_SPECTRUM_CHART_TITLE,
                DEFAULT_MZ_AXIS_LABEL,
                DEFAULT_INTENSITY_AXIS_LABEL,
                DEFAULT_PLOT_ORIENTATION,
                DEFAULT_LEGEND_VISIBILITY,
                DEFAULT_GRID_LINE_VISIBILITY,
                DEFAULT_X_AXIS_UPPER_MARGIN,
                DEFAULT_Y_AXIS_UPPER_MARGIN,
                DEFAULT_CHART_THEME);
        // set type
        setType("Spectrum");

        // peak list dataset and renderer
        spectrumPanelModel = new SpectrumPanelModel();
        spectrumPanelModel.setPeaks(mz, intensity);
        spectrumPanelModel.addPropertyChangeListener(this);

        // visibility
        this.basePeakVisibility = DEFAULT_PEAK_VISIBILITY;

        paintGraph();

    }

    public SpectrumPanelModel getModel() {
        return spectrumPanelModel;
    }

    public void setModel(SpectrumPanelModel spectrumPanelModel) {
        this.spectrumPanelModel = spectrumPanelModel;
    }

    public void reset() {
        // remove all annotations
        java.util.List<XYAnnotation> annotations = plot.getAnnotations();
        for (XYAnnotation annotation : annotations) {
            plot.removeAnnotation(annotation, true);
        }

        // reset peak renderer
        if (peakRenderer != null) {
            peakRenderer.reset();
        }
        // reset amino acid renderer
        if (aminoAcidRenderer != null) {
            aminoAcidRenderer.reset();
        }

        // call reset method in super class
        super.reset();
    }

    public void removeIonAnnotations() {
        spectrumPanelModel.removeIonAnnotations();
    }

    public void setPeaks(double[] mz, double[] intensity) {
        spectrumPanelModel.setPeaks(mz, intensity);
    }

    public void addFragmentIons(List<IonAnnotation> ions) {
        spectrumPanelModel.addAnnotations(ions);
    }

    public void setAminoAcidAnnotationParameters(int peptideLength, Map<Integer, List<PTModification>> modifications) {
        spectrumPanelModel.setAminoAcidAnnotationParameters(peptideLength, modifications);
    }

    public double getAminoAcidAnnotationMassError() {
        return spectrumPanelModel.getAminoAcidAnnotationMassError();
    }

    public void setAminoAcidAnnotationMassError(double massError) {
        spectrumPanelModel.setAminoAcidAnnotationMassError(massError);
    }

    public void setAminoAcidAnnotationGenerator(AminoAcidAnnotationGenerator generator) {
        spectrumPanelModel.setAminoAcidAnnotationGenerator(generator);
    }

    /**
     * Alter the visibility of the peaks
     */
    public void hidePeakList() {
        // reverse the visibility
        basePeakVisibility = !basePeakVisibility;
        plot.setRenderer(DEFAULT_PEAK_DATASET_INDEX, basePeakVisibility ? peakRenderer : null);
    }

    public void hideFragmentIons(Comparable seriesKey) {
        XYSeriesCollection ionDataset = spectrumPanelModel.getIonDataset();
        int cnt = ionDataset.getSeriesCount();
        for (int i = 0; i < cnt; i++) {
            Comparable key = ionDataset.getSeriesKey(i);
            if (key.equals(seriesKey)) {
                Boolean visibility = ionRenderer.getSeriesVisible(i);
                if (visibility == null) {
                    visibility = true;
                }
                ionRenderer.setSeriesVisible(i, !visibility);
            }
        }
    }

    public void clearMassDiffAnnotations() {
        spectrumPanelModel.removeAllMassDiffAnnotations();
    }

    /**
     * Get the current visibility for all fragment ion series.
     *
     * @return Map<Comparable, Boolean> a map of <fragment ion series' name, their visibility>.
     */
    public Map<Comparable, Boolean> getFragmentIonVisibilities() {
        Map<Comparable, Boolean> labels = new LinkedHashMap<Comparable, Boolean>();
        XYSeriesCollection ionDataset = spectrumPanelModel.getIonDataset();
        int count = ionDataset.getSeriesCount();
        for (int i = 0; i < count; i++) {
            Boolean visibility = ionRenderer.getSeriesVisible(i);
            labels.put(ionDataset.getSeriesKey(i), visibility == null ? ionRenderer.getBaseSeriesVisible() : visibility);
        }

        return labels;
    }

    public void hideAminoAcids(Comparable seriesKey) {
        XYIntervalSeriesCollection aminoAcidDataset = spectrumPanelModel.getAminoAcidDataset();
        int cnt = aminoAcidDataset.getSeriesCount();
        for (int i = 0; i < cnt; i++) {
            Comparable key = aminoAcidDataset.getSeriesKey(i);
            if (key.equals(seriesKey)) {
                Boolean visibility = aminoAcidRenderer.getSeriesVisible(i);
                if (visibility == null) {
                    visibility = true;
                }
                aminoAcidRenderer.setSeriesVisible(i, !visibility);
            }
        }
    }

    public Map<Comparable, Boolean> getAminoAcidVisibilities() {
        Map<Comparable, Boolean> labels = new HashMap<Comparable, Boolean>();
        XYIntervalSeriesCollection aminoAcidDataset = spectrumPanelModel.getAminoAcidDataset();
        int count = aminoAcidDataset.getSeriesCount();
        for (int i = 0; i < count; i++) {
            Boolean visibility = aminoAcidRenderer.getSeriesVisible(i);
            labels.put(aminoAcidDataset.getSeriesKey(i), visibility == null ?
                    aminoAcidRenderer.getBaseSeriesVisible() : visibility);
        }

        return labels;
    }

    public void setIonSeriesPaint(FragmentIonType ionType) {
        XYSeriesCollection ionDataset = spectrumPanelModel.getIonDataset();
        int seriesIndex = ionDataset.indexOf(ionType.getName());
        // also need to set renderer color
        if (seriesIndex >= 0) {
            ionRenderer.setSeriesPaint(seriesIndex, FragmentIonTypeColor.getColor(ionType));
        }
    }

    public void setIonSeriesVisibility(FragmentIonType ionType, boolean visibility) {
        XYSeriesCollection ionDataset = spectrumPanelModel.getIonDataset();
        int seriesIndex = ionDataset.indexOf(ionType.getName());
        // also need to set renderer color
        if (seriesIndex >= 0) {
            ionRenderer.setSeriesVisible(seriesIndex, visibility);
        }
    }

    public void setAminoAcidSeriesPaint(FragmentIonType ionType) {
        XYIntervalSeriesCollection aminoAcidDataset = spectrumPanelModel.getAminoAcidDataset();
        int seriesIndex = aminoAcidDataset.indexOf(ionType.getName());
        if (seriesIndex >= 0) {
            aminoAcidRenderer.setSeriesPaint(seriesIndex, FragmentIonTypeColor.getColor(ionType));
        }
    }

    public void setAminoAcidSeriesVisibility(FragmentIonType ionType, boolean visibility) {
        XYIntervalSeriesCollection aminoAcidDataset = spectrumPanelModel.getAminoAcidDataset();
        int seriesIndex = aminoAcidDataset.indexOf(ionType.getName());
        if (seriesIndex >= 0) {
            aminoAcidRenderer.setSeriesVisible(seriesIndex, visibility);
        }
    }

    public void chartMouseClicked(ChartMouseEvent chartMouseEvent) {
        MouseEvent mouseEvent = chartMouseEvent.getTrigger();
        switch (mouseEvent.getModifiers()) {
            case InputEvent.BUTTON1_MASK: {
                ChartEntity entity = chartMouseEvent.getEntity();
                if (entity instanceof XYItemEntity) {
                    XYItemEntity xyItemEntity = (XYItemEntity) entity;
                    XYDataset dataset = xyItemEntity.getDataset();
                    int seriesKey = xyItemEntity.getSeriesIndex();
                    int itemKey = xyItemEntity.getItem();
                    Number x = dataset.getX(seriesKey, itemKey);
                    Number y = dataset.getY(seriesKey, itemKey);
                    spectrumPanelModel.addMassDiffAnnotation(x.doubleValue(), y.doubleValue());
                }
                break;
            }
        }
        super.chartMouseClicked(chartMouseEvent);
    }

    public void chartMouseMoved(ChartMouseEvent chartMouseEvent) {
        ChartEntity entity = chartMouseEvent.getEntity();
        if (!(entity instanceof XYItemEntity)) {
            peakRenderer.setHighlightedItem(-1, -1);
            ionRenderer.setHighlightedItem(-1, -1);
        } else {
            XYItemEntity xyItemEntity = (XYItemEntity) entity;
            int seriesKey = xyItemEntity.getSeriesIndex();
            int itemKey = xyItemEntity.getItem();
            XYDataset dataSet = xyItemEntity.getDataset();
            XYDataItem peakDataItem = null;
            if (dataSet.equals(spectrumPanelModel.getPeakDataset())) {
                peakRenderer.setHighlightedItem(seriesKey, itemKey);
                peakDataItem = spectrumPanelModel.getPeakDataset().getSeries(seriesKey).getDataItem(itemKey);
            } else if (dataSet.equals(spectrumPanelModel.getIonDataset())) {
                ionRenderer.setHighlightedItem(seriesKey, itemKey);
                peakDataItem = spectrumPanelModel.getIonDataset().getSeries(seriesKey).getDataItem(itemKey);
            }
            // set temporary mass difference annotation
            if (peakDataItem != null) {
                spectrumPanelModel.addTempMassDiffAnnotation(peakDataItem.getXValue(), peakDataItem.getYValue());
            }
        }
    }

    /**
     * Set up the attributes for plot and chart
     */
    protected void setupGraph() {
        super.setupGraph();
        // set renderer color, this has to happen after applying ChartTheme
        peakRenderer.setSeriesPaint(DEFAULT_PEAK_DATA_SERIES_INDEX, DEFAULT_PEAK_COLOR);
        massDiffRenderer.setSeriesPaint(DEFAULT_MASS_DIFF_SERIES_INDEX, DEFAULT_MASS_DIFF_COLOR);
        massDiffRenderer.setSeriesPaint(DEFAULT_TEMP_MASS_DIFF_SERIES_INDEX, DEFAULT_TEMP_MASS_DIFF_COLOR);
        this.setGridLineVisibility(false);
    }

    @Override
    protected Number[][] getGraphDataset() {
        XYSeriesCollection peakSeries = spectrumPanelModel.getPeakDataset();
        // get the raw peak list
        XYSeries peaks = peakSeries.getSeries(DEFAULT_PEAK_LIST_DATASET_NAME);
        return MzGraphDatasetUtils.getXYSeriesData(peaks);
    }

    /**
     * Add chart datasets
     */
    protected void setGraphDataset() {
        // add peak list dataset
        plot.setDataset(DEFAULT_PEAK_DATASET_INDEX, spectrumPanelModel.getPeakDataset());

        // add mass diff dataset
        plot.setDataset(DEFAULT_MASS_DIFF_DATASET_INDEX, spectrumPanelModel.getMassDiffDataset());

        // add fragment ion dataset
        plot.setDataset(DEFAULT_ION_DATASET_INDEX, spectrumPanelModel.getIonDataset());

        // add amino acid dataset
        plot.setDataset(DEFAULT_AMINO_ACID_DATASET_INDEX, spectrumPanelModel.getAminoAcidDataset());
    }

    /**
     * Add chart renderers
     */
    protected void setGraphRenderer() {
        // peak list renderer
        peakRenderer = new PeakRenderer();
        peakRenderer.setBaseToolTipGenerator(new PeakToolTipGenerator());
        plot.setRenderer(DEFAULT_PEAK_DATASET_INDEX, peakRenderer);

        // mass diff renderer
        massDiffRenderer = new MassDiffRenderer();
        plot.setRenderer(DEFAULT_MASS_DIFF_DATASET_INDEX, massDiffRenderer);

        // fragment ion renderer
        ionRenderer = new PeakRenderer();
        ionRenderer.setAdditionalItemLabelGenerator(new IonAnnotationLabelGenerator());
        ionRenderer.setAdditionalItemLabelVisible(true);
        ionRenderer.setBaseToolTipGenerator(new PeakToolTipGenerator());
        plot.setRenderer(DEFAULT_ION_DATASET_INDEX, ionRenderer);

        // amino acid annotation renderer
        aminoAcidRenderer = new PeakRangeRenderer();
        aminoAcidRenderer.setBaseItemLabelGenerator(new AminoAcidAnnotationLabelGenerator());
        aminoAcidRenderer.setAdditionalItemLabelGenerator(new MassDiffRangeLabelGenerator());
        aminoAcidRenderer.setAdditionalItemLabelVisible(false);
        plot.setRenderer(DEFAULT_AMINO_ACID_DATASET_INDEX, aminoAcidRenderer);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        super.actionPerformed(event);
        if (event instanceof FilterActionEvent) {
            FilterActionEvent filterEvent = (FilterActionEvent) event;
            String category = filterEvent.getCategory();
            String name = filterEvent.getName();
            if (category.equals(FRAGMENT_ION_NAME)) {
                hideFragmentIons(name);
            } else if (category.equals(AMINO_ACID_NAME)) {
                hideAminoAcids(name);
            }
        } else if (event instanceof MassErrorToleranceEvent) {
            double massTolerance = ((MassErrorToleranceEvent) event).getMassErrorTolerance();
            spectrumPanelModel.setAminoAcidAnnotationMassError(massTolerance);
        } else if (event instanceof ShowMassDifferentEvent) {
            boolean toShowMassDiff = ((ShowMassDifferentEvent) event).toShow();
            aminoAcidRenderer.setAdditionalItemLabelVisible(toShowMassDiff);
        } else {
            String command = event.getActionCommand();
            if (CLEAR_MASS_DIFF.equals(command)) {
                clearMassDiffAnnotations();
            } else if (HIDE_PEAK_LIST.equals(command)) {
                hidePeakList();
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String evtName = evt.getPropertyName();
        if (SpectrumPanelModel.NEW_ION_SERIES_PROP.equals(evtName)) {
            FragmentIonType ionType = (FragmentIonType) evt.getNewValue();
            setIonSeriesPaint(ionType);
            setIonSeriesVisibility(ionType, true);
            setAminoAcidSeriesPaint(ionType);
            // by default, we hide the amino acid annotations
            setAminoAcidSeriesVisibility(ionType, false);
        } else if (SpectrumPanelModel.NEW_PEAK_SERIES_PROP.equals(evtName)) {
            // reset the graph panel
            reset();
        } else if (SpectrumPanelModel.AMINO_ACID_ANNOTATION_GEN_PROP.equals(evtName) ||
                SpectrumPanelModel.AMINO_ACID_MASS_ERROR_CHANGE.equals(evtName) ||
                SpectrumPanelModel.AMINO_ACID_ANNOTATION_GEN_PARAM_CHANGE.equals(evtName)) {
            aminoAcidRenderer.reset();
        }
    }
}
