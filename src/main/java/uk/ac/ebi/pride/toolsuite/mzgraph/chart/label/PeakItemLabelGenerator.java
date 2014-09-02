package uk.ac.ebi.pride.toolsuite.mzgraph.chart.label;

import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.data.xy.XYDataset;

import java.text.NumberFormat;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 11-Jun-2010
 * Time: 12:03:45
 */
public class PeakItemLabelGenerator implements XYItemLabelGenerator{

    public String generateLabel(XYDataset xyDataset, int row, int column) {
        String label = null;
        Number mzVal = xyDataset.getX(row, column);
        if (mzVal != null) {
            label = NumberFormat.getInstance().format(mzVal);
        }
        return label;
    }
}
