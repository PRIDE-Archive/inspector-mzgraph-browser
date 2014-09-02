package uk.ac.ebi.pride.toolsuite.mzgraph.gui.setting;

import uk.ac.ebi.pride.toolsuite.mzgraph.chart.graph.MzGraphConstants;

import java.awt.event.ActionEvent;

/**
 * Action event to notify a change on mass error tolerance
 *
 * User: rwang
 * Date: 06/09/2011
 * Time: 11:20
 */
public class MassErrorToleranceEvent extends ActionEvent{
    private double massErrorTolerance;

    public MassErrorToleranceEvent(Object source, int id, double massErrorTolerance) {
        super(source, id, MzGraphConstants.MASS_ERROR_TOLERANCE);

        this.massErrorTolerance = massErrorTolerance;
    }

    public double getMassErrorTolerance() {
        return massErrorTolerance;
    }
}
