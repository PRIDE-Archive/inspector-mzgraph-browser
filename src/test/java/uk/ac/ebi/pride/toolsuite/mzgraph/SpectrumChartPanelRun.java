package uk.ac.ebi.pride.toolsuite.mzgraph;

import uk.ac.ebi.pride.utilities.mol.NeutralLoss;
import uk.ac.ebi.pride.utilities.iongen.ion.FragmentIonType;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation.IonAnnotation;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation.IonAnnotationInfo;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.graph.SpectrumPanel;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 17-Jun-2010
 * Time: 16:24:48
 */
public class SpectrumChartPanelRun {
    public static void main(String[] args) {
//        SpectrumPanel chartPanel = new SpectrumPanel(ExampleUtil.mzArr, ExampleUtil.intentArr);
//        chartPanel.setPeptide(ExampleUtil.generatePeptide());
        SpectrumPanel chartPanel = new SpectrumPanel();
        chartPanel.setPeaks(ExampleUtil.mzArr, ExampleUtil.intentArr);
        chartPanel.paintGraph();
        chartPanel.setGridLineVisibility(false);

        JFrame frame = new JFrame("Spectrum");
        frame.setContentPane(chartPanel);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        IonAnnotationInfo ionInfo1 = new IonAnnotationInfo();
        IonAnnotationInfo.Item annotationItem = new IonAnnotationInfo.Item(-2, FragmentIonType.Y_ION, 2, NeutralLoss.WATER_LOSS);
        ionInfo1.addItem(annotationItem);
        IonAnnotation ion1 = new IonAnnotation(ExampleUtil.mzArr[3], ExampleUtil.intentArr[3], ionInfo1);

        IonAnnotationInfo ionInfo2 = new IonAnnotationInfo();
        IonAnnotationInfo.Item annotationItem2 = new IonAnnotationInfo.Item(-2, FragmentIonType.B_ION, 2, null);
        ionInfo2.addItem(annotationItem2);
        IonAnnotation ion2 = new IonAnnotation(ExampleUtil.mzArr[5], ExampleUtil.intentArr[5], ionInfo2);

        java.util.List<IonAnnotation> ions = new ArrayList<IonAnnotation>();
        ions.add(ion1);
        ions.add(ion2);
        chartPanel.addFragmentIons(ions);
    }

//    public static void main(String[] args) {
//        double[] mzArr = new double[]{1.0, 2.012312313, 3.0, 4.234, 6.0, 7.34342};
//        double[] intentArr = new double[]{2.0, 4.345345345, 6.0, 1.4545, 5.0, 8.23423};
//
//        SpectrumPanel chartPanel = new SpectrumPanel(mzArr, intentArr);
//        chartPanel.paintGraph();
//        chartPanel.setGridLineVisibility(false);
//
//        JFrame frame = new JFrame("Spectrum");
//        frame.setContentPane(chartPanel);
//        frame.setSize(1500, 600);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setVisible(true);
//
//        IonAnnotationInfo ionInfo1 = new IonAnnotationInfo();
//        IonAnnotationInfo.Item annotationItem = new IonAnnotationInfo.Item(-2, FragmentIonType.Y_ION, 2, NeutralLoss.WATER_LOSS);
//        ionInfo1.addItem(annotationItem);
//        IonAnnotation ion1 = new IonAnnotation(1.0, 0.05, ionInfo1);
//
//        IonAnnotationInfo ionInfo2 = new IonAnnotationInfo();
//        IonAnnotationInfo.Item annotationItem2 = new IonAnnotationInfo.Item(-2, FragmentIonType.B_ION, 2, null);
//        ionInfo1.addItem(annotationItem2);
//        IonAnnotation ion2 = new IonAnnotation(2.012312313, 4.345345345, ionInfo2);
//
//        java.util.List<IonAnnotation> ions = new ArrayList<IonAnnotation>();
//        ions.add(ion1);
//        ions.add(ion2);
//        chartPanel.addAllManualAnnotations(ions);
//    }

}
