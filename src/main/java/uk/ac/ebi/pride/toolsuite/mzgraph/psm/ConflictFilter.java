package uk.ac.ebi.pride.toolsuite.mzgraph.psm;

import uk.ac.ebi.pride.utilities.iongen.model.PrecursorIon;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation.IonAnnotation;
import uk.ac.ebi.pride.toolsuite.mzgraph.gui.data.ExperimentalFragmentedIonsTableModel;

/**
 * Creator: Qingwei-XU
 * Date: 06/11/12
 */

public interface ConflictFilter {
    public IonAnnotation[][] filterConflict(IonAnnotation[][] src, ExperimentalFragmentedIonsTableModel tableModel);
}
