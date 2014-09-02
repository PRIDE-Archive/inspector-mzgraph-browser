package uk.ac.ebi.pride.toolsuite.mzgraph;

import uk.ac.ebi.pride.toolsuite.gui.SideToolBarPanel;
import uk.ac.ebi.pride.toolsuite.gui.action.ActionCascadePanel;
import uk.ac.ebi.pride.utilities.mol.NeutralLoss;
import uk.ac.ebi.pride.utilities.iongen.ion.FragmentIonType;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation.IonAnnotation;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation.IonAnnotationInfo;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.graph.MzGraphConstants;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.graph.SpectrumPanel;
import uk.ac.ebi.pride.toolsuite.mzgraph.gui.filter.CheckBoxFilterPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 22-Jun-2010
 * Time: 12:37:00
 */
public class SideBarPanelRun {
    public static void main(String[] args) {
        Runnable runner = new Runnable() {
            public void run() {
                createGUI();
            }
        };

        EventQueue.invokeLater(runner);
    }

    private static void createGUI() {
        SpectrumPanel chartPanel = new SpectrumPanel(ExampleUtil.mzArr, ExampleUtil.intentArr);
//        chartPanel.setPeptide(ExampleUtil.generatePeptide());

        chartPanel.paintGraph();
        chartPanel.setGridLineVisibility(false);

        SideToolBarPanel tool = new SideToolBarPanel(chartPanel, SideToolBarPanel.WEST);
        JFrame frame = new JFrame("Side Bar Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(tool, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

        IonAnnotationInfo ionInfo1 = new IonAnnotationInfo();
        IonAnnotationInfo.Item annotationItem = new IonAnnotationInfo.Item(-2, FragmentIonType.Y_ION, 2, NeutralLoss.WATER_LOSS);
        ionInfo1.addItem(annotationItem);
        IonAnnotation ion1 = new IonAnnotation(1.0, 0.05, ionInfo1);

        IonAnnotationInfo ionInfo2 = new IonAnnotationInfo();
        IonAnnotationInfo.Item annotationItem2 = new IonAnnotationInfo.Item(-2, FragmentIonType.B_ION, 2, null);
        ionInfo2.addItem(annotationItem2);
        IonAnnotation ion2 = new IonAnnotation(2.012312313, 4.345345345, ionInfo2);

        IonAnnotationInfo ionInfo3 = new IonAnnotationInfo();
        IonAnnotationInfo.Item annotationItem3 = new IonAnnotationInfo.Item(-2, FragmentIonType.B_ION, 2, null);
        ionInfo3.addItem(annotationItem3);
        IonAnnotationInfo.Item annotationItem4 = new IonAnnotationInfo.Item(-2, FragmentIonType.Y_ION, 2, null);
        ionInfo3.addItem(annotationItem4);
        IonAnnotation ion3 = new IonAnnotation(3.012312313, 7.345345345, ionInfo3);

        java.util.List<IonAnnotation> ions = new ArrayList<IonAnnotation>();
        ions.add(ion1);
        ions.add(ion2);
        ions.add(ion3);
        chartPanel.getModel().addAnnotations(ions);


        CheckBoxFilterPanel fragPanel = new CheckBoxFilterPanel(MzGraphConstants.FRAGMENT_ION_NAME, chartPanel.getFragmentIonVisibilities());
        CheckBoxFilterPanel aminoPanel = new CheckBoxFilterPanel(MzGraphConstants.AMINO_ACID_NAME, chartPanel.getAminoAcidVisibilities());
        ActionCascadePanel acPanel = new ActionCascadePanel();
        acPanel.add(fragPanel);
        acPanel.add(aminoPanel);
//        fragPanel.setBackground(Color.white);
//        fragPanel.setPreferredSize(new Dimension(200, 200));
        tool.addComponent(null, "Filter", "Filter", "Filter", acPanel);

        tool.addCommand(null, "Mass", "Mass Difference", SpectrumPanel.CLEAR_MASS_DIFF, false);
        tool.addCommand(null, "Peak", "Peak List", SpectrumPanel.HIDE_PEAK_LIST, true);
        tool.addCommand(null, "Grid", "Show grid", SpectrumPanel.GRID_LINE_COMMAND, true);
        tool.addCommand(null, "Save", "Save as", SpectrumPanel.SAVE_AS, false);
        tool.addCommand(null, "Print", "Print", SpectrumPanel.PRINT_COMMAND, false);


    }


}
