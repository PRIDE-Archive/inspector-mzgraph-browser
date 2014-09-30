package uk.ac.ebi.pride.toolsuite.mzgraph.chart.legend;

import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.LegendItemSource;
import uk.ac.ebi.pride.utilities.util.Tuple;

import java.awt.*;
import java.util.Map;

/**
 * Legend based on a map.
 *
 * User: rwang
 * Date: 10-Nov-2010
 * Time: 14:09:24
 * To change this template use File | Settings | File Templates.
 */
public class MapLegendItemSource implements LegendItemSource {
    private Map<Tuple<String, String>, Paint> legendMap;

    public MapLegendItemSource(Map<Tuple<String, String>, Paint> legendMap) {
        this.legendMap = legendMap;
    }

    @Override
    public LegendItemCollection getLegendItems() {
        LegendItemCollection legendCollection = new LegendItemCollection();
        for (Map.Entry<Tuple<String, String>, Paint> entry : legendMap.entrySet()) {
            Tuple<String, String> desc = entry.getKey();
            LegendItem item = new LegendItem(desc.getKey() + " - " + desc.getValue(), entry.getValue());
            legendCollection.add(item);
        }
        return legendCollection;
    }
}
