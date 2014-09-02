package uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation;

import org.jfree.data.xy.XYIntervalDataItem;

/**
 * AminoAcidAnnotation annotates a range with amino acids. 
 *
 * User: rwang
 * Date: 14-Jun-2010
 * Time: 17:58:41
 */
public class AminoAcidAnnotation extends XYIntervalDataItem implements RangeAnnotation {

    private final AminoAcidAnnotationInfo info;

    public AminoAcidAnnotation(Number xLow, Number yLow,
                               Number xHigh, Number yHigh,
                               AminoAcidAnnotationInfo info) {
        this(xLow.doubleValue(), yLow.doubleValue(), xHigh.doubleValue(), yHigh.doubleValue(), info);
    }

    public AminoAcidAnnotation(double xLow, double yLow,
                               double xHigh, double yHigh,
                               AminoAcidAnnotationInfo info) {
        super(0, xLow, xHigh, 0, yLow, yHigh);
        this.info = info;
    }

    @Override
    public Number getStartMz() {
        return this.getXLowValue();
    }

    @Override
    public Number getStopMz() {
        return this.getXHighValue();
    }

    @Override
    public Number getStartIntensity() {
        return this.getYLowValue();
    }

    @Override
    public Number getStopIntensity() {
        return this.getYHighValue();
    }

    @Override
    public AminoAcidAnnotationInfo getRangeAnnotationInfo() {
        return info;
    }
}
