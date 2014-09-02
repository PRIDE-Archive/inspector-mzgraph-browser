package uk.ac.ebi.pride.toolsuite.mzgraph.psm;

import uk.ac.ebi.pride.utilities.iongen.model.PeakSet;
import uk.ac.ebi.pride.utilities.iongen.model.PrecursorIon;
import uk.ac.ebi.pride.utilities.mol.ProductIonPair;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation.IonAnnotation;
import uk.ac.ebi.pride.toolsuite.mzgraph.gui.data.ExperimentalFragmentedIonsTableModel;
import uk.ac.ebi.pride.toolsuite.mzgraph.psm.conflict.IonTypeConflictFilter;
import uk.ac.ebi.pride.toolsuite.mzgraph.psm.match.MaxSpectraMatcher;
import uk.ac.ebi.pride.toolsuite.mzgraph.psm.noise.ASANoiseFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Creator: Qingwei-XU
 * Date: 13/11/12
 */

public class PSMMatcher {
    private NoiseFilter noiseFilter;
    private SpectraMatcher spectraMatcher;
    private ConflictFilter conflictFilter;

    public PSMMatcher(NoiseFilter noiseFilter, SpectraMatcher spectraMatcher, ConflictFilter conflictFilter) {
        this.noiseFilter = noiseFilter;
        this.spectraMatcher = spectraMatcher;
        this.conflictFilter = conflictFilter;
    }

    public PSMMatcher(SpectraMatcher spectraMatcher) {
        this.spectraMatcher = spectraMatcher;
    }

    public static PSMMatcher getInstance() {
        return new PSMMatcher(new ASANoiseFilter(), new MaxSpectraMatcher(), new IonTypeConflictFilter());
    }

    public IonAnnotation[][] match(ExperimentalFragmentedIonsTableModel tableModel, PeakSet peakSet) {
        if (peakSet == null) {
            return new IonAnnotation[tableModel.getRowCount()][tableModel.getColumnCount()];
        }

        PeakSet filterPeakSet;
        if (this.noiseFilter == null) {
            filterPeakSet = peakSet;
        } else {
            filterPeakSet = noiseFilter.filterNoise(peakSet, tableModel);
        }

        IonAnnotation[][] result = this.spectraMatcher.match(filterPeakSet, tableModel);

        if (this.conflictFilter == null) {
            return result;
        } else {
            return conflictFilter.filterConflict(result, tableModel);
        }
    }

    public IonAnnotation[][] match(PrecursorIon precursorIon, ProductIonPair ionPair, PeakSet peakSet) {
        if (precursorIon == null) {
            throw new NullPointerException("Precursor ion is null!");
        }

        if (ionPair == null) {
            ionPair = ProductIonPair.B_Y;
        }

        ExperimentalFragmentedIonsTableModel tableModel = new ExperimentalFragmentedIonsTableModel(precursorIon, ionPair);

        return match(tableModel, peakSet);
    }

    public List<IonAnnotation> match(PrecursorIon precursorIon, PeakSet peakSet) {
        List<IonAnnotation> annotationList = new ArrayList<IonAnnotation>();

        IonAnnotation[][] matrix = match(precursorIon, ProductIonPair.B_Y, peakSet);
        annotationList.addAll(toList(matrix));
        matrix = match(precursorIon, ProductIonPair.A_X, peakSet);
        annotationList.addAll(toList(matrix));
        matrix = match(precursorIon, ProductIonPair.C_Z, peakSet);
        annotationList.addAll(toList(matrix));

        return annotationList;
    }

    private List<IonAnnotation> toList(IonAnnotation[][] matrix) {
        List<IonAnnotation> annotationList = new ArrayList<IonAnnotation>();

        if (matrix == null) {
            return annotationList;
        }

        IonAnnotation annotation;
        for (IonAnnotation[] row : matrix) {
            for (IonAnnotation cell : row) {
                annotation = cell;
                if (annotation != null) {
                    annotationList.add(annotation);
                }
            }
        }

        return annotationList;
    }

    public NoiseFilter getNoiseFilter() {
        return noiseFilter;
    }

    public void setNoiseFilter(NoiseFilter noiseFilter) {
        this.noiseFilter = noiseFilter;
    }

    public SpectraMatcher getSpectraMatcher() {
        return spectraMatcher;
    }

    public void setSpectraMatcher(SpectraMatcher spectraMatcher) {
        this.spectraMatcher = spectraMatcher;
    }

    public ConflictFilter getConflictFilter() {
        return conflictFilter;
    }

    public void setConflictFilter(ConflictFilter conflictFilter) {
        this.conflictFilter = conflictFilter;
    }
}
