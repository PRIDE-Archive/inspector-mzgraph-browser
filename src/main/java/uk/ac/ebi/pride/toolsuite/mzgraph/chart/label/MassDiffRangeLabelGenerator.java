package uk.ac.ebi.pride.toolsuite.mzgraph.chart.label;

import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYIntervalSeriesCollection;

import java.text.NumberFormat;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 21-Jun-2010
 * Time: 10:38:09
 */
public class MassDiffRangeLabelGenerator implements XYItemLabelGenerator {

    public String generateLabel(XYDataset xyDataset, int series, int item) {
        String label = "";
        if (xyDataset instanceof XYIntervalSeriesCollection) {
            XYIntervalSeriesCollection intervalDataset = (XYIntervalSeriesCollection) xyDataset;
            double x1 = intervalDataset.getStartXValue(series, item);
            double x2 = intervalDataset.getEndXValue(series, item);
            if (x2 != x1) {
                label = NumberFormat.getInstance().format(Math.abs(x2 -x1));
            } else {
                label = NumberFormat.getInstance().format(x2);
            }
        }
        return label;
    }
}
