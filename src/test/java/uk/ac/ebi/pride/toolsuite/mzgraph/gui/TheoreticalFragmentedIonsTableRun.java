package uk.ac.ebi.pride.toolsuite.mzgraph.gui;

import uk.ac.ebi.pride.utilities.iongen.model.PrecursorIon;
import uk.ac.ebi.pride.utilities.iongen.impl.DefaultPrecursorIon;
import uk.ac.ebi.pride.utilities.mol.ProductIonPair;
import uk.ac.ebi.pride.toolsuite.mzgraph.ExampleUtil;

import javax.swing.*;

/**
 * Creator: Qingwei-XU
 * Date: 11/10/12
 */

public class TheoreticalFragmentedIonsTableRun {
    public static void main(String[] args) {
        // table value matrix reference: theoretical_fragmented_ions.proteomecluster file
        // which stored in the test resources directory.
        PrecursorIon precursorIon = new DefaultPrecursorIon(ExampleUtil.generatePeptide(), 2);
        TheoreticalFragmentedIonsTable table = new TheoreticalFragmentedIonsTable(precursorIon);

        table.setProductIonPair(ProductIonPair.A_X);
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);
        JFrame mainFrame = new JFrame();
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.getContentPane().add(scrollPane);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }
}
