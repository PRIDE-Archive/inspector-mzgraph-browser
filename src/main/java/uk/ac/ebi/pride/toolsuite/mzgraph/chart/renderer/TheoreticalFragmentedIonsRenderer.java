package uk.ac.ebi.pride.toolsuite.mzgraph.chart.renderer;

import uk.ac.ebi.pride.utilities.iongen.model.PeptideIon;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.graph.MzGraphConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.NumberFormat;

/**
 * Creator: Qingwei-XU
 * Date: 11/10/12
 */

public class TheoreticalFragmentedIonsRenderer extends DefaultTableCellRenderer {
    private NumberFormat formatter;

    /**
     * when the table repaint, system will re-create new renderer for all the table cell.
     * Thus, we declare highlight row and column argument is static, which can be shared
     * in different PeptideIon renderer object.
     */
    private static int highlightRow;
    private static int highlightColumn;

    public TheoreticalFragmentedIonsRenderer(int row, int column) {
        if (row == highlightRow && column == highlightColumn) {
            setBackground(Color.lightGray);
        }

        this.formatter = NumberFormat.getNumberInstance();
        this.formatter.setMaximumFractionDigits(MzGraphConstants.TABLE_FRACTION);
        setHorizontalAlignment(SwingConstants.RIGHT);
    }

    public void setValue(Object o) {
        if (o == null) {
            setText("");
        } else if (! (o instanceof PeptideIon)) {
            setText("N/A");
        } else {
            PeptideIon peptideIon = (PeptideIon) o;
            setText(this.formatter.format(peptideIon.getMassOverCharge()));
        }
    }

    public void setHighlight(int row, int column) {
        this.highlightRow = row;
        this.highlightColumn = column;
    }
}
