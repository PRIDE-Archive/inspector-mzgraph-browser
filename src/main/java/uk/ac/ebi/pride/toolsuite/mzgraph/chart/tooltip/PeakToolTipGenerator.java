package uk.ac.ebi.pride.toolsuite.mzgraph.chart.tooltip;

import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import uk.ac.ebi.pride.utilities.mol.NeutralLoss;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation.IonAnnotation;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation.IonAnnotationInfo;

import java.text.NumberFormat;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 21-Jun-2010
 * Time: 11:50:20
 */
public class PeakToolTipGenerator implements XYToolTipGenerator {
    public String generateToolTip(XYDataset xyDataset, int series, int item) {
        StringBuffer label = new StringBuffer();
        label.append("<html>");
        XYSeriesCollection seriesCollection = (XYSeriesCollection)xyDataset;
        XYSeries seriesData = seriesCollection.getSeries(series);
        XYDataItem itemData = seriesData.getDataItem(item);
        if (itemData instanceof IonAnnotation) {
            IonAnnotationInfo info =((IonAnnotation) itemData).getAnnotationInfo();
            int size = info.getNumberOfItems();
            for (int i = 0; i < size; i++) {
                IonAnnotationInfo.Item annotationItem = info.getItem(i);
                String ion = annotationItem.getType().getLabel() + annotationItem.getLocation();
                NeutralLoss fragLoss = annotationItem.getNeutralLoss();
                if (fragLoss != null) {
                    ion += "-" + fragLoss.getName();
                }
                label.append("<b><i><font size=\"3\">" + ion + "</font></i></b><br>");
                String charge = annotationItem.getCharge() + "";
                label.append("<b>charge</b>: " + charge + "<br>");

            }
        }
        label.append("<b>m/z</b>: " + NumberFormat.getInstance().format(itemData.getX()) + "<br>");
        label.append("<b>Intensity</b>: " + NumberFormat.getInstance().format(itemData.getY()) + "<br>");
        label.append("</html>");
        return label.toString();
    }
}
