package uk.ac.ebi.pride.toolsuite.mzgraph;

import uk.ac.ebi.pride.utilities.mol.NeutralLoss;
import uk.ac.ebi.pride.utilities.mol.PTModification;
import uk.ac.ebi.pride.utilities.mol.Peptide;
import uk.ac.ebi.pride.utilities.iongen.ion.FragmentIonType;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation.IonAnnotation;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation.IonAnnotationInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Creator: Qingwei-XU
 * Date: 17/10/12
 */

public class ExampleUtil {
    public static double[] mzArr = new double[] {
            74.21673968615,
            152,
            174.894233918164,
            407.748486097306,
            499.173902356863,
            500.702474820402,
            570,
            580,
            600,
            637.871693379752,
            765.819714307543,
            782.89539392012,
            854.535170582246,
            868.510487442724,
            999.511048008345,
            1016.88982244366
    };

    public static double[] intentArr = new double[]{
            0.05,
            88,
            44.345345345,
            6.0,
            1.4545,
            5.0,
            42,
            34,
            72,
            8.23423,
            20.1231231,
            11.23,
            25.1243,
            30.29,
            12.345,
            9.12345
    };

    /**
     * generate a peptide with two modifications. the mass table reference following URL:
     * https://www.proteomecluster.com/thegpm-cgi/peptide.pl?ltype=&path=/tandem/archive/www.proteomecluster.com-279-c8c544be.xml&uid=21149&label=ENSP00000264335&homolog=21149&id=3771.1.1&proex=-1
     */
    public static Peptide generatePeptide() {
        String sequence = "YDEMVESMK";
        Peptide peptide = new Peptide(sequence);
        List<Double> monoMassDeltas1 = new ArrayList<Double>();
        monoMassDeltas1.add(15.9949);
        PTModification m1 = new PTModification(null, null, null, monoMassDeltas1, null);
        List<Double> monoMassDeltas2 = new ArrayList<Double>();
        monoMassDeltas2.add(15.9949);
        PTModification m2 = new PTModification(null, null, null, monoMassDeltas2, null);
        peptide.addModification(3, m1);
        peptide.addModification(7, m2);

        return peptide;
    }

    public static List<IonAnnotation> generateAnnotationList() {
        IonAnnotationInfo ionInfo1 = new IonAnnotationInfo();
        // row=8 col=13 mz=500.18
        IonAnnotationInfo.Item annotationItem = new IonAnnotationInfo.Item(2, FragmentIonType.Z_ION, 8, NeutralLoss.WATER_LOSS);
        ionInfo1.addItem(annotationItem);
        // mzArr[4]=499.173902356863
        IonAnnotation ion1 = new IonAnnotation(mzArr[4], intentArr[4], ionInfo1);

        IonAnnotationInfo ionInfo2 = new IonAnnotationInfo();
        // row=5 col=10 mz=637.22
        IonAnnotationInfo.Item annotationItem2 = new IonAnnotationInfo.Item(1, FragmentIonType.B_ION, 5, NeutralLoss.AMMONIA_LOSS);
        ionInfo2.addItem(annotationItem2);
        // mzArr[9]=637.871693379752
        IonAnnotation ion2 = new IonAnnotation(mzArr[9], intentArr[9], ionInfo2);

        IonAnnotationInfo ionInfo3 = new IonAnnotationInfo();
        // row=7 col=10 mz=853.29
        IonAnnotationInfo.Item annotationItem3 = new IonAnnotationInfo.Item(1, FragmentIonType.B_ION, 7, NeutralLoss.AMMONIA_LOSS);
        ionInfo3.addItem(annotationItem3);
        // mzArr[12]=854.535170582246
        IonAnnotation ion3 = new IonAnnotation(mzArr[12], intentArr[12], ionInfo3);

        List<IonAnnotation> annotationList = new ArrayList<IonAnnotation>();
        annotationList.add(ion1);
        annotationList.add(ion2);
        annotationList.add(ion3);

        return annotationList;
    }

    public static List<IonAnnotation> specialAnnotationList() {
        List<IonAnnotation> annotationList = new ArrayList<IonAnnotation>();

        IonAnnotationInfo ionInfo1 = new IonAnnotationInfo();
        // row=1 col=13 mz=73.53  immonium ion
        IonAnnotationInfo.Item annotationItem = new IonAnnotationInfo.Item(2, FragmentIonType.IMMONIUM_ION, 0, null);
        ionInfo1.addItem(annotationItem);
        // mzArr[0]=74.173902356863
        IonAnnotation ion1 = new IonAnnotation(mzArr[0], intentArr[0], ionInfo1);

        annotationList.add(ion1);

        return annotationList;
    }
}
