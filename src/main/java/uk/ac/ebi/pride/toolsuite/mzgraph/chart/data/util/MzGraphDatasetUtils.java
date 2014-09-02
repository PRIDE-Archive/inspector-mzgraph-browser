package uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.util;

import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * Created by IntelliJ IDEA.
 * <p/>
 * User: rwang
 * Date: 11-Jun-2010
 * Time: 15:53:36
 */
public class MzGraphDatasetUtils {

    public static XYSeriesCollection createXYDataset(Comparable seriesKey, double[] x, double[] y) {
        return createXYDataset(seriesKey, x, y, true, true);
    }

    public static XYSeriesCollection createXYDataset(Comparable seriesKey, double[] x, double[] y, boolean autoSort, boolean allowDuplicateXValues) {
        // create jfreechart format data set
        XYSeries series = createXYSeries(seriesKey, x, y, autoSort, allowDuplicateXValues);
        XYSeriesCollection dataSet = new XYSeriesCollection();
        dataSet.addSeries(series);
        return dataSet;
    }

    public static XYSeries createXYSeries(Comparable seriesKey, double[] x, double[] y) {
        return createXYSeries(seriesKey, x, y, true, true);
    }

    public static XYSeries createXYSeries(Comparable seriesKey, double[] x, double[] y, boolean autoSort, boolean allowDuplicateXValues) {
        XYSeries series = new XYSeries(seriesKey, autoSort, allowDuplicateXValues);
        if (x != null && y != null && x.length == y.length) {
            for (int i = 0; i < x.length; i++) {
                series.add(x[i], y[i]);
            }
        }
        return series;
    }

    public static boolean hasXYSeries(XYDataset dataset, Comparable seriesKey) {
        boolean exist = false;
        int seriesCount = dataset.getSeriesCount();
        for (int i = 0; i < seriesCount; i++) {
            if (dataset.getSeriesKey(i).equals(seriesKey)) {
                exist = true;
            }
        }
        return exist;
    }

    public static int getXYSeriesIndex(XYDataset dataset, Comparable seriesKey) {
        int index = -1;
        int seriesCount = dataset.getSeriesCount();
        for (int i = 0; i < seriesCount; i++) {
            if (dataset.getSeriesKey(i).equals(seriesKey)) {
                index = i;
            }
        }
        return index;
    }

    public static XYSeries getXYSeries(XYSeriesCollection dataset, Comparable seriesKey) {
        if (hasXYSeries(dataset, seriesKey)) {
            return dataset.getSeries(seriesKey);
        } else {
            return null;
        }
    }

    public static void removeXYSeries(XYSeriesCollection seriesCollection, Comparable seriesKey) {
        XYSeries series = seriesCollection.getSeries(seriesKey);
        if (series != null) {
            seriesCollection.removeSeries(series);
        }
    }

    public static XYSeries addXYSeries(XYSeriesCollection seriesCollection, Comparable seriesKey, double[] x, double[] y) {
        XYSeries series = MzGraphDatasetUtils.createXYSeries(seriesKey, x, y);
        seriesCollection.addSeries(series);
        return series;
    }

    public static XYSeries addXYSeries(XYSeriesCollection seriesCollection, Comparable seriesKey, double[] x, double[] y,
                                       boolean autoSort, boolean allowDuplicateXValues) {
        XYSeries series = MzGraphDatasetUtils.createXYSeries(seriesKey, x, y, autoSort, allowDuplicateXValues);
        seriesCollection.addSeries(series);
        return series;
    }

    public static Number[][] getXYSeriesData(XYSeries series) {
        int cnt = series.getItemCount();
        Number[][] dataSet = new Number[cnt][2];
        for (int i = 0; i < cnt; i++) {
            dataSet[i][0] = series.getX(i);
            dataSet[i][1] = series.getY(i);
        }
        return dataSet;
    }
}
