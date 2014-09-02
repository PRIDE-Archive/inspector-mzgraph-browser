package uk.ac.ebi.pride.toolsuite.mzgraph.chart.graph;

import org.jfree.data.xy.XYIntervalSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import uk.ac.ebi.pride.utilities.mol.PTModification;
import uk.ac.ebi.pride.utilities.iongen.ion.FragmentIonType;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation.AminoAcidAnnotation;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation.AminoAcidAnnotationGenerator;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation.IonAnnotation;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation.IonAnnotationUtils;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.util.ExtendedXYIntervalSeries;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.util.MzGraphDatasetUtils;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static uk.ac.ebi.pride.toolsuite.mzgraph.chart.graph.MzGraphConstants.*;

/**
 * DataModel for SpectrumPanel
 * <p/>
 * User: rwang
 * Date: 11-Aug-2010
 * Time: 14:32:47
 */
public class SpectrumPanelModel implements PropertyChangeListener {
    public final static String AMINO_ACID_ANNOTATION_GEN_PROP = "amino_acid_generator";
    public final static String NEW_ION_SERIES_PROP = "new_ion_series";
    public final static String NEW_PEAK_SERIES_PROP = "new_peak_series";
    public final static String AMINO_ACID_MASS_ERROR_CHANGE = "amino_acid_mass_error_changed";
    public final static String AMINO_ACID_ANNOTATION_GEN_PARAM_CHANGE = "amino_acid_generator_parameters_changed";


    private Map<FragmentIonType, List<IonAnnotation>> ionAnnotations;

    /**
     * peak dataset stores all the raw peaks
     */
    private final XYSeriesCollection peakDataset;
    private final XYSeriesCollection massDiffDataset;
    private final XYSeriesCollection ionDataset;
    private final XYIntervalSeriesCollection aminoAcidDataset;

    /**
     * Keep track of the latest selection of peak
     */
    private double currMz = -1;
    private double currIntensity = -1;

    /**
     * Use to generate amino acid annotations.
     */
    private AminoAcidAnnotationGenerator aminoAcidAnnotationGenerator;
    /**
     * Property change support
     */
    private PropertyChangeSupport supporter;

    private boolean peaksReady = false;
    private boolean ionsReady = false;


    public SpectrumPanelModel() {
        peakDataset = MzGraphDatasetUtils.createXYDataset(DEFAULT_PEAK_LIST_DATASET_NAME, null, null);
        massDiffDataset = MzGraphDatasetUtils.createXYDataset(DEFAULT_MASS_DIFF_DATASET_NAME, null, null, true, false);
        MzGraphDatasetUtils.addXYSeries(massDiffDataset, DEFAULT_TMP_MASS_DIFF_DATASET_NAME, null, null, true, false);
        ionDataset = new XYSeriesCollection();
        ionAnnotations = new LinkedHashMap<FragmentIonType, List<IonAnnotation>>();
        aminoAcidDataset = new XYIntervalSeriesCollection();
        aminoAcidAnnotationGenerator = new AminoAcidAnnotationGenerator();
        supporter = new PropertyChangeSupport(this);
        this.addPropertyChangeListener(this);
    }

    /**
     * This method should be used carefully.
     *
     * @return XYSeriesCollection   peak data set stores all peaks.
     */
    public XYSeriesCollection getPeakDataset() {
        return peakDataset;
    }

    /**
     * Remove all data and annotations.
     */
    public void removeAll() {
        removeAllPeaks();
        removeAllMassDiffAnnotations();
        removeIonAnnotations();
        removeAminoAcidAnnotations();
        resetAminoAcidAnnotationGenerator();
    }


    /**
     * Set or overwrite the initial peak dataset.
     *
     * @param mz        mz double array.
     * @param intensity intensity double array.
     */
    public void setPeaks(double[] mz, double[] intensity) {
        MzGraphDatasetUtils.removeXYSeries(peakDataset, DEFAULT_PEAK_LIST_DATASET_NAME);
        MzGraphDatasetUtils.addXYSeries(peakDataset, DEFAULT_PEAK_LIST_DATASET_NAME, mz, intensity);

        peaksReady = true;
        if (peaksReady && ionsReady) {
            firePropertyChange(NEW_PEAK_SERIES_PROP, null, peakDataset.getSeries(DEFAULT_PEAK_LIST_DATASET_NAME));
        }
    }

    /**
     * Remove all the peaks from the spectrum panel
     * Note: this deletes the peaks permanently
     */
    public void removeAllPeaks() {
        peakDataset.removeAllSeries();
    }

    public XYSeriesCollection getMassDiffDataset() {
        return massDiffDataset;
    }

    /**
     * Add an annotation of mass difference.
     *
     * @param mz        mz value.
     * @param intensity intensity value.
     */
    public void addMassDiffAnnotation(double mz, double intensity) {
        XYSeries series = massDiffDataset.getSeries(DEFAULT_MASS_DIFF_DATASET_NAME);
        if (mz > 0 && intensity > 0 && series.indexOf(mz) < 0) {
            series.add(mz, intensity, true);
            currMz = mz;
            currIntensity = intensity;
        }
    }

    /**
     * Remove an annotation of mass difference.
     *
     * @param mz        mz value.
     * @param intensity intensity value.
     */
    public void removeMassDiffAnnotation(double mz, double intensity) {
        XYSeries series = massDiffDataset.getSeries(DEFAULT_MASS_DIFF_DATASET_NAME);
        if (mz > 0 && series.indexOf(mz) >= 0) {
            series.remove(mz);
        }
    }

    /**
     * Add a temporary mass difference annotation.
     * Note: This will clear the previous temporary mass difference annotation.
     *
     * @param mz        mz value.
     * @param intensity intensity value.
     */
    public void addTempMassDiffAnnotation(double mz, double intensity) {
        XYSeries series = massDiffDataset.getSeries(DEFAULT_TMP_MASS_DIFF_DATASET_NAME);

        if (mz > 0 && intensity > 0 && currMz >= 0 && currIntensity >= 0 && currMz != mz) {
            //clear the current temporary mass difference annotations
            series.clear();
            series.add(currMz, currIntensity, true);
            series.add(mz, intensity, true);
        }
    }

    public void removeAllMassDiffAnnotations() {
        removeTempMassDiffAnnotations();
        removeMassDiffAnnotations();
    }

    /**
     * Remove all temporary mass difference annotations.
     */
    public void removeTempMassDiffAnnotations() {
        currMz = -1;
        currIntensity = -1;
        massDiffDataset.getSeries(DEFAULT_TMP_MASS_DIFF_DATASET_NAME).clear();
    }

    /**
     * Remove all the mass difference annotations.
     */
    public void removeMassDiffAnnotations() {
        massDiffDataset.getSeries(DEFAULT_MASS_DIFF_DATASET_NAME).clear();
    }

    public XYSeriesCollection getIonDataset() {
        return ionDataset;
    }

    public void removeIonAnnotations() {
        ionAnnotations.clear();
        ionDataset.removeAllSeries();
    }

    public void removeIonAnnotationSeries(String ionSeriesName) {
        for (FragmentIonType ionType : ionAnnotations.keySet()) {
            if (ionType.getName().equals(ionSeriesName)) {
                ionAnnotations.remove(ionType);
            }
        }
        MzGraphDatasetUtils.removeXYSeries(ionDataset, ionSeriesName);
    }

    /**
     * Add a new series of fragment ion annotations.
     * Note: this method will overwrite any ion annotation series with the same
     * FragmentIonType.
     *
     * @param ionType ion annotation type.
     * @param ions    a list of ions in the series.
     */
    public void addFragmentIonSeries(FragmentIonType ionType, List<IonAnnotation> ions) {
        if (ions == null) {
            throw new IllegalArgumentException("Can not add null fragment ion series");
        } else if (!ions.isEmpty()) {
            // add fragment ion series
            String ionTypeName = ionType.getName();
            // get existing ion series
            XYSeries ionDataSeries = MzGraphDatasetUtils.getXYSeries(ionDataset, ionTypeName);
            // else create an new series
            if (ionDataSeries == null) {
                ionDataSeries = new XYSeries(ionTypeName);
                ionDataset.addSeries(ionDataSeries);
            }
            for (IonAnnotation ion : ions) {
                ionDataSeries.add(ion);
            }
            ionAnnotations.put(ionType, ions);
            firePropertyChange(NEW_ION_SERIES_PROP, null, ionType);
        }
    }

    /**
     * Add a list of unsorted and unformatted ions.
     * Note: this method calls addFragmentIonSeries in the background.
     *
     * @param ions a list of ion annotations.
     */
    public void addAnnotations(List<IonAnnotation> ions) {
        Map<FragmentIonType, List<IonAnnotation>> ionMap = IonAnnotationUtils.sortByType(ions);

        ionsReady = true;

        for (Map.Entry<FragmentIonType, List<IonAnnotation>> ionTypeListEntry : ionMap.entrySet()) {
            addFragmentIonSeries(ionTypeListEntry.getKey(), ionTypeListEntry.getValue());
        }
    }

    public void addAminoAcidAnnotations(FragmentIonType ionType, List<IonAnnotation> ions) {
        // generate amino acid annotations
        if (aminoAcidAnnotationGenerator != null && !ionType.equals(FragmentIonType.AMBIGUOUS_ION)) {
            List<AminoAcidAnnotation> aminoAcids = aminoAcidAnnotationGenerator.generate(ions);
            if (!aminoAcids.isEmpty()) {
                addAminoAcidAnnotations(ionType.getName(), aminoAcids);
            }
        }
    }

    /**
     * Add a new series of amino acid annotations with a specified series name.
     * Note: any previous series with the same series name will not be overwritten.
     *
     * @param seriesName series name for the annotation.
     * @param aminoAcids a list of amino acid annotations.
     */
    public void addAminoAcidAnnotations(Comparable seriesName, List<AminoAcidAnnotation> aminoAcids) {
        ExtendedXYIntervalSeries aminoAcidDataSeries = null;
        int seriesIndex = MzGraphDatasetUtils.getXYSeriesIndex(aminoAcidDataset, seriesName);
        if (seriesIndex >= 0 ) {
            aminoAcidDataSeries = (ExtendedXYIntervalSeries)aminoAcidDataset.getSeries(seriesIndex);
        } else {
            aminoAcidDataSeries = new ExtendedXYIntervalSeries(seriesName);
            aminoAcidDataset.addSeries(aminoAcidDataSeries);
        }
        for (AminoAcidAnnotation aminoAcid : aminoAcids) {
            aminoAcidDataSeries.add(aminoAcid);
        }
    }

    public XYIntervalSeriesCollection getAminoAcidDataset() {
        return aminoAcidDataset;
    }

    public AminoAcidAnnotationGenerator getAminoAcidAnnotationGenerator() {
        return aminoAcidAnnotationGenerator;
    }

    public void setAminoAcidAnnotationGenerator(AminoAcidAnnotationGenerator aminoAcidAnnotationGenerator) {
        AminoAcidAnnotationGenerator oldGenerator, newGenerator;
        synchronized (this) {
            oldGenerator = this.aminoAcidAnnotationGenerator;
            this.aminoAcidAnnotationGenerator = aminoAcidAnnotationGenerator;
            newGenerator = aminoAcidAnnotationGenerator;
        }
        firePropertyChange(AMINO_ACID_ANNOTATION_GEN_PROP, oldGenerator, newGenerator);
    }

    public void setAminoAcidAnnotationParameters(int peptideLength,
                                                 Map<Integer, List<PTModification>> modifications) {
        aminoAcidAnnotationGenerator.setPeptideLength(peptideLength);
        aminoAcidAnnotationGenerator.setModifications(modifications);
        firePropertyChange(AMINO_ACID_ANNOTATION_GEN_PARAM_CHANGE, null, modifications);
    }

    /**
     * Get amino acid annotation mass error.
     * @return double   mass error.
     */
    public double getAminoAcidAnnotationMassError() {
        if (aminoAcidAnnotationGenerator ==  null) {
            return 0;
        } else {
            return aminoAcidAnnotationGenerator.getMassError();
        }
    }

    /**
     * Set the mass error for amino acid annotations.
     *
     * @param massError mass error.
     */
    public void setAminoAcidAnnotationMassError(double massError) {
        double oldMassError, newMassError;
        synchronized (this) {
            oldMassError = aminoAcidAnnotationGenerator.getMassError();
            aminoAcidAnnotationGenerator.setMassError(massError);
            newMassError = massError;
        }
        firePropertyChange(AMINO_ACID_MASS_ERROR_CHANGE, oldMassError, newMassError);
    }

    public void resetAminoAcidAnnotationGenerator() {
        setAminoAcidAnnotationParameters(-1, null);
    }

    public void removeAminoAcidAnnotations() {
        aminoAcidDataset.removeAllSeries();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String evtCmd = evt.getPropertyName();
        if (AMINO_ACID_ANNOTATION_GEN_PROP.equals(evtCmd)
                || AMINO_ACID_MASS_ERROR_CHANGE.equals(evtCmd)
                || AMINO_ACID_ANNOTATION_GEN_PARAM_CHANGE.equals(evtCmd)) {
            // remove amino acid annotations.
            removeAminoAcidAnnotations();
            for (Map.Entry<FragmentIonType, List<IonAnnotation>> entry : ionAnnotations.entrySet()) {
                addAminoAcidAnnotations(entry.getKey(), entry.getValue());
            }
        } else if (NEW_ION_SERIES_PROP.equals(evtCmd)) {
            FragmentIonType ionType = (FragmentIonType)evt.getNewValue();
            List<IonAnnotation> ions = ionAnnotations.get(ionType);
            // generate amino acid annotations
            addAminoAcidAnnotations(ionType, ions);
        } else if (NEW_PEAK_SERIES_PROP.equals(evtCmd)) {
            // remove all fragment ions
            removeIonAnnotations();
            // remove all mass difference annotations.
            removeAllMassDiffAnnotations();
            // remove amino acid annotations.
            removeAminoAcidAnnotations();
            // reset amino acid annotation generator
            resetAminoAcidAnnotationGenerator();
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        supporter.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propName, PropertyChangeListener listener) {
        supporter.addPropertyChangeListener(propName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        supporter.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propName, PropertyChangeListener listener) {
        supporter.removePropertyChangeListener(propName, listener);
    }

    public void removeAllPropertyChangeListeners() {
        PropertyChangeListener[] listeners = supporter.getPropertyChangeListeners();
        for (PropertyChangeListener listener : listeners) {
            removePropertyChangeListener(listener);
        }
    }

    public PropertyChangeListener[] getPropertyChangeListeners() {
        return supporter.getPropertyChangeListeners();
    }

    public PropertyChangeListener[] getPropertyChangeListeners(String propName) {
        return supporter.getPropertyChangeListeners(propName);
    }

    public void firePropertyChange(final PropertyChangeEvent event) {
        supporter.firePropertyChange(event);
    }

    public void firePropertyChange(String propName, Object oldValue, Object newValue) {
        supporter.firePropertyChange(propName, oldValue, newValue);
    }
}