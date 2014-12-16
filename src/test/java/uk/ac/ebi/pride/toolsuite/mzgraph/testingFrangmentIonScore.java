package uk.ac.ebi.pride.toolsuite.mzgraph;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation.IonAnnotation;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation.IonAnnotationInfo;
import uk.ac.ebi.pride.toolsuite.mzgraph.gui.data.ExperimentalFragmentedIonsTableModel;
import uk.ac.ebi.pride.toolsuite.mzgraph.gui.data.ExperimentalParams;
import uk.ac.ebi.pride.toolsuite.mzgraph.psm.PSMTestUtils;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.impl.ControllerImpl.MzIdentMLControllerImpl;
import uk.ac.ebi.pride.utilities.data.core.BinaryDataArray;
import uk.ac.ebi.pride.utilities.data.core.CvParam;
import uk.ac.ebi.pride.utilities.data.core.FragmentIon;
import uk.ac.ebi.pride.utilities.data.core.Modification;
import uk.ac.ebi.pride.utilities.data.core.Peptide;
import uk.ac.ebi.pride.utilities.data.core.PeptideSequence;
import uk.ac.ebi.pride.utilities.data.core.Spectrum;
import uk.ac.ebi.pride.utilities.iongen.impl.DefaultPrecursorIon;
import uk.ac.ebi.pride.utilities.iongen.ion.FragmentIonType;
import uk.ac.ebi.pride.utilities.iongen.ion.FragmentIonUtilities;
import uk.ac.ebi.pride.utilities.iongen.model.PeakSet;
import uk.ac.ebi.pride.utilities.iongen.model.PeptideScore;
import uk.ac.ebi.pride.utilities.iongen.model.PrecursorIon;
import uk.ac.ebi.pride.utilities.iongen.model.ProductIonSet;
import uk.ac.ebi.pride.utilities.mol.*;



import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by yperez on 28/11/2014.
 */
public class testingFrangmentIonScore {

    private MzIdentMLControllerImpl  dataAccessController = null;

    private ExperimentalParams params = ExperimentalParams.getInstance();

    public static final double MZ_OUTLIER = 4;


    private double[] weightList = {0.25, 0.25, 0.5, 0.5, 0.75, 1, 0.75, 0.5, 0.5, 0.25};

    @Before
    public void setUp() throws Exception {
        URL url = testingFrangmentIonScore.class.getClassLoader().getResource("fragmentIonScore/HepG2HGFsolB02T01c.mzid");
        URL urlMfg = testingFrangmentIonScore.class.getClassLoader().getResource("fragmentIonScore/");

        if (url == null) {
            throw new IllegalStateException("no file for input found!");
        }
        dataAccessController = new MzIdentMLControllerImpl(new File(url.toURI()),true);

        File inputFile = new File(urlMfg.toURI());
        if(inputFile != null && inputFile.listFiles().length != 0){
            dataAccessController.addMSController(Arrays.asList(inputFile.listFiles()));
        }

    }

    @After
    public void tearDown() throws Exception {
        dataAccessController.close();
    }

    @Test
    public void scanMetadata() throws IOException {
//        long start = System.currentTimeMillis();
//        scanForGeneralMetadata(mzIdentMLController);
//        scanForSoftware(mzIdentMLController);
//        scanForSearchDetails(mzIdentMLController);
//        scanEntryByEntry(mzIdentMLController);
//        scanMzIdentMLSpecificDetails(mzIdentMLController);
//        System.out.println("Final time in miliseconds: " + (System.currentTimeMillis() - start));
    }




    private void scanEntryByEntry(DataAccessController dataAccessController) {

        Set<CvParam> ptms = new HashSet<CvParam>();
        Set<String> peptideSequences = new HashSet<String>();
        Set<Comparable> spectrumIds = new HashSet<Comparable>();
        double errorPSMCount = 0.0;
        double totalPSMCount = 0.0;
        long count = 0;

        Collection<Comparable> proteinIds = dataAccessController.getProteinIds();
        for (Comparable proteinId : proteinIds) {
            count ++;
            Collection<Comparable> peptideIds = dataAccessController.getPeptideIds(proteinId);
            for (Comparable peptideId : peptideIds) {
                totalPSMCount++;

                // peptide
                Peptide peptide = dataAccessController.getPeptideByIndex(proteinId, peptideId);
                PeptideSequence peptideSequence = peptide.getPeptideSequence();
                peptideSequences.add(peptideSequence.getSequence());

                // ptm
                List<Modification> modifications = new ArrayList<Modification>(dataAccessController.getPTMs(proteinId, peptideId));
                List<Double> ptmMasses = new ArrayList<Double>();

                // precursor charge
                Integer charge = dataAccessController.getPeptidePrecursorCharge(proteinId, peptideId);
                double mz = dataAccessController.getPeptidePrecursorMz(proteinId, peptideId);
                if ((charge == null || mz == -1)) {
                    Comparable specId = dataAccessController.getPeptideSpectrumId(proteinId, peptideId);
                    if(specId != null){
                        charge = dataAccessController.getSpectrumPrecursorCharge(specId);
                        mz = dataAccessController.getSpectrumPrecursorMz(specId);
                        if (charge == null || charge == 0) {
                            charge = null;
                        }
                    }
                }

                // delta mass
                if (charge == null) {
                    errorPSMCount++;
                } else {
                    Double deltaMass = MoleculeUtilities.calculateDeltaMz(peptideSequence.getSequence(), mz, charge, ptmMasses);
                    if (!isDeltaMzInRange(deltaMass)) {
                        errorPSMCount++;
                    }
                }

                // spectrum
                if (peptide.getSpectrumIdentification() != null && peptide.getSpectrumIdentification().getSpectrum() != null) {
                    Spectrum spectrum = peptide.getSpectrumIdentification().getSpectrum();
                    spectrumIds.add(spectrum.getId());
                }
                double mass = checkPeptideQualityFragmentation(proteinId, peptide);
            }

            if (count % 500 == 0) {
                System.out.println("Scanned " + count+ " entries of proteins from file : " + dataAccessController.getName());
            }
        }

        System.out.println("Peptide Sequences: " + peptideSequences.size());
        System.out.println("Number of Spectrums: " + spectrumIds.size());
        System.out.println("PTMs: " + ptms.toString());
        System.out.println("Delta Error Rate: " + (errorPSMCount / totalPSMCount));
    }

    private double checkPeptideQualityFragmentation(Comparable proteinId, Peptide peptide) {
        double deltaMass = 0.0;

        BinaryDataArray mzBinary = (peptide == null || peptide.getSpectrum() == null) ? null : peptide.getSpectrum().getMzBinaryDataArray();
        BinaryDataArray intentBinary = (peptide == null || peptide.getSpectrum() == null) ? null : peptide.getSpectrum().getIntensityBinaryDataArray();

        if (mzBinary != null && intentBinary != null && !mzBinary.isEmpty() && !intentBinary.isEmpty()) {
            int charge = getCharge(peptide);

            uk.ac.ebi.pride.utilities.mol.Peptide newPeptide = translate(peptide);
            // fragmentation table only show charge<=2 fragmentation ions.
            charge = charge > 2 ? 2 : charge;
            PrecursorIon precursorIon = new DefaultPrecursorIon(newPeptide, charge);
            ExperimentalFragmentedIonsTableModel model = new ExperimentalFragmentedIonsTableModel(precursorIon, ProductIonPair.B_Y);
            model.setShowAuto(true);
            model.setCalculate(true);
            model.setPeaks(mzBinary.getDoubleArray(), intentBinary.getDoubleArray());
            model.setShowWaterLoss(true);


        }
            return deltaMass;
    }

    private int getCharge(Peptide peptide) {
        int chartState = peptide.getSpectrumIdentification().getChargeState();
        return chartState;
    }

    public uk.ac.ebi.pride.utilities.mol.Peptide translate(Peptide peptide) {
        uk.ac.ebi.pride.utilities.mol.Peptide newPeptide = new uk.ac.ebi.pride.utilities.mol.Peptide(peptide.getSequence());
        PTModification newModification;

        String name;
        String type = null;
        String label;
        List<Double> monoMassDeltas;
        List<Double> avgMassDeltas;
        int position;
        for (uk.ac.ebi.pride.utilities.data.core.Modification oldModification : peptide.getModifications()) {
            name = oldModification.getName();
            label = null;
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

    protected boolean isDeltaMzInRange(Double deltaMz) {
        return deltaMz != null && (deltaMz >= -MZ_OUTLIER) && (deltaMz <= MZ_OUTLIER);
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

    public static IonAnnotation getOverlapIonAnnotation(FragmentIon ion, List<IonAnnotation> ionAnnotations) {
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

    /**
     * Convert fragment ion.
     *
     * @param ion fragment ion.
     * @return IonAnnotation    ion annotation.
     */
    public static IonAnnotation getIonAnnotation(FragmentIon ion) {
        // get the fragment ion type
        FragmentIonType ionType = getIonType(ion);

        // get the fragment loss
        NeutralLoss fragLoss = FragmentIonUtilities.getFragmentIonNeutralLoss(ion.getIonType());
        IonAnnotationInfo ionInfo = new IonAnnotationInfo();
        ionInfo.addItem(ion.getCharge(), ionType, ion.getLocation(), fragLoss);
        return new IonAnnotation(ion.getMz(), ion.getIntensity(), ionInfo);
    }

    public static FragmentIonType getIonType(FragmentIon ion) {
        return FragmentIonUtilities.getFragmentIonType(ion.getIonType());
    }

    public static Map<Integer, List<PTModification>> createModificationMap(List<Modification> mods, int peptideLength) {
        Map<Integer, List<PTModification>> modMap
                = new HashMap<Integer, List<PTModification>>();
        for (uk.ac.ebi.pride.utilities.data.core.Modification mod : mods) {
            int location = mod.getLocation();
            // merge the N-terminus modification to the first amino acid
            location = location == 0 ? 1 : location;
            // merge the C-terminus modification to the last amino acid
            location = location == peptideLength ? location - 1 : location;

            List<PTModification> subMods = modMap.get(location);
            if (subMods == null) {
                subMods = new ArrayList<PTModification>();
                modMap.put(mod.getLocation(), subMods);
            }
            subMods.add(new PTModification(mod.getName(), mod.getModDatabase(),
                    mod.getName(), mod.getMonoisotopicMassDelta(), mod.getAvgMassDelta()));
        }
        return modMap;
    }

    public static List<PTModification> convertModifications(List<Modification> modifications) {
        List<PTModification> newMods = new ArrayList<PTModification>();
        for (Modification mod : modifications) {
            newMods.add(new PTModification(mod.getName(), mod.getModDatabase(),
                    mod.getName(), mod.getMonoisotopicMassDelta(), mod.getAvgMassDelta()));
        }
        return newMods;
    }



    private double getScore(uk.ac.ebi.pride.utilities.data.core.Peptide peptide, uk.ac.ebi.pride.utilities.data.core.Spectrum spectrum) {
        uk.ac.ebi.pride.utilities.mol.Peptide newPeptide = PSMTestUtils.toPeptide(peptide);
        int charge = 0;
        try {
            List<uk.ac.ebi.pride.utilities.data.core.CvParam> params = spectrum.getPrecursors().get(0).getSelectedIons().get(0).getCvParams();
            for (uk.ac.ebi.pride.utilities.data.core.CvParam param : params) {
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

    private Double getDeltaMass(uk.ac.ebi.pride.utilities.data.core.Peptide peptide) {
        String sequence = peptide.getSequence();
        int charge = peptide.getSpectrumIdentification().getChargeState();
        double mz = peptide.getPrecursorMz();

        List<Modification> mods = peptide.getModifications();

        java.util.List<Double> ptmMasses = new ArrayList<Double>();
        for (Modification mod : mods) {
            java.util.List<Double> monoMasses = mod.getMonoisotopicMassDelta();
            if (monoMasses != null && !monoMasses.isEmpty()) {
                ptmMasses.add(monoMasses.get(0));
            }
        }

        return MoleculeUtilities.calculateDeltaMz(sequence, mz, charge, ptmMasses);
    }

    @Test
    public void reportDeltaMass_PeptideScore() throws Exception {
        String ret = "\r\n";

        long start = new Date().getTime();

        BufferedWriter writer = new BufferedWriter(new FileWriter(new File("test.csv")));
        Set<CvParam> ptms = new HashSet<CvParam>();
        Set<String> peptideSequences = new HashSet<String>();
        Set<Comparable> spectrumIds = new HashSet<Comparable>();
        double errorPSMCount = 0.0;
        double totalPSMCount = 0.0;
        long count = 0;

        Collection<Comparable> proteinIds = dataAccessController.getProteinIds();
        for (Comparable proteinId : proteinIds) {
            count ++;
            Collection<Comparable> peptideIds = dataAccessController.getPeptideIds(proteinId);
            for (Comparable peptideId : peptideIds) {
                totalPSMCount++;

                // peptide
                Peptide peptide = dataAccessController.getPeptideByIndex(proteinId, peptideId);
                PeptideSequence peptideSequence = peptide.getPeptideSequence();
                peptideSequences.add(peptideSequence.getSequence());

                // ptm
                List<Modification> modifications = new ArrayList<Modification>(dataAccessController.getPTMs(proteinId, peptideId));
                List<Double> ptmMasses = new ArrayList<Double>();

                // precursor charge
                Integer charge = dataAccessController.getPeptidePrecursorCharge(proteinId, peptideId);
                double mz = dataAccessController.getPeptidePrecursorMz(proteinId, peptideId);
                if ((charge == null || mz == -1)) {
                    Comparable specId = dataAccessController.getPeptideSpectrumId(proteinId, peptideId);
                    if(specId != null){
                        charge = dataAccessController.getSpectrumPrecursorCharge(specId);
                        mz = dataAccessController.getSpectrumPrecursorMz(specId);
                        if (charge == null || charge == 0) {
                            charge = null;
                        }
                    }
                }
                Double deltaMass = 0.0;
                // delta mass
                if (charge == null) {
                    errorPSMCount++;
                } else {
                    deltaMass = MoleculeUtilities.calculateDeltaMz(peptideSequence.getSequence(), mz, charge, ptmMasses);
                    if (!isDeltaMzInRange(deltaMass)) {
                        errorPSMCount++;
                    }
                }

                // spectrum
                if (peptide.getSpectrumIdentification() != null && peptide.getSpectrumIdentification().getSpectrum() != null) {
                    Spectrum spectrum = peptide.getSpectrumIdentification().getSpectrum();
                    spectrumIds.add(spectrum.getId());
                    mz = dataAccessController.getSpectrumPrecursorMz(spectrum.getId());
                    mz = (mz == -1)? dataAccessController.getPeptidePrecursorMz(peptideId,peptideId):mz;
                    deltaMass = MoleculeUtilities.calculateDeltaMz(peptide.getSequence(), mz, charge, ptmMasses);
                    DecimalFormat df = new DecimalFormat("#");
                    df.setMaximumFractionDigits(7);

                    writer.write(peptide.getSequence() + "\t" + df.format(Math.abs(deltaMass)) + "\t" + getScore(peptide, spectrum) + "\t" + peptide.getSpectrumIdentification().getRank() + "\t" + peptide.getPeptideEvidence().isDecoy());
                    for(Number score: peptide.getScore().getAllScoreValues())
                        if(score != null)
                            writer.write("\t" + score.toString());
                    writer.write(ret);
                }


            }

            if (count % 500 == 0) {
                System.out.println("Scanned " + count+ " entries of proteins from file : " + dataAccessController.getName());
            }
        }

        long end = new Date().getTime();
        long elapse = end - start;

        writer.write("total work: " + elapse + "(ms)" + ret);
        writer.write("total peptide count:" + count);

        writer.close();
        dataAccessController.close();
    }


}
