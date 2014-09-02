package uk.ac.ebi.pride.toolsuite.mzgraph.gui.setting;

import uk.ac.ebi.pride.toolsuite.mzgraph.chart.graph.MzGraphConstants;

import java.awt.event.ActionEvent;

/**
 * Action event to set the visibility of mass difference
 *
 * User: rwang
 * Date: 06/09/2011
 * Time: 11:49
 */
public class ShowMassDifferentEvent extends ActionEvent{
    private boolean show;

    public ShowMassDifferentEvent(Object source, int id, boolean show) {
        super(source, id, MzGraphConstants.SHOW_MASS_DIFFERENT_LABEL);
        this.show = show;
    }

    public boolean toShow() {
        return show;
    }
}
