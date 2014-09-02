package uk.ac.ebi.pride.toolsuite.mzgraph.psm;

import uk.ac.ebi.pride.data.controller.impl.ControllerImpl.PrideXmlControllerImpl;
import uk.ac.ebi.pride.data.core.CvParam;
import uk.ac.ebi.pride.data.core.FragmentIon;
import uk.ac.ebi.pride.data.core.Protein;
import uk.ac.ebi.pride.data.core.Spectrum;
import uk.ac.ebi.pride.utilities.iongen.model.PrecursorIon;
import uk.ac.ebi.pride.utilities.iongen.impl.DefaultPrecursorIon;
import uk.ac.ebi.pride.utilities.mol.Peptide;
import uk.ac.ebi.pride.utilities.mol.ProductIonPair;
import uk.ac.ebi.pride.utilities.iongen.ion.FragmentIonType;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation.IonAnnotation;
import uk.ac.ebi.pride.toolsuite.mzgraph.gui.data.ExperimentalFragmentedIonsTableModel;
import uk.ac.ebi.pride.toolsuite.mzgraph.gui.data.ExperimentalParams;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Creator: Qingwei-XU
 * Date: 07/11/12
 */

public class PSMRun {
    private List<IonAnnotation> getAutoAnnotationList(uk.ac.ebi.pride.data.core.Peptide peptide, Spectrum spectrum) {
        Peptide newPeptide = PSMTestUtils.toPeptide(peptide);
        int charge = 0;
        try {
            List<CvParam> params = spectrum.getPrecursors().get(0).getSelectedIons().get(0).getCvParams();
            for (CvParam param : params) {
                if (param.getName().equals("ChargeState")) {
                    charge = Integer.parseInt(param.getValue());
                    break;
                }
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        } finally {
            charge = charge == 0 ? 2 : charge;
            if (charge > 2) {
                charge = 2;
            }
        }
        double[] mzArray = spectrum.getMzBinaryDataArray().getDoubleArray();
        double[] intensityArray = spectrum.getIntensityBinaryDataArray().getDoubleArray();

        PrecursorIon precursorIon = new DefaultPrecursorIon(newPeptide, charge);
        ExperimentalFragmentedIonsTableModel tableModel = new ExperimentalFragmentedIonsTableModel(precursorIon, ProductIonPair.B_Y);
        tableModel.setCalculate(true);
        tableModel.setShowAuto(true);
        tableModel.setPeaks(mzArray, intensityArray);
        return tableModel.getAutoAnnotations();
    }

    private List<IonAnnotation> getManualAnnotationList(uk.ac.ebi.pride.data.core.Peptide peptide) {
        List<FragmentIon> ions = peptide.getFragmentation();
        List<IonAnnotation> annotationList = PSMTestUtils.convertToIonAnnotations(ions);

        List<IonAnnotation> newAnnotationList = new ArrayList<IonAnnotation>();

        for (IonAnnotation annotation : annotationList) {
            FragmentIonType type = annotation.getAnnotationInfo().getItem(0).getType();
            if (type.equals(FragmentIonType.B_ION) || type.equals(FragmentIonType.Y_ION)) {
                newAnnotationList.add(annotation);
            }
        }

        return newAnnotationList;
    }

    public static void main(String[] args) throws Exception {
        String ret = "\r\n";

        long start = new Date().getTime();

        File inputFile = new File(args[0]);
        File outFile = new File(args[1] + "-" + start + ".csv");

        PrideXmlControllerImpl controller = new PrideXmlControllerImpl(inputFile);
        BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
        writer.write("input file: " + inputFile.getName() + ret + ret);

        Protein protein;
        Spectrum spectrum;
        PSMRun test = new PSMRun();
        int count = 0;
        List<Double> result = new ArrayList<Double>();
        for (int i = 0; i < controller.getProteinIds().size(); i++) {
            protein = controller.getProteinById(i);
            for (uk.ac.ebi.pride.data.core.Peptide peptide : protein.getPeptides()) {
                spectrum = peptide.getSpectrum();
                count++;
                List<IonAnnotation> autoList = test.getAutoAnnotationList(peptide, spectrum);
                List<IonAnnotation> manualList = test.getManualAnnotationList(peptide);
                result.add(PSMTestUtils.overlap(autoList, manualList));
            }
        }
        writer.write(PSMTestUtils.cluster(result) + ret);

        long end = new Date().getTime();
        long elapse = end - start;

        writer.write("total work: " + elapse + "(ms)" + ret);
        writer.write("total count:" + count);

        writer.close();
        controller.close();
    }

}
