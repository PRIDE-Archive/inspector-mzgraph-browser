package uk.ac.ebi.pride.toolsuite.mzgraph.gui.data;

import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import uk.ac.ebi.pride.utilities.iongen.model.ProductIon;
import uk.ac.ebi.pride.utilities.mol.ProductIonPair;
import uk.ac.ebi.pride.utilities.iongen.ion.FragmentIonType;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation.IonAnnotation;

/**
 * This is a experimental table model observer. When model data changed, re-create all XYSeries.
 *
 * Creator: Qingwei-XU
 * Date: 11/10/12
 */

public class ExperimentalFragmentedIonsDataset extends XYSeriesCollection {
    private ExperimentalParams params = ExperimentalParams.getInstance();

    public ExperimentalFragmentedIonsDataset(ExperimentalFragmentedIonsTableModel tableModel) {
        update(tableModel);
    }

    public void update(ExperimentalFragmentedIonsTableModel tableModel) {
        if (tableModel == null) {
            throw new NullPointerException("ExperimentalFragmentedIonsTableModel is null!");
        }

        // Step 1 : update series list.
        ProductIonPair ionPair = params.getIonPair();

        // delete all old series.
        removeAllSeries();

        switch (ionPair) {
            case A_X:
                addSeries(new XYSeries(FragmentIonType.X_ION.getName()));
                addSeries(new XYSeries(FragmentIonType.A_ION.getName()));
                break;
            case B_Y:
                addSeries(new XYSeries(FragmentIonType.Y_ION.getName()));
                addSeries(new XYSeries(FragmentIonType.B_ION.getName()));
                break;
            case C_Z:
                addSeries(new XYSeries(FragmentIonType.Z_ION.getName()));
                addSeries(new XYSeries(FragmentIonType.C_ION.getName()));
                break;
        }

        IonAnnotation[][] matchedData = tableModel.getMatchedData();

        Object o;
        ProductIon theoreticalIon;
        Double matchedMass;

        XYSeries series;
        int seriesIndex;
        double x;
        double y;
        for (int col = 1; col < tableModel.getColumnCount(); col++) {
            if (! tableModel.isMassColumn(col)) {
                continue;
            }

            for (int row = 0; row < tableModel.getRowCount(); row++) {
                o = tableModel.getValueAt(row, col);
                theoreticalIon = (ProductIon) o;

                if (matchedData[row][col] == null || o == null) {
                    continue;
                }

                matchedMass = matchedData[row][col].getMz().doubleValue();
                seriesIndex = indexOf(theoreticalIon.getType().getGroup().getName());
                series = getSeries(seriesIndex);
                x = theoreticalIon.getMassOverCharge();
                y = matchedMass - x;

                // add new series items.
                series.add(x, y);
            }
        }

        //Step 2: update point matrix
        pointMatrix = new Point[tableModel.getRowCount()][tableModel.getColumnCount()];
        Point point;

        ProductIon ion;
        int itemIndex;
        for (int col = 1; col < tableModel.getColumnCount(); col++) {
            for (int row = 0; row < tableModel.getRowCount(); row++) {
                if (matchedData[row][col] != null && (ion = (ProductIon) tableModel.getValueAt(row, col)) != null) {
                    matchedMass = matchedData[row][col].getMz().doubleValue();
                    series = getSeries(ion.getType().getGroup().getName());
                    seriesIndex = indexOf(ion.getType().getGroup().getName());
                    x = ion.getMassOverCharge();
                    y = matchedMass - x;
                    itemIndex = getItemIndex(series, x, y);
                    point = new Point(seriesIndex, itemIndex);
                    pointMatrix[row][col] = point;
                }
            }
        }
    }

    private class Point {
        int series;
        int item;

        Point(int series, int item) {
            this.series = series;
            this.item = item;
        }

        public int getSeries() {
            return series;
        }

        public int getItem() {
            return item;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Point point = (Point) o;

            return item == point.item && series == point.series;
        }

        @Override
        public int hashCode() {
            int result = series;
            result = 31 * result + item;
            return result;
        }
    }

    /**
     * this matrix use to store the series and item value of each matched data.
     */
    private Point[][] pointMatrix;

    private int getItemIndex(XYSeries series, double x, double y) {
        XYDataItem item;
        for (int i = 0; i < series.getItemCount(); i++) {
            item = series.getDataItem(i);

            if (item.getXValue() == x && item.getYValue() == y) {
                return i;
            }
        }

        return -1;
    }

    public int getRowNumber(int seriesIndex, int itemIndex) {
        Point point;
        for (int row = 0; row < pointMatrix.length; row++) {
            for (int col = 0; col < pointMatrix[row].length; col++) {
                point = pointMatrix[row][col];
                if (point != null && point.getSeries() == seriesIndex && point.getItem() == itemIndex) {
                    return row;
                }
            }
        }

        return -1;
    }

    public int getColNumber(int series, int itemIndex) {
        Point point;
        for (int row = 0; row < pointMatrix.length; row++) {
            for (int col = 0; col < pointMatrix[row].length; col++) {
                point = pointMatrix[row][col];
                if (point != null && point.getSeries() == series && point.getItem() == itemIndex) {
                    return col;
                }
            }
        }

        return -1;
    }

    public int getItemNumber(int row, int col) {
        Point point = pointMatrix[row][col];
        if (point == null) {
            return -1;
        } else {
            return point.getItem();
        }
    }

    public int getSeriesNumber(int row, int col) {
        Point point = pointMatrix[row][col];
        if (point == null) {
            return -1;
        } else {
            return point.getSeries();
        }
    }
}