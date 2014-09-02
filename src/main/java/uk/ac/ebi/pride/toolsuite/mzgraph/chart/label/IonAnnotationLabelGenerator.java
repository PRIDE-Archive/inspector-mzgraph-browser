package uk.ac.ebi.pride.toolsuite.mzgraph.chart.label;

import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import uk.ac.ebi.pride.utilities.mol.NeutralLoss;
import uk.ac.ebi.pride.utilities.iongen.ion.FragmentIonType;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation.IonAnnotation;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation.IonAnnotationInfo;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 15-Jun-2010
 * Time: 13:41:58
 */
public class IonAnnotationLabelGenerator implements XYItemLabelGenerator {
    public String generateLabel(XYDataset xyDataset, int series, int item) {
        String label = "";

        XYSeriesCollection seriesCollection = (XYSeriesCollection) xyDataset;
        XYSeries seriesData = seriesCollection.getSeries(series);
        XYDataItem itemData = seriesData.getDataItem(item);
        if (itemData instanceof IonAnnotation) {
            IonAnnotationInfo info = ((IonAnnotation) itemData).getAnnotationInfo();
            if (info != null) {
                int size = info.getNumberOfItems();
                for (int i = 0; i < size; i++) {
                    IonAnnotationInfo.Item annotationItem = info.getItem(i);
                    FragmentIonType type = annotationItem.getType();
                    String labelPart = type.getLabel();
                    int location = annotationItem.getLocation();
                    if (location >= 0 &&!FragmentIonType.IMMONIUM_ION.equals(type) && !FragmentIonType.NON_IDENTIFIED_ION.equals(type)) {
                        labelPart += location;
                    }

                    int charge = annotationItem.getCharge();
                    String sign = charge >= 0 ? "+" : "-";
                    for (int j = 0; j < Math.abs(charge); j++) {
                        labelPart += sign;
                    }

                    NeutralLoss loss = annotationItem.getNeutralLoss();
                    if (loss != null) {
                        labelPart += "[" + loss.getName() + "]";
                    }
                    label += ("".equals(label) ? "" : ", ") + labelPart;
                }
            }
        }
        return label;
    }
}
