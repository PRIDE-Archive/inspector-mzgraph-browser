package uk.ac.ebi.pride.toolsuite.mzgraph.chart.label;

import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.data.ComparableObjectItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYIntervalSeriesCollection;
import uk.ac.ebi.pride.utilities.mol.AminoAcid;
import uk.ac.ebi.pride.utilities.mol.AminoAcidSequence;
import uk.ac.ebi.pride.utilities.mol.PTModification;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation.AminoAcidAnnotation;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation.AminoAcidAnnotationInfo;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.util.ExtendedXYIntervalSeries;

import java.util.List;

/**
 * Modifications and Neutral losses are ignored for the moment.
 *
 * //todo: add modifications and neutral losses
 *
 * User: rwang
 * Date: 19-Jun-2010
 * Time: 13:06:13
 */
public class AminoAcidAnnotationLabelGenerator implements XYItemLabelGenerator {
    private final static String ANNOTATION_SEPARATOR = "; ";
    private final static String MODIFICATION_INDICATOR = "*";

    public String generateLabel(XYDataset xyDataset, int series, int item) {
        StringBuffer label = new StringBuffer();

        XYIntervalSeriesCollection seriesCollection = (XYIntervalSeriesCollection) xyDataset;
        ExtendedXYIntervalSeries seriesData = (ExtendedXYIntervalSeries) seriesCollection.getSeries(series);
        ComparableObjectItem itemData = seriesData.getDataItem(item);
        if (itemData instanceof AminoAcidAnnotation) {
            AminoAcidAnnotationInfo info = ((AminoAcidAnnotation) itemData).getRangeAnnotationInfo();
            int cnt = info.getNumberOfItems();
            for (int i = 0; i < cnt; i++) {
                AminoAcidAnnotationInfo.Item annotationItem = info.getItem(i);
                AminoAcidSequence peptide = annotationItem.getPeptide();
                int index = 0;
                for (AminoAcid aminoAcid : peptide.getAminoAcids()) {
                    label.append(aminoAcid.getOneLetterCode());
                    List<PTModification> mods = annotationItem.getModifications(index);
                    if (mods != null && !mods.isEmpty()) {
                        label.append(MODIFICATION_INDICATOR);
                    }
                    index++;
                }
                if (cnt > 1 && i < cnt - 1) {
                    label.append(ANNOTATION_SEPARATOR);
                }
            }
        }
        return label.toString();
    }
}
