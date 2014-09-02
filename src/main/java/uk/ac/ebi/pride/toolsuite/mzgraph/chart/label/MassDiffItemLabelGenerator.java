package uk.ac.ebi.pride.toolsuite.mzgraph.chart.label;

import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.data.xy.XYDataset;

import java.text.NumberFormat;

/**
 * This label generator is used by MassDiffRenderer
 *
 * User: rwang
 * Date: 13-Jun-2010
 * Time: 18:03:14
 */
public class MassDiffItemLabelGenerator implements XYItemLabelGenerator{

    public String generateLabel(XYDataset xyDataset, int series, int item) {
        String label = "";
        if (xyDataset.getItemCount(series) -1 > item) {
            double x1 = xyDataset.getXValue(series, item);
            double x2 = xyDataset.getXValue(series, item + 1);
            label = NumberFormat.getInstance().format(Math.abs(x2 -x1));
        }
        return label;
    }
}
