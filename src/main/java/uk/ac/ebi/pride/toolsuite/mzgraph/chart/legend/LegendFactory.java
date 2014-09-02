package uk.ac.ebi.pride.toolsuite.mzgraph.chart.legend;

import org.jfree.chart.title.LegendTitle;
import uk.ac.ebi.pride.utilities.data.Tuple;

import java.awt.*;
import java.util.Map;

/**
 * LegendFactory is for generating different type of legends.
 *
 * User: rwang
 * Date: 10-Nov-2010
 * Time: 14:01:18
 * To change this template use File | Settings | File Templates.
 */
public class LegendFactory {

    public static LegendTitle createLegendFromMap(Map<Tuple<String, String>, Paint> entries) {
        return new LegendTitle(new MapLegendItemSource(entries));
    }
}
