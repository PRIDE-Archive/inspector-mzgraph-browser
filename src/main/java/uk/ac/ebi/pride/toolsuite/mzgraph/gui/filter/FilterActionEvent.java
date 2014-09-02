package uk.ac.ebi.pride.toolsuite.mzgraph.gui.filter;

import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 28-Jun-2010
 * Time: 17:19:49
 */

public class FilterActionEvent extends ActionEvent {
    /** the category of the filter action command,
     * this could be amino acid, fragment ion and etc */
    private String category;
    
    /** the series name of the filter action command,
     * this could be amino acid series name, fragment ion series name
     * Note: these are the data series name
     * */
    private String name;

    public FilterActionEvent(Object source, int id, String category, String seriesName) {
        super(source, id, category + "-" + seriesName);

        this.category = category;
        this.name = seriesName;
    }

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }
}
