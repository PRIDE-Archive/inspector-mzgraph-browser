package uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation;


import uk.ac.ebi.pride.utilities.iongen.ion.FragmentIonType;

import java.util.*;

/**
 * Utilities for sorting and filtering IonAnnotations.
 * <p/>
 * User: rwang
 * Date: 11-Aug-2010
 * Time: 13:48:34
 */
public class IonAnnotationUtils {

    /**
     * Remove ambiguous ions from a list of IonAnnotations.
     *
     * @param ions a list of IonAnnotations.
     */
    public static void removeAmbiguousIons(List<IonAnnotation> ions) {
        Iterator<IonAnnotation> iter = ions.iterator();
        while (iter.hasNext()) {
            IonAnnotation ion = iter.next();
            if (ion.getAnnotationInfo().getNumberOfItems() > 1) {
                iter.remove();
            }
        }
    }

    /**
     * Sort ion annotations by their type.
     * Note: the ions with more than one Items will be classified as AMBIGUOUS_ION.
     *
     * @param ions a list of ion annotations.
     * @return Map<FragmentIonType, List<IonAnnotation>>    map of ion annotations sorted by fragment ion type.
     */
    public static Map<FragmentIonType, List<IonAnnotation>> sortByType(List<IonAnnotation> ions) {
        Map<FragmentIonType, List<IonAnnotation>> ionMap = new LinkedHashMap<FragmentIonType, List<IonAnnotation>>();
        // sort ions by series
        for (IonAnnotation ion : ions) {
            IonAnnotationInfo ionInfo = ion.getAnnotationInfo();
            if (ionInfo != null) {
                FragmentIonType ionType = ionInfo.getNumberOfItems() > 1 ?
                        FragmentIonType.AMBIGUOUS_ION :
                        ionInfo.getItem(0).getType();
                List<IonAnnotation> sortedIons = ionMap.get(ionType);
                if (sortedIons == null) {
                    sortedIons = new ArrayList<IonAnnotation>();
                    ionMap.put(ionType, sortedIons);
                }
                sortedIons.add(ion);
            }
        }

        return ionMap;
    }

    /**
     * Filter by charge occurrence will sort the ions then only keep the ions which are the same charge
     * and with the highest numbers.
     *
     * @param ions a list of ion annotations.
     */
    public static void filterByChargeOccurrence(List<IonAnnotation> ions) {
        Map<Integer, List<IonAnnotation>> sortedIonMap = new HashMap<Integer, List<IonAnnotation>>();
        for (IonAnnotation ion : ions) {
            int charge = ion.getAnnotationInfo().getItem(0).getCharge();
            List<IonAnnotation> sortedIons = sortedIonMap.get(charge);
            if (sortedIons == null) {
                sortedIons = new ArrayList<IonAnnotation>();
                sortedIonMap.put(charge, sortedIons);
            }
            sortedIons.add(ion);
        }
        // get the charge with highest number of ions.
        int charge = 0;
        int ni = 0;
        for (Map.Entry<Integer, List<IonAnnotation>> sortedEntry : sortedIonMap.entrySet()) {
            int currCharge = sortedEntry.getKey();
            int numOfIons = sortedEntry.getValue().size();
            if (ni < numOfIons) {
                charge = currCharge;
                ni = numOfIons;
            }
        }

        ions.clear();
        ions.addAll(sortedIonMap.get(charge));
        sortedIonMap.clear();
    }

    /**
     * Remove duplicated ions (Same ion type and same location)
     *
     * @param ions ion annotations.
     */
    public static void removeDuplicateIons(List<IonAnnotation> ions) {
        List<IonAnnotation> tmpIons = new ArrayList<IonAnnotation>();
        for (int i = 0; i < ions.size(); i++) {
            IonAnnotation currIon = ions.get(i);
            IonAnnotationInfo.Item currIonItem = currIon.getAnnotationInfo().getItem(0);
            boolean valid = true;
            for (int j = 0; j < ions.size(); j++) {
                IonAnnotation targetIon = ions.get(j);
                IonAnnotationInfo.Item targetIonItem = targetIon.getAnnotationInfo().getItem(0);
                if (targetIonItem.getLocation() == currIonItem.getLocation() &&
                        currIon.getIntensity().doubleValue() < targetIon.getIntensity().doubleValue()) {
                    valid = false;
                    break;
                }
            }
            if (valid) {
                tmpIons.add(currIon);
            }
        }
        ions.clear();
        ions.addAll(tmpIons);
        tmpIons.clear();
    }

    /**
     * Sort ions by their location
     *
     * @param ions a list of ions.
     */
    public static void sortByLocation(List<IonAnnotation> ions) {
        Comparator<IonAnnotation> comparator = new IonLocationComparator();
        Collections.sort(ions, comparator);
    }

    /**
     * Check the fragment ion type, if there are more than one ion items assigned to one IonAnnotation,
     * it regards as an ambiguous ion.
     * @param ion   ion annotation.
     * @return FragmentIonType  ion type.
     */
    public static FragmentIonType getFragmentIonType(IonAnnotation ion) {
        IonAnnotationInfo info = ion.getAnnotationInfo();
        if (info.getNumberOfItems() > 1) {
            return FragmentIonType.AMBIGUOUS_ION;
        } else {
            return info.getItem(0).getType();
        }
    }

    private static class IonLocationComparator implements Comparator<IonAnnotation> {

        @Override
        public int compare(IonAnnotation o1, IonAnnotation o2) {
            int location1 = o1.getAnnotationInfo().getItem(0).getLocation();
            int location2 = o2.getAnnotationInfo().getItem(0).getLocation();
            if (location1 > location2) {
                return 1;
            } else if (location1 < location2) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}
