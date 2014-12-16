package uk.ac.ebi.pride.toolsuite.mzgraph.psm;

import uk.ac.ebi.pride.utilities.data.controller.impl.ControllerImpl.PrideXmlControllerImpl;
import uk.ac.ebi.pride.utilities.data.core.CvParam;
import uk.ac.ebi.pride.utilities.data.core.Protein;
import uk.ac.ebi.pride.utilities.data.core.Spectrum;
import uk.ac.ebi.pride.utilities.iongen.model.PeakSet;
import uk.ac.ebi.pride.utilities.iongen.model.PeptideScore;
import uk.ac.ebi.pride.utilities.iongen.model.PrecursorIon;
import uk.ac.ebi.pride.utilities.iongen.model.ProductIonSet;
import uk.ac.ebi.pride.utilities.iongen.impl.DefaultPrecursorIon;
import uk.ac.ebi.pride.utilities.mol.MoleculeUtilities;
import uk.ac.ebi.pride.utilities.mol.Peptide;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: qingwei
 * Date: 10/12/12
 */
public class PeptideScoreRun {
    private double[] weightList = {0.25, 0.25, 0.5, 0.5, 0.75, 1, 0.75, 0.5, 0.5, 0.25};

    private double getScore(uk.ac.ebi.pride.utilities.data.core.Peptide peptide, Spectrum spectrum) {
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
        PeakSet peakSet = PeakSet.getInstance(mzArray, intensityArray);
        PrecursorIon precursorIon = new DefaultPrecursorIon(newPeptide, charge);

        PeptideScore peptideScore = new PeptideScore(precursorIon, peakSet);
        ProductIonSet productIonSet = peptideScore.getProductIonSet();
        int splitSize = 100;
        return peptideScore.getWeightedAvgScore(productIonSet, splitSize, weightList);
    }

    private void reportClustering(File inFile, File outFile) throws Exception {
        String ret = "\r\n";

        long start = new Date().getTime();
        PrideXmlControllerImpl controller = new PrideXmlControllerImpl(inFile);
        BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));

        writer.write("input file: " + inFile.getName() + ret + ret);

        Protein protein;
        Spectrum spectrum;

        int count = 0;
        List<Double> result = new ArrayList<Double>();
        for (int i = 0; i < controller.getProteinIds().size(); i++) {
            protein = controller.getProteinById(i);
            for (uk.ac.ebi.pride.utilities.data.core.Peptide peptide : protein.getPeptides()) {
                spectrum = peptide.getSpectrum();
                result.add(getScore(peptide, spectrum));
                count++;
            }
        }
        writer.write(PSMTestUtils.cluster(result) + ret);

        long end = new Date().getTime();
        long elapse = end - start;

        writer.write("total work: " + elapse + "(ms)" + ret);
        writer.write("total peptide count:" + count);

        writer.close();
        controller.close();
    }

    private Double getDeltaMass(uk.ac.ebi.pride.utilities.data.core.Peptide peptide) {
        String sequence = peptide.getSequence();
        int charge = peptide.getSpectrumIdentification().getChargeState();
        double mz = peptide.getPrecursorMz();

        java.util.List<uk.ac.ebi.pride.utilities.data.core.Modification> mods = peptide.getModifications();

        java.util.List<Double> ptmMasses = new ArrayList<Double>();
        for (uk.ac.ebi.pride.utilities.data.core.Modification mod : mods) {
            java.util.List<Double> monoMasses = mod.getMonoisotopicMassDelta();
            if (monoMasses != null && !monoMasses.isEmpty()) {
                ptmMasses.add(monoMasses.get(0));
            }
        }

        return MoleculeUtilities.calculateDeltaMz(sequence, mz, charge, ptmMasses);
    }

    private void reportDeltaMass_PeptideScore(File inFile, File outFile) throws Exception {
        String ret = "\r\n";

        long start = new Date().getTime();
        PrideXmlControllerImpl controller = new PrideXmlControllerImpl(inFile);
        BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));

        writer.write(inFile.getName() + ret + ret);

        Protein protein;
        Spectrum spectrum;

        writer.write("DeltaMass\tPeptideScore" + ret);
        int count = 0;
        for (int i = 0; i < controller.getProteinIds().size(); i++) {
            protein = controller.getProteinById(i);
            for (uk.ac.ebi.pride.utilities.data.core.Peptide peptide : protein.getPeptides()) {
                spectrum = peptide.getSpectrum();
                writer.write(getDeltaMass(peptide) + "\t" + getScore(peptide, spectrum) + ret);
                count++;
            }
        }

        long end = new Date().getTime();
        long elapse = end - start;

        writer.write("total work: " + elapse + "(ms)" + ret);
        writer.write("total peptide count:" + count);

        writer.close();
        controller.close();
    }


    public static void main(String[] args) throws Exception {
        PeptideScoreRun test = new PeptideScoreRun();

        File inFile = new File(args[0]);
        File outFile = new File(args[1] + "-clustering.csv");
        test.reportClustering(inFile, outFile);

        outFile = new File(args[1] + "-deltamz.csv");
        test.reportDeltaMass_PeptideScore(inFile, outFile);
    }
}
