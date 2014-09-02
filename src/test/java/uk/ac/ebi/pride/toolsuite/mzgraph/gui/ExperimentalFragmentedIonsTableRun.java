package uk.ac.ebi.pride.toolsuite.mzgraph.gui;

import uk.ac.ebi.pride.utilities.iongen.model.PrecursorIon;
import uk.ac.ebi.pride.utilities.iongen.impl.DefaultPrecursorIon;
import uk.ac.ebi.pride.utilities.mol.ProductIonPair;
import uk.ac.ebi.pride.toolsuite.mzgraph.ExampleUtil;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation.IonAnnotation;
import uk.ac.ebi.pride.toolsuite.mzgraph.gui.data.ExperimentalFragmentedIonsTableModel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Creator: Qingwei-XU
 * Date: 11/10/12
 */

public class ExperimentalFragmentedIonsTableRun {
    public static void main(String[] args) {
        PrecursorIon precursorIon = new DefaultPrecursorIon(ExampleUtil.generatePeptide());

        ExperimentalFragmentedIonsTable table = new ExperimentalFragmentedIonsTable(precursorIon, ProductIonPair.B_Y, ExampleUtil.mzArr, ExampleUtil.intentArr);

        //add annotation by hand
        ExperimentalFragmentedIonsTableModel tableModel = (ExperimentalFragmentedIonsTableModel) table.getModel();
        List<IonAnnotation> annotationList = ExampleUtil.generateAnnotationList();
        tableModel.addAllManualAnnotations(annotationList);

        tableModel.addAllManualAnnotations(ExampleUtil.specialAnnotationList());

        // test whether show auto and manual annotations, or not.
//        table.setShowAuto(true);
//        table.setShowAuto(false);

//        table.setProductIonPair(ProductIonPair.A_X);

        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table);
        JFrame mainFrame = new JFrame();
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.getContentPane().add(scrollPane);
        mainFrame.setPreferredSize(new Dimension(1000, 300));
        mainFrame.pack();
        mainFrame.setVisible(true);
    }
}
