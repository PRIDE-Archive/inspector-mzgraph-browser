package uk.ac.ebi.pride.toolsuite.mzgraph.gui;

import uk.ac.ebi.pride.utilities.iongen.model.PeakSet;
import uk.ac.ebi.pride.utilities.iongen.model.PrecursorIon;
import uk.ac.ebi.pride.utilities.iongen.model.ProductIon;
import uk.ac.ebi.pride.utilities.mol.ProductIonPair;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation.IonAnnotation;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.graph.MzGraphConstants;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.renderer.ExperimentalFragmentedIonsRenderer;
import uk.ac.ebi.pride.toolsuite.mzgraph.gui.data.ExperimentalFragmentedIonsTableModel;
import uk.ac.ebi.pride.toolsuite.mzgraph.gui.data.ExperimentalParams;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;

/**
 * Creator: Qingwei-XU
 * Date: 11/10/12
 */

public class ExperimentalFragmentedIonsTable extends TheoreticalFragmentedIonsTable {
    private ExperimentalFragmentedIonsTableModel tableModel;
    private ExperimentalParams params = ExperimentalParams.getInstance();

    public static final String FLUSH_TABLEMODEL = "flush tablemodel";

    public ExperimentalFragmentedIonsTable(PrecursorIon precursorIon, ProductIonPair pair, PeakSet peakSet) {
        super(precursorIon, pair);

        this.tableModel = new ExperimentalFragmentedIonsTableModel(precursorIon, pair);
        this.tableModel.setPeaks(peakSet);
        setModel(this.tableModel);
    }

    public ExperimentalFragmentedIonsTable(PrecursorIon precursorIon, ProductIonPair pair,
                                           double[] mzArray, double[] intensityArray) {
        super(precursorIon, pair);

        this.tableModel = new ExperimentalFragmentedIonsTableModel(precursorIon, pair);
        this.tableModel.setPeaks(mzArray, intensityArray);
        setModel(this.tableModel);
    }

    public ExperimentalFragmentedIonsTable(PrecursorIon precursorIon, ProductIonPair pair) {
        this(precursorIon, pair, null, null);
    }

    public ExperimentalFragmentedIonsTable(PrecursorIon precursorIon, PeakSet peakSet) {
        this(precursorIon, ProductIonPair.B_Y, peakSet);
    }

    public ExperimentalFragmentedIonsTable(PrecursorIon precursorIon, double[] mzArray, double[] intensityArray) {
        this(precursorIon, ProductIonPair.B_Y, mzArray, intensityArray);
    }

    public ExperimentalFragmentedIonsTable(PrecursorIon precursorIon) {
        this(precursorIon, ProductIonPair.B_Y);
    }

    public void setProductIonPair(ProductIonPair ionPair) {
        if (this.tableModel != null && ! params.getIonPair().equals(ionPair)) {
            this.tableModel.setProductIonPair(ionPair);
        }

        TableColumnModel columnModel = getColumnModel();
        TableColumn column;
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            column = columnModel.getColumn(i);
            column.setHeaderValue(tableModel.getColumnName(i));
        }

        firePropertyChange(FLUSH_TABLEMODEL, null, this.tableModel);
    }

    public void setShowAuto(boolean showAuto) {
        if (this.tableModel != null && this.tableModel.isShowAuto() != showAuto) {
            this.tableModel.setShowAuto(showAuto);
            firePropertyChange(FLUSH_TABLEMODEL, null, this.tableModel);
        }
    }

    public boolean isShowAuto() {
        return this.tableModel.isShowAuto();
    }

    public void flush() {
        firePropertyChange(FLUSH_TABLEMODEL, null, tableModel);
    }

    /**
     * whether calculate auto annotations, or not.
     */
    public void setCalculate(boolean calculate) {
        if (this.tableModel != null && calculate != this.tableModel.isCalculate()) {
            this.tableModel.setCalculate(calculate);
            firePropertyChange(FLUSH_TABLEMODEL, null, this.tableModel);
        }
    }

    public boolean isCalculate() {
        return this.tableModel.isCalculate();
    }

    public void setShowWaterLoss(boolean showWaterLoss) {
        if (this.tableModel != null && showWaterLoss != params.isShowWaterLoss()) {
            this.tableModel.setShowWaterLoss(showWaterLoss);
            firePropertyChange(FLUSH_TABLEMODEL, null, this.tableModel);
        }
    }

    public void setShowAmmoniaLoss(boolean showAmmoniaLoss) {
        if (this.tableModel != null && showAmmoniaLoss != params.isShowAmmoniaLoss()) {
            this.tableModel.setShowAmmoniaLoss(showAmmoniaLoss);
            firePropertyChange(FLUSH_TABLEMODEL, null, this.tableModel);
        }
    }

    public void setPeaks(double[] mzArray, double[] intensityArray) {
        if (this.tableModel != null) {
            this.tableModel.setPeaks(mzArray, intensityArray);
            firePropertyChange(FLUSH_TABLEMODEL, null, this.tableModel);
        }
    }

    public void setPeaks(PeakSet peakSet) {
        if (this.tableModel != null) {
            this.tableModel.setPeaks(peakSet);
            firePropertyChange(FLUSH_TABLEMODEL, "", this.tableModel);
        }
    }

    public void setRange(double range) {
        if (this.tableModel != null) {
            this.tableModel.setRange(range);
            firePropertyChange(FLUSH_TABLEMODEL, "", this.tableModel);
        }
    }

    public void addManualAnnotation(IonAnnotation annotation) {
        if (this.tableModel != null) {
            this.tableModel.addManualAnnotation(annotation);
            firePropertyChange(FLUSH_TABLEMODEL, "", this.tableModel);
        }
    }

    public void addAllManualAnnotations(List<IonAnnotation> annotationList) {
        if (this.tableModel != null) {
            this.tableModel.addAllManualAnnotations(annotationList);
            firePropertyChange(FLUSH_TABLEMODEL, "", this.tableModel);
        }
    }

    public boolean hasManualAnnotations() {
        return this.tableModel.getAllManualAnnotations().size() > 0;
    }

    public TableCellRenderer getCellRenderer(int row, int column) {
        if (this.tableModel.isMassColumn(column)) {
            return new ExperimentalFragmentedIonsRenderer(this.tableModel.getMatchedData(), row, column);
        } else {
            return super.getCellRenderer(row, column);
        }
    }

    public Component prepareRenderer(TableCellRenderer renderer,
                                     int rowIndex, int vColIndex) {
        Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
        Object to = getValueAt(rowIndex, vColIndex);

        IonAnnotation[][] matchedData = tableModel.getMatchedData();
        IonAnnotation po = matchedData[rowIndex][vColIndex];

        if (po != null && to != null) {
            double practice = po.getMz().doubleValue();
            double theoretical = ((ProductIon) to).getMassOverCharge();

            NumberFormat formatter = NumberFormat.getInstance();
            formatter.setMaximumFractionDigits(MzGraphConstants.TABLE_FRACTION);

            JComponent jc = (JComponent)c;
            jc.setToolTipText("m/z:" + formatter.format(practice) + " " +
                              "Error: " + formatter.format(practice - theoretical));
        }

        return c;
    }
}
