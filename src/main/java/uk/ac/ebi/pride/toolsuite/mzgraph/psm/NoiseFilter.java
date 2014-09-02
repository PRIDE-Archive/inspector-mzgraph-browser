package uk.ac.ebi.pride.toolsuite.mzgraph.psm;

import uk.ac.ebi.pride.utilities.iongen.model.PeakSet;
import uk.ac.ebi.pride.toolsuite.mzgraph.gui.data.ExperimentalFragmentedIonsTableModel;

/**
 * Creator: Qingwei-XU
 * Date: 02/11/12
 */
public interface NoiseFilter {
    public PeakSet filterNoise(PeakSet peaks, ExperimentalFragmentedIonsTableModel tableModel);
}
