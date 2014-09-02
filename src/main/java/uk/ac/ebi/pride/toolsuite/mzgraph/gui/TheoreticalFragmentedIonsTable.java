package uk.ac.ebi.pride.toolsuite.mzgraph.gui;

import uk.ac.ebi.pride.utilities.iongen.model.PrecursorIon;
import uk.ac.ebi.pride.utilities.mol.AminoAcid;
import uk.ac.ebi.pride.utilities.mol.PTModification;
import uk.ac.ebi.pride.utilities.mol.ProductIonPair;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.graph.MzGraphConstants;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.renderer.AminoAcidRenderer;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.renderer.TheoreticalFragmentedIonsRenderer;
import uk.ac.ebi.pride.toolsuite.mzgraph.gui.data.TheoreticalFragmentedIonsTableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.Map;

/**
 * Creator: Qingwei-XU
 * Date: 10/10/12
 */

public class TheoreticalFragmentedIonsTable extends JTable {
    private String fontName = MzGraphConstants.TABLE_FONT_NAME;
    private int columnFontSize = MzGraphConstants.TABLE_COLUMN_FONT_SIZE;
    private int cellFontSize = MzGraphConstants.TABLE_CELL_FONT_SIZE;
    private TheoreticalFragmentedIonsTableModel tableModel;

    private Map<Integer, PTModification> ptm;

    public TheoreticalFragmentedIonsTable(PrecursorIon precursorIon) {
        this(precursorIon, ProductIonPair.B_Y);
    }

    public TableCellRenderer getCellRenderer(int row, int column) {
        if (tableModel.isMassColumn(column)) {
            return new TheoreticalFragmentedIonsRenderer(row, column);
        } else if (tableModel.isIDColumn(column)) {
            return new DefaultTableCellRenderer() {
                public void setHorizontalAlignment(int alignment) {
                    super.setHorizontalAlignment(SwingConstants.RIGHT);
                }

                public void setForeground(Color fg) {
                    super.setForeground(Color.blue);
                }

                public void setBackground(Color fg) {
                    super.setBackground(Color.lightGray);
                }

                public void setFont(Font font) {
                    super.setFont(new Font(fontName, Font.BOLD, cellFontSize));
                }
            };
        } else if (tableModel.isSeqColumn(column)) {
            return new AminoAcidRenderer(ptm.get(row));
        } else {
            return super.getCellRenderer(row, column);
        }
    }

    public TheoreticalFragmentedIonsTable(PrecursorIon precursorIon,
                                          ProductIonPair ionPair) {
        tableModel = new TheoreticalFragmentedIonsTableModel(precursorIon, ionPair);
        setModel(tableModel);
        tableModel.addTableModelListener(this);
        this.ptm = precursorIon.getPeptide().getPTM();

        setPreferredScrollableViewportSize(new Dimension(1400, 300));
        getTableHeader().setFont(new Font(fontName, Font.BOLD, columnFontSize));
        setFont(new Font(fontName, Font.PLAIN, cellFontSize));

        setAutoCreateColumnsFromModel(false);
        setRowHeight(cellFontSize + 8);

        //set ID column width
        getColumnModel().getColumn(0).setMaxWidth(24);
        getColumnModel().getColumn(0).setMinWidth(24);
        getColumnModel().getColumn(getColumnCount() - 1).setMaxWidth(24);
        getColumnModel().getColumn(getColumnCount() - 1).setMinWidth(24);
        getColumnModel().getColumn(getColumnCount() / 2).setMaxWidth(48);
        getColumnModel().getColumn(getColumnCount() / 2).setMinWidth(48);

        getTableHeader().setReorderingAllowed(false);
    }

    public void setProductIonPair(ProductIonPair ionPair) {
        this.tableModel.setProductIonPair(ionPair);

        TableColumnModel columnModel = getColumnModel();
        TableColumn column;
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            column = columnModel.getColumn(i);
            column.setHeaderValue(tableModel.getColumnName(i));
        }
    }

    public Component prepareRenderer(TableCellRenderer renderer,
                                     int rowIndex, int vColIndex) {
        Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
        Object o = getValueAt(rowIndex, vColIndex);

        PTModification modification;
        if (o instanceof AminoAcid) {
            modification = ptm.get(rowIndex);
            if (modification != null) {
                JComponent jc = (JComponent)c;
                jc.setToolTipText((modification.getName() == null ? "" : modification.getName()) + "[" + modification.getMonoMassDeltas().get(0) + "]");
            }
        }

        return c;
    }
}
