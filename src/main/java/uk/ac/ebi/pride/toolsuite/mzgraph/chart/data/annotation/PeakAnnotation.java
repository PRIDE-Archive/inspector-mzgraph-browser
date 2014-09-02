package uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation;

/**
 * PeakAnnotation describes a annotation for a peak in spectrum.
 *
 * User: rwang
 * Date: 08-Aug-2010
 * Time: 08:09:18
 */
public interface PeakAnnotation {
    /** get the m/z value */
    public Number getMz();
    /** get the intensity value */
    public Number getIntensity();
    /** get the annotation information, which contains all the detailed information */
    public PeakAnnotationInfo getAnnotationInfo();
}
