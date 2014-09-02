package uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.util;

import org.jfree.data.ComparableObjectItem;
import org.jfree.data.xy.XYIntervalDataItem;
import org.jfree.data.xy.XYIntervalSeries;

/**
 * This class is created solely to expose add methods for adding an item.
 *
 * User: rwang
 * Date: 15-Jun-2010
 * Time: 11:02:52
 */
public class ExtendedXYIntervalSeries extends XYIntervalSeries {

    public ExtendedXYIntervalSeries(Comparable key) {
        super(key);
    }

    public ExtendedXYIntervalSeries(Comparable key, boolean autoSort, boolean allowDuplicateXValues) {
        super(key, autoSort, allowDuplicateXValues);
    }

    public void add(XYIntervalDataItem item) {
        super.add(item, true);
    }

    public void add(XYIntervalDataItem item, boolean notify) {
        super.add(item, notify);
    }

    public ComparableObjectItem remove(XYIntervalDataItem item) {
        return super.remove(item);
    }
}
