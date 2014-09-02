package uk.ac.ebi.pride.toolsuite.mzgraph.gui.data;

import uk.ac.ebi.pride.utilities.mol.ProductIonPair;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.graph.MzGraphConstants;

/**
 * User: qingwei
 * Date: 05/12/12
 */
public class ExperimentalParams {
    private transient static ExperimentalParams params;

    private ProductIonPair ionPair;

    private boolean showWaterLoss;

    private boolean showAmmoniaLoss;

    /**
     * default Peptide Spectra Matches Algorithm abs(range interval) is [0Da, 0.5Da]
     */
    private double range;

    public static ExperimentalParams getInstance() {
        if (params == null) {
            params = new ExperimentalParams();
        }

        return params;
    }

    private ExperimentalParams() {
        reset();
    }

    public void reset() {
        ionPair = ProductIonPair.B_Y;
        showWaterLoss = false;
        showAmmoniaLoss = false;
        range = MzGraphConstants.INTERVAL_RANGE;
    }

    public ProductIonPair getIonPair() {
        return ionPair;
    }

    public void setIonPair(ProductIonPair ionPair) {
        if (ionPair == null) {
            this.ionPair = ProductIonPair.B_Y;
        }

        this.ionPair = ionPair;
    }

    public boolean isShowWaterLoss() {
        return showWaterLoss;
    }

    public void setShowWaterLoss(boolean showWaterLoss) {
        this.showWaterLoss = showWaterLoss;
    }

    public boolean isShowAmmoniaLoss() {
        return showAmmoniaLoss;
    }

    public void setShowAmmoniaLoss(boolean showAmmoniaLoss) {
        this.showAmmoniaLoss = showAmmoniaLoss;
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        if (Double.compare(range, 0) > 0) {
            this.range = range;
        } else {
            throw new IllegalArgumentException(range + " should great than 0!");
        }
    }
}
