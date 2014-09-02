package uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation;


import uk.ac.ebi.pride.utilities.mol.AminoAcid;
import uk.ac.ebi.pride.utilities.mol.AminoAcidSequence;
import uk.ac.ebi.pride.utilities.mol.NeutralLoss;
import uk.ac.ebi.pride.utilities.mol.PTModification;

import java.util.*;

/**
 * AminoAcidAnnotationInfo contains a list of all possible peptides along
 * with their modifications and neutral losses.
 *
 * User: rwang
 * Date: 15-Jun-2010
 * Time: 09:18:45
 */
public class AminoAcidAnnotationInfo implements RangeAnnotationInfo {

    private final List<Item> items;

    public AminoAcidAnnotationInfo() {
        items = new ArrayList<Item>();
    }

    public void addItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Can't add null item to IonAnnotationInfo");
        } else {
            items.add(item);
        }
    }

    public Item addItem(AminoAcidSequence peptide) {
        Item item = null;
        // peptide can not null
        if (peptide != null) {
            item = new Item(peptide);
            items.add(item);
        }
        return item;
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

    public int getNumberOfItems() {
        return items.size();
    }

    public Item getItem(int index) {
        Item item = null;
        if (index >= 0 && index < items.size()) {
            item = items.get(index);
        }
        return item;
    }

    public Iterator<Item> iterator() {
        return items.iterator();
    }

    // todo add remove methods to this class.
    public class Item {
        private AminoAcidSequence peptide;
        private Map<Integer, List<PTModification>> modifications;
        private Map<Integer, List<NeutralLoss>> neutralLosses;

        public Item(AminoAcidSequence peptide) {
            this.peptide = peptide;
            this.modifications = new HashMap<Integer, List<PTModification>>();
            this.neutralLosses = new HashMap<Integer, List<NeutralLoss>>();
        }

        public AminoAcidSequence getPeptide() {
            return peptide;
        }

        public int getPeptideLength() {
            return peptide.getLength();
        }

        public AminoAcid getAminoAcid(int index) {
            return peptide.getAminoAcid(index);
        }

        /**
         * add a set of modifications.
         * Note: this uses addModification internally
         *
         * @param mods  a map of modifications.
         */
        public void addModifications(Map<Integer, List<PTModification>> mods) {
            for (Integer index: mods.keySet()) {
                List<PTModification> ms = mods.get(index);
                for (PTModification m : ms) {
                    addModification(index, m);
                }
            }
        }

        /**
         * Add a modification to a specific amino acid.
         * index is zero based, 0 often means N-terminus modification.
         *
         * @param index modification index.
         * @param mod   modification.
         */
        public void addModification(int index, PTModification mod) {
            if (index >= 0 && index <= peptide.getLength()) {
                List<PTModification> existingMods = modifications.get(index);
                if (existingMods == null) {
                    existingMods = new ArrayList<PTModification>();
                    modifications.put(index, existingMods);
                }
                existingMods.add(mod);
            }
        }

        public List<PTModification> getModifications(int index) {
            return modifications.get(index);
        }


        /**
         * Add a neutral loss to a specific amino acid.
         * index is zero based, 0 often means N-terminus neutral loss.
         *
         * @param index neutral loss index.
         * @param loss  neutral loss.
         */
        public void addNeutralLoss(int index, NeutralLoss loss) {
            if (index >= 0 && index <= peptide.getLength()) {
                List<NeutralLoss> existingLosses = neutralLosses.get(index);
                if (existingLosses == null) {
                    existingLosses = new ArrayList<NeutralLoss>();
                    neutralLosses.put(index, existingLosses);
                }
                existingLosses.add(loss);
            }
        }

        public List<NeutralLoss> getNeutralLosses(int index){
            return neutralLosses.get(index);
        }
    }
}
