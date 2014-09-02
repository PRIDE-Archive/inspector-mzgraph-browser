package uk.ac.ebi.pride.toolsuite.mzgraph.psm.conflict;

import uk.ac.ebi.pride.utilities.iongen.model.PrecursorIon;
import uk.ac.ebi.pride.utilities.mol.ProductIonType;
import uk.ac.ebi.pride.utilities.iongen.ion.FragmentIonType;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation.IonAnnotation;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation.IonAnnotationInfo;
import uk.ac.ebi.pride.toolsuite.mzgraph.gui.data.ExperimentalFragmentedIonsTableModel;
import uk.ac.ebi.pride.toolsuite.mzgraph.psm.ConflictFilter;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Creator: Qingwei-XU
 * Date: 06/11/12
 */

public class IonTypeConflictFilter implements ConflictFilter {
    /**
     * generate the prior score based on the fragment ion type.
     * <P>b > y > a > c</P>
     * Currently, we not consider about the neutral loss.
     */
    private int getScore(IonAnnotationInfo.Item item) {
        int store = 0;

        if (item == null) {
            store = 0;
        }

        FragmentIonType type = item.getType();
        if (type.equals(FragmentIonType.B_ION)) {
            store = 100;
        } else if (type.equals(FragmentIonType.Y_ION)) {
            store = 99;
        } else if (type.equals(FragmentIonType.A_ION)) {
            store = 98;
        } else if (type.equals(FragmentIonType.X_ION)) {
            store = 97;
        } else if (type.equals(FragmentIonType.C_ION)) {
            store = 96;
        } else if (type.equals(FragmentIonType.Z_ION)) {
            store = 95;
        }

        if (item.getNeutralLoss() != null) {
            store -= 6;
        }

        return store;
    }

    private void filterConflictMatrixCell(IonAnnotation[][] src, IonAnnotation annotation,
                                          ExperimentalFragmentedIonsTableModel tableModel) {
        IonAnnotationInfo info = annotation.getAnnotationInfo();

        // there exists more than one annotation for one peak.
        IonAnnotationInfo.Item item, maxItem = null;
        int score, maxScore = 0;
        int row, col;
        for (java.util.Iterator<IonAnnotationInfo.Item> it = info.iterator(); it.hasNext(); ) {
            item = it.next();
            score = getScore(item);
            if (score > maxScore) {
                maxScore = score;

                if (maxItem != null) {
                    row = tableModel.getRowNumber(maxItem.getType(), maxItem.getLocation());
                    col = tableModel.getColumnNumber(maxItem.getType(), maxItem.getCharge(), maxItem.getNeutralLoss());
                    src[row][col] = null;
                }

                maxItem = item;
            } else {
                // erase mirror item from matrix
                row = tableModel.getRowNumber(item.getType(), item.getLocation());
                col = tableModel.getColumnNumber(item.getType(), item.getCharge(), item.getNeutralLoss());
                src[row][col] = null;
            }
        }

        info = new IonAnnotationInfo();
        info.addItem(maxItem);
        annotation.setInfo(info);
    }

    @Override
    public IonAnnotation[][] filterConflict(IonAnnotation[][] src, ExperimentalFragmentedIonsTableModel tableModel) {
        if (tableModel == null) {
            throw new NullPointerException("ExperimentalFragmentedIonsTableModel is null!");
        }

        if (src == null) {
            return new IonAnnotation[tableModel.getRowCount()][tableModel.getColumnCount()];
        }

        IonAnnotation annotation;
        int rowCount = src.length;
        int colCount = src[0].length;

        for (int col = 0; col < colCount; col++) {
            for (int row = 0; row < rowCount; row++) {
                annotation = src[row][col];
                // there exists more than one annotation for one peak.
                if (annotation != null && annotation.getAnnotationInfo().getNumberOfItems() > 1) {
                    filterConflictMatrixCell(src, annotation, tableModel);
                }
            }
        }

        return src;
    }
}
