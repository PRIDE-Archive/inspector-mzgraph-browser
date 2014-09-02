package uk.ac.ebi.pride.toolsuite.mzgraph.chart.tooltip;

import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.data.xy.XYDataset;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.graph.MzGraphConstants;

import java.text.NumberFormat;

/**
 * Creator: Qingwei-XU
 * Date: 15/10/12
 * Version: 0.1-SNAPSHOT
 */

public class ExperimentalFragmentedIonsScatterChartTooltipGenerator extends StandardXYToolTipGenerator {
    public ExperimentalFragmentedIonsScatterChartTooltipGenerator() {
    }

    public String generateToolTip(XYDataset xyDataset, int series, int item) {
        double diff = xyDataset.getYValue(series, item);
        double mass = xyDataset.getXValue(series, item);

        NumberFormat formatter = NumberFormat.getInstance();
        formatter.setMaximumFractionDigits(MzGraphConstants.TABLE_FRACTION);

        StringBuilder sb = new StringBuilder();
        sb.append("m/z:" + formatter.format(mass) + ", ");
        sb.append("Error:" + formatter.format(diff));

        return sb.toString();
    }
}
