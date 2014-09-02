package uk.ac.ebi.pride.toolsuite.mzgraph.chart.renderer;

import uk.ac.ebi.pride.utilities.mol.AminoAcid;
import uk.ac.ebi.pride.utilities.mol.PTModification;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Creator: Qingwei-XU
 * Date: 11/10/12
 */

public class AminoAcidRenderer extends DefaultTableCellRenderer {
    private PTModification modification;

    public AminoAcidRenderer(PTModification modification) {
        setHorizontalAlignment(SwingConstants.CENTER);
        setForeground(Color.black);
        setBackground(Color.lightGray);
        Font font = getFont();
        setFont(new Font(font.getFontName(), Font.BOLD, font.getSize()));

        this.modification = modification;
    }

    public void setValue(Object o) {
        AminoAcid acid = (AminoAcid) o;

        if (this.modification != null) {
            setText("[" + acid.getOneLetterCode() + "]");
        } else {
            setText("" + acid.getOneLetterCode());
        }
    }
}
