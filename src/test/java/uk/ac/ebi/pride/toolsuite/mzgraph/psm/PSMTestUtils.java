package uk.ac.ebi.pride.toolsuite.mzgraph.psm;

import uk.ac.ebi.pride.utilities.data.core.FragmentIon;
import uk.ac.ebi.pride.utilities.data.core.Modification;
import uk.ac.ebi.pride.utilities.mol.NeutralLoss;
import uk.ac.ebi.pride.utilities.mol.PTModification;
import uk.ac.ebi.pride.utilities.mol.Peptide;
import uk.ac.ebi.pride.utilities.iongen.ion.FragmentIonType;
import uk.ac.ebi.pride.utilities.iongen.ion.FragmentIonUtilities;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation.IonAnnotation;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation.IonAnnotationInfo;

import java.io.IOException;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: qingwei
 * Date: 10/12/12
 */
public class PSMTestUtils {
    public static Peptide toPeptide(uk.ac.ebi.pride.utilities.data.core.Peptide oldPeptide) {
        String sequence = oldPeptide.getSequence();
        List<Modification> oldModifications = oldPeptide.getModifications();

        Peptide newPeptide = new Peptide(sequence);
        PTModification newModification;

        String name;
        String type = null;
        String label;
        List<Double> monoMassDeltas;
        List<Double> avgMassDeltas;
        int position;
        for (uk.ac.ebi.pride.utilities.data.core.Modification oldModification : oldModifications) {
            name = oldModification.getName();
            label = "111";
            monoMassDeltas = oldModification.getMonoisotopicMassDelta();
            avgMassDeltas = oldModification.getAvgMassDelta();
            newModification = new PTModification(name, type, label, monoMassDeltas, avgMassDeltas);

            /**
             * old modification position from [0..length], 0 means the position locate in c-terminal.
             * the new modification from [0..length-1], 0 means the first amino acid of peptide.
             * The modification worked in c-terminal or first amino acid, the theoretical mass are same.
             */
            position = oldModification.getLocation() - 1;
            if (position == -1) {
                position = 0;
            }

            newPeptide.addModification(position, newModification);
        }

        return newPeptide;
    }

    public static FragmentIonType getIonType(FragmentIon ion) {
        return FragmentIonUtilities.getFragmentIonType(ion.getIonType());
    }

    private static IonAnnotation getOverlapIonAnnotation(FragmentIon ion, List<IonAnnotation> ionAnnotations) {
        IonAnnotation result = null;
        double mz = ion.getMz();
        double intensity = ion.getIntensity();

        for (IonAnnotation ionAnnotation : ionAnnotations) {
            if (ionAnnotation.getMz().doubleValue() == mz
                    && ionAnnotation.getIntensity().doubleValue() == intensity) {
                result = ionAnnotation;
            }
        }
        return result;
    }

    public static List<IonAnnotation> convertToIonAnnotations(List<FragmentIon> ions) {
        List<IonAnnotation> ionAnnotations = new ArrayList<IonAnnotation>();
        if (ions != null) {
            for (FragmentIon ion : ions) {
                // get the fragment ion type
                FragmentIonType ionType = getIonType(ion);
                // get the fragment loss
                NeutralLoss fragLoss = FragmentIonUtilities.getFragmentIonNeutralLoss(ion.getIonType());
                // m/z and intensity
                IonAnnotation ionAnnotation = getOverlapIonAnnotation(ion, ionAnnotations);
                IonAnnotationInfo ionInfo;
                if (ionAnnotation == null) {
                    ionInfo = new IonAnnotationInfo();
                    ionAnnotation = new IonAnnotation(ion.getMz(), ion.getIntensity(), ionInfo);
                    ionAnnotations.add(ionAnnotation);
                } else {
                    ionInfo = ionAnnotation.getAnnotationInfo();
                }
                ionInfo.addItem(ion.getCharge(), ionType, ion.getLocation(), fragLoss);
            }
        }
        return ionAnnotations;
    }

    /**
     * Calculate the overlap (percent value) between auto annotation list and manual annotation list.
     * There are three parts in overlap output:
     * <P>A means Auto annotations collection, M means Manual annotations collection.</P>
     * <ol>
     *     <li>auto annotation list size</li>
     *     <li>overlap in auto: (A and M) / A</li>
     *     <li>manual annotation list size</li>
     *     <li>overlap in manual: (A and M) / M</li>
     *     <li>the overlap factor: (A and M) / (A or M)</li>
     * </ol>
     *
     */
    public static double overlap(List<IonAnnotation> autoList, List<IonAnnotation> manualList) {
        Set<IonAnnotation> union = new HashSet<IonAnnotation>();
        Set<IonAnnotation> intersection = new HashSet<IonAnnotation>();
        union.addAll(manualList);

        for (IonAnnotation autoItem : autoList) {
            if (! manualList.contains(autoItem)) {
                union.add(autoItem);
            } else {
                intersection.add(autoItem);
            }
        }

        int interSize = intersection.size();
        int unionSize = union.size();
        int autoSize = autoList.size();
        int manualSize = manualList.size();

        double overlap = interSize * 100.0d / unionSize;
        double overlap_a = autoSize == 0 ? autoSize : interSize * 100.0d / autoSize;
        double overlap_m = manualSize == 0 ? manualSize : interSize * 100.0d / manualSize;

        NumberFormat formatter = NumberFormat.getInstance();
        formatter.setMaximumFractionDigits(2);

        return overlap_m;
    }

    public static String cluster(List<Double> overlapList) {
        int g1 = 0;  // less than or equal 20%;
        int g2 = 0;  // (20% - 40%];
        int g3 = 0;  // (40% - 60%];
        int g4 = 0;  // (60% - 80%];
        int g5 = 0;  // great than 80%;

        for (Double overlap : overlapList) {
            if (overlap <= 20) {
                g1++;
            } else if (overlap <= 40) {
                g2++;
            } else if (overlap <= 60) {
                g3++;
            } else if (overlap <= 80) {
                g4++;
            } else {
                g5++;
            }
        }

       return   "-NaN < x <=20\t" + g1 + "\r\n" +
                "20 < x <=40\t" + g2 + "\r\n" +
                "40 < x <=60\t" + g3 + "\r\n" +
                "60 < x <=80\t" + g4 + "\r\n" +
                "80 < x < +NaN\t" + g5;
    }
}
