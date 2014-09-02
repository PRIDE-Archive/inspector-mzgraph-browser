package uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation;

import uk.ac.ebi.pride.utilities.mol.*;
import uk.ac.ebi.pride.utilities.iongen.ion.FragmentIonUtilities;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * AminoAcidAnnotationGenerator generates amino acid annotations.
 * <p/>
 * User: rwang
 * Date: 11-Aug-2010
 * Time: 08:57:54
 */
public class AminoAcidAnnotationGenerator
        implements RangeAnnotationGenerator<List<AminoAcidAnnotation>, List<IonAnnotation>> {

    public static final int DEFAULT_MAX_NUMBER_OF_AMINO_ACIDS = 3;
    public static final int DEFAULT_MAX_NUMBER_OF_COMBINATIONS = 6;
    public static final double DEFAULT_MASS_ERROR = 0.5;

    /**
     * Controls the maximum number of amino acids can be assigned to a range
     */
    private int maxNumberOfAminoAcid;
    /**
     * The cut-off threshold for generating possible number of amino acid combinations
     */
    private int combinationThreshold;
    /**
     * Mass error threshold
     */
    private double massError;
    /**
     * Length of the peptide
     */
    private int peptideLength;
    /**
     * Modifications
     */
    private Map<Integer, List<PTModification>> modifications;

    public AminoAcidAnnotationGenerator() {
        this(DEFAULT_MAX_NUMBER_OF_AMINO_ACIDS, DEFAULT_MAX_NUMBER_OF_COMBINATIONS, DEFAULT_MASS_ERROR, 0, null);
    }

    public AminoAcidAnnotationGenerator(int maxNumberOfAminoAcid, int combinationThreshold,
                                        double massError, int peptideLength,
                                        Map<Integer, List<PTModification>> modifications) {
        this.maxNumberOfAminoAcid = maxNumberOfAminoAcid;
        this.combinationThreshold = combinationThreshold;
        this.massError = massError;
        this.peptideLength = peptideLength;
        this.modifications = modifications;
    }

    @Override
    public List<AminoAcidAnnotation> generate(List<IonAnnotation> ions) {
        List<IonAnnotation> newIons = new ArrayList<IonAnnotation>();
        newIons.addAll(ions);

        // check whether the ion type is immonium ions
        boolean isImmonium = false;
        IonAnnotation ion = newIons.get(0);
        IonAnnotationInfo.Item item = ion.getAnnotationInfo().getItem(0);
        if (item != null) {
            isImmonium = FragmentIonUtilities.isImmoniumFragmentation(item.getType());
        }

        // filter by charge frequency
        IonAnnotationUtils.filterByChargeOccurrence(newIons);
        // remove duplicated ions
        if (!isImmonium) {
            IonAnnotationUtils.removeDuplicateIons(newIons);
        }
        // sort by ion location
        IonAnnotationUtils.sortByLocation(newIons);

        List<AminoAcidAnnotation> residueAnnotations = null;
        if (ions != null && !ions.isEmpty()) {
            if (isImmonium) {
                residueAnnotations = generateImmoniumAnnotation(newIons);
            } else {
                residueAnnotations = generateBackboneAnnotation(newIons);
            }
        }
        return residueAnnotations;
    }

    private List<AminoAcidAnnotation> generateImmoniumAnnotation(List<IonAnnotation> ions) {
        List<AminoAcidAnnotation> anns = new ArrayList<AminoAcidAnnotation>();

        for (IonAnnotation ion : ions) {
            IonAnnotationInfo annotationInfo = ion.getAnnotationInfo();
            // the number annotation item is more then 1, then it is a ambiguous ion, which should
            // not be annotated.
            IonAnnotationInfo.Item item = annotationInfo.getItem(0);
            // get m/z and intensity
            double mz = ion.getMz().doubleValue();
            double intensity = ion.getIntensity().doubleValue();
            // calculate the mass
            double mass = Math.abs(mz * item.getCharge());
            mass += MoleculeUtilities.calculateMonoMass(Atom.O_16, Atom.C_12) - NuclearParticle.PROTON.getMonoMass();
            // search for peptide
            List<AminoAcidSequence> peptides = MoleculeUtilities.searchForPeptide(mass - massError, mass + massError, true, 1);
            if (!peptides.isEmpty()) {
                AminoAcidAnnotationInfo info = new AminoAcidAnnotationInfo();
                for (AminoAcidSequence peptide : peptides) {
                    info.addItem(peptide);
                }
                anns.add(new AminoAcidAnnotation(mz, intensity, mz, intensity, info));
            }
        }
        return anns;
    }

    private List<AminoAcidAnnotation> generateBackboneAnnotation(List<IonAnnotation> ions) {
        List<AminoAcidAnnotation> anns = new ArrayList<AminoAcidAnnotation>();

        for (int i = 0; i < ions.size() - 1; i++) {
            IonAnnotation ion1 = ions.get(i);
            IonAnnotation ion2 = ions.get(i + 1);
            IonAnnotationInfo ionInfo1 = ion1.getAnnotationInfo();
            IonAnnotationInfo ionInfo2 = ion2.getAnnotationInfo();
            if (ionInfo1.getNumberOfItems() == 1 && ionInfo2.getNumberOfItems() == 1) {
                IonAnnotationInfo.Item ionItem1 = ionInfo1.getItem(0);
                IonAnnotationInfo.Item ionItem2 = ionInfo2.getItem(0);
                // get masses and apply the charges
                double ionMass1 = Math.abs(ion1.getMz().doubleValue() * ionItem1.getCharge());
                double ionMass2 = Math.abs(ion2.getMz().doubleValue() * ionItem2.getCharge());
                // add all the fragment losses: H2O, NH3
                NeutralLoss ionLoss1 = ionItem1.getNeutralLoss();
                if (ionLoss1 != null) {
                    ionMass1 += ionLoss1.getMonoMass();
                }
                NeutralLoss ionLoss2 = ionItem2.getNeutralLoss();
                if (ionLoss2 != null) {
                    ionMass2 += ionLoss2.getMonoMass();
                }
                // calculate different combination of modification mass differences.
                // massDiff is used to store all possible masses
                List<Double> massDiff = new ArrayList<Double>();
                // current mass difference between the two ions
                // put the initial mass difference between the two ions into the massDiff list
                double initialIonMassDiff = ionMass2 - ionMass1;
                massDiff.add(initialIonMassDiff);
                // get number of residues between these two ions
                int ionLocation1 = ionItem1.getLocation();
                int ionLocation2 = ionItem2.getLocation();
                int numOfResidues = ionLocation2 - ionLocation1;
                boolean isNTerminal = FragmentIonUtilities.isNTerminalFragmentation(ionItem1.getType());
                if (numOfResidues <= maxNumberOfAminoAcid) {
                    // add modification mass differences, there are possible many difference combination of
                    // modification mass differences, this is why List is used.
                    List<Double> modMassDiff = getModMassDiff(ionLocation1 + 1, ionLocation2, isNTerminal);
                    if (!modMassDiff.isEmpty()) {
                        massDiff.clear();
                        for (Double mmd : modMassDiff) {
                            massDiff.add(Math.abs(initialIonMassDiff - mmd));
                        }
                    }
                    // search for all amino acids which are qualified for both the mass range and the number of residues.
                    List<AminoAcidSequence> peptides = new ArrayList<AminoAcidSequence>();
                    for (Double md : massDiff) {
                        List<AminoAcidSequence> peptideResults = MoleculeUtilities.searchForPeptide(md - massError, md + massError, true, numOfResidues);
                        if (peptideResults.size() <= combinationThreshold) {
                            peptides.addAll(peptideResults);
                        }
                    }

                    // construct amino acid annotation
                    if (!peptides.isEmpty()) {
                        AminoAcidAnnotationInfo info = new AminoAcidAnnotationInfo();
                        // todo: add modification and neutral loss information here.
                        for (AminoAcidSequence peptide : peptides) {
                            AminoAcidAnnotationInfo.Item item = info.addItem(peptide);
                            item.addModifications(getModifications(ionLocation1 + 1, ionLocation2, isNTerminal));
                        }
                        anns.add(new AminoAcidAnnotation(ion1.getMz(), ion1.getIntensity(), ion2.getMz(), ion2.getIntensity(), info));
                    }
                }
            }
        }

        return anns;
    }

    /**
     * calculate all possible modification mass differences between a starting location and a stop location.
     *
     * @param startLocation start residue position (1 based).
     * @param stopLocation  stop residue position (1 based).
     * @param isNTerminal   the direction of the peptide.
     * @return List<Double> a list of all possible modifications
     */
    private List<Double> getModMassDiff(int startLocation, int stopLocation, boolean isNTerminal) {
        List<Double> results = new ArrayList<Double>();
        if (modifications != null && !modifications.isEmpty()) {
            for (int i = startLocation; i <= stopLocation; i++) {
                List<PTModification> mods = isNTerminal ? modifications.get(i) : modifications.get(peptideLength - i + 1);
                if (mods != null && !mods.isEmpty()) {
                    List<Double> residueMassDiff = getResidueModMassDiff(mods);
                    if (results.isEmpty()) {
                        results.addAll(residueMassDiff);
                    } else {
                        List<Double> tmp = new ArrayList<Double>();
                        for (Double mass : results) {
                            for (Double rmd : residueMassDiff) {
                                tmp.add(mass + rmd);
                            }
                        }
                        results = tmp;
                    }
                }
            }
        }
        return results;
    }

    /**
     * return a map of modifications according to the locations of the peptide.
     *
     * @param startLocation start location of the peptide
     * @param stopLocation  stop location of the peptide
     * @param isNTerminal   the direction of the peptide.
     * @return Map<Integer, List<Modification>> modification map.
     */
    private Map<Integer, List<PTModification>> getModifications(int startLocation, int stopLocation, boolean isNTerminal) {
        Map<Integer, List<PTModification>> results = new LinkedHashMap<Integer, List<PTModification>>();
        if (modifications != null && !modifications.isEmpty()) {
            int index = 0;
            for (int i = startLocation; i <= stopLocation; i++) {
                List<PTModification> mods = isNTerminal ? modifications.get(i) : modifications.get(peptideLength - i + 1);
                if (mods != null && !mods.isEmpty()) {
                    results.put(index, mods);
                }
                index++;
            }
        }
        return results;
    }

    /**
     * calculate all possible modification mass differences for a residue.
     *
     * @param mods
     * @return
     */
    private static List<Double> getResidueModMassDiff(List<PTModification> mods) {
        List<Double> results = new ArrayList<Double>();
        if (mods != null && !mods.isEmpty()) {
            for (PTModification mod : mods) {
                List<Double> masses = mod.getMonoMassDeltas();
                if (results.isEmpty()) {
                    results.addAll(masses);
                } else {
                    List<Double> tmp = new ArrayList<Double>();
                    for (Double result : results) {
                        for (Double mass : masses) {
                            tmp.add(result + mass);
                        }
                    }
                    results = tmp;
                }
            }
        }
        return results;
    }

    public int getMaxNumberOfAminoAcid() {
        return maxNumberOfAminoAcid;
    }

    public void setMaxNumberOfAminoAcid(int maxNumberOfAminoAcid) {
        this.maxNumberOfAminoAcid = maxNumberOfAminoAcid;
    }

    public int getCombinationThreshold() {
        return combinationThreshold;
    }

    public void setCombinationThreshold(int combinationThreshold) {
        this.combinationThreshold = combinationThreshold;
    }

    public double getMassError() {
        return massError;
    }

    public void setMassError(double massError) {
        this.massError = massError;
    }

    public int getPeptideLength() {
        return peptideLength;
    }

    public void setPeptideLength(int peptideLength) {
        this.peptideLength = peptideLength;
    }

    public Map<Integer, List<PTModification>> getModifications() {
        return modifications;
    }

    public void setModifications(Map<Integer, List<PTModification>> modifications) {
        this.modifications = modifications;
    }
}
