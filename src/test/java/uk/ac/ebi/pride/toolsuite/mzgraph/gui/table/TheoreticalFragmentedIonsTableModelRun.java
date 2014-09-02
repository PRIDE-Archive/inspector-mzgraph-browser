package uk.ac.ebi.pride.toolsuite.mzgraph.gui.table;

import uk.ac.ebi.pride.utilities.iongen.model.PrecursorIon;
import uk.ac.ebi.pride.utilities.iongen.model.ProductIon;
import uk.ac.ebi.pride.utilities.iongen.impl.DefaultPrecursorIon;
import uk.ac.ebi.pride.utilities.mol.ProductIonPair;
import uk.ac.ebi.pride.toolsuite.mzgraph.gui.data.TheoreticalFragmentedIonsTableModel;

/**
 * Creator: Qingwei-XU
 * Date: 10/10/12
 */

public class TheoreticalFragmentedIonsTableModelRun {
    public static void main(String[] args) {
        String sequence = "EASPLSSNKLILR";
        PrecursorIon precursorIon = new DefaultPrecursorIon(sequence, 3);
        TheoreticalFragmentedIonsTableModel model = new TheoreticalFragmentedIonsTableModel(precursorIon, ProductIonPair.B_Y);

        for (int i = 0; i < model.getColumnCount(); i++) {
            System.out.print(model.getColumnName(i) + "\t");
        }
        System.out.println();

        Object o;
        for (int i = 0; i < model.getRowCount(); i++) {
            for (int j = 0; j < model.getColumnCount(); j++) {
                o = model.getValueAt(i, j);

                if (o instanceof ProductIon) {
                    System.out.print(((ProductIon) o).getMassOverCharge() + "\t");
                } else {
                    System.out.print((o == null ? "" : o) + "\t");
                }
            }
            System.out.println();
        }
    }
}
