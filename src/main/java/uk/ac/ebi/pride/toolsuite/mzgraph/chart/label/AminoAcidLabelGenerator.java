package uk.ac.ebi.pride.toolsuite.mzgraph.chart.label;

import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.data.xy.XYDataset;
import uk.ac.ebi.pride.utilities.mol.AminoAcidSequence;
import uk.ac.ebi.pride.utilities.mol.MoleculeUtilities;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Generate possible peptides based on the mass differences.
 * 
 * User: rwang
 * Date: 25-Aug-2010
 * Time: 10:07:18
 */
public class AminoAcidLabelGenerator implements XYItemLabelGenerator {
    public static final int DEFAULT_MAX_NUMBER_OF_AMINO_ACIDS = 2;
    public static final int DEFAULT_MAX_NUMBER_OF_COMBINATIONS = 10;
    public static final double DEFAULT_MASS_ERROR = 0.5;
    public static final int DEFAULT_CHARGE_RANGE = 3;

    private double massError;
    private int maxNumberOfAminoAcids;
    private int maxNumberOfResults;
    private int chargeRange;

    public AminoAcidLabelGenerator() {
        this(DEFAULT_MASS_ERROR, DEFAULT_MAX_NUMBER_OF_AMINO_ACIDS,
                DEFAULT_MAX_NUMBER_OF_COMBINATIONS, DEFAULT_CHARGE_RANGE);
    }

    public AminoAcidLabelGenerator(double massError, int maxNumberOfAminoAcids,
                                           int maxNumberOfResults, int chargeRange) {
        this.massError = massError;
        this.maxNumberOfAminoAcids = maxNumberOfAminoAcids;
        this.maxNumberOfResults = maxNumberOfResults;
        this.chargeRange = chargeRange;
    }

    @Override
    public String generateLabel(XYDataset xyDataset, int series, int item) {
        String label = "";
        if (xyDataset.getItemCount(series) - 1 > item) {
            double x1 = xyDataset.getXValue(series, item);
            double x2 = xyDataset.getXValue(series, item + 1);
            double massDiff = x2 - x1;

            Map<Integer, List<AminoAcidSequence>> peptides = new LinkedHashMap<Integer, List<AminoAcidSequence>>();
            int cnt = 0;
            for (int charge = 1; charge <= chargeRange; charge++) {
                massDiff = massDiff * charge;
                for (int number = 0; number < maxNumberOfAminoAcids; number++) {
                    List<AminoAcidSequence> pes = MoleculeUtilities.searchForPeptide(massDiff - massError, massDiff + massError, true, number);
                    peptides.put(charge, pes);
                    cnt += pes.size();
                    if (cnt > maxNumberOfResults) {
                        break;
                    }
                }
            }

            if (cnt <= maxNumberOfResults) {
                for (Integer charge : peptides.keySet()) {
                    List<AminoAcidSequence> pes = peptides.get(charge);
                    for (AminoAcidSequence pe : pes) {
                        label += ("".equals(label) ? "" : ";") + pe.getOneLetterCodeString() + getChargeStr(charge);
                    }
                }
            }
        }
        return label;
    }

    public double getMassError() {
        return massError;
    }

    public void setMassError(double massError) {
        this.massError = massError;
    }

    public int getMaxNumberOfAminoAcids() {
        return maxNumberOfAminoAcids;
    }

    public void setMaxNumberOfAminoAcids(int maxNumberOfAminoAcids) {
        this.maxNumberOfAminoAcids = maxNumberOfAminoAcids;
    }

    public int getMaxNumberOfResults() {
        return maxNumberOfResults;
    }

    public void setMaxNumberOfResults(int maxNumberOfResults) {
        this.maxNumberOfResults = maxNumberOfResults;
    }

    private String getChargeStr(int charge) {
        String c = "";
        for (int i = 0; i < charge; i++) {
            c += "+";
        }
        return c;
    }
}
