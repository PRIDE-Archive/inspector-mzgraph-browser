package uk.ac.ebi.pride.toolsuite.mzgraph.psm.match;

import uk.ac.ebi.pride.utilities.iongen.model.Peak;
import uk.ac.ebi.pride.utilities.iongen.model.PeakSet;
import uk.ac.ebi.pride.utilities.iongen.model.ProductIon;
import uk.ac.ebi.pride.utilities.util.ApproximateComparator;
import uk.ac.ebi.pride.utilities.mol.NeutralLoss;
import uk.ac.ebi.pride.utilities.iongen.ion.FragmentIonType;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation.IonAnnotation;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation.IonAnnotationInfo;
import uk.ac.ebi.pride.toolsuite.mzgraph.gui.data.ExperimentalFragmentedIonsTableModel;
import uk.ac.ebi.pride.toolsuite.mzgraph.gui.data.ExperimentalParams;
import uk.ac.ebi.pride.toolsuite.mzgraph.psm.SpectraMatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Creator: Qingwei-XU
 * Date: 05/11/12
 */

public class MaxSpectraMatcher implements SpectraMatcher {
    private ApproximateComparator comparator = new ApproximateComparator();

    private IonAnnotation findAnnotation(double mz, List<IonAnnotation> annotationList) {
        if (annotationList == null) {
            return null;
        }

        for (IonAnnotation annotation : annotationList) {
            if (comparator.compare(annotation.getMz().doubleValue(), mz) == 0) {
                return annotation;
            }
        }

        return null;
    }

    @Override
    public IonAnnotation[][] match(PeakSet peakSet, ExperimentalFragmentedIonsTableModel tableModel) {
        if (peakSet == null || tableModel == null) {
            return null;
        }

        double range = ExperimentalParams.getInstance().getRange();

        IonAnnotation[][] autoData = new IonAnnotation[tableModel.getRowCount()][tableModel.getColumnCount()];
        List<IonAnnotation> autoAnnotations = new ArrayList<IonAnnotation>();

        double mz;
        double intensity;
        double theoretical;
        Object cell;
        ProductIon ion;
        IonAnnotation annotation;
        IonAnnotationInfo annotationInfo;
        int charge;
        FragmentIonType type;
        int location;
        NeutralLoss loss;
        PeakSet set;
        Peak peak;

        for (int row = 0; row < tableModel.getRowCount(); row++) {
            for (int col = 0; col < tableModel.getColumnCount(); col++) {
                cell = tableModel.getValueAt(row, col);
                if (cell instanceof ProductIon) {
                    ion = (ProductIon) cell;
                    theoretical = ion.getMassOverCharge();

                    set = peakSet.subSet(theoretical, range);
                    if (set.size() > 0) {
                        peak = set.getMaxIntensityPeak();
                        mz = peak.getMz();
                        intensity = peak.getIntensity();

                        charge = ion.getCharge();
                        location = ion.getPosition();
                        type = ion.getType().getGroup();
                        loss = ion.getType().getLoss();

                        annotation = findAnnotation(mz, autoAnnotations);
                        if (annotation == null) {
                            annotationInfo = new IonAnnotationInfo();
                            annotationInfo.addItem(charge, type, location, loss);
                            annotation = new IonAnnotation(mz, intensity, annotationInfo);
                            autoAnnotations.add(annotation);
                        } else {
                            annotationInfo = annotation.getAnnotationInfo();
                            annotationInfo.addItem(charge, type, location, loss);
                        }

                        autoData[row][col] = annotation;
                    }
                }
            }
        }

        return autoData;
    }
}
